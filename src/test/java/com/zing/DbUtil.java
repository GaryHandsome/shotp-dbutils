package com.zing;

import java.sql.*;

/**
 * 连接数据库 & 释放相关对象
 *
 *
 *
 * @Date 2023-03-29
 * @Author zqx
 */
public class DbUtil {
    /**
     * 连接驱动程序
     */
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    /**
     * 连接URL
     */
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/jdbc_test?useUnicode=true;characterEncoding=utf8;serverTimezone=Asia/Shanghai";
    /**
     * 帐号
     */
    private static final String USER = "root";
    /**
     * 密码
     */
    private static final String PASS = "root";


    static {
        /*
         * 加载驱动程序
         */
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("加载驱动程序失败...");
            e.printStackTrace();
        }
    }

    /**
     * 获取连接对象 -- Java程序 与 数据库之间的桥梁
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("获取连接对象失败...");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭相关的 JDBC 对象
     *
     * DriverManager：驱动管理对象，获取连接对象
     *
     * DriverManager.getConnection(URL, USER, PASS);
     *
     * ResultSet：结果集对象 用于接收查询数据时，返回的结果
     *
     * Statement：语句对象 用于执行SQL语句（PreparedStatement、CallableStatement）
     * 增、删、改：executeUpdate() 查询：executeQuery()
     *
     *
     * Connection：连接对象 建立JAVA程序与数据库之间的桥梁
     *
     * @param rst
     * @param stmt 父类对象可以接收子类对象 - 多态
     * @param conn
     */
    public static void close(ResultSet rst, Statement stmt, Connection conn) {
        if (rst != null) {
            try {
                rst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // executeUpdate()、executeQuery() - 略
    public static void main(String[] args) {
        System.out.println(DbUtil.getConnection());
    }

}
