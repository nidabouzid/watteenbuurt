package com.nbodev.watteenbuurt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class WattEenBuurtApplication {

    public static void main(String[] args) {
        SpringApplication.run(WattEenBuurtApplication.class, args);
    }

}
