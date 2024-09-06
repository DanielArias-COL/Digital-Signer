package com.digital.signer.controller;

import com.digital.signer.dto.files.ShareFileRequestDTO;
import com.digital.signer.dto.files.SignedFileDTO;
import com.digital.signer.dto.files.VerifyFileRequestDTO;
import com.digital.signer.dto.user.CreateUserRequestDTO;
import com.digital.signer.dto.user.GoogleSingInRequestDTO;
import com.digital.signer.dto.user.SingInRequestDTO;
import com.digital.signer.service.DigitalSignerService;
import com.digital.signer.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/digitalSigner")
public class DigitalSignerController {

    @Autowired
    private DigitalSignerService digitalSignerService;


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

    @PostMapping("/user/googleSingIn")
    public ResponseEntity<Object> googleSingIn(@RequestBody() GoogleSingInRequestDTO request) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.googleSingIn(request));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".googleSingIn ", e.getMessage());
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

    @PostMapping("/user/saveFiles")
    public ResponseEntity<Object> saveFiles(HttpServletRequest request, @RequestBody MultipartFile[] files) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.saveFiles(request,files));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".saveFiles ", e.getMessage());
        }
    }

    @PostMapping("/user/signedFile")
    public ResponseEntity<Object> signedFile(HttpServletRequest request, @ModelAttribute SignedFileDTO signedFileDTO) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.signedFile(request, signedFileDTO));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".signedFile ", e.getMessage());
        }
    }

    @PostMapping("/user/verifyFile")
    public ResponseEntity<Object> verifyFile(HttpServletRequest request, @RequestBody VerifyFileRequestDTO verifyFileRequestDTO) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.verifyFile(request, verifyFileRequestDTO));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".verifyFile ", e.getMessage());
        }
    }

    @PostMapping("/user/shareFile")
    public ResponseEntity<Object> shareFile(HttpServletRequest request, @RequestBody ShareFileRequestDTO verifyFileRequestDTO) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.shareFile(request, verifyFileRequestDTO));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".shareFile ", e.getMessage());
        }
    }

    @GetMapping("/user/listShareUsers")
    public ResponseEntity<Object> listShareUsers(HttpServletRequest request) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.listShareUsers(request));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".listShareUsers ", e.getMessage());
        }
    }

    @GetMapping("/user/listMySharesFile")
    public ResponseEntity<Object> listMySharesFile(HttpServletRequest request) {
        try {
            return Util.getResponseSuccessful(this.digitalSignerService.listMySharesFile(request));
        } catch (Exception e) {
            return Util.getResponseError(DigitalSignerController.class.getSimpleName() + ".listMySharesFile ", e.getMessage());
        }
    }
}
