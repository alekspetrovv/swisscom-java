package com.swisscom.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOwnerDto {
    private String name;
    private String accountNumber;
    private Integer level;
}
