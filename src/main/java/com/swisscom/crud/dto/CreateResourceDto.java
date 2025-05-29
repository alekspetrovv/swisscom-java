package com.swisscom.crud.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateResourceDto {
    @NotBlank(message = "Resource name cannot be blank")
    private String name;
}
