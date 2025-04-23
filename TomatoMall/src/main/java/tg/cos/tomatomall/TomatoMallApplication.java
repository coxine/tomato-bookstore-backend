package tg.cos.tomatomall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class TomatoMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(TomatoMallApplication.class, args);
    }
    //try github actions
}
