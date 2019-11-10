package com.efimchick.ifmo.web.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

public class SetMapperFactory {


    public SetMapper<Set<Employee>> employeesSetMapper() {
        //throw new UnsupportedOperationException();
        return resultSet -> {
            Set<Employee> employeeSet = new HashSet<>();
            try {
                while (resultSet.next()) {
                    employeeSet.add(getEmployee(resultSet));
                }
                return employeeSet;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    private Employee getEmployee(ResultSet resultSet) throws SQLException {
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
                    getManager(resultSet)
            );

    }

    private Employee getManager(ResultSet resultSet) {
        try {
            Employee manager = null;
            int managerID = resultSet.getInt("MANAGER");
            int current = resultSet.getRow();
            resultSet.beforeFirst();
            while (resultSet.next())
                if (resultSet.getInt("ID") == managerID)
                    manager = getEmployee(resultSet);
            resultSet.absolute(current);
            return manager;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
