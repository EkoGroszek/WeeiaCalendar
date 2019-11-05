package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar")
public class WeeiaCalendar {



    @GetMapping("/test/{testval}")
    public ResponseEntity<String> getTest(@PathVariable String testval) {
        return new ResponseEntity<>(testval, HttpStatus.OK);
    }

}

