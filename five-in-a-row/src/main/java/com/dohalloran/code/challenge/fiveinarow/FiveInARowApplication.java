package com.dohalloran.code.challenge.fiveinarow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FiveInARowApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(FiveInARowApplication.class, args);
        } catch (Exception ex){
            LOG.error("error - shutdown", ex);
        }
    }

}
