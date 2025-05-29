package com.swisscom.crud.controller;

import com.swisscom.crud.dto.CreateServiceDto;
import com.swisscom.crud.dto.ReadServiceDto;
import com.swisscom.crud.dto.UpdateServiceDto;
import com.swisscom.crud.model.Service;
import com.swisscom.crud.service.ServiceManager;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final ServiceManager serviceManager;
    private final ModelMapper modelMapper;

    public ServiceController(ServiceManager serviceManager, ModelMapper modelMapper) {
        this.serviceManager = serviceManager;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<ReadServiceDto> createService(@Valid @RequestBody CreateServiceDto createServiceDto) {
        Service createdService = serviceManager.createService(createServiceDto);
        return new ResponseEntity<>(modelMapper.map(createdService, ReadServiceDto.class), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadServiceDto> getServiceById(@PathVariable String id) {
        Service service = serviceManager.getServiceByIdWithoutSummary(id);
        return new ResponseEntity<>(modelMapper.map(service, ReadServiceDto.class), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ReadServiceDto>> getAllServices() {
        List<Service> services = serviceManager.getAllServices();
        List<ReadServiceDto> serviceDtos = services.stream()
                .map(service -> modelMapper.map(service, ReadServiceDto.class))
                .toList();
        return new ResponseEntity<>(serviceDtos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadServiceDto> updateService(@PathVariable String id, @Valid @RequestBody UpdateServiceDto updateServiceDto) {
        Service updatedService = serviceManager.updateService(id, updateServiceDto);
        return new ResponseEntity<>(modelMapper.map(updatedService, ReadServiceDto.class), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable String id) {
        serviceManager.deleteService(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}