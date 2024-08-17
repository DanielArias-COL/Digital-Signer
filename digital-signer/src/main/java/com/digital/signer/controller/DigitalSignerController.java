package com.digital.signer.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/digitalSigner")
public class DigitalSignerController {

    @GetMapping("/test")
    public String test() {
        return "test";

    }

}
