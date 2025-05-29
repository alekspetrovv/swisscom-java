package com.swisscom.crud.controller;

import com.swisscom.crud.dto.CreateOwnerDto;
import com.swisscom.crud.dto.ReadOwnerDto;
import com.swisscom.crud.dto.UpdateOwnerDto;
import com.swisscom.crud.model.Owner;
import com.swisscom.crud.service.OwnerManager;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services/{serviceId}/resources/{resourceId}/owners")
public class OwnerController {
    private final OwnerManager ownerManager;
    private final ModelMapper modelMapper;

    public OwnerController(OwnerManager ownerManager, ModelMapper modelMapper) {
        this.ownerManager = ownerManager;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<ReadOwnerDto> createOwner(@Valid @RequestBody CreateOwnerDto createOwnerDto, @PathVariable String serviceId, @PathVariable String resourceId) {
        Owner createdOwner = ownerManager.createOwner(createOwnerDto, serviceId, resourceId);
        return new ResponseEntity<>(modelMapper.map(createdOwner, ReadOwnerDto.class), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<ReadOwnerDto>> getOwnersPerResource(@PathVariable String serviceId, @PathVariable String resourceId) {
        List<Owner> owners = ownerManager.getOwnersPerResources(serviceId, resourceId);
        List<ReadOwnerDto> ownerDtos = owners.stream()
                .map(owner -> modelMapper.map(owner, ReadOwnerDto.class))
                .toList();
        return new ResponseEntity<>(ownerDtos, HttpStatus.OK);
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<ReadOwnerDto> getOwnerById(@PathVariable String serviceId, @PathVariable String resourceId, @PathVariable String ownerId) {
        Owner owner = ownerManager.getOwnerById(ownerId, serviceId, resourceId);
        return new ResponseEntity<>(modelMapper.map(owner, ReadOwnerDto.class), HttpStatus.OK);
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<ReadOwnerDto> updateResource(@PathVariable String ownerId, @PathVariable String resourceId, @PathVariable String serviceId, @Valid @RequestBody UpdateOwnerDto updateOwnerDto) {
        Owner updatedOwner = ownerManager.updateOwner(ownerId, serviceId, resourceId, updateOwnerDto);
        return new ResponseEntity<>(modelMapper.map(updatedOwner, ReadOwnerDto.class), HttpStatus.OK);
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteResource(@PathVariable String ownerId, @PathVariable String resourceId, @PathVariable String serviceId) {
        ownerManager.deleteOwner(ownerId, serviceId, resourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}