package com.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


//@SpringBootApplication
//public class App
//{
//	public static void main( String[] args )
//	{
//		SpringApplication.run(App.class, args);
//	}
//}
@SpringBootApplication
@EnableAsync
@ComponentScan
public class App extends SpringBootServletInitializer {

	protected SpringApplicationBuilder context(SpringApplicationBuilder builder){
		return builder.sources(App.class);
	}


	public static void main(String[] args) throws Exception{
		SpringApplication.run(App.class,args);
	}
}