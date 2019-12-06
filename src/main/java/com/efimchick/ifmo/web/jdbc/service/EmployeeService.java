package com.efimchick.ifmo.web.jdbc.service;

import java.util.List;

import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;

public interface EmployeeService {

    List<Employee> getAllSortByHireDate(Paging paging);

    List<Employee> getAllSortByLastName(Paging paging);

    List<Employee> getAllSortBySalary(Paging paging);

    List<Employee> getAllSortByDepartmentNameAndLastName(Paging paging);

    List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging);

    List<Employee> getByDepartmentSortBySalary(Department department, Paging paging);

    List<Employee> getByDepartmentSortByLastName(Department department, Paging paging);

    List<Employee> getByManagerSortByLastName(Employee manager, Paging paging);

    List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging);

    List<Employee> getByManagerSortBySalary(Employee manager, Paging paging);

    Employee getWithDepartmentAndFullManagerChain(Employee employee);

    Employee getTopNthBySalaryByDepartment(int salaryRank, Department department);
}
