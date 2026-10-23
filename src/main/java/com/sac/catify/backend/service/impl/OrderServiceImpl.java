package com.sac.catify.backend.service.impl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.CheckoutRequest;
import com.sac.catify.backend.dto.OrderItemResponse;
import com.sac.catify.backend.dto.OrderResponse;
import com.sac.catify.backend.dto.TrackingStepResponse;
import com.sac.catify.backend.entity.Address;
import com.sac.catify.backend.entity.CartItem;
import com.sac.catify.backend.entity.Order;
import com.sac.catify.backend.entity.OrderAddress;
import com.sac.catify.backend.entity.OrderItem;
import com.sac.catify.backend.entity.OrderStatus;
import com.sac.catify.backend.entity.Product;
import com.sac.catify.backend.entity.TrackingStep;
import com.sac.catify.backend.entity.User;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.AddressRepository;
import com.sac.catify.backend.repository.CartItemRepository;
import com.sac.catify.backend.repository.OrderRepository;
import com.sac.catify.backend.repository.ProductRepository;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.service.CouponService;
import com.sac.catify.backend.service.NotificationService;
import com.sac.catify.backend.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	
	 // 5 repositories chahiye — har ek ka apna specific kaam:
    private final OrderRepository orderRepository;         // Order banana/dhoondhna
    private final CartItemRepository cartItemRepository;   // Cart se items uठाना, phir clear karna
    private final AddressRepository addressRepository;     // Delivery address dhoondhna
    private final ProductRepository productRepository;     // Stock kam karne ke liye
    private final UserRepository userRepository;           // Order ke saath User link karne ke liye
    private final CouponService couponService;

    private final NotificationService notificationService;
    // Order-number generate karne ke liye random numbers chahiye
    private final Random random = new Random();

    public OrderServiceImpl(OrderRepository orderRepository,
                             CartItemRepository cartItemRepository,
                             AddressRepository addressRepository,
                             ProductRepository productRepository,
                             UserRepository userRepository,
                             NotificationService notificationService,
                             CouponService couponService) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.couponService = couponService;
    }

