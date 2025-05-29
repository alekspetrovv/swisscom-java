package com.swisscom.crud.service;

import com.swisscom.crud.dto.CreateOwnerDto;
import com.swisscom.crud.dto.UpdateOwnerDto;
import com.swisscom.crud.exception.RecordNotFoundException;
import com.swisscom.crud.model.Owner;
import com.swisscom.crud.model.Resource;
import com.swisscom.crud.model.Service;
import com.swisscom.crud.repository.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@org.springframework.stereotype.Service
public class OwnerManager {
    private static final Logger logger = LoggerFactory.getLogger(OwnerManager.class);
    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;
    private final ServiceManager serviceManager;
    private final ResourceManager resourceManager;

    public OwnerManager(ServiceRepository serviceRepository, ServiceManager serviceManager, ResourceManager resourceManager, ModelMapper modelMapper) {
        this.serviceRepository = serviceRepository;
        this.serviceManager = serviceManager;
        this.resourceManager = resourceManager;
        this.modelMapper = modelMapper;
    }

    public Owner createOwner(CreateOwnerDto createOwnerDto, String serviceId, String resourceId) {
        Service service = serviceManager.getServiceById(serviceId);
        Resource foundResource = null;

        for (Resource resource : service.getResources()) {
            if (resource.getId().equals(resourceId)) {
                foundResource = resource;
                break;
            }
        }

        if (foundResource == null) {
            throw new RecordNotFoundException("Resource not found with id: " + resourceId);
        }

        Owner owner = modelMapper.map(createOwnerDto, Owner.class);
        foundResource.getOwners().add(owner);

        serviceRepository.save(service);
        logger.info("Creating owner: {}", owner.getName());
        return owner;
    }

    public Owner getOwnerById(String ownerId, String serviceId, String resourceId) {
        logger.info("Calling method");
        Resource resource = resourceManager.getResourceById(resourceId, serviceId);
        for (Owner owner : resource.getOwners()) {
            if (owner.getId().equals(ownerId)) {
                return owner;
            }
        }
        throw new RecordNotFoundException("Owner not found with id: " + ownerId);
    }

    public List<Owner> getOwnersPerResources(String serviceId, String resourceId) {
        Resource resource = resourceManager.getResourceById(resourceId, serviceId);
        return resource.getOwners();
    }


    public Owner updateOwner(String ownerId, String serviceId, String resourceId, UpdateOwnerDto updateOwnerDto) {
        Service service = serviceManager.getServiceById(serviceId);
        Resource foundResource = null;

        for (Resource resource : service.getResources()) {
            if (resource.getId().equals(resourceId)) {
                foundResource = resource;
                break;
            }
        }

        if (foundResource == null) {
            throw new RecordNotFoundException("Resource not found with id: " + resourceId);
        }

        for (Owner owner : foundResource.getOwners()) {
            if (owner.getId().equals(ownerId)) {
                modelMapper.map(updateOwnerDto, owner);
                serviceRepository.save(service);
                return owner;
            }
        }

        throw new RecordNotFoundException("Owner not found with id: " + ownerId);
    }

    public void deleteOwner(String ownerId, String serviceId, String resourceId) {
        Service service = serviceManager.getServiceById(serviceId);
        Resource foundResource = null;

        for (Resource resource : service.getResources()) {
            if (resource.getId().equals(resourceId)) {
                foundResource = resource;
                break;
            }
        }

        if (foundResource == null) {
            throw new RecordNotFoundException("Resource not found with id: " + resourceId);
        }

        for (Owner owner : foundResource.getOwners()) {
            if (owner.getId().equals(ownerId)) {
                foundResource.getOwners().remove(owner);
                serviceRepository.save(service);
                return;
            }
        }

        throw new RecordNotFoundException("Resource not found with id: " + resourceId);
    }


}