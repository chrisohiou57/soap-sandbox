package com.chrias.bootcamelcfx.endpoint;

import com.chrias.bootcamelcfx.service.BackendService;
import com.chrias.employee.EmployeeByIdRequest;
import com.chrias.employee.EmployeeByNameRequest;
import com.chrias.employee.EmployeeResponse;
import com.chrias.employee.EmployeeServicePortType;
import com.chrias.employee.EmployeesResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmployeeServiceEndpoint implements EmployeeServicePortType {

    Logger log = LoggerFactory.getLogger(EmployeeServiceEndpoint.class);

    BackendService backendService;

    public EmployeeServiceEndpoint (BackendService backendService){
        this.backendService = backendService;
    }

    @Override
    public EmployeesResponse getEmployeesByName(EmployeeByNameRequest parameters) {
        EmployeesResponse employeesResponse = new EmployeesResponse();
        try {
            employeesResponse.getEmployee().addAll(backendService.getEmployeesByName(parameters.getFirstname(), parameters.getLastname()));
        } catch (Exception e) {
            log.error("Error while setting values for employee object", e);
        }
        return employeesResponse;
    }

    @Override
    public EmployeeResponse getEmployeeById(EmployeeByIdRequest parameters) {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        try {
            employeeResponse.setEmployee(backendService.getEmployeeById(parameters.getId()));
        } catch (Exception e) {
            log.error("Error while setting values for employee object", e);
        }
        return employeeResponse;
    }
}