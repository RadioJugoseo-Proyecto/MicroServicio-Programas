package cl.radiojugoseo.programas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProgramasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProgramasApplication.class, args);
    }
}
