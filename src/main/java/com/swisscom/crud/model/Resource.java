package com.swisscom.crud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
    private String id = ObjectId.get().toString();
    private String name;
    private List<Owner> owners = new ArrayList<>();
}
