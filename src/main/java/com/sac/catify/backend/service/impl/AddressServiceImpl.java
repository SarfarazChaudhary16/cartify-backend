package com.sac.catify.backend.service.impl;


import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.AddressRequest;
import com.sac.catify.backend.dto.AddressResponse;
import com.sac.catify.backend.entity.Address;
import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.AddressRepository;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.service.AddressService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AddressResponse> getAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));

        Address address = new Address();
        address.setLabel(request.getLabel());
        address.setFullName(request.getFullName());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setPhone(request.getPhone());
        address.setUser(user);

        // Agar ye user ka PEHLA address hai, use automatically default
        // bana dete hain (frontend mein bhi shayad yehi expectation thi —
        // pehla address hamesha usable/default hona chahiye checkout ke liye)
        boolean isFirstAddress = addressRepository.findByUserId(userId).isEmpty();
        address.setDefault(isFirstAddress);

        Address saved = addressRepository.save(address);
        return mapToResponse(saved);
    }

    @Override
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = getOwnedAddress(userId, addressId);

        address.setLabel(request.getLabel());
        address.setFullName(request.getFullName());
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setPhone(request.getPhone());

        Address updated = addressRepository.save(address);
        return mapToResponse(updated);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = getOwnedAddress(userId, addressId);
        addressRepository.delete(address);
    }

    @Override
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        // Step 1: Confirm karte hain ye address isi user ka hai
        Address targetAddress = getOwnedAddress(userId, addressId);

        // Step 2: User ke SAARE addresses nikalte hain, taaki purane
        // default ko FALSE kar sakein (ek time pe sirf EK address hi
        // default ho sakta hai)
        List<Address> allAddresses = addressRepository.findByUserId(userId);

        for (Address addr : allAddresses) {
            // Har address ke liye check karte hain: "kya ye WAHI address hai
            // jise humein default banana hai?" — agar haan to true, warna false
            addr.setDefault(addr.getId().equals(addressId));
            addressRepository.save(addr);
        }

        // targetAddress ka field bhi memory mein update kar dete hain
        // (upar wale loop ne database mein already save kar diya hai,
        // ye sirf return-value ke liye consistent rakhne ke liye hai)
        targetAddress.setDefault(true);
        return mapToResponse(targetAddress);
    }

    // ===================================================================
    // PRIVATE HELPER: Address dhoondhna + ownership verify karna (security)
    // ===================================================================
    // Ye method 2 kaam ek saath karta hai: (1) address exist karta hai ya
    // nahi check karna, (2) ye address WAHI user ka hai jo request kar
    // raha hai — warna koi bhi kisi aur ka address edit/delete kar sakta
    // tha sirf ID guess karke (IDOR vulnerability, jaisa CartServiceImpl
    // mein bhi discuss kiya tha)
    private Address getOwnedAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> ApiException.notFound("Address not found with id: " + addressId));

        if (!address.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("This address does not belong to you.");
        }

        return address;
    }

    private AddressResponse mapToResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getLabel(),
                address.getFullName(),
                address.getLine1(),
                address.getLine2(),
                address.getCity(),
                address.getState(),
                address.getPincode(),
                address.getPhone(),
                address.isDefault()
        );
    }
}
