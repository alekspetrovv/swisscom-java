package com.swisscom.crud.dto;

import com.swisscom.crud.model.Owner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadResourceDto {
    private String id;
    private String name;
    private List<Owner> owners;
}