//===================================================================================================================
 // ===================================================================
    // PLACE ORDER — sabse important method, Cart ko Order mein badalta hai
    // ===================================================================
	@Override
	public OrderResponse placeOrder(Long userId, CheckoutRequest request) {
		
		// ===== STEP 1: User ke current cart items uठाते hain =====
        // Dhyan do: hum CLIENT se cart-items nahi maang rahe (CheckoutRequest
        // mein sirf addressId + paymentMethod hai) — hum KHUD database se
        // "abhi is user ke cart mein kya hai" nikal rahe hain. Ye SECURITY
        // ke liye critical hai — agar hum client-supplied prices/quantities
        // pe bharosa karte, koi bhi request tamper karke sasta order kar sakta tha.
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw ApiException.badRequest("Your cart is empty. Add items before placing an order.");
        }
        
        // ===== STEP 2: Address dhoondhte hain, aur verify karte hain ki
        // ye address ISI user ka hai (security check, jaisa Address module
        // mein bhi tha) =====
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> ApiException.notFound("Address not found with id: " + request.getAddressId()));

        if (!address.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("This address does not belong to you.");
        }
        
        // ===== STEP 3: Stock check karte hain HAR item ke liye =====
        // Order confirm karne se PEHLE hi check kar lete hain ki sab
        // products ke paas ENOUGH stock hai — agar beech mein kisi ek
        // item ka stock kam nikla, poora order fail hona chahiye (koi
        // "aadha order" nahi banna chahiye)
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw ApiException.badRequest(
                        "Insufficient stock for \"" + product.getTitle() + "\". Only " +
                                product.getStock() + " left.");
            }
        }
        
        // ===== STEP 4: Total calculate karte hain =====
        // BigDecimal.ZERO se shuru karte hain, phir har item ka
        // (price * quantity) add karte jate hain. Normal "+" operator
        // BigDecimal pe kaam NAHI karta (ye ek class hai, primitive nahi),
        // isliye .add() method use karna padta hai.
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            BigDecimal lineTotal = item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(lineTotal);
        }
        
       // ===== STEP 4.5: Coupon apply karna (agar diya gaya ho) =====
        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedCouponCode = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            var couponResult = couponService.validateCoupon(request.getCouponCode(), total, userId);
            if (!couponResult.isValid()) {
                throw ApiException.badRequest(couponResult.getMessage());
            }
            discountAmount = couponResult.getDiscountAmount();
            appliedCouponCode = request.getCouponCode().toUpperCase();
            total = total.subtract(discountAmount);
            if (total.compareTo(BigDecimal.ZERO) < 0) {
                total = BigDecimal.ZERO;
            }
        }
        
        // ===== STEP 5: User entity chahiye Order ke saath link karne ke liye =====
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found with id: " + userId));
        
        // ===== STEP 6: Order ka "OrderAddress" SNAPSHOT banate hain =====
        // Phase B2 mein discuss kiya tha? Order, Address Entity
        // ko SEEDHA reference nahi karta — apni khud ki COPY rakhta hai,
        // taaki agar user baad mein apna saved address edit/delete kare,
        // purane order ka delivery-address CHANGE na ho.
        OrderAddress orderAddress = new OrderAddress(
                address.getFullName(), address.getLine1(), address.getLine2(),
                address.getCity(), address.getState(), address.getPincode(), address.getPhone()
        );
        
        // ===== STEP 7: Order banate hain (abhi PENDING status ke saath) =====
        String orderNumber = generateOrderNumber();
        Order order = new Order(orderNumber, user, orderAddress, request.getPaymentMethod(),
                OrderStatus.PENDING, total);
        
        order.setCouponCode(appliedCouponCode);
        order.setDiscountAmount(discountAmount);
        
        // ===== STEP 8: Cart items ko OrderItems mein convert karte hain =====
        // Yahan bhi SNAPSHOT pattern hai — OrderItem apna khud ka title/
        // image/price copy rakhta hai (Product ko seedha reference nahi
        // karta poori tarah), taaki order-history hamesha "jo order kiya
        // tha wahi dikhaye", chahe product baad mein badal jaye/delete ho jaye
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            
            String imageUrl = product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl();

            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    product.getTitle(),
                    imageUrl,
                    product.getPrice(),
                    cartItem.getQuantity(),
                    cartItem.getColor(),
                    cartItem.getSize(),
                    order
            );
            
            orderItems.add(orderItem);

            // ===== STEP 9: Product ka STOCK kam karte hain =====
            // Order place hote hi stock turant reserve/deduct kar dete hain
            // (real e-commerce mein ye standard practice hai — warna 2 log
            // ek saath last-item order kar sakte the aur dono ko "confirm"
            // mil jata, jabki stock sirf 1 tha)
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        // FIX: Ye line MISSING thi — humne orderItems list banayi thi,
        // lekin Order object ke saath kabhi LINK nahi ki. Isi wajah se
        // save() hote waqt items khaali reh jate the (cascade=ALL sirf
        // tab kaam karta hai jab "order.setItems(...)" se list Order
        // ke andar set ho — warna Order ko pata hi nahi chalta ki
        // uske "items" hain kya).
        order.setItems(orderItems);
        
        // ===== STEP 10: Initial Tracking Timeline banate hain =====
        // Frontend ke pattern jaisa hi — order place hote hi 4 steps
        // ban jate hain: PENDING (completed=true, kyunki abhi-abhi hua),
        // aur baaki 3 (PACKED/SHIPPED/DELIVERED) completed=false (future
        // mein admin ke "advance status" se true honge, one by one)
     // FIX: "OUT_FOR_DELIVERY" step yahan MISSING tha — isi wajah se
        // jab admin status advance karta tha, ye step galat jagah
        // (Delivered ke BAAD) add ho jata tha. Ab isse sahi sequence
        // mein (Shipped ke baad, Delivered se pehle) daal diya hai.
        List<TrackingStep> trackingSteps = new ArrayList<>();
        trackingSteps.add(new TrackingStep(OrderStatus.PENDING, "Order placed", LocalDateTime.now(), true));
        trackingSteps.add(new TrackingStep(OrderStatus.PACKED, "Packed", null, false));
        trackingSteps.add(new TrackingStep(OrderStatus.SHIPPED, "Shipped", null, false));
        trackingSteps.add(new TrackingStep(OrderStatus.OUT_FOR_DELIVERY, "Out for delivery", null, false));
        trackingSteps.add(new TrackingStep(OrderStatus.DELIVERED, "Delivered", null, false));
        order.setTrackingSteps(trackingSteps);
        
        // ===== STEP 11: Order save karte hain =====
        // save() ek hi call mein Order + uske saare OrderItems save kar
        // dega, kyunki Order Entity mein "items" field pe
        // cascade=CascadeType.ALL laga hai (Phase B2 mein banaya tha)
        Order savedOrder = orderRepository.save(order);
        
        // NAYA: Order place hote hi turant ek notification banate hain —
        // Notification Center page + header bell-icon pe dikhegi
        notificationService.createNotification(
                userId,
                com.sac.catify.backend.entity.NotificationCategory.ORDERS,
                "Order Placed",
                "Your order #" + savedOrder.getOrderNumber() + " has been placed successfully."
        );
        
        // NAYA: Coupon successfully use ho gaya — redemption record karte
        // hain, taaki ye USER ye COUPON dobara na use kar sake
        if (appliedCouponCode != null) {
            couponService.recordRedemption(appliedCouponCode, userId);
        }
        
        // ===== STEP 12: Cart clear karte hain =====
        // Order successfully ban gaya, ab purana cart khaali kar dete hain
        cartItemRepository.deleteByUserId(userId);

        return mapToResponse(savedOrder);
	}
