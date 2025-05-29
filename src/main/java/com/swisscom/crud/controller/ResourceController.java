package com.swisscom.crud.controller;

import com.swisscom.crud.dto.CreateResourceDto;
import com.swisscom.crud.dto.ReadResourceDto;
import com.swisscom.crud.dto.UpdateResourceDto;
import com.swisscom.crud.model.Resource;
import com.swisscom.crud.service.ResourceManager;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services/{serviceId}/resources")
public class ResourceController {
    private final ResourceManager resourceManager;
    private final ModelMapper modelMapper;

    public ResourceController(ResourceManager resourceManager, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.resourceManager = resourceManager;
    }

    @PostMapping
    public ResponseEntity<ReadResourceDto> createResource(@Valid @RequestBody CreateResourceDto createResourceDto, @PathVariable String serviceId) {
        Resource createdResource = resourceManager.createResource(createResourceDto, serviceId);
        return new ResponseEntity<>(modelMapper.map(createdResource, ReadResourceDto.class), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<ReadResourceDto>> getResourcePerService(@PathVariable String serviceId) {
        List<Resource> resources = resourceManager.getResourcePerService(serviceId);
        List<ReadResourceDto> resourceDtos = resources.stream()
                .map(resource -> modelMapper.map(resource, ReadResourceDto.class))
                .toList();
        return new ResponseEntity<>(resourceDtos, HttpStatus.OK);
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ReadResourceDto> getResourceById(@PathVariable String resourceId, @PathVariable String serviceId) {
        Resource resource = resourceManager.getResourceById(resourceId, serviceId);
        return new ResponseEntity<>(modelMapper.map(resource, ReadResourceDto.class), HttpStatus.OK);
    }

    @PutMapping("/{resourceId}")
    public ResponseEntity<ReadResourceDto> updateResource(@PathVariable String resourceId, @PathVariable String serviceId, @Valid @RequestBody UpdateResourceDto updateResourceDto) {
        Resource updatedResource = resourceManager.updateResource(resourceId, serviceId, updateResourceDto);
        return new ResponseEntity<>(modelMapper.map(updatedResource, ReadResourceDto.class), HttpStatus.OK);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable String resourceId, @PathVariable String serviceId) {
        resourceManager.deleteResource(resourceId, serviceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}