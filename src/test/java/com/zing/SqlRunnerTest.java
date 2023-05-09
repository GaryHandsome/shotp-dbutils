package com.zing;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @Date 2023-04-25
 * @Author zqx
 */
public class SqlRunnerTest {
    /**
     * 测试是否连接成功
     */
    @Test
    public void testConnection() {
        System.out.println(DbUtil.getConnection());
    }


    /**
     * 测试 executeUpdate
     */
    @Test
    public void testInsert() {
        // 实例化 SqlRunner 对象
        SqlRunner sqlRunner = new SqlRunner(DbUtil.getConnection());

        // 定义要操作的SQL语句
        String sql = "insert into staffs(name,age,phone,sta_pos) values (?,?,?,?)";

        int row = sqlRunner.executeUpdate(sql, "张无忌", 180, "13417474131", "架构师");

        Assert.assertEquals(1,row);
    }

    /**
     * 测试 executeQuery
     */
    @Test
    public void testQuery() {
        // 实例化 SqlRunner 对象
        SqlRunner sqlRunner = new SqlRunner(DbUtil.getConnection());

        // 定义要操作的SQL语句
        String sql = "select id,name,age,phone,sta_pos,add_time as addTime from staffs";

        List<Staff> list = sqlRunner.executeQuery(Staff.class, sql);

        for (Staff staff : list) {
            System.out.println(staff);
        }
    }

    @Test
    public void testQueryCount() {
        // 实例化 SqlRunner 对象
        SqlRunner sqlRunner = new SqlRunner(DbUtil.getConnection());

        // 定义要操作的SQL语句
        String sql = "select count(*) from staffs";

        Long count = sqlRunner.query4Count(sql);

        System.out.println(count);


    }
}
