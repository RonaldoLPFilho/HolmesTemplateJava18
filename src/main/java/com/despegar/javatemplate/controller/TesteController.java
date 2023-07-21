package com.despegar.javatemplate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesteController {

    @GetMapping("/olaMundo")
    public String teste() {
        return "Olá, mundo!";
    }
}
