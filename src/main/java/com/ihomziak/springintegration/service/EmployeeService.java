package com.ihomziak.springintegration.service;

import com.ihomziak.springintegration.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.*;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Service
public class EmployeeService {

    // ##############################  SERVICE ACTIVATOR  ##############################  //
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

    // ##############################  TRANSFORMER  ##############################  //
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

    // ##############################  SPLITTER  ##############################  //
    @Splitter(inputChannel = "emp-managers-channel", outputChannel = "managers-channel")
    public List<Message<String>> splitMassage(Message<?> message){
        List<Message<String>> massages = new ArrayList<>();

        String[] msgSplits = message.getPayload().toString().split(",");

        for (String split : msgSplits) {
            Message<String> msg =  MessageBuilder.withPayload(split)
                    .copyHeaders(message.getHeaders())
                    .build();

            massages.add(msg);
        }

        return massages;
    }

    // ##############################  AGGREGATOR  ##############################  //
    @Aggregator(inputChannel = "managers-channel", outputChannel = "output-channel")
    public Message<String> getAllManagers(List<Message<String>> massages){
        StringJoiner joiner = new StringJoiner(" & ", "[", "]" );

        for  (Message<String> message : massages) {
            joiner.add(message.getPayload());
        }

        String managers = joiner.toString();
        log.info("All managers are: {}", managers);
        Message<String> updatedMsg = MessageBuilder.withPayload(managers).build();

        return updatedMsg;
    }

    // ##############################  FILTER  ##############################  //
    @Filter(inputChannel = "dev-emp-channel", outputChannel = "output-channel")
    public boolean filter(Message<?> message){
        String msg =  message.getPayload().toString();
        return msg.contains("Dev");
    }

    // ##############################  ROUTER  ##############################  //
    @Router(inputChannel = "emp-dept-channel")
    public String getEmployeeDepartment(Message<Employee> message){
        String deptRout = null;

        switch (message.getPayload().getEmployeeDepartment()) {
            case "SALES" :
                deptRout = "sales-channel";
                break;

            case "MARKETING" :
                deptRout = "marketing-channel";
                break;
        }
        return deptRout;
    }

    @ServiceActivator(inputChannel = "sales-channel")
    public void getSalesDept(Message<Employee> employee){
        Message<String> sales = MessageBuilder.withPayload("SALES DEPARTMENT").build();
        log.info("Received message from: {}", sales.getPayload());

        MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
        replyChannel.send(sales);
    }

    @ServiceActivator(inputChannel = "marketing-channel")
    public void getMarketingDept(Message<Employee> employee){
        Message<String> marketing = MessageBuilder.withPayload("MARKETING DEPARTMENT").build();
        log.info("Received message from: {}", marketing.getPayload());

        MessageChannel replyChannel = (MessageChannel) employee.getHeaders().getReplyChannel();
        replyChannel.send(marketing);
    }

    // ############################## COMMON OUTPUT CHANNEL ##############################  //
    @ServiceActivator(inputChannel = "output-channel")
    public void consumeStringMessage(Message<String> message){
        log.info("Received string message from 'output-channel': {}", message.getPayload());
        MessageChannel replyChannel = (MessageChannel) message.getHeaders().getReplyChannel();
        replyChannel.send(message);
    }



}
