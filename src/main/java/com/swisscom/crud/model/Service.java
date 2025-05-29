package com.swisscom.crud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "services")
public class Service {
    @Version
    private Long version;
    @Id
    private String id;
    private String name;
    private List<Resource> resources = new ArrayList<>();
}
