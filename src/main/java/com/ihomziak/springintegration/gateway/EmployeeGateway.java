package com.ihomziak.springintegration.gateway;

import com.ihomziak.springintegration.model.Employee;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway
public interface EmployeeGateway {

    // ### Service Activator ###
    // GET call
    @Gateway(requestChannel = "request-emp-name-channel")
    String getEmployeeName(String name);

    // POST call
    @Gateway(requestChannel = "request-hire-emp-channel")
    Message<Employee> hireEmployee(Employee name);
}
