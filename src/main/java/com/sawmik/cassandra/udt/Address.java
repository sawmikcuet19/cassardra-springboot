package com.sawmik.cassandra.udt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@UserDefinedType("address")
public class Address {

    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
