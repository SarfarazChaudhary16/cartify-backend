package com.sac.catify.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sac.catify.backend.entity.Address;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // Account > Addresses page load hote hi: is user ke saare saved
    // addresses (Home, Office, waghera) — Checkout ke address-selection
    // step mein bhi yehi method reuse hoga
    List<Address> findByUserId(Long userId);
}
