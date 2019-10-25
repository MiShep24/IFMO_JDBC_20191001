package com.efimchick.ifmo.web.jdbc;

public class SqlQueries {
    //Select all employees sorted by last name in ascending order
    //language=HSQLDB
    public String select01 = "select * from EMPLOYEE order by lastname ASC";

    //Select employees having no more than 5 characters in last name sorted by last name in ascending order
    //language=HSQLDB
    public String select02 = "select * from EMPLOYEE where LENGTH(lastname) <=5 order by lastname ASC";

    //Select employees having salary no less than 2000 and no more than 3000
    //language=HSQLDB
    public String select03 = "select * from EMPLOYEE where salary between 2000 and 3000";

    //Select employees having salary no more than 2000 or no less than 3000
    //language=HSQLDB
    public String select04 = "select * from EMPLOYEE where salary <= 2000 or salary >= 3000";

    //Select employees assigned to a department and corresponding department name
    //language=HSQLDB
    public String select05 = "select * from EMPLOYEE inner join department on EMPLOYEE.department = department.id";

    //Select all employees and corresponding department name if there is one.
    //Name column containing name of the department "depname".
    //language=HSQLDB
    public String select06 = "select EMPLOYEE.id, EMPLOYEE.firstname, EMPLOYEE.department, EMPLOYEE.lastname, EMPLOYEE.salary, EMPLOYEE.middlename, department.id, department.name as \"depname\"" +
            "from EMPLOYEE full join department on EMPLOYEE.department = department.id where EMPLOYEE.middlename is not null";

    //Select total salary pf all employees. Name it "total".
    //language=HSQLDB
    public String select07 = "select SUM(salary) AS \"total\" from EMPLOYEE";

    //Select all departments and amount of employees assigned per department
    //Name column containing name of the department "depname".
    //Name column containing employee amount "staff_size".
    //language=HSQLDB
    public String select08 = "select department.name AS \"depname\", COUNT(EMPLOYEE.id) AS \"staff_size\" "+
            "from department inner join EMPLOYEE ON EMPLOYEE.department = department.id "+
            "group by department.name";

    //Select all departments and values of total and average salary per department
    //Name column containing name of the department "depname".
    //language=HSQLDB
    public String select09 = "select department.name AS \"depname\", SUM(EMPLOYEE.salary) AS \"total\", AVG(EMPLOYEE.salary) AS \"average\" "+
            "from department inner join EMPLOYEE on EMPLOYEE.department = department.id "+
            "group by department.name";

    //Select all employees and their managers if there is one.
    //Name column containing employee lastname "employee".
    //Name column containing manager lastname "manager".
    //language=HSQLDB
    public String select10 = "select employee1.lastname AS \"employee\", employee2.lastname AS \"manager\" "+
            "from EMPLOYEE employee1 left join EMPLOYEE employee2 on employee1.manager = employee2.id";


}