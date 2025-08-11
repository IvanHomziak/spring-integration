package com.ihomziak.springintegration.controller;

import com.ihomziak.springintegration.gateway.EmployeeGateway;
import com.ihomziak.springintegration.model.Employee;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/integrate")
public class EmployeeController {

    private final EmployeeGateway employeeGateway;

    @Autowired
    public EmployeeController(EmployeeGateway employeeGateway) {
        this.employeeGateway = employeeGateway;
    }

    @GetMapping(value = "{name}")
    public String getEmployeeName(@PathVariable("name") String name){
        return employeeGateway.getEmployeeName(name);
    }

    @PostMapping("/hireEmployee")
    public Employee hireEmployee(@RequestBody Employee employee){
        Message<Employee> reply = employeeGateway.hireEmployee(employee);
        Employee empResponse = reply.getPayload();
        return empResponse;
    }
}
