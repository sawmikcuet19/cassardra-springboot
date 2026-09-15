package com.sawmik.cassandra.udt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@UserDefinedType("review")
public class Review {

    private String reviewer;
    private String comment;
    private Integer rating;
}
