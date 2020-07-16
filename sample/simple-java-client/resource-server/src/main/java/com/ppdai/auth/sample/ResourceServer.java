package com.ppdai.auth.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Please add description here.
 *
 */
@SpringBootApplication
@RestController
public class ResourceServer {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServer.class, args);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> greet() {
        return new ResponseEntity<>("hello, I am protected by pauth filter.", HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        return new ResponseEntity<>("hello, this is login api.", HttpStatus.OK);
    }
}
