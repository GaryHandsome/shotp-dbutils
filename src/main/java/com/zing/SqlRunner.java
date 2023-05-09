package com.zing;

import com.zing.annotation.Column;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL执行器 - 执行 增、删、改、查的这么一个工具类
 * <p>
 * 更新操作：insert、delete、update
 * 查询操作：select
 *
 * @Date 2023-03-29
 * @Author zqx
 */
public class SqlRunner {


    private Connection connection;

    public SqlRunner(Connection connection) {
        this.connection = connection;
    }

    /**
     * 通用更新操作：执行 增、删、改的 SQL 语句
     * <p>
     * insert into staffs(name,age,phone,sta_pos)
     * values (?,?,?,?) ;
     *
     * @param sql
     * @param params 不定长参数，本质上是一个数组
     * @return
     */
    public int executeUpdate(String sql, Object... params) {
        // 第一：获取连接对象
        if (connection == null) {
            throw new RuntimeException("连接对象为null");
        }

        // 第二：预编译SQL语句
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);

            // 第三：填充参数
            setParameter(ps, params);

            // 第四：执行SQL语句
            // 第五：返回结果
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // 第六：关闭相关的对象
            close(ps);
            close(connection);
        }
    }

    /**
     * 填充 SQL 语句中的占位符数据
     *
     * @param ps
     * @param params
     * @throws SQLException
     */
    private static void setParameter(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }


    /**
     * 返回实体对象的属性名称
     *
     * @param clazz       实体对象的 Class 对象
     * @param columnLabel select 后面的名称
     * @param <T>
     * @return
     */
    private <T> String getFieldName(Class<T> clazz, String columnLabel) {
        // 1.获取实体对象所有的字段对象
        Field[] fields = clazz.getDeclaredFields();

        // 2.循环遍历字段数组
        for (Field field : fields) {
            // 3.判断字段是否存在 Column 注解
            boolean isExist = field.isAnnotationPresent(Column.class);

            // 4.如果存在，则获取注解的内容
            if (isExist) {
                String columnName = field.getAnnotation(Column.class).value();

                // 5.判断注解的内容是否与 select 后面的字段名称一样
                if (columnName.equals(columnLabel)) {
                    // 6.如果一样，则返回字段名称 - staPos
                    return field.getName();
                }
            }
        }
        // 7.如果不一样，则返回 select 后面的字段名称
        return columnLabel;
    }


    /**
     * 通用查询操作 - 返回的List集合
     *
     * @param clazz  实体对象的Class对象
     * @param sql    要执行查询语句
     * @param params 查询语句点位符数据
     * @param <T>    T为具体的实体类型对象
     * @return 返回的List集合
     */
    public <T> List<T> executeQuery(Class<T> clazz, String sql, Object... params) {
        List<T> list = new ArrayList<>();

        // 第一：获取连接对象
        if (connection == null) {
            throw new RuntimeException("连接对象为null");
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 第二：预编译SQL语句
            ps = connection.prepareStatement(sql);

            // 第三：填充参数
            setParameter(ps, params);

            // 第四：执行SQL语句 - ResultSet
            rs = ps.executeQuery();

            // 第五：获取结果集元数据对象
            ResultSetMetaData metaData = rs.getMetaData();

            // 第六：获取查询字段的数量
            int count = metaData.getColumnCount();

            // 第七：对结果集进行处理 - 遍历结果集，读取结果集中的数据，封装到List集合
            while (rs.next()) {
                // 1.实例化实体对象 - 思考：实体对象是谁呢？ - 在这里，谁都可以，我们要做一个通用的查询 - 通过 clazz 这个参数来确定要操作的具体实体对象的Class对象
                T entity = clazz.getConstructor().newInstance();
                // 2.读取结果集各列的数据 - 思考：有几列数据？是不确定的 - 解决？ - ResultSetMetaData
                // Object xxx = rs.getObject("yyy") ;
                for (int i = 1; i <= count; i++) {
                    // 2.1）获取查询字段名称 - 必须和实体对象的属性名称保持一致 - sta_pos、add_time
                    String name = getFieldName(clazz, metaData.getColumnLabel(i));

                    // 2.2）根据名称获取实体对象的字段对象
                    Field declaredField = clazz.getDeclaredField(name);

                    // 2.3）设置字段访问权限
                    declaredField.setAccessible(true);

                    // 2.4）获取结果集中的数据
                    Object obj = rs.getObject(i);

                    // 2.5）封装数据到实体对象中 - 思考：获取数据后，给对象的哪个属性初始化呢？ - 反射
                    declaredField.set(entity, obj);
                }

                // 3.把实体对象添加到 List 集合中
                list.add(entity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 第八：关闭对象
            close(rs);
            close(ps);
            close(connection);
        }


        return list;
    }


    /**
     * 查询数据，返回数据表的一行数据
     *
     * @param clazz
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public <T> T query4Entity(Class<T> clazz, String sql, Object... params) {
        T entity = null;

        // 第一：获取连接对象
        if (connection == null) {
            throw new RuntimeException("连接对象为null");
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 第二：预编译SQL语句
            ps = connection.prepareStatement(sql);

            // 第三：填充参数
            setParameter(ps, params);

            // 第四：执行SQL语句 - ResultSet
            rs = ps.executeQuery();

            // 第五：获取结果集元数据对象
            ResultSetMetaData metaData = rs.getMetaData();

            // 第六：获取查询字段的数量
            int count = metaData.getColumnCount();

            // 第七：对结果集进行处理 - 遍历结果集，读取结果集中的数据，封装到List集合
            if (rs.next()) {
                // 1.实例化实体对象 - 思考：实体对象是谁呢？ - 在这里，谁都可以，我们要做一个通用的查询 - 通过 clazz 这个参数来确定要操作的具体实体对象的Class对象
                entity = clazz.getConstructor().newInstance();
                // 2.读取结果集各列的数据 - 思考：有几列数据？是不确定的 - 解决？ - ResultSetMetaData
                // Object xxx = rs.getObject("yyy") ;
                for (int i = 1; i <= count; i++) {
                    // 2.1）获取查询字段名称 - 必须和实体对象的属性名称保持一致 - sta_pos、add_time
                    String name = getFieldName(clazz, metaData.getColumnLabel(i));

                    // 2.2）根据名称获取实体对象的字段对象
                    Field declaredField = clazz.getDeclaredField(name);

                    // 2.3）设置字段访问权限
                    declaredField.setAccessible(true);

                    // 2.4）获取结果集中的数据
                    Object obj = rs.getObject(i);

                    // 2.5）封装数据到实体对象中 - 思考：获取数据后，给对象的哪个属性初始化呢？ - 反射
                    declaredField.set(entity, obj);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 第八：关闭对象
            close(rs);
            close(ps);
            close(connection);
        }


        return entity;
    }


    /**
     * 查询单行单列的数据 - 记录数
     *
     * select count(*) from product
     *
     * @param sql
     * @param params
     * @return
     */
    public Long query4Count(String sql, Object... params) {
        // 第一：获取连接对象
        if (connection == null) {
            throw new RuntimeException("连接对象为null");
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        long count = 0;

        try {
            // 第二：预编译SQL语句
            ps = connection.prepareStatement(sql);

            // 第三：填充参数
            setParameter(ps, params);

            // 第四：执行SQL语句 - ResultSet
            rs = ps.executeQuery();

            // 第七：对结果集进行处理 - 遍历结果集，读取结果集中的数据，封装到List集合
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 第八：关闭对象
            close(rs);
            close(ps);
            close(connection);
        }

        return count;
    }

    /**
     * 关闭结果集对象
     *
     * @param rs
     */
    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 关闭语句对象
     *
     * @param stmt
     */
    private void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 关闭结果集对象
     *
     * @param conn
     */
    private void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
