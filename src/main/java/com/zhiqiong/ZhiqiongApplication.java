package com.zhiqiong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZhiqiongApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhiqiongApplication.class, args);
        //获取当前项目的启动端口
        System.out.println("项目启动成功......");
    }

}
