package org.example.nectus.user;

import org.example.nectus.common.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public ResponseEntity<User> getCurrentUser(){
        User user = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(user);
    }
}
