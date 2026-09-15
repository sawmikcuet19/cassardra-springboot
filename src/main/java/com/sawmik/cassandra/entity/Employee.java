package com.sawmik.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Table("employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @PrimaryKey
    @Id
    private UUID id;

    @Column("department")
    private String department;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    @Column("skills")
    private Set<String> skills;

    @Column("certifications")
    private Set<String> certifications;

    @Column("attributes")
    private Map<String, String> attributes;
}
