package com.notes.notesplatform.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PageController {
    @GetMapping("/index")
    public String home() {
        return "index";
    }

    @GetMapping("/bin")
    public String binPage() {
        return "bin";
    }

    

    @GetMapping("/notesDemo")
    public String notedemopage(){
        return "notedemo";
    }

}