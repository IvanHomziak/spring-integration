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

    // ##############################  SERVICE ACTIVATOR  ##############################  //
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

    // ##############################  TRANSFORMER  ##############################  //
    @GetMapping(value = "/processEmployeeStatus/{status}")
    public String processEmployeeStatus(@PathVariable("status") String status){
        return employeeGateway.processEmployeeStatus(status);
    }

    @GetMapping(value = "/processEmployeeSalary/{salary}")
    public String processEmployeeSalary(@PathVariable("salary") String salary){
        return employeeGateway.processEmployeeSalary(salary);
    }

    // ##############################  SPLITTER  ############################## //
    @GetMapping(value = "/getManagerList/{managers}")
    public String getManagerList(@PathVariable("managers") String managers){
        return employeeGateway.getManagersList(managers);
    }

    // ##############################  FILTER  ############################## //
    @GetMapping(value = "/getEmployeeIfADeveloper/{empDesignation}")
    public String getEmployeeIfADeveloper(@PathVariable("empDesignation") String empDesignation){
        return employeeGateway.getEmployeeIfADeveloper(empDesignation);
    }

    // ##############################  ROUTER  ############################## //
    @GetMapping(value = "/getEmployeeDepartment")
    public String getEmployeeDepartment(@RequestBody Employee employee){
        return employeeGateway.getEmployeeDepartment(employee);
    }

    @GetMapping(value = "/getSocialAccount")
    public String getSocialAccount(@RequestBody Employee employee){
        return employeeGateway.getSocialAccount(employee);
    }
}
