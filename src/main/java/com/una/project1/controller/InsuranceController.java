package com.una.project1.controller;

import com.una.project1.form.InsuranceCreateHelper;
import com.una.project1.model.*;
import com.una.project1.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {
    @Autowired
    private InsuranceService insuranceService;
    @Autowired
    private PaymentScheduleService paymentScheduleService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CoverageService coverageService;
    @GetMapping("")
    public ResponseEntity<?> getInsuranceList(Authentication authentication,
                                              @RequestParam(value = "search", required = false) String search) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("User does not exist");
        }
        List<Insurance> insurances = insuranceService.findByUser(user.get());
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok().body(insurances.stream()
                    .filter(insurance -> insurance.getNumberPlate().contains(search))
                    .collect(Collectors.toList()));
        }
        return ResponseEntity.ok().body(insurances);
    }
    @PreAuthorize("isSelfOrAdmin(#username)")
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getInsuranceListByUsername(Authentication authentication,
            @PathVariable("username") String username,
            @RequestParam(value = "search", required = false) String search) {
        Optional<User> user = userService.findByUsername(username);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("User does not exist");
        }
        List<Insurance> insurances = insuranceService.findByUser(user.get());
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok().body(insurances.stream()
                    .filter(insurance -> insurance.getNumberPlate().contains(search))
                    .collect(Collectors.toList()));
        }
        return ResponseEntity.ok().body(insurances);
    }
    @PreAuthorize("hasAuthority('StandardClient')")
    @PostMapping("")
    public ResponseEntity<?> createInsurance(@RequestBody InsuranceCreateHelper insuranceHelper,
                                     BindingResult result,
                                     Authentication authentication) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("User does not exist");
        }
        Optional<Payment> payment = paymentService.findById(Long.valueOf(insuranceHelper.getPayment()));
        Optional<PaymentSchedule> paymentSchedule = paymentScheduleService.findById((long) insuranceHelper.getPaymentSchedule());
        Optional<Vehicle> vehicle = vehicleService.findById((long) insuranceHelper.getVehicle());
        Set<Coverage> coverageSet = new HashSet<>();
        for (int coverageId : insuranceHelper.getCoverages()){
            Optional<Coverage> coverage = coverageService.findById((long) coverageId);
            if (coverage.isPresent()){
                coverageSet.add(coverage.get());
            }
        }
        if (!payment.isPresent()){
            result.rejectValue("payment", "error.Payment", "Payment value is not valid.");
        }
        if (!paymentSchedule.isPresent()){
            result.rejectValue("paymentSchedule", "error.PaymentSchedule", "PaymentSchedule value is not valid.");
        }
        if (!vehicle.isPresent()){
            result.rejectValue("vehicle", "error.Vehicle", "Vehicle value is not valid.");
        }
        Insurance insurance = new Insurance(
                insuranceHelper.getNumberPlate(),
                insuranceHelper.getCarYear(),
                insuranceHelper.getValuation(),
                payment.get(),
                paymentSchedule.get(),
                vehicle.get(),
                coverageSet
        );
        insuranceService.validateCreation(insurance, result, "create");
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        insuranceService.starDate(insurance);
        insuranceService.assignUser(insurance, user.get());
        insuranceService.createInsurance(insurance);
        return ResponseEntity.ok().body(insurance);
    }

    @PreAuthorize("hasAuthority('StandardClient')")
    @GetMapping("/{numberPlate}")
    public ResponseEntity<?> getInsuranceByNumberPlate(@PathVariable("numberPlate") String numberPlate,
                                               Authentication authentication) {
        Optional<Insurance> optionalInsurance = insuranceService.findByNumberPlate(numberPlate);
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (!optionalInsurance.isPresent() || !user.isPresent()) {
            return ResponseEntity.badRequest().body("Insurance or User does not exist");
        }
        Insurance insurance = optionalInsurance.get();
        if (!authentication.getName().equals(insurance.getClient().getUsername())) {
            return ResponseEntity.badRequest().body("Access Denied");
        }
        return ResponseEntity.ok().body(insurance);
    }
    @PreAuthorize("hasAuthority('StandardClient')")
    @PutMapping("/{numberPlate}")
    public ResponseEntity<?> updateInsurance(@PathVariable("numberPlate") String numberPlate,
                                     @Valid @RequestBody Insurance updatedInsurance,
                                     Authentication authentication) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        Optional<Insurance> existingInsurance = insuranceService.findByNumberPlate(numberPlate);
        if (!user.isPresent() || !existingInsurance.isPresent()) {
            return ResponseEntity.badRequest().body("Insurance or User does not exist");
        }
        Insurance insurance = existingInsurance.get();
        if (!authentication.getName().equals(insurance.getClient().getUsername())) {
            return ResponseEntity.badRequest().body("Access Denied");
        }
        insuranceService.updateInsurance(insurance, updatedInsurance);
        return ResponseEntity.ok().body(insurance);
    }

    @PreAuthorize("hasAuthority('StandardClient')")
    @DeleteMapping("/{numberPlate}")
    public ResponseEntity<?> deleteInsurance(@PathVariable("numberPlate") String numberPlate, Authentication authentication) {
        Optional<Insurance> optionalInsurance = insuranceService.findByNumberPlate(numberPlate);
        if (!optionalInsurance.isPresent()) {
            return ResponseEntity.badRequest().body("Insurance not found");
        }
        Insurance insurance = optionalInsurance.get();
        if (!authentication.getName().equals(insurance.getClient().getUsername())) {
            return ResponseEntity.badRequest().body("Access Denied");
        }
        insuranceService.deleteInsurance(insurance);
        return ResponseEntity.ok().body("Insurance successfully deleted");
    }
}
