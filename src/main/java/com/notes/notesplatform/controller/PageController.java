package com.notes.notesplatform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

}