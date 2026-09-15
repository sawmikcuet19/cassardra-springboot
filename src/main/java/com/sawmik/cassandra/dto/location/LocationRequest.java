package com.sawmik.cassandra.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {

    private String name;
    private String address;
    private String type;
    private Double latitude;
    private Double longitude;
    private String country;
    private String city;
}
