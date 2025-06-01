package com.swisscom.crud;

import com.swisscom.crud.dto.CreateOwnerDto;
import com.swisscom.crud.dto.UpdateOwnerDto;
import com.swisscom.crud.exception.RecordNotFoundException;
import com.swisscom.crud.model.Owner;
import com.swisscom.crud.model.Resource;
import com.swisscom.crud.model.Service;
import com.swisscom.crud.repository.ServiceRepository;
import com.swisscom.crud.service.OwnerManager;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerManagerTests {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceManager serviceManager;

    @Mock
    private ResourceManager resourceManager;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OwnerManager ownerManager;

    private Service service;
    private Resource resource1;
    private Owner owner1;
    private Owner owner2;
    private CreateOwnerDto createOwnerDto;
    private UpdateOwnerDto updateOwnerDto;

    private final String serviceId = "service123";
    private final String resourceId1 = "resourceABC";
    private final String ownerId1 = "ownerXYZ";
    private final String ownerId2 = "ownerDEF";
    private final String nonExistentId = "nonExistent123";


    @BeforeEach
    void setUp() {
        owner1 = new Owner(ownerId1, "Owner Test 1", "ACC001", 1);
        owner2 = new Owner(ownerId2, "Owner Test 2", "ACC002", 2);

        List<Owner> ownersInResource1 = new ArrayList<>();
        ownersInResource1.add(owner1);
        ownersInResource1.add(owner2);

        resource1 = new Resource(resourceId1, "Test Resource 1", ownersInResource1);
        Resource resource2 = new Resource("resourceOther", "Test Resource Other", new ArrayList<>());

        List<Resource> resourcesInService = new ArrayList<>();
        resourcesInService.add(resource1);
        resourcesInService.add(resource2);

        service = new Service(1L, serviceId, "Test Service", resourcesInService);

        createOwnerDto = new CreateOwnerDto("New Owner", "ACCNEW", 3);
        updateOwnerDto = new UpdateOwnerDto("Updated Owner Name", "ACCUPD", 4);
    }

    @Test
    void createOwner_whenServiceAndResourceExist_shouldAddOwnerAndSave() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);
        Owner newOwner = new Owner("newOwnerId", "New Owner", "ACCNEW", 3);
        when(modelMapper.map(createOwnerDto, Owner.class)).thenReturn(newOwner);
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        Owner created = ownerManager.createOwner(createOwnerDto, serviceId, resourceId1);

        assertNotNull(created);
        assertEquals("New Owner", created.getName());
        assertTrue(resource1.getOwners().contains(newOwner));
        verify(serviceManager).getServiceById(serviceId);
        verify(modelMapper).map(createOwnerDto, Owner.class);
        verify(serviceRepository).save(service);
    }

    @Test
    void createOwner_whenServiceNotFound_shouldThrowException() {
        when(serviceManager.getServiceById(serviceId)).thenThrow(new RecordNotFoundException("Service not found"));

        assertThrows(RecordNotFoundException.class, () -> ownerManager.createOwner(createOwnerDto, serviceId, resourceId1));
        verify(serviceManager).getServiceById(serviceId);
        verifyNoInteractions(modelMapper, serviceRepository);
    }

    @Test
    void createOwner_whenResourceNotFound_shouldThrowException() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> ownerManager.createOwner(createOwnerDto, serviceId, nonExistentId));
        assertEquals("Resource not found with id: " + nonExistentId, exception.getMessage());
        verify(serviceManager).getServiceById(serviceId);
        verifyNoInteractions(modelMapper, serviceRepository);
    }


    @Test
    void getOwnerById_whenResourceAndOwnerExist_shouldReturnOwner() {
        when(resourceManager.getResourceById(resourceId1, serviceId)).thenReturn(resource1);

        Owner found = ownerManager.getOwnerById(ownerId1, serviceId, resourceId1);

        assertNotNull(found);
        assertEquals(ownerId1, found.getId());
        assertEquals("Owner Test 1", found.getName());
        verify(resourceManager).getResourceById(resourceId1, serviceId);
    }

    @Test
    void getOwnerById_whenResourceNotFound_shouldPropagateExceptionFromResourceManager() {
        when(resourceManager.getResourceById(nonExistentId, serviceId))
                .thenThrow(new RecordNotFoundException("Resource not found"));

        assertThrows(RecordNotFoundException.class,
                () -> ownerManager.getOwnerById(ownerId1, serviceId, nonExistentId));
        verify(resourceManager).getResourceById(nonExistentId, serviceId);
    }

    @Test
    void getOwnerById_whenOwnerNotFoundInResource_shouldThrowException() {
        when(resourceManager.getResourceById(resourceId1, serviceId)).thenReturn(resource1);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> ownerManager.getOwnerById(nonExistentId, serviceId, resourceId1));
        assertEquals("Owner not found with id: " + nonExistentId, exception.getMessage());
        verify(resourceManager).getResourceById(resourceId1, serviceId);
    }

    @Test
    void getOwnersPerResources_whenResourceExists_shouldReturnOwners() {
        when(resourceManager.getResourceById(resourceId1, serviceId)).thenReturn(resource1);

        List<Owner> result = ownerManager.getOwnersPerResources(serviceId, resourceId1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(owner1));
        assertTrue(result.contains(owner2));
        verify(resourceManager).getResourceById(resourceId1, serviceId);
    }

    @Test
    void getOwnersPerResources_whenResourceHasNoOwners_shouldReturnEmptyList() {
        resource1.setOwners(new ArrayList<>());
        when(resourceManager.getResourceById(resourceId1, serviceId)).thenReturn(resource1);

        List<Owner> result = ownerManager.getOwnersPerResources(serviceId, resourceId1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(resourceManager).getResourceById(resourceId1, serviceId);
    }

    @Test
    void getOwnersPerResources_whenResourceNotFound_shouldPropagateExceptionFromResourceManager() {
        when(resourceManager.getResourceById(nonExistentId, serviceId))
                .thenThrow(new RecordNotFoundException("Resource not found"));

        assertThrows(RecordNotFoundException.class,
                () -> ownerManager.getOwnersPerResources(serviceId, nonExistentId));
        verify(resourceManager).getResourceById(nonExistentId, serviceId);
    }

    @Test
    void updateOwner_whenAllExist_shouldUpdateAndSave() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);
        doAnswer(invocation -> {
            UpdateOwnerDto dto = invocation.getArgument(0);
            Owner o = invocation.getArgument(1);
            o.setName(dto.getName());
            o.setAccountNumber(dto.getAccountNumber());
            o.setLevel(dto.getLevel());
            return null;
        }).when(modelMapper).map(eq(updateOwnerDto), any(Owner.class));
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        Owner updated = ownerManager.updateOwner(ownerId1, serviceId, resourceId1, updateOwnerDto);

        assertNotNull(updated);
        assertEquals(updateOwnerDto.getName(), updated.getName());
        assertEquals(updateOwnerDto.getAccountNumber(), updated.getAccountNumber());
        assertEquals(updateOwnerDto.getLevel(), updated.getLevel());
        verify(serviceManager).getServiceById(serviceId);
        verify(modelMapper).map(updateOwnerDto, owner1);
        verify(serviceRepository).save(service);
    }

    @Test
    void updateOwner_whenServiceNotFound_shouldThrowException() {
        when(serviceManager.getServiceById(serviceId)).thenThrow(new RecordNotFoundException("Service not found"));

        assertThrows(RecordNotFoundException.class,
                () -> ownerManager.updateOwner(ownerId1, serviceId, resourceId1, updateOwnerDto));
        verify(serviceManager).getServiceById(serviceId);
        verifyNoInteractions(modelMapper, serviceRepository);
    }

    @Test
    void updateOwner_whenResourceNotFound_shouldThrowException() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> ownerManager.updateOwner(ownerId1, serviceId, nonExistentId, updateOwnerDto));
        assertEquals("Resource not found with id: " + nonExistentId, exception.getMessage());
        verify(serviceManager).getServiceById(serviceId);
        verifyNoInteractions(modelMapper, serviceRepository);
    }

    @Test
    void updateOwner_whenOwnerNotFound_shouldThrowException() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> ownerManager.updateOwner(nonExistentId, serviceId, resourceId1, updateOwnerDto));
        assertEquals("Owner not found with id: " + nonExistentId, exception.getMessage());
        verify(serviceManager).getServiceById(serviceId);
        verifyNoInteractions(modelMapper, serviceRepository);
    }

    @Test
    void deleteOwner_whenAllExist_shouldRemoveAndSave() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);
        when(serviceRepository.save(any(Service.class))).thenReturn(service);
        int initialSize = resource1.getOwners().size();

        ownerManager.deleteOwner(ownerId1, serviceId, resourceId1);

        assertEquals(initialSize - 1, resource1.getOwners().size());
        assertFalse(resource1.getOwners().contains(owner1));
        verify(serviceManager).getServiceById(serviceId);
        verify(serviceRepository).save(service);
    }

    @Test
    void deleteOwner_whenServiceNotFound_shouldThrowException() {
        when(serviceManager.getServiceById(serviceId)).thenThrow(new RecordNotFoundException("Service not found"));

        assertThrows(RecordNotFoundException.class,
                () -> ownerManager.deleteOwner(ownerId1, serviceId, resourceId1));
        verify(serviceManager).getServiceById(serviceId);
        verifyNoInteractions(serviceRepository);
    }

    @Test
    void deleteOwner_whenResourceNotFound_shouldThrowException() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> ownerManager.deleteOwner(ownerId1, serviceId, nonExistentId));
        assertEquals("Resource not found with id: " + nonExistentId, exception.getMessage());
        verify(serviceManager).getServiceById(serviceId);
        verifyNoInteractions(serviceRepository);
    }

    @Test
    void deleteOwner_whenOwnerNotFound_shouldThrowExceptionWithResourceId() {
        when(serviceManager.getServiceById(serviceId)).thenReturn(service);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> ownerManager.deleteOwner(nonExistentId, serviceId, resourceId1));
        assertEquals("Resource not found with id: " + resourceId1, exception.getMessage());
        verify(serviceManager).getServiceById(serviceId);
        verifyNoInteractions(serviceRepository);
    }
}