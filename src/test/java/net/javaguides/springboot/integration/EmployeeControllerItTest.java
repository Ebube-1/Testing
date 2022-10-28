package net.javaguides.springboot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class EmployeeControllerItTest {

    @Container
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest")
            .withUsername("docker")
            .withPassword("testContainer")
            .withDatabaseName("ems");

    //username:test
    //password:test
    //databaseName:test
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        employeeRepository.deleteAll();
    }

    @Test
    public void givenEmployeeObject_whenCreateEmployee_shouldReturnSavedEmployee() throws Exception {
        Employee employee = Employee.builder()
                .firstName("john")
                .lastName("doe")
                .email("johndoe@gmail.com")
                .build();

        mockMvc.perform(post("/api/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(employee)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(employee.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(employee.getLastName()))
                .andExpect(jsonPath("$.email").value(employee.getEmail()));

    }


    @Test
    public void givenListOfEmployees_whenGetAllEmployees_thenReturnEmployeesList() throws Exception {
        List<Employee> employeeList = new ArrayList<>(List.of(
                Employee.builder()
                        .firstName("john")
                        .lastName("doe")
                        .email("johndoe@gmail.com")
                        .build(),

                Employee.builder()
                        .firstName("mary")
                        .lastName("hope")
                        .email("maryhope@gmail.com")
                        .build(),

                Employee.builder()
                        .firstName("mary")
                        .lastName("hope")
                        .email("maryhope@gmail.com")
                        .build()
        ));
        employeeRepository.saveAll(employeeList);

        mockMvc.perform(get("/api/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(employeeList)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(employeeList.size()));

    }

    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() throws Exception {
        Employee employee = Employee.builder()
                .firstName("john")
                .lastName("doe")
                .email("johndoe@gmail.com")
                .build();

        employeeRepository.save(employee);

        mockMvc.perform(get("/api/employees/{id}", employee.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(employee.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(employee.getLastName()))
                .andExpect(jsonPath("$.email").value(employee.getEmail()));

    }

    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenReturnError() throws Exception {
        long employeeId = 1L;
        Employee employee = Employee.builder()
                .firstName("john")
                .lastName("doe")
                .email("johndoe@gmail.com")
                .build();

        employeeRepository.save(employee);

        mockMvc.perform(get("/api/employees/{id}", employeeId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());

    }

    @Test
    public void givenEmployeeId_whenUpdateEmployee_thenReturnUpdatedEmployeeObject() throws Exception {
        Employee employee = Employee.builder()
                .firstName("john")
                .lastName("doe")
                .email("johndoe@gmail.com")
                .build();

        Employee updatedEmployee = Employee.builder()
                .firstName("steve")
                .lastName("jobs")
                .email("stevejobs@gmail.com")
                .build();

        employeeRepository.save(employee);

        mockMvc.perform(put("/api/employees/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(updatedEmployee.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedEmployee.getLastName()))
                .andExpect(jsonPath("$.email").value(updatedEmployee.getEmail()));
    }


    @Test
    public void givenEmployeeId_whenUpdateEmployee_thenReturnErrorCode() throws Exception {
        long employeeId = 1L;
        Employee employee = Employee.builder()
                .firstName("john")
                .lastName("doe")
                .email("johndoe@gmail.com")
                .build();

        Employee updatedEmployee = Employee.builder()
                .firstName("steve")
                .lastName("jobs")
                .email("stevejobs@gmail.com")
                .build();

        employeeRepository.save(employee);

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEmployee)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenReturnResponseMessage() throws Exception {
        Employee employee = Employee.builder()
                .firstName("steve")
                .lastName("jobs")
                .email("stevejobs@gmail.com")
                .build();

        employeeRepository.save(employee);

        mockMvc.perform(delete("/api/employees/{id}", employee.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }


}