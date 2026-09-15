package com.sawmik.cassandra.service;

import com.sawmik.cassandra.entity.Employee;
import com.sawmik.cassandra.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee save(Employee employee) {
        if (employee.getId() == null) employee.setId(UUID.randomUUID());
        return employeeRepository.save(employee);
    }

    public Optional<Employee> findById(UUID id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public void deleteById(UUID id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> findByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    public List<Employee> findBySkill(String skill) {
        return employeeRepository.findBySkill(skill);
    }
}
