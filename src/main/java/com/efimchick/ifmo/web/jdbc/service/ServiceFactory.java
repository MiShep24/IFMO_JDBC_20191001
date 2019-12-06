package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class ServiceFactory {

    public EmployeeService employeeService(){
        //throw new UnsupportedOperationException();
        return new EmployeeService() {
            private Employee getEmployee(ResultSet resultSet, boolean chain, boolean firstManager){
                try {
                    Employee manager = null;
                    BigInteger managerID = BigInteger.valueOf(resultSet.getInt("MANAGER"));
                    if(managerID != null && firstManager) {
                        if (!chain) {
                            firstManager = false;
                        }
                        ResultSet newResultSet = ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery("SELECT * FROM EMPLOYEE");
                        while (newResultSet.next()) {
                            if(BigInteger.valueOf(newResultSet.getInt("ID")).equals(managerID)) {
                                manager = getEmployee(newResultSet, chain, firstManager);
                            }
                        }
                    }
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
                            manager,
                            getDepartment(resultSet.getString("DEPARTMENT")));
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new Employee(null, null, null, null, null, null, null);
                }
            }

            private Department getDepartment(String ID) {
                try {
                    ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery("SELECT * FROM DEPARTMENT");
                    if (ID == null) {
                        return null;
                    }
                    while (resultSet.next()) {
                        if (ID.equals(resultSet.getString("ID"))) {
                            return new Department(
                                    new BigInteger(resultSet.getString("ID")),
                                    resultSet.getString("NAME"),
                                    resultSet.getString("LOCATION")
                            );
                        }
                    }
                    return null;
                } catch (SQLException e){
                    e.printStackTrace();
                    return new Department(null, null, null);
                }
            }

            private List<Employee> employeeList(Paging paging, String SQLString){
                try {
                    ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(SQLString);
                    List<Employee> result = new LinkedList<>();
                    int item = (paging.page - 1) * paging.itemPerPage;
                    resultSet.absolute(item);
                    while (resultSet.next() && item < (paging.page) * paging.itemPerPage) {
                        result.add(getEmployee(resultSet, false, true));
                        item++;
                    }
                    return result;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                return employeeList(paging, "select * from EMPLOYEE order by HIREDATE");
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                return employeeList(paging, "select * from EMPLOYEE order by LASTNAME");
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                return employeeList(paging, "select * from EMPLOYEE order by SALARY");
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                return employeeList(paging, "select * from EMPLOYEE order by DEPARTMENT, LASTNAME");
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                return employeeList(paging, "select * from EMPLOYEE where DEPARTMENT = " + department.getId() + " order by HIREDATE");
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                return employeeList(paging, "select * from EMPLOYEE where DEPARTMENT = " + department.getId() + " order by SALARY");
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                return employeeList(paging, "select * from EMPLOYEE where DEPARTMENT = " + department.getId() + " order by LASTNAME");
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                return employeeList(paging, "select * from EMPLOYEE where MANAGER = " + manager.getId() + " order by LASTNAME");
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                return employeeList(paging, "select * from EMPLOYEE where MANAGER = " + manager.getId() + " order by HIREDATE");
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                return employeeList(paging, "select * from EMPLOYEE where MANAGER = " + manager.getId() + " order by SALARY");
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                try {
                    ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery("SELECT * FROM EMPLOYEE");
                    while (resultSet.next()) {
                        if (resultSet.getString("ID").equals(String.valueOf(employee.getId()))){
                            return getEmployee(resultSet, true, true);
                        }
                    }
                    return null;
                } catch (SQLException exception){
                    return null;
                }
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                try {
                    ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery("SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + department.getId() + " ORDER BY SALARY DESC");
                    List<Employee> employees = new LinkedList<>();
                    while (resultSet.next()){
                        employees.add(getEmployee(resultSet, false, true));
                    }
                    return employees.get(salaryRank - 1);
                } catch (SQLException exception) {
                    return null;
                }
            }
        };
    }
}
