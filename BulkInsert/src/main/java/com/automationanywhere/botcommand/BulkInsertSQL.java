package com.automationanywhere.botcommand;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.rules.LocalFile;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.logging.*;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import static com.automationanywhere.commandsdk.model.DataType.STRING;



@BotCommand
@CommandPkg(
        name = "BulkInsertCommand",
        label = "BulkInsert Command",
        description = "BulkInsert Command description",
        node_label = "BulkInsert Command Node Label",
        return_type = STRING,
        return_label = "BulkInsert Command return Label"
        )
public class BulkInsertSQL {

    private static final Logger logger = Logger.getLogger(BulkInsertSQL.class.getName());
    @Execute
    public StringValue action(
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
            @Idx(index = "3", type = AttributeType.TEXT)
            @LocalFile
            @Pkg(label = "Enter the username for the DB")
            @NotEmpty
            final String username,
            @Idx(index = "4", type = AttributeType.TEXT)
            @LocalFile
            @Pkg(label = "Enter the password for the DB")
            @NotEmpty
            final String password,
            @Idx(index = "5", type = AttributeType.TEXT)
            @LocalFile
            @Pkg(label = "Enter the query to insert")
            @NotEmpty
            final String sql,
            @Idx(index = "6", type = AttributeType.TEXT)
            @LocalFile
            @Pkg(label = "Enter the filePath for logs")
            @NotEmpty
            final String logFilePath
    ) {
        configureLogger(logFilePath);
        try {
            FileInputStream file = new FileInputStream(new File(inputFilePath));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            int totalNumberOfRecords = 0;
            try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
                connection.setAutoCommit(false);
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    int numRows = sheet.getLastRowNum() + 1;
                    for (int rowIndex = 1; rowIndex < numRows; rowIndex++) {
                        Row row = sheet.getRow(rowIndex);
                        int numCells = row.getLastCellNum();
                        try {
                            for (int cellIndex = 0; cellIndex < numCells; cellIndex++) {
                                Cell cell = row.getCell(cellIndex);
                                String cellValue = getCellValueAsString(cell);
                                statement.setString(cellIndex + 1, cellValue);
                            }
                            statement.addBatch();
                            totalNumberOfRecords++;
                        }
                        catch (SQLException e){
                            logger.log(Level.SEVERE,"Error processing row: "+row.getRowNum(),e);
                        }

                    }
//                    totalNumberOfRecords++;
                    statement.executeBatch();
                }
                logger.info("Total number of records inserted: "+totalNumberOfRecords);
                connection.commit();
                return new StringValue("Records inserted Successfully");
            }catch (SQLException e) {
                logger.log(Level.SEVERE,"Error connecting to database: " ,e);
            }

            workbook.close();
            file.close();
        }catch (IOException e) {
            logger.log(Level.SEVERE,"Error reading excel file: " ,e);
        }
        return new StringValue("Records were not inserted. Check the error");
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell==null){
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return null;
        }
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
