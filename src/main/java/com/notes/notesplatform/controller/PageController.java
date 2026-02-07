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

     @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Deletes the cookie
        response.addCookie(jwtCookie);
        SecurityContextHolder.clearContext();
        return "redirect:/index";
    }

    @GetMapping("/notesDemo")
    public String notedemopage(){
        return "notedemo";
    }

}