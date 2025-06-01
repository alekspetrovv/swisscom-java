package com.swisscom.crud;

import com.swisscom.crud.dto.CreateResourceDto;
import com.swisscom.crud.dto.UpdateResourceDto;
import com.swisscom.crud.exception.RecordNotFoundException;
import com.swisscom.crud.model.Resource;
import com.swisscom.crud.model.Service;
import com.swisscom.crud.repository.ServiceRepository;
import com.swisscom.crud.service.ResourceManager;
import com.swisscom.crud.service.ServiceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceManagerTests {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceManager serviceManager;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ResourceManager resourceManager;

    private Service service;
    private Resource resource1;
    private Resource resource2;
    private CreateResourceDto createResourceDto;
    private UpdateResourceDto updateResourceDto;

    private final String serviceId = "service123";
    private final String resourceId1 = "resourceABC";
    private final String resourceId2 = "resourceXYZ";
    private final String nonExistentResourceId = "nonExistentResource";


    @BeforeEach
    void setUp() {
        resource1 = new Resource(resourceId1, "Test Resource 1", new ArrayList<>());
        resource2 = new Resource(resourceId2, "Test Resource 2", new ArrayList<>());

        List<Resource> resources = new ArrayList<>();
        resources.add(resource1);
        resources.add(resource2);

        service = new Service(1L, serviceId, "Test Service", resources);

        createResourceDto = new CreateResourceDto("New Resource");
        updateResourceDto = new UpdateResourceDto("Updated Resource Name");
    }

    @Test
    void createResource_whenServiceExists_shouldAddResourceAndSave() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);
        Resource newResource = new Resource("newId", "New Resource", new ArrayList<>());
        when(modelMapper.map(createResourceDto, Resource.class)).thenReturn(newResource);
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        Resource created = resourceManager.createResource(createResourceDto, serviceId);

        assertNotNull(created);
        assertEquals("New Resource", created.getName());
        assertTrue(service.getResources().contains(newResource));
        verify(serviceManager).getServiceById(serviceId);
        verify(modelMapper).map(createResourceDto, Resource.class);
        verify(serviceRepository).save(service);
    }

    @Test
    void createResource_whenServiceNotFound_shouldThrowRecordNotFoundException() {
        when(serviceManager.getServiceById(serviceId)).thenThrow(new RecordNotFoundException("Service not found"));

        assertThrows(RecordNotFoundException.class, () -> {
            resourceManager.createResource(createResourceDto, serviceId);
        });

        verify(serviceManager).getServiceById(serviceId);
        verify(modelMapper, never()).map(any(), any());
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void getResourceById_whenServiceAndResourceExist_shouldReturnResource() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);

        Resource found = resourceManager.getResourceById(resourceId1, serviceId);

        assertNotNull(found);
        assertEquals(resourceId1, found.getId());
        assertEquals("Test Resource 1", found.getName());
        verify(serviceManager).getServiceById(serviceId);
    }

    @Test
    void getResourceById_whenServiceNotFound_shouldThrowRecordNotFoundException() {
        when(serviceManager.getServiceById(serviceId)).thenThrow(new RecordNotFoundException("Service not found"));

        assertThrows(RecordNotFoundException.class, () -> {
            resourceManager.getResourceById(resourceId1, serviceId);
        });
        verify(serviceManager).getServiceById(serviceId);
    }

    @Test
    void getResourceById_whenResourceNotFoundInService_shouldThrowRecordNotFoundException() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);

        assertThrows(RecordNotFoundException.class, () -> {
            resourceManager.getResourceById(nonExistentResourceId, serviceId);
        });
        verify(serviceManager).getServiceById(serviceId);
    }

    @Test
    void getResourcePerService_whenServiceExists_shouldReturnResources() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);

        List<Resource> result = resourceManager.getResourcePerService(serviceId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(resource1));
        assertTrue(result.contains(resource2));
        verify(serviceManager).getServiceById(serviceId);
    }

    @Test
    void getResourcePerService_whenServiceHasNullResources_shouldReturnEmptyList() {
        Service serviceWithNullResources = new Service(2L, serviceId, "Service With Null Resources", null);
        when(serviceManager.getServiceById(serviceId)).thenReturn(serviceWithNullResources);

        List<Resource> result = resourceManager.getResourcePerService(serviceId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(serviceManager).getServiceById(serviceId);
    }

    @Test
    void getResourcePerService_whenServiceHasEmptyResources_shouldReturnEmptyList() {
        Service serviceWithEmptyResources = new Service(3L, serviceId, "Service With Empty Resources", new ArrayList<>());
        when(serviceManager.getServiceById(serviceId)).thenReturn(serviceWithEmptyResources);

        List<Resource> result = resourceManager.getResourcePerService(serviceId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(serviceManager).getServiceById(serviceId);
    }

    @Test
    void getResourcePerService_whenServiceNotFound_shouldThrowRecordNotFoundException() {
        when(serviceManager.getServiceById(serviceId)).thenThrow(new RecordNotFoundException("Service not found"));

        assertThrows(RecordNotFoundException.class, () -> {
            resourceManager.getResourcePerService(serviceId);
        });
        verify(serviceManager).getServiceById(serviceId);
    }


    @Test
    void updateResource_whenServiceAndResourceExist_shouldUpdateAndSave() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);
        doAnswer(invocation -> {
            UpdateResourceDto dto = invocation.getArgument(0);
            Resource res = invocation.getArgument(1);
            res.setName(dto.getName());
            return null;
        }).when(modelMapper).map(eq(updateResourceDto), any(Resource.class));
        when(serviceRepository.save(any(Service.class))).thenReturn(service);


        Resource updated = resourceManager.updateResource(resourceId1, serviceId, updateResourceDto);

        assertNotNull(updated);
        assertEquals(updateResourceDto.getName(), updated.getName());
        verify(serviceManager).getServiceById(serviceId);
        verify(modelMapper).map(updateResourceDto, resource1);
        verify(serviceRepository).save(service);
    }

    @Test
    void updateResource_whenServiceNotFound_shouldThrowRecordNotFoundException() {
        when(serviceManager.getServiceById(serviceId)).thenThrow(new RecordNotFoundException("Service not found"));

        assertThrows(RecordNotFoundException.class, () -> {
            resourceManager.updateResource(resourceId1, serviceId, updateResourceDto);
        });
        verify(serviceManager).getServiceById(serviceId);
        verify(modelMapper, never()).map(any(), any());
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void updateResource_whenResourceNotFoundInService_shouldThrowRecordNotFoundException() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);

        assertThrows(RecordNotFoundException.class, () -> {
            resourceManager.updateResource(nonExistentResourceId, serviceId, updateResourceDto);
        });
        verify(serviceManager).getServiceById(serviceId);
        verify(modelMapper, never()).map(any(), any());
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void deleteResource_whenServiceAndResourceExist_shouldRemoveAndSave() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        int initialSize = service.getResources().size();
        resourceManager.deleteResource(resourceId1, serviceId);

        assertEquals(initialSize - 1, service.getResources().size());
        assertFalse(service.getResources().contains(resource1));
        verify(serviceManager).getServiceById(serviceId);
        verify(serviceRepository).save(service);
    }

    @Test
    void deleteResource_whenServiceNotFound_shouldThrowRecordNotFoundException() {
        when(serviceManager.getServiceById(serviceId)).thenThrow(new RecordNotFoundException("Service not found"));

        assertThrows(RecordNotFoundException.class, () -> {
            resourceManager.deleteResource(resourceId1, serviceId);
        });
        verify(serviceManager).getServiceById(serviceId);
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void deleteResource_whenResourceNotFoundInService_shouldThrowRecordNotFoundException() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);

        assertThrows(RecordNotFoundException.class, () -> {
            resourceManager.deleteResource(nonExistentResourceId, serviceId);
        });
        verify(serviceManager).getServiceById(serviceId);
        verify(serviceRepository, never()).save(any());
    }
}