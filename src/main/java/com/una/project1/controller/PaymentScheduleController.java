package com.una.project1.controller;

import com.una.project1.model.PaymentSchedule;
import com.una.project1.model.Role;
import com.una.project1.model.User;
import com.una.project1.service.PaymentScheduleService;
import com.una.project1.service.RoleService;
import com.una.project1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/paymentSchedule")
public class PaymentScheduleController {
    @Autowired
    PaymentScheduleService paymentScheduleService;
    @Autowired
    private UserService userService;
    @GetMapping("")
    public ResponseEntity<?> getRoleList(Authentication authentication) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("User does not exist");
        }
        return ResponseEntity.ok().body(paymentScheduleService.findAll());
    }

}
