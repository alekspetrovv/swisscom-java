package com.swisscom.crud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Owner {
    private String id = ObjectId.get().toString();
    private String name;
    private String accountNumber;
    private Number level;
}
