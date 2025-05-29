package com.swisscom.crud.service;

import com.swisscom.crud.dto.CreateResourceDto;
import com.swisscom.crud.dto.UpdateResourceDto;
import com.swisscom.crud.exception.RecordNotFoundException;
import com.swisscom.crud.model.Resource;
import com.swisscom.crud.model.Service;
import com.swisscom.crud.repository.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class ResourceManager {
    private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);
    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;
    private final ServiceManager serviceManager;

    public ResourceManager(ServiceRepository serviceRepository, ServiceManager serviceManager, ModelMapper modelMapper) {
        this.serviceRepository = serviceRepository;
        this.serviceManager = serviceManager;
        this.modelMapper = modelMapper;
    }

    public Resource createResource(CreateResourceDto createResourceDto, String serviceId) {
        Service service = serviceManager.getServiceById(serviceId);

        Resource resource = modelMapper.map(createResourceDto, Resource.class);
        service.getResources().add(resource);


        serviceRepository.save(service);
        logger.info("Creating resource: {}", resource.getName());
        return resource;
    }

    public Resource getResourceById(String resourceId, String serviceId) {
        Service service = serviceManager.getServiceById(serviceId);
        logger.info("Attempting to retrieve resource with ID: {} from service ID: {}", resourceId, serviceId);
        for (Resource resource : service.getResources()) {
            if (resource.getId().equals(resourceId)) {
                return resource;
            }
        }
        throw new RecordNotFoundException("Resource not found with id: " + resourceId);
    }

    public List<Resource> getResourcePerService(String serviceId) {
        Service service = serviceManager.getServiceById(serviceId);

        if (service.getResources() == null) {
            return new ArrayList<>();
        }

        return service.getResources();
    }

    public Resource updateResource(String resourceId, String serviceId, UpdateResourceDto updateResourceDto) {
        Service service = serviceManager.getServiceById(serviceId);

        for (Resource resource : service.getResources()) {
            if (resource.getId().equals(resourceId)) {
                modelMapper.map(updateResourceDto, resource);
                serviceRepository.save(service);
                return resource;
            }
        }

        throw new RecordNotFoundException("Resource not found with id: " + resourceId);
    }

    public void deleteResource(String resourceId, String serviceId) {
        Service service = serviceManager.getServiceById(serviceId);

        for (Resource resource : service.getResources()) {
            if (resource.getId().equals(resourceId)) {
                service.getResources().remove(resource);
                serviceRepository.save(service);
                return;
            }
        }

        throw new RecordNotFoundException("Resource not found with id: " + resourceId);
    }

}