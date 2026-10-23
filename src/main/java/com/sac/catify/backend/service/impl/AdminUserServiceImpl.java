package com.sac.catify.backend.service.impl;
import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.AdminUserResponse;
import com.sac.catify.backend.entity.Order;
import com.sac.catify.backend.entity.Role;
import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.OrderRepository;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.service.AdminUserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    // OrderRepository chahiye kyunki har user ke "ordersCount" aur
    // "totalSpent" nikalne ke liye humein us user ke SAARE orders dekhne
    // padenge
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public AdminUserServiceImpl(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<AdminUserResponse> getAllUsers() {
        // Saare users nikalte hain, phir FILTER karte hain — sirf
        // Role.CUSTOMER wale rakhte hain (Admin accounts ko is list mein
        // dikhane ka koi matlab nahi, ye "customer management" page hai)
        List<User> customers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CUSTOMER)
                .collect(Collectors.toList());

        return customers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminUserResponse toggleBlock(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));

        // "!" (NOT operator) se current value ULTA kar dete hain — agar
        // abhi false (unblocked) hai to true ho jayega, aur vice-versa.
        // Ye bilkul wahi "toggle" pattern hai jo Coupon ke "isActive"
        // toggle mein bhi use kiya tha (B9 mein)
        user.setBlocked(!user.isBlocked());

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    // ===================================================================
    // PRIVATE HELPER
    // ===================================================================

    private AdminUserResponse mapToResponse(User user) {

        // ===== ORDERS COUNT + TOTAL SPENT — real-time calculation =====
        // findByUserIdOrderByPlacedAtDesc() Phase B2 mein bana tha —
        // isi user ke saare orders nikal lete hain
        List<Order> userOrders = orderRepository.findByUserIdOrderByPlacedAtDesc(user.getId());

        long ordersCount = userOrders.size();

        // Saare orders ke "total" ko jodते hain (jaisa Dashboard-analytics
        // mein bhi kiya tha — .stream().map().reduce() pattern)
        BigDecimal totalSpent = userOrders.stream()
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt(),
                ordersCount,
                totalSpent,
                user.isBlocked()
        );
    }
}
