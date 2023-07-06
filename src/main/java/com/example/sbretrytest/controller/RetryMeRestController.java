package com.example.sbretrytest.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class RetryMeRestController {

    @Autowired
    private RestTemplate restTemplate;

    private int attempts=1;

    @GetMapping("/retryMe")
    @Retryable(maxAttempts = 4, backoff = @Backoff(delay = 2000, multiplier = 2))
    public ResponseEntity<String> getRetryme() {
        log.info("retryMe service call attempted:::"+ attempts++);

        String response = restTemplate.getForObject("http://localhost:8081/api/v1/item", String.class);

        log.info("item service called");
        return new ResponseEntity<String>(response, HttpStatus.OK);

    }

    @Recover
    public ResponseEntity<String> fallback_retry(Exception e){
        attempts=1;
        return new ResponseEntity<String>("Item service is down", HttpStatus.INTERNAL_SERVER_ERROR);

    }


}
