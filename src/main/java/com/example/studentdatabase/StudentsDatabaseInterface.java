package com.example.studentdatabase;

public interface StudentsDatabaseInterface {
    String SCHEMA = "Students";

    String ddlCreateTableSchedule = "CREATE TABLE Schedule(" +
            "courseId CHAR(12) NOT NULL UNIQUE, " +
            "sectionNumber VARCHAR(8) NOT NULL UNIQUE, " +
            "title VARCHAR(64), " +
            "year INT, " +
            "semester CHAR(6), " +
            "instructor VARCHAR(24), " +
            "department CHAR(16), " +
            "program VARCHAR(48), " +
            "PRIMARY KEY (courseId, sectionNumber))";

    String ddlCreateTableStudents = "CREATE TABLE Students(" +
            "empId INT PRIMARY KEY, " +
            "firstName VARCHAR(32) NOT NULL, " +
            "lastName VARCHAR(32) NOT NULL, " +
            "email VARCHAR(64) UNIQUE, " +
            "gender CHAR CHECK (gender IN ('F', 'M', 'U')))";

    String ddlCreateTableCourses = "CREATE TABLE Courses(" +
            "courseId CHAR(12) PRIMARY KEY, " +
            "title VARCHAR(64), " +
            "department CHAR(16))";

    String ddlCreateTableClasses = "CREATE TABLE Classes(" +
            "courseId CHAR(12) REFERENCES Courses(courseId), " +
            "studentId INT REFERENCES Students(empId), " +
            "sectionNo VARCHAR(8) REFERENCES Schedule(sectionNumber), " +
            "year INT, " +
            "semester CHAR(6), " +
            "grade CHAR CHECK(grade IN ('A', 'B', 'C', 'D', 'F', 'W')), " +
            "PRIMARY KEY (courseId, studentId, sectionNo))";

    String ddlCreateTableAggregateGrades = "CREATE TABLE AggregateGrades(grade CHAR, numberStudents INT)";

    String ddlInsertTableStudents = "INSERT INTO Students VALUES " +
            "(9991001, 'John', 'Doe', 'johndoe@cuny.edu','M'), " +
            "(9991002, 'Sarah', 'Johnson', 'sarahjohnson@cuny.edu', 'F'), " +
            "(9991003, 'David', 'Lee', 'davidlee@cuny.edu', 'M'), " +
            "(9991004, 'Jessica', 'Smith', 'jessicasmith@cuny.edu', 'F'), " +
            "(9991005, 'Christopher', 'Wang', 'christopherwang@cuny.edu', 'M'), " +
            "(9991006, 'Sophia', 'Kim', 'sophiakim@cuny.edu', 'F'), " +
            "(9991007, 'Matthew', 'Nguyen', 'matthewnguyen@cuny.edu', 'M'), " +
            "(9991008, 'Olivia', 'Garcia', 'oliviagarcia@cuny.edu', 'F'), " +
            "(9991009, 'Ethan', 'Martinez', 'ethanmartinez@cuny.edu', 'M')";

    String ddlInsertTableClasses = "INSERT INTO Classes VALUES " +
            "('22100 F', 9991001, '32131', 2021, 'Spring', 'A'), " +
            "('22100 P', 9991002, '32132', 2021, 'Spring', 'B'), " +
            "('22100 R', 9991003, '32150', 2021, 'Spring', 'C'), " +
            "('22100 F', 9991004, '32131', 2021, 'Spring', 'D'), " +
            "('22100 P', 9991005, '32132', 2021, 'Spring', 'F'), " +
            "('22100 R', 9991006, '32150', 2021, 'Spring', 'W'), " +
            "('22100 F', 9991007, '32131', 2021, 'Spring', 'A'), " +
            "('22100 P', 9991008, '32132', 2021, 'Spring', 'B'), " +
            "('22100 R', 9991009, '32150', 2021, 'Spring', 'C')";

    String sqlAggregateGrades = "SELECT grade, COUNT(grade) FROM Classes GROUP BY grade";

    static String ddlUpDateCourseInstructor(String courseId, String sectionNumber, String nameInstructor) {
        return "UPDATE Schedule" +
                " SET instructor = " + nameInstructor +
                " WHERE courseId = " + courseId + " AND sectionNumber = " + sectionNumber;
    }

    static String ddlUpDateInstructor(String nameInstructor, String nameNewInstructor) {
        return "UPDATE Schedule " +
                " SET instructor = " + nameInstructor +
                " WHERE instructor = " + nameNewInstructor;
    }

    static String ddlInsertTableCourses(String nameToTable, String nameFromTable) {
        return "INSERT INTO " + nameToTable +
                " SELECT courseId, title, department" +
                " FROM " + nameFromTable;
    }

    static String ddlInsertTableAggregateGrades(String nameToTable, String nameFromTable) {
        return "INSERT INTO " + nameToTable +
                " SELECT grade, COUNT(grade) FROM " + nameFromTable +
                " GROUP BY grade ORDER BY grade";
    }

}
