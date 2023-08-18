package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.core.security.SecureString;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;


import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

@BotCommand


@CommandPkg(
        name = "BulkInsert",
        label = "BulkInsert Command",
        description = "BulkInsert Command description",
        node_label = "BulkInsert Command Node Label",
        return_label = "BulkInsert Command return Label",
        return_type = DataType.NUMBER, return_required = true
)
public class BulkInsertSQL {

    private static final Logger logger = Logger.getLogger(BulkInsertSQL.class.getName());

    @Execute
    public NumberValue action(
            @Idx(index = "1", type = AttributeType.FILE)
            @LocalFile
            @Pkg(label = "Excel File Path")
            @NotEmpty
            final String inputFilePath,
            @Idx(index = "2", type = AttributeType.TEXT)
            @LocalFile
            @Pkg(label = "Enter the dbUrl (a database url of the form jdbc:subprotocol:subname)" +
                    "eg :jdbc:server:hostname\\instancename;databaseName: ")
            @NotEmpty
            final String dbUrl,
            @Idx(index = "3", type = AttributeType.CREDENTIAL)
            @LocalFile
            @Pkg(label = "Enter the username for the DB")
            @NotEmpty
            final SecureString username,
            @Idx(index = "4", type = AttributeType.CREDENTIAL)
            @LocalFile
            @Pkg(label = "Enter the password for the DB")
            @NotEmpty
            final SecureString password,
            @Idx(index = "5", type = AttributeType.TEXT)
            @LocalFile
            @Pkg(label = "Enter the query to insert")
            @NotEmpty
            final String sql,
            @Idx(index = "6", type = AttributeType.TEXT)
            @LocalFile
            @Pkg(label = "Enter the filePath for logs")
            @NotEmpty
            final String logFilePath,
            @Idx(index = "7", type = AttributeType.TEXT)
            @LocalFile
            @Pkg(label = "Enter the table name")
            @NotEmpty
            final String tableName
    ) {
        configureLogger(logFilePath);

        int totalNumberOfRecords= 0;

        try (Connection connection = DriverManager.getConnection(dbUrl, username.getInsecureString(), password.getInsecureString())) {
            connection.setAutoCommit(false);

            if (inputFilePath.toLowerCase().endsWith(".xls")) {

                totalNumberOfRecords= bulkInsertXLS(connection, inputFilePath,sql,tableName);

            } else if (inputFilePath.toLowerCase().endsWith(".xlsx")) {

                totalNumberOfRecords= bulkInsertXLSX(connection, inputFilePath,sql,tableName);

            } else if (inputFilePath.toLowerCase().endsWith(".txt") || inputFilePath.toLowerCase().endsWith(".csv")) {

                totalNumberOfRecords=bulkInsertTextOrCSV(connection, inputFilePath,sql);

            } else {

                System.err.println("Unsupported file format");

            }

            connection.commit();
            connection.close();


        } catch (Exception e) {

            e.printStackTrace();
            logger.log(Level.SEVERE,"Error " +" "+e.getMessage());

        }
        return new NumberValue(totalNumberOfRecords);
    }


    private static int bulkInsertXLS(Connection connection, String filePath, String insertQuery,String tableName) throws Exception {

        FileInputStream fis = new FileInputStream(filePath);
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        for (int rowIndex = 1; rowIndex<=sheet.getLastRowNum();rowIndex++) {
            try{
                Row row = sheet.getRow(rowIndex);
                processRow(row, preparedStatement,connection,tableName,sheet);
            }catch (SQLException e){
                logger.log(Level.SEVERE, "Error in row"+(rowIndex+1)+": "+e.getMessage());
            }


        }

        int[] batchResult;
        try {
            batchResult = preparedStatement.executeBatch();
        } catch (BatchUpdateException e) {
            batchResult = e.getUpdateCounts();
        }

        preparedStatement.clearBatch();
        fis.close();
        preparedStatement.close();
        return batchResult.length;

    }


