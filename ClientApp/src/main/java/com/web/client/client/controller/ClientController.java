package com.web.client.client.controller;

import com.web.client.client.service.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ServerService serverService;

    @GetMapping("/client")
    public String getResourceFromServer(){
        LOGGER.info("Client called");
        return serverService.getResource();
    }
}
