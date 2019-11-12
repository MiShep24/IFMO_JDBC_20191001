package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {

    private ResultSet getResultSet (String sql) {
        try {
            Connection connection = ConnectionSource.instance().createConnection();
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

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
            return null;
        }
    }


    private List<Employee> getEmployeeList() {
        try {
            List<Employee> employeeList = new LinkedList<>();
            ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE");
            while (resultSet.next()){
                employeeList.add(getEmployee(resultSet));
            }
            return employeeList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Employee> employees = getEmployeeList();

    private Department getDepartment(ResultSet resultSet) {
        try {
            return new Department(
                    new BigInteger(resultSet.getString("ID")),
                    resultSet.getString("NAME"),
                    resultSet.getString("LOCATION")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Department> getDepartmentList() {
        try {
            List<Department> departmentList = new LinkedList<>();
            ResultSet resultSet = getResultSet("SELECT * FROM DEPARTMENT");
            while (resultSet.next()) {
                departmentList.add(getDepartment(resultSet));
            }
            return departmentList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Department> departments = getDepartmentList();

    public EmployeeDao employeeDAO() {
        //throw new UnsupportedOperationException();
        return new EmployeeDao() {

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    Optional<Employee> employee = Optional.empty();
                    for (Employee emp : employees) {
                        if (emp.getId().equals(Id)) {
                            employee = Optional.of(emp);
                            break;
                        }
                    }
                    return employee;

                } catch (Exception e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll() {
                return employees;
            }

            @Override
            public Employee save(Employee employee) {
                try {
                    for (int i = 0; i < employees.size(); i++){
                        if(employees.get(i).getId().equals(employee.getId())){
                            employees.remove(i);
                        }
                    }
                    employees.add(employee);
                    return employee;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void delete(Employee employee) {
                employees.remove(employee);
            }

            @Override
            public List<Employee> getByDepartment(Department department) {
                try {
                    List<Employee> resultList = new LinkedList<>();
                    for (Employee emp : employees) {
                        if(emp.getDepartmentId().equals(department.getId())){
                            resultList.add(emp);
                        }
                    }
                    return resultList;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                try {
                    List<Employee> resultList = new LinkedList<>();
                    for (Employee value : employees) {
                        if(value.getManagerId().equals(employee.getId())){
                            resultList.add(value);
                        }
                    }
                    return resultList;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    public DepartmentDao departmentDAO() {
        //throw new UnsupportedOperationException();
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    Optional<Department> resultList = Optional.empty();
                    for (Department department : departments) {
                        if(department.getId().equals(Id)) {
                            resultList = Optional.of(department);
                        }
                    }
                    return resultList;
                } catch (Exception e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            }

            @Override
            public List<Department> getAll() {
                return departments;
            }

            @Override
            public Department save(Department department) {
                for (int i = 0; i < departments.size(); i++){
                    if(departments.get(i).getId().equals(department.getId())) {
                        departments.remove(i);
                    }
                }
                departments.add(department);
                return department;
            }

            @Override
            public void delete(Department department) {
                departments.remove(department);
            }
        };
    }
}
