package com.sawmik.cassandra.repository;

import com.sawmik.cassandra.entity.Employee;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends CassandraRepository<Employee, UUID> {

    List<Employee> findByDepartment(String department);

    @Query("SELECT * FROM employees WHERE skills CONTAINS ?0 ALLOW FILTERING")
    List<Employee> findBySkill(String skill);
}
