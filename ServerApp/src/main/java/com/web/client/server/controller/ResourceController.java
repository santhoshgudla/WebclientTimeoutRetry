package com.web.client.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    @GetMapping("/resource")
    public ResponseEntity<String> getResource(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted Exception: ", e);
        }
        return new ResponseEntity<>("Resource", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
