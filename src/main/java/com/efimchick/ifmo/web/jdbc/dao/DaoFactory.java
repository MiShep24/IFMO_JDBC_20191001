package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {
    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            private Employee getEmployee(ResultSet resultSet) {
                try {
                    return new Employee(
                            new BigInteger(String.valueOf(resultSet.getInt("ID"))),
                            new FullName(
                                    resultSet.getString("FIRSTNAME"),
                                    resultSet.getString("LASTNAME"),
                                    resultSet.getString("MIDDLENAME")
                            ),
                            Position.valueOf(resultSet.getString("POSITION")),
                            LocalDate.parse(resultSet.getString("HIREDATE")),
                            new BigDecimal(String.valueOf(resultSet.getBigDecimal("SALARY"))),
                            new BigInteger(String.valueOf(resultSet.getInt("MANAGER"))),
                            new BigInteger(String.valueOf(resultSet.getInt("DEPARTMENT")))
                    );
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new Employee(null, null, null, null, null, null, null);
                }
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                try {
                    ResultSet resultSet = (ConnectionSource.instance().createConnection().createStatement()).executeQuery(
                    "select * from EMPLOYEE where MANAGER = " + employee.getId()
                    );
                    List<Employee> employees = new LinkedList<>();
                    while (resultSet.next()) {
                        employees.add(getEmployee(resultSet));
                    }
                    return employees;
                } catch (SQLException exception) {
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartment(Department department){
                try {
                    ResultSet resultSet = (ConnectionSource.instance().createConnection().createStatement()).executeQuery(
                            "select * from EMPLOYEE where DEPARTMENT = " + department.getId()
                    );
                    List<Employee> employees = new LinkedList<>();
                    while (resultSet.next()) {
                        employees.add(getEmployee(resultSet));
                    }
                    return employees;
                } catch (SQLException exception) {
                    return null;
                }
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = (ConnectionSource.instance().createConnection().createStatement()).executeQuery(
                    "select * from EMPLOYEE where ID = " + Id
                    );
                    if (resultSet.next()) {
                        return Optional.of(getEmployee(resultSet));
                    }
                    return Optional.empty();
                } catch (SQLException exception){
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll(){
                try {
                    ResultSet resultSet = (ConnectionSource.instance().createConnection().createStatement()).executeQuery(
                    "select * from EMPLOYEE"
                    );
                    List<Employee> employees = new LinkedList<>();
                    while (resultSet.next()) {
                        employees.add(getEmployee(resultSet));
                    }
                    return employees;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public Employee save(Employee employee){
                try {
                    (ConnectionSource.instance().createConnection().createStatement()).execute(
                    "insert into EMPLOYEE values ('" + employee.getId() + "', '" + employee.getFullName().getFirstName()  + "', '" +
                        employee.getFullName().getLastName()   + "', '" + employee.getFullName().getMiddleName() + "', '" +
                        employee.getPosition() + "', '" + employee.getManagerId() + "', '" + Date.valueOf(employee.getHired()) + "', '" +
                        employee.getSalary() + "', '" + employee.getDepartmentId() + "')"
                    );
                    return employee;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public void delete(Employee employee){
                try {
                    (ConnectionSource.instance().createConnection().createStatement()).execute(
                    "delete from EMPLOYEE where ID = " + employee.getId()
                    );
                } catch (SQLException ignored){}
            }
        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            private Department getDepartment(ResultSet resultSet) {
                try {
                    return new Department(
                            new BigInteger(resultSet.getString("ID")),
                            resultSet.getString("NAME"),
                            resultSet.getString("LOCATION")
                    );
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new Department(null, null, null);
                }
            }

            @Override
            public Optional<Department> getById(BigInteger Id){
                try {
                    ResultSet resultSet = (ConnectionSource.instance().createConnection().createStatement()).executeQuery(
                    "select * from DEPARTMENT where ID = " + Id
                    );
                    if (resultSet.next()) {
                        return Optional.of(getDepartment(resultSet));
                    } else {
                        return Optional.empty();
                    }
                } catch (SQLException exception){
                    return Optional.empty();
                }
            }

            @Override
            public List<Department> getAll(){
                try {
                    ResultSet resultSet = (ConnectionSource.instance().createConnection().createStatement()).executeQuery(
                    "select * from DEPARTMENT"
                    );
                    List<Department> departments = new LinkedList<>();
                    while (resultSet.next()) {
                        departments.add(getDepartment(resultSet));
                    }
                    return departments;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public Department save(Department department){
                try {
                    if (getById(department.getId()).equals(Optional.empty())) {
                        (ConnectionSource.instance().createConnection().createStatement()).execute(
                        "insert into DEPARTMENT values ('" + department.getId() + "', '" + department.getName() + "', '" + department.getLocation() + "')"
                        );
                    } else {
                        (ConnectionSource.instance().createConnection().createStatement()).execute(
                        "update DEPARTMENT set " + "NAME = '" + department.getName() + "', " + "LOCATION = '" + department.getLocation() + "' " + "where ID = '" + department.getId() + "'"
                        );
                    }
                    return department;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public void delete(Department department){
                try{
                    (ConnectionSource.instance().createConnection().createStatement()).execute(
                    "delete from DEPARTMENT where ID = " + department.getId()
                    );
                } catch (SQLException ignored){}
            }
        };
    }
}