//===================================================================================================================
	// ===================================================================
    // CUSTOMER: Apne saare orders dekhna
    // ===================================================================
	@Override
	public List<OrderResponse> getOrdersForUser(Long userId) {
		
		
		return orderRepository.findByUserIdOrderByPlacedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
	}
//===================================================================================================================
	// ===================================================================
    // CUSTOMER: Ek specific order dekhna (ownership-check ke saath)
    // ===================================================================
	@Override
	public OrderResponse getOrderForUser(Long userId, Long orderId) {
		
		Order order = getOwnedOrder(userId, orderId);
        return mapToResponse(order);
	}
//===================================================================================================================

	//CANCEL ORDER BY USER.
	@Override
	public OrderResponse cancelOrder(Long userId, Long orderId, String reason) {
		
		Order order = getOwnedOrder(userId, orderId);

        // Business rule: sirf PENDING ya PACKED order hi cancel ho sakta hai
        // (jo already SHIPPED ho chuka, use cancel karna sahi nahi — courier
        // raste mein hai, real-world mein ye allowed nahi hota)
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PACKED) {
            throw ApiException.badRequest("This order can no longer be cancelled.");
        }

        order.setStatus(OrderStatus.CANCELLED);

        // Naya tracking-step add karte hain jo cancellation ka reason bataye
        order.getTrackingSteps().add(
                new TrackingStep(OrderStatus.CANCELLED, "Cancelled — " + reason, LocalDateTime.now(), true));

        Order updated = orderRepository.save(order);
        
        // NAYA: Cancellation confirm hone par notification
        notificationService.createNotification(
                userId,
                com.sac.catify.backend.entity.NotificationCategory.ORDERS,
                "Order Cancelled",
                "Your order #" + updated.getOrderNumber() + " has been cancelled."
        );
        return mapToResponse(updated);
	}
//===================================================================================================================

	// ===================================================================
    // CUSTOMER: Return/Replace request karna
    // ===================================================================
	@Override
	public OrderResponse requestReturnOrReplace(Long userId, Long orderId, String type, String reason) {
		
		
		Order order = getOwnedOrder(userId, orderId);

        // Business rule: sirf DELIVERED order hi return/replace ho sakta hai
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw ApiException.badRequest("Only delivered orders can be returned or replaced.");
        }

        order.setStatus(OrderStatus.RETURNED);

        String label = ("replace".equalsIgnoreCase(type) ? "Replacement" : "Return") + " requested — " + reason;
        order.getTrackingSteps().add(
                new TrackingStep(OrderStatus.RETURNED, label, LocalDateTime.now(), true));

        Order updated = orderRepository.save(order);
        
        // NAYA: Return/Replace request confirm hone par notification
        notificationService.createNotification(
                userId,
                com.sac.catify.backend.entity.NotificationCategory.ORDERS,
                "replace".equalsIgnoreCase(type) ? "Replacement Requested" : "Return Requested",
                "Your request for order #" + updated.getOrderNumber() + " has been received."
        );

        return mapToResponse(updated);	
	}
//===================================================================================================================

	
	
	
	
	
	// ===================================================================
    // ADMIN: Saare orders dekhna (koi ownership-check nahi)
    // ===================================================================
	@Override
	public List<OrderResponse> getAllOrders() {
		
		
		return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
	}
//===================================================================================================================

	// ===================================================================
    // ADMIN: Koi bhi ek order dekhna
    // ===================================================================
	@Override
	public OrderResponse getOrderByIdForAdmin(Long orderId) {
		
		
		Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found with id: " + orderId));
		
        return mapToResponse(order);
	}
