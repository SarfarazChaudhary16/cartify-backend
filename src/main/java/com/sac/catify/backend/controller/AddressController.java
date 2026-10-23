package com.sac.catify.backend.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.sac.catify.backend.dto.AddressRequest;
import com.sac.catify.backend.dto.AddressResponse;
import com.sac.catify.backend.security.CustomUserDetails;
import com.sac.catify.backend.service.AddressService;

import java.util.List;
//eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYW0yMDAzLnNhY0BnbWFpbC5jb20iLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3ODQ5NzcxMzgsImV4cCI6MTc4NTA2MzUzOH0.JlM7ftEexN6LVQDUnS4_W5YqVTALbIIUG-IIUERyr0s
@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(addressService.getAddresses(currentUser.getId()));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.addAddress(currentUser.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(currentUser.getId(), addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long addressId) {
        addressService.deleteAddress(currentUser.getId(), addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.setDefaultAddress(currentUser.getId(), addressId));
    }
}
