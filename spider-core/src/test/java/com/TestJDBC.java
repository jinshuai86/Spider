package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author: JS
 * @date: 2018/4/22
 * @description:
 * conn.close() 调用关闭以后
 */
public class TestJDBC {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("");
        connection.close();
        try (InputStream inputStream = new FileInputStream(new File(""))) {
            connection.close();
        } catch (Exception e) {

        } finally {
            // ...
        }
    }
}
