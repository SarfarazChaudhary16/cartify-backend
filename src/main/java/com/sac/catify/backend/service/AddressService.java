package com.sac.catify.backend.service;

import java.util.List;

import com.sac.catify.backend.dto.AddressRequest;
import com.sac.catify.backend.dto.AddressResponse;

// Address CRUD ka contract — Account > Addresses page, aur Checkout ke
// address-selection step, dono isi service ko use karenge
public interface AddressService {

    List<AddressResponse> getAddresses(Long userId);

    // Naya address add karte waqt agar isDefault=true diya, to purane
    // saare addresses ka isDefault=false ho jana chahiye (sirf ek hi
    // default ho sakta hai) — ye logic Impl mein hoga
    AddressResponse addAddress(Long userId, AddressRequest request);

    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

    void deleteAddress(Long userId, Long addressId);

    // "Set as default" button ke liye — is address ko default banata hai,
    // baaki sabko non-default
    AddressResponse setDefaultAddress(Long userId, Long addressId);
}
