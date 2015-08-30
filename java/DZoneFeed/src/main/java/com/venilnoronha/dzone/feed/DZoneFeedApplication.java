package com.venilnoronha.dzone.feed;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DZoneFeedApplication {

    public static void main(String[] args) {
    	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(DZoneFeedApplication.class, args);
    }
    
}
