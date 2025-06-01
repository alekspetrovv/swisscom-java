package com.swisscom.crud;

import com.swisscom.crud.dto.CreateServiceDto;
import com.swisscom.crud.dto.UpdateServiceDto;
import com.swisscom.crud.exception.RecordNotFoundException;
import com.swisscom.crud.model.Service;
import com.swisscom.crud.repository.ServiceRepository;
import com.swisscom.crud.service.ServiceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceManagerTests {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ServiceManager serviceManager;

    private Service service;
    private CreateServiceDto createServiceDto;
    private UpdateServiceDto updateServiceDto;

    @BeforeEach
    void setUp() {
        service = new Service(1L, "service1", "Test Service", new ArrayList<>());
        createServiceDto = new CreateServiceDto("New Service");
        updateServiceDto = new UpdateServiceDto(1L, "Updated Service Name");
    }

    @Test
    void createService_shouldReturnSavedService() {
        Service mappedService = new Service(null, null, "New Service", new ArrayList<>());
        Service savedService = new Service(0L, "genId1", "New Service", new ArrayList<>());

        when(modelMapper.map(createServiceDto, Service.class)).thenReturn(mappedService);
        when(serviceRepository.save(mappedService)).thenReturn(savedService);

        Service result = serviceManager.createService(createServiceDto);

        assertNotNull(result);
        assertEquals("genId1", result.getId());
        assertEquals("New Service", result.getName());
        assertEquals(0L, result.getVersion());
        verify(modelMapper).map(createServiceDto, Service.class);
        verify(serviceRepository).save(mappedService);
    }

    @Test
    void getServiceByIdWithoutSummary_whenServiceExists_shouldReturnService() {
        when(serviceRepository.findByServiceId("service1")).thenReturn(Optional.of(service));

        Service result = serviceManager.getServiceByIdWithoutSummary("service1");

        assertNotNull(result);
        assertEquals("service1", result.getId());
        verify(serviceRepository).findByServiceId("service1");
    }

    @Test
    void getServiceByIdWithoutSummary_whenServiceNotExists_shouldThrowRecordNotFoundException() {
        when(serviceRepository.findByServiceId("nonexistent")).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            serviceManager.getServiceByIdWithoutSummary("nonexistent");
        });
        assertEquals("Service not found with id: nonexistent", exception.getMessage());
        verify(serviceRepository).findByServiceId("nonexistent");
    }

    @Test
    void getServiceById_whenServiceExists_shouldReturnService() {
        when(serviceRepository.findById("service1")).thenReturn(Optional.of(service));

        Service result = serviceManager.getServiceById("service1");

        assertNotNull(result);
        assertEquals("service1", result.getId());
        verify(serviceRepository).findById("service1");
    }

    @Test
    void getServiceById_whenServiceNotExists_shouldThrowRecordNotFoundException() {
        when(serviceRepository.findById("nonexistent")).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            serviceManager.getServiceById("nonexistent");
        });
        assertEquals("Service not found with id: nonexistent", exception.getMessage());
        verify(serviceRepository).findById("nonexistent");
    }


    @Test
    void getAllServices_whenServicesExist_shouldReturnServiceList() {
        List<Service> services = List.of(service, new Service(2L, "service2", "Another Service", new ArrayList<>()));
        when(serviceRepository.findAllServices()).thenReturn(services);

        List<Service> result = serviceManager.getAllServices();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(serviceRepository).findAllServices();
    }

    @Test
    void getAllServices_whenNoServicesExist_shouldReturnEmptyList() {
        when(serviceRepository.findAllServices()).thenReturn(Collections.emptyList());

        List<Service> result = serviceManager.getAllServices();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(serviceRepository).findAllServices();
    }

    @Test
    void updateService_whenServiceExistsAndVersionMatches_shouldReturnUpdatedService() {
        Service existingService = new Service(1L, "service1", "Test Service", new ArrayList<>());
        Service savedService = new Service(1L, "service1", "Updated Service Name", new ArrayList<>());

        when(serviceRepository.findByServiceId("service1")).thenReturn(Optional.of(existingService));
        doAnswer(invocation -> {
            UpdateServiceDto dto = invocation.getArgument(0);
            Service svc = invocation.getArgument(1);
            svc.setName(dto.getName());
            return null;
        }).when(modelMapper).map(any(UpdateServiceDto.class), any(Service.class));
        when(serviceRepository.save(existingService)).thenReturn(savedService);

        Service result = serviceManager.updateService("service1", updateServiceDto);

        assertNotNull(result);
        assertEquals("Updated Service Name", result.getName());
        assertEquals(savedService.getName(), result.getName());

        verify(serviceRepository).findByServiceId("service1");
        verify(modelMapper).map(updateServiceDto, existingService);
        verify(serviceRepository).save(existingService);
    }

    @Test
    void updateService_whenServiceNotFound_shouldThrowRecordNotFoundException() {
        when(serviceRepository.findByServiceId("nonexistent")).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            serviceManager.updateService("nonexistent", updateServiceDto);
        });
        assertEquals("Service not found with id: nonexistent", exception.getMessage());
        verify(serviceRepository).findByServiceId("nonexistent");
        verify(modelMapper, never()).map(any(), any());
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void updateService_whenVersionMismatch_shouldThrowOptimisticLockingFailureException() {
        Service existingServiceInDb = new Service(2L, "service1", "Test Service", new ArrayList<>());
        UpdateServiceDto updateDtoWithOldVersion = new UpdateServiceDto(1L, "Trying to update");

        when(serviceRepository.findByServiceId("service1")).thenReturn(Optional.of(existingServiceInDb));

        OptimisticLockingFailureException exception = assertThrows(OptimisticLockingFailureException.class, () -> {
            serviceManager.updateService("service1", updateDtoWithOldVersion);
        });
        assertTrue(exception.getMessage().contains("Conflict: Service Test Service was updated by another user"));
        verify(serviceRepository).findByServiceId("service1");
        verify(modelMapper, never()).map(any(), any());
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void deleteService_whenServiceExists_shouldCallDeleteById() {
        when(serviceRepository.findByServiceId("service1")).thenReturn(Optional.of(service));
        doNothing().when(serviceRepository).deleteById("service1");

        serviceManager.deleteService("service1");

        verify(serviceRepository).findByServiceId("service1");
        verify(serviceRepository).deleteById("service1");
    }

    @Test
    void deleteService_whenServiceNotFound_shouldThrowRecordNotFoundException() {
        when(serviceRepository.findByServiceId("nonexistent")).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            serviceManager.deleteService("nonexistent");
        });
        assertEquals("Service not found with id: nonexistent", exception.getMessage());
        verify(serviceRepository).findByServiceId("nonexistent");
        verify(serviceRepository, never()).deleteById(anyString());
    }
}
