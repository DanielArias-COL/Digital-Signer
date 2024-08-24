package com.digital.signer.controller;

import com.digital.signer.dto.user.CreateUserRequestDTO;
import com.digital.signer.dto.user.SingInRequestDTO;
import com.digital.signer.service.DigitalSignerService;
import com.digital.signer.util.JwtUtil;
import com.digital.signer.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/digitalSigner")
public class DigitalSignerController {

    @Autowired
    private DigitalSignerService digitalSignerService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/test")
    public String test() {
        return "test";

    }

    @GetMapping("user/generateKeyPair")
    public ResponseEntity<Object> generateKeyPairForUser(HttpServletRequest request) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.generateKeyPairForUser(request));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".generateKeyPairForUser ", e.getMessage());
        }
    }

    @PostMapping("/user/create")
    public ResponseEntity<Object> createUser(@RequestBody() CreateUserRequestDTO request) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.createUser(request));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".createUser ", e.getMessage());
        }
    }

    @PostMapping("/user/singIn")
    public ResponseEntity<Object> singIn(@RequestBody() SingInRequestDTO request) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.singIn(request));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".singIn ", e.getMessage());
        }
    }

    @GetMapping("/user/listFiles")
    public ResponseEntity<Object> listFiles(HttpServletRequest request) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.listFiles(request));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".listFiles ", e.getMessage());
        }
    }
}
