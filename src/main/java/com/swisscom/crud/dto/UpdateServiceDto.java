package com.swisscom.crud.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceDto {
    @NotNull(message = "Version is required for updates to enable optimistic locking.")
    private Long version;
    private String name;
}
