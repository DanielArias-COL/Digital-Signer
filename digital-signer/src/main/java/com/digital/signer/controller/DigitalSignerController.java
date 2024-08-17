package com.digital.signer.controller;

import com.digital.signer.dto.user.CreateUserRequestDTO;
import com.digital.signer.service.DigitalSignerService;
import com.digital.signer.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/digitalSigner")
public class DigitalSignerController {

    @Autowired
    private DigitalSignerService digitalSignerService;

    @GetMapping("/test")
    public String test() {
        return "test";

    }

    @PostMapping("/user/create")
    public ResponseEntity<Object> createUser(@RequestBody() CreateUserRequestDTO request) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.createUser(request));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".createUser ", e.getMessage());
        }
    }
}
