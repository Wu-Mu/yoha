package com.yoha.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
@MapperScan("com.yoha.server.mapper")

/**
 * This is the main entrance of the application.
 *
 *
 * @param args the system call input
 * @return void
 * @author Chenhao Li
 * @date 19-4-20 上午9:14
 **/
public class ServerApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(ServerApplication.class);
  }

}
