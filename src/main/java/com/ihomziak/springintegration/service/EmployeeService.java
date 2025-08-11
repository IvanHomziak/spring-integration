package com.ihomziak.springintegration.service;

import com.ihomziak.springintegration.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmployeeService {

    // ##############################  SERVICE ACTIVATOR ##############################  //
    // GET call
    @ServiceActivator(inputChannel = "request-emp-name-channel")
    public void getEmployeeName(Message<String> name){
        MessageChannel replyChannel = (MessageChannel) name.getHeaders().getReplyChannel();
        replyChannel.send(name);
    }

    // POST call
    @ServiceActivator(inputChannel = "request-hire-emp-channel", outputChannel = "process-emp-channel")
    public Message<Employee> hireEmployee(Message<Employee> employee){
        return employee;
    }

    @ServiceActivator(inputChannel = "process-emp-channel", outputChannel = "get-emp-status-channel")
    public Message<Employee> processEmployee(Message<Employee> employee){
        employee.getPayload().setEmployeeStatus("Permanent Role");
        return employee;
    }

    @ServiceActivator(inputChannel = "get-emp-status-channel")
    public void getEmployeeStatus(Message<Employee> employee){
        MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
        replyChannel.send(employee);
    }

    // ##############################  TRANSFORMER ##############################  //
    @Transformer(inputChannel = "emp-status-channel", outputChannel = "output-channel")
    public Message<String> convertIntoUpperCase(Message<String> message){
        String payload =  message.getPayload();
        Message<String> messageUpperCase = MessageBuilder.withPayload(payload.toUpperCase())
                .copyHeaders(message.getHeaders())
                .build();
        return messageUpperCase;
    }

    @Transformer(inputChannel = "emp-salary-channel", outputChannel = "output-channel")
    public Message<String> convertEmployeeSalary(Message<String> message){
        String payload =  message.getPayload();
        Message<String> messageUpperCase = MessageBuilder.withPayload(String.format("Salary: $ %s $", payload))
                .copyHeaders(message.getHeaders())
                .build();
        return messageUpperCase;
    }

    // ############################## COMMON OUTPUT CHANNEL ##############################  //
    @ServiceActivator(inputChannel = "output-channel")
    public void consumeStringMessage(Message<String> message){
        log.info("Received string message from 'output-channel': {}", message.getPayload());
        MessageChannel replyChannel = (MessageChannel) message.getHeaders().getReplyChannel();
        replyChannel.send(message);
    }
}
