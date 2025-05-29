package com.swisscom.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadServiceDto {
    private String id;
    private Long version;
    private String name;
}
