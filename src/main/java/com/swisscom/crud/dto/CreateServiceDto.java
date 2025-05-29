package com.swisscom.crud.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceDto {
    @NotBlank(message = "Service name cannot be blank")
    private String name;
}
