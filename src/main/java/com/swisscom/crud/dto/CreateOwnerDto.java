package com.swisscom.crud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOwnerDto {
    @NotBlank(message = "Owner name cannot be blank")
    private String name;

    @NotBlank(message = "Account number cannot be blank")
    private String accountNumber;

    @NotNull(message = "Level cannot be null")
    private Integer level;
}
