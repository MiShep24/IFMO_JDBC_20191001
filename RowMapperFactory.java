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
        RowMapper<Employee> rowMap = new RowMapper<Employee>() {
            @Override
            public Employee rowMap(ResultSet resultSet) {
                try {
                        BigInteger ID = new BigInteger(String.valueOf(resultSet.getInt("ID"));
                        FullName fName = new FullName(
                                    resultSet.getString("FIRSTNAME"),
                                    resultSet.getString("MIDDLENAME"),
                                    resultSet.getString("LASTNAME")
                            );
                        Position pos = Position.valueOf(resultSet.getString("POSITION"));
                        LocalDate lDate = LocalDate.parse(resultSet.getString("HIREDATE"));
                        BigDecimal salary = new BigDecimal(resultSet.getBigDecimal("SALARY"));
                        return Employee(ID, fName, pos, lDate, salary);
                    );
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        return rowMap;
    }
}
