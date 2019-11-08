package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.LocalDate;

public class RowMapperFactory {
    public RowMapper<Employee> employeeRowMapper() {
        return resultSet -> {
                try {
                        BigInteger ID = new BigInteger(String.valueOf(resultSet.getInt("Id"));
                        FullName fName = new FullName(
                                    resultSet.getString("Firstname"),
                                    resultSet.getString("Middlename"),
                                    resultSet.getString("Lastname")
                            );
                        Position pos = Position.valueOf(resultSet.getString("Position"));
                        LocalDate lDate = LocalDate.parse(resultSet.getString("Hiredate"));
                        BigDecimal salary = new BigDecimal(resultSet.getBigDecimal("Salary"));
                        Employee emp = new Employee(ID, fName, pos, lDate, salary);
                        return emp;
                    );
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
        }
    };
}
