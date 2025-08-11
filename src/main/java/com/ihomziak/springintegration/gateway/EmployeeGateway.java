package com.ihomziak.springintegration.gateway;

import com.ihomziak.springintegration.model.Employee;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway
public interface EmployeeGateway {

    // ##############################  SERVICE ACTIVATOR ##############################  //
    // GET call
    @Gateway(requestChannel = "request-emp-name-channel")
    String getEmployeeName(String name);

    // POST call
    @Gateway(requestChannel = "request-hire-emp-channel")
    Message<Employee> hireEmployee(Employee name);

    // ##############################  TRANSFORMER  ############################## //
    @Gateway(requestChannel = "emp-status-channel")
    String processEmployeeStatus(String status);

    @Gateway(requestChannel = "emp-salary-channel")
    String processEmployeeSalary(String salary);

    // ##############################  SPLITTER  ############################## //
    @Gateway(requestChannel = "emp-managers-channel")
    String getManagersList(String managers);

    // ##############################  FILTER  ############################## //
    @Gateway(requestChannel = "dev-emp-channel")
    String getEmployeeIfADeveloper(String empDesignation);

    // ##############################  ROUTER  ############################## //
    @Gateway(requestChannel = "emp-dept-channel")
    String getEmployeeDepartment(Employee employee);
}
