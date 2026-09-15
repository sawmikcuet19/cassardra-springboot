package com.sawmik.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

import org.springframework.data.cassandra.core.mapping.Indexed;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @PrimaryKey
    @Id
    private String id;

    @Column("username")
    @Indexed
    private String username;

    @Column("email")
    @Indexed
    private String email;

    @Column("password")
    private String password;

    @Column("role")
    private String role;

    @Column("enabled")
    private Boolean enabled = true;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
