package com.efimchick.ifmo.web.jdbc.service;

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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ServiceFactory {

    public ResultSet getResultSet(String sql) {
        try {
            Connection connection = ConnectionSource.instance().createConnection();
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee getEmployee(ResultSet resultSet, boolean chain, boolean firstManager) {
        try {
            Employee manager = null;
            BigInteger managerID = BigInteger.valueOf(resultSet.getInt("MANAGER"));
            if(managerID != null && firstManager) {
                if (!chain) {
                    firstManager = false;
                }
                ResultSet newResultSet = getResultSet("SELECT * FROM EMPLOYEE");
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
                    getDepartmentFromEmployee(resultSet)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Department getDepartmentById(BigInteger Id) {
        try {
            Department resultDep = null;
            for (Department department : departments) {
                if (department.getId().equals(Id)) {
                    resultDep = department;
                }
            }
            return resultDep;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Department getDepartmentFromEmployee(ResultSet resultSet) throws SQLException {
        BigInteger departmentID = BigInteger.valueOf(resultSet.getInt("DEPARTMENT"));
        Department department = getDepartmentById(departmentID);
        return department;
    }

    private List<Employee> getEmployeeList(boolean chain){
        try {
            List<Employee> employeeList = new LinkedList<>();
            ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE");
            while (resultSet.next()){
                employeeList.add(getEmployee(resultSet, chain, true));
            }
            return employeeList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Employee> trueEmployees = getEmployeeList(true);
    private List<Employee> falseEmployees = getEmployeeList(false);

    private List<Employee> getEmployeeByDepartment(Department department) {
        try {
            List<Employee> resultList = new LinkedList<>();
            for (Employee employee : falseEmployees) {
                if (employee.getDepartment() != null && employee.getDepartment().equals(department)) {
                    resultList.add(employee);
                }
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Employee> getEmployeeByManager(Employee manager) {
        try {
            List<Employee> resultList = new LinkedList<>();
            for (Employee employee : falseEmployees) {
                if (employee.getManager() != null && employee.getManager().getId().equals(manager.getId())) {
                    resultList.add(employee);
                }
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Department getDepartment(ResultSet resultSet) {
        try {
            return new Department(
                    new BigInteger(String.valueOf(resultSet.getInt("ID"))),
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

    private List<Employee> getPage(Paging paging, List<Employee> list) {
        try {
            List<Employee> resultList = new LinkedList<>();
            int from = paging.itemPerPage*(paging.page - 1);
            int to = Math.min(paging.itemPerPage*paging.page, list.size());
            while(from < to) {
                resultList.add(list.get(from));
                from++;
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public EmployeeService employeeService(){
        //throw new UnsupportedOperationException();

        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                try {
                    List<Employee> resultList = new LinkedList<>(falseEmployees);
                    resultList.sort(Comparator.comparing(Employee::getHired));
                    return getPage(paging, resultList);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                try {
                    List<Employee> resultList = new LinkedList<>(falseEmployees);
                    resultList.sort(Comparator.comparing(Employee -> Employee.getFullName().getLastName()));
                    return getPage(paging, resultList);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                try {
                    List<Employee> resultList = new LinkedList<>(falseEmployees);
                    resultList.sort(Comparator.comparing(Employee::getSalary));
                    return resultList;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                try {
                    List<Employee> resultList = new LinkedList<>(falseEmployees);
                    resultList.sort((p1, p2) -> {
                        if(p1.getDepartment() == null) return -1;
                        else if(p2.getDepartment() == null) return 1;
                        else if (p1.getDepartment().getName().compareTo(p2.getDepartment().getName()) == 0) return p1.getFullName().getLastName().compareTo(p2.getFullName().getLastName());
                        else return p1.getDepartment().getName().compareTo(p2.getDepartment().getName());
                    });
                    return getPage(paging, resultList);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                try {
                    List<Employee> resultList = getEmployeeByDepartment(department);
                    resultList.sort(Comparator.comparing(Employee::getHired));
                    return getPage(paging, resultList);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                try {
                    List<Employee> resultList = getEmployeeByDepartment(department);
                    resultList.sort(Comparator.comparing(Employee::getSalary));
                    return getPage(paging, resultList);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                try {
                    List<Employee> resultList = getEmployeeByDepartment(department);
                    resultList.sort(Comparator.comparing(Employee -> Employee.getFullName().getLastName()));
                    return getPage(paging, resultList);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                try {
                    List<Employee> resultList = getEmployeeByManager(manager);
                    resultList.sort(Comparator.comparing(Employee -> Employee.getFullName().getLastName()));
                    return getPage(paging, resultList);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                try {
                    List<Employee> resultList = getEmployeeByManager(manager);
                    resultList.sort(Comparator.comparing(Employee::getHired));
                    return getPage(paging, resultList);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                try {
                    List<Employee> resultList = getEmployeeByManager(manager);
                    resultList.sort(Comparator.comparing(Employee::getSalary));
                    return getPage(paging, resultList);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                try {
                    Employee employees = null;
                    for (Employee employee1 : trueEmployees) {
                        if(employee1.getId().equals(employee.getId())) {
                            employees = employee1;
                            break;
                        }
                    }
                    return employees;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                try {
                    List<Employee> resultList = getEmployeeByDepartment(department);
                    resultList.sort(Comparator.comparing(Employee::getSalary).reversed());
                    return resultList.get(salaryRank - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }
}
