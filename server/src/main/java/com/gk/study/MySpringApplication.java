package com.gk.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Administrator
 * @email 123456@qq.com
 */
@SpringBootApplication
public class MySpringApplication {

    public static void main(String[] args) {
        System.out.println("项目准备启动");
        SpringApplication.run(MySpringApplication.class, args);
        System.out.println("项目启动成功");
    }

}
