package com.zing;

import com.zing.annotation.Column;

import java.util.Date;

/**
 * 员工
 *
 * @Date 2023-03-29
 * @Author zqx
 */
public class Staff {
    /**
     * 编号
     */
    private int id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 年龄
     */
    private int age;
    /**
     * 电话
     */
    private String phone;
    /**
     * 职位
     */
    @Column("sta_pos")
    private String staPos;
    /**
     * 注册时间
     */
    @Column("add_time")
    private Date addTime;

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", staPos='" + staPos + '\'' +
                ", addTime=" + addTime +
                '}';
    }
}
