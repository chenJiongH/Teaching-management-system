package per.cjh.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("per.cjh.example.mappers")
public class ExampleApplication {

	public static void main(String[] args) {

		SpringApplication.run(ExampleApplication.class, args);
	}

}