//===================================================================================================================

	 // ===================================================================
    // ADMIN: Status ko agle step pe le jana (PENDING->PACKED->SHIPPED->...)
    // ===================================================================
	@Override
	public OrderResponse advanceStatus(Long orderId) {
		
		
		Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found with id: " + orderId));

		// Ye "status flow map" hai — batata hai "current status ke baad
        // AGLA status kya hoga". Frontend ke nextOrderStatus map (lib/orderStatus.ts)
        // ka hi Java-equivalent hai.
        OrderStatus next = switch (order.getStatus()) {
            case PENDING -> OrderStatus.PACKED;
            case PACKED -> OrderStatus.SHIPPED;
            case SHIPPED -> OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> OrderStatus.DELIVERED;
            default -> null; // DELIVERED/CANCELLED/RETURNED/REFUNDED — ye "terminal" states hain, aage nahi badh sakte
        };
        
        if (next == null) {
            throw ApiException.badRequest("This order cannot be advanced further from its current status.");
        }

        order.setStatus(next);
        
        // Check karte hain — kya is naye status ka tracking-step PEHLE SE
        // list mein maujood hai (jaise PACKED/SHIPPED/DELIVERED — ye pehle
        // se placeOrder() mein bana diye the, completed=false ke saath)?
        // Agar hai, to use "completed=true" kar dete hain. Agar nahi hai
        // (jaise OUT_FOR_DELIVERY — ye humne shuru mein add nahi kiya tha),
        // to ek NAYA step add kar dete hain.
        boolean stepExists = false;
        for (TrackingStep step : order.getTrackingSteps()) {
            if (step.getStatus() == next) {
                step.setCompleted(true);
                step.setTimestamp(LocalDateTime.now());
                stepExists = true;
                break;
            }
        }
        
        if (!stepExists) {
            order.getTrackingSteps().add(
                    new TrackingStep(next, formatStatusLabel(next), LocalDateTime.now(), true));
        }

        Order updated = orderRepository.save(order);
        
        // NAYA: Status advance hone par (jaise SHIPPED, OUT_FOR_DELIVERY,
        // DELIVERED) user ko notification milti hai
        notificationService.createNotification(
                updated.getUser().getId(),
                com.sac.catify.backend.entity.NotificationCategory.ORDERS,
                "Order " + formatStatusLabel(next),
                "Your order #" + updated.getOrderNumber() + " is now " + formatStatusLabel(next) + "."
        );
        return mapToResponse(updated);
	}
//===================================================================================================================

	// ===================================================================
    // ADMIN: Refund process karna
    // ===================================================================
	@Override
	public OrderResponse processRefund(Long orderId) {
		
		
		 Order order = orderRepository.findById(orderId)
	                .orElseThrow(() -> ApiException.notFound("Order not found with id: " + orderId));

	        // Sirf CANCELLED ya RETURNED order ka refund process ho sakta hai
	        if (order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.RETURNED) {
	            throw ApiException.badRequest("Only cancelled or returned orders can be refunded.");
	        }

	        order.setStatus(OrderStatus.REFUNDED);
	        order.getTrackingSteps().add(
	                new TrackingStep(OrderStatus.REFUNDED, "Refund processed", LocalDateTime.now(), true));

	        Order updated = orderRepository.save(order);
	        
	        // NAYA: Refund process hone par notification
	        notificationService.createNotification(
	                updated.getUser().getId(),
	                com.sac.catify.backend.entity.NotificationCategory.ORDERS,
	                "Refund Processed",
	                "A refund for order #" + updated.getOrderNumber() + " has been processed."
	        );
	        return mapToResponse(updated);
	}
    
//===================================================================================================================
   
	
	// ===================================================================
    // PRIVATE HELPERS
    // ===================================================================

    // Order dhoondhta hai AUR verify karta hai ki ye order ISI user ka hai
    // (security check — IDOR se bachne ke liye, jaisa Cart/Address mein bhi tha)
    private Order getOwnedOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ApiException.notFound("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("This order does not belong to you.");
        }
        return order;
    }

//-----------------------------------------------------------------------------------------------------------------
    
    // Human-readable order number banata hai, jaise "CF-48213"
    private String generateOrderNumber() {
        int number = 10000 + random.nextInt(90000); // 5-digit random number
        return "CF-" + number;
    }

//------------------------------------------------------------------------------------------------------
    
    // Enum ("OUT_FOR_DELIVERY") ko readable label ("Out for delivery") mein badalta hai
    private String formatStatusLabel(OrderStatus status) {
        return status.name().replace("_", " ").toLowerCase();
    }

//------------------------------------------------------------------------------------------------------
    // Order Entity ko poori tarah OrderResponse DTO mein convert karta hai —
    // items, tracking-steps, address sab flatten karke
    private OrderResponse mapToResponse(Order order) {
    	
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setStatus(order.getStatus().name());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setTotal(order.getTotal());
        response.setPlacedAt(order.getPlacedAt());
        
        response.setCouponCode(order.getCouponCode());
        response.setDiscountAmount(order.getDiscountAmount());

        OrderAddress addr = order.getAddress();
        response.setAddress(new OrderResponse.AddressSnapshot(
                addr.getFullName(), addr.getLine1(), addr.getLine2(),
                addr.getCity(), addr.getState(), addr.getPincode(), addr.getPhone()
        ));

        response.setItems(order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(), item.getProductId(), item.getTitle(), item.getImage(),
                        item.getPrice(), item.getQuantity(), item.getColor(), item.getSize(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .collect(Collectors.toList()));

        response.setTrackingSteps(order.getTrackingSteps().stream()
                .map(step -> new TrackingStepResponse(
                        step.getStatus(), step.getLabel(), step.getTimestamp(), step.isCompleted()))
                .collect(Collectors.toList()));

        return response;
    }
}
