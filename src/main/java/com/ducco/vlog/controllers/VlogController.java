package com.ducco.vlog.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VlogController {
    @GetMapping("/")
    public String greet(){
        return "Hello World!";
    }


}
