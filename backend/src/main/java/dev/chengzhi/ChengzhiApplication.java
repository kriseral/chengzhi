package dev.chengzhi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"dev.chengzhi.auth", "dev.chengzhi.acl", "dev.chengzhi.document", "dev.chengzhi.search"})
public class ChengzhiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChengzhiApplication.class, args);
    }
}
