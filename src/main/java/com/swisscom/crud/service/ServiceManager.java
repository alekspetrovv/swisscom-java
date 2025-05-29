package com.swisscom.crud.service;

import com.swisscom.crud.dto.CreateServiceDto;
import com.swisscom.crud.dto.UpdateServiceDto;
import com.swisscom.crud.exception.RecordNotFoundException;
import com.swisscom.crud.model.Service;
import com.swisscom.crud.repository.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
    private static final String CACHE_NAME = "ServiceCache";
    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    public ServiceManager(ServiceRepository serviceRepository, ModelMapper modelMapper) {
        this.serviceRepository = serviceRepository;
        this.modelMapper = modelMapper;
    }

    @CachePut(value = CACHE_NAME, key = "#result.id")
    public Service createService(CreateServiceDto createServiceDto) {
        Service service = modelMapper.map(createServiceDto, Service.class);
        logger.info("Creating service: {}", service.getName());
        return serviceRepository.save(service);
    }

    @Cacheable(value = CACHE_NAME, key = "#id")
    public Service getServiceByIdWithoutSummary(String id) {
        return serviceRepository.findByServiceId(id)
                .orElseThrow(() -> new RecordNotFoundException("Service not found with id: " + id));
    }

    public Service getServiceById(String id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Service not found with id: " + id));
    }

    public List<Service> getAllServices() {
        logger.info("Fetching all services from DB.");
        List<Service> services = serviceRepository.findAllServices();
        if (services.isEmpty()) {
            return new ArrayList<>();
        }

        return services;
    }

    @CachePut(value = CACHE_NAME, key = "#id")
    public Service updateService(String id, UpdateServiceDto updateServiceDto) {
        Service existingService = getServiceByIdWithoutSummary(id);

        if (!existingService.getVersion().equals(updateServiceDto.getVersion())) {
            logger.warn("Optimistic locking conflict for Service ID: {}. Client version: {}, DB version: {}",
                    id, updateServiceDto.getVersion(), existingService.getVersion());
            throw new OptimisticLockingFailureException(
                    "Conflict: Service " + existingService.getName() +
                            " was updated by another user . Please refresh and try again.");
        }

        modelMapper.map(updateServiceDto, existingService);
        return serviceRepository.save(existingService);
    }

    @CacheEvict(value = CACHE_NAME, key = "#id")
    public void deleteService(String id) {
        getServiceByIdWithoutSummary(id);
        logger.info("Deleting service with id: {}. Cache entry will be evicted.", id);
        serviceRepository.deleteById(id);
    }
}