    private static int bulkInsertXLSX(Connection connection, String filePath, String insertQuery,String tableName) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        for (int rowIndex = 1; rowIndex<=sheet.getLastRowNum();rowIndex++) {
            try{
                Row row = sheet.getRow(rowIndex);
                processRow(row, preparedStatement,connection,tableName,sheet);

            }catch (SQLException e){
                logger.log(Level.SEVERE, "Error in row"+(rowIndex+1)+": "+e.getMessage());
            }


        }

//        preparedStatement.executeBatch();
        int[] batchResult;
        try {
            batchResult = preparedStatement.executeBatch();
        } catch (BatchUpdateException e) {
            batchResult = e.getUpdateCounts();
        }

        preparedStatement.clearBatch();
        fis.close();
        preparedStatement.close();
        return batchResult.length;
    }


    private static int bulkInsertTextOrCSV(Connection connection, String filePath, String insertQuery) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        while ((line = reader.readLine()) != null) {
            processLine(line, preparedStatement);
        }
        int[] batchResult;
        try {
            batchResult = preparedStatement.executeBatch();
        } catch (BatchUpdateException e) {
            batchResult = e.getUpdateCounts();
        }

        preparedStatement.clearBatch();
        reader.close();
        preparedStatement.close();
        return batchResult.length;

    }


    private static void processRow(Row row, PreparedStatement preparedStatement, Connection connection, String tableName, Sheet sheet) throws Exception {
        Map<String, Integer> columnMaxLengths = getColumnMaxLength(connection, tableName);

// Set values for each column in the prepared statement
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            String columnName = getColumnHeader(i, sheet);
            String columnValue = cell != null ? cell.toString() : null;

            int maxColumnLength = columnMaxLengths.getOrDefault(columnName, Integer.MAX_VALUE);

            if (columnValue != null && columnValue.length() > maxColumnLength) {
                logger.log(Level.SEVERE, "Error in row "+row.getRowNum());
//                System.err.println("Data too long for column " + columnName + ": " + columnValue);

                return; // Skip this row
            }

            preparedStatement.setString(i + 1, columnValue);
        }
        preparedStatement.addBatch();
    }


    private static void processLine(String line, PreparedStatement preparedStatement) throws Exception {

        String[] data = line.split(","); 

        for (int i = 0; i < data.length; i++) {
            preparedStatement.setString(i + 1, data[i]);
        }

        preparedStatement.addBatch();
    }
    public static Map<String, Integer> getColumnMaxLength(Connection connection,String tableName)throws SQLException {
        Map<String, Integer> columnMaxLengths = new HashMap<>();
        try(PreparedStatement statement=connection.prepareStatement("SELECT name,max_length FROM sys.columns WHERE object_id = OBJECT_ID(?)")){
            statement.setString(1,tableName);

            try(ResultSet resultSet= statement.executeQuery()){
                while(resultSet.next()){
                    String columnName = resultSet.getString("name");
                    int maxLength = resultSet.getInt("max_length");
                    columnMaxLengths.put(columnName,maxLength);
                }
            }
        }
        return columnMaxLengths;
    }

    public static String getColumnHeader(int columnIndex, Sheet sheet){
        Row headerRow = sheet.getRow(0);
        if(headerRow != null){
            Cell cell = headerRow.getCell(columnIndex);
            if (cell!= null){
                return cell.getStringCellValue();
            }
        }
        return "UnknownColum"+(columnIndex+1);
    }
    public static String getColumnHeader(int columnIndex){
        return "Column"+(columnIndex+1);
    }

    private static void configureLogger(String logFilePath){
        try {
            Path logDirectory = Paths.get(logFilePath).getParent();
            Files.createDirectories(logDirectory);

            LogManager.getLogManager().reset();
            logger.setLevel(Level.ALL);
            FileHandler fileHandler = new FileHandler(logFilePath);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        }catch(IOException e){
            logger.log(Level.SEVERE,"Failed to configure logger",e);
        }
    }


}



