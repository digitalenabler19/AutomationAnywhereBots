package com.automationanywhere.botcommand;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        BulkInsertSQL ins = new BulkInsertSQL();
        String filepath = "F:\\Downloaded\\Filepath\\filename.xlxs";
        String dbUrl="jdbc:sqlserver://EC2AMAZ\\SQLEXPRESS;databaseName=dbName";
        String username="username";
        String password="password";
        String sql = "INSERT INTO dbName.tableName(column1,column2) VALUES(?, ?)";

        System.out.println(ins.action(filepath,dbUrl,username,password,sql));

    }
}
