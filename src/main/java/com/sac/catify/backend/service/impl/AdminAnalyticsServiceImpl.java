package com.sac.catify.backend.service.impl;

import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.CategorySalesResponse;
import com.sac.catify.backend.dto.DashboardStatsResponse;
import com.sac.catify.backend.dto.OrderItemResponse;
import com.sac.catify.backend.dto.OrderResponse;
import com.sac.catify.backend.dto.RevenuePointResponse;
import com.sac.catify.backend.dto.TopProductResponse;
import com.sac.catify.backend.dto.TrackingStepResponse;
import com.sac.catify.backend.entity.Order;
import com.sac.catify.backend.entity.OrderItem;
import com.sac.catify.backend.entity.Role;
import com.sac.catify.backend.repository.OrderItemRepository;
import com.sac.catify.backend.repository.OrderRepository;
import com.sac.catify.backend.repository.ProductRepository;
import com.sac.catify.backend.repository.UserRepository;
import com.sac.catify.backend.service.AdminAnalyticsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    // 4 repositories chahiye — Order (revenue/status/recent-orders ke
    // liye), OrderItem (top-products aggregation ke liye), User
    // (customer-count ke liye), Product (product-count ke liye)
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public AdminAnalyticsServiceImpl(OrderRepository orderRepository,
                                      OrderItemRepository orderItemRepository,
                                      UserRepository userRepository,
                                      ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ===================================================================
    // DASHBOARD STATS — 4 stat cards + genuine month-over-month deltas
    // ===================================================================
    @Override
    public DashboardStatsResponse getDashboardStats() {

        // Saare orders ek baar mein utha lete hain — inhi se Revenue aur
        // Orders-count dono nikalenge (baar-baar database query karne
        // ke bajaye, ek baar List mein le ke Java Streams se process karte hain)
        List<Order> allOrders = orderRepository.findAll();

        // ===== TOTAL REVENUE =====
        // .stream().map(Order::getTotal) -> har order ka "total" nikalta hai
        // .reduce(BigDecimal.ZERO, BigDecimal::add) -> sabko ek-ek karke
        // jodta hai, ZERO se shuru karke (jaisa normal "sum = sum + x"
        // loop hota hai, bas Streams ka "reduce" pattern use kiya hai)
        BigDecimal totalRevenue = allOrders.stream()
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalOrders = allOrders.size();
        long totalCustomers = userRepository.countByRole(Role.CUSTOMER);
        long totalProducts = productRepository.count(); // count() JpaRepository ka built-in method hai — "SELECT COUNT(*) FROM products"

        // ===== MONTH-OVER-MONTH DELTA CALCULATION =====
        // Abhi ke mahine ka pehla din, aur pichle mahine ka pehla/aakhri din nikalte hain
        LocalDateTime startOfThisMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfThisMonth.minusSeconds(1);

        // Is mahine ke orders (filter karte hain placedAt >= startOfThisMonth)
        List<Order> thisMonthOrders = allOrders.stream()
                .filter(o -> o.getPlacedAt().isAfter(startOfThisMonth) || o.getPlacedAt().isEqual(startOfThisMonth))
                .collect(Collectors.toList());

        // Pichle mahine ke orders (filter karte hain startOfLastMonth aur endOfLastMonth ke BEECH mein)
        List<Order> lastMonthOrders = allOrders.stream()
                .filter(o -> !o.getPlacedAt().isBefore(startOfLastMonth) && !o.getPlacedAt().isAfter(endOfLastMonth))
                .collect(Collectors.toList());

        BigDecimal thisMonthRevenue = thisMonthOrders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal lastMonthRevenue = lastMonthOrders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        double revenueDelta = calculatePercentChange(thisMonthRevenue.doubleValue(), lastMonthRevenue.doubleValue());
        double ordersDelta = calculatePercentChange(thisMonthOrders.size(), lastMonthOrders.size());

        // Customers/Products ke liye — "is mahine banaye gaye" vs "pichle
        // mahine banaye gaye" count compare karte hain (createdAt field use karke)
        //
        // FIX: kuch purane test-users ka createdAt NULL ho sakta hai
        // (jaise koi user jo manually SQL se insert/edit hua ho testing
        // ke dauran, ya OTP-verify se pehle waali koi edge-case). Agar
        // hum seedha u.getCreatedAt().isBefore(...) karte, NULL pe
        // NullPointerException aa ke POORA endpoint 500-crash kar deta
        // tha (jo asal mein hua bhi tha — "January 1970" wala user yaad
        // hai?). "u.getCreatedAt() != null &&" check laga ke aise users
        // ko safely SKIP kar dete hain calculation se.
        long thisMonthCustomers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CUSTOMER && u.getCreatedAt() != null && !u.getCreatedAt().isBefore(startOfThisMonth))
                .count();
        long lastMonthCustomers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CUSTOMER && u.getCreatedAt() != null
                        && !u.getCreatedAt().isBefore(startOfLastMonth)
                        && !u.getCreatedAt().isAfter(endOfLastMonth))
                .count();
        double customersDelta = calculatePercentChange(thisMonthCustomers, lastMonthCustomers);

        // FIX: Yahi pattern products ke liye bhi — kisi manually-Postman-
        // se-add-kiye product ka createdAt bhi NULL ho sakta hai (jaise
        // "Apple Watch" wala product jo humne kabhi Postman se add kiya
        // tha, jiska "createdAt": null tha JSON response mein). Wahi
        // crash yahan bhi ho sakta tha, isliye same null-check.
        long thisMonthProducts = productRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null && !p.getCreatedAt().isBefore(startOfThisMonth))
                .count();
        long lastMonthProducts = productRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null && !p.getCreatedAt().isBefore(startOfLastMonth) && !p.getCreatedAt().isAfter(endOfLastMonth))
                .count();
        double productsDelta = calculatePercentChange(thisMonthProducts, lastMonthProducts);

        return new DashboardStatsResponse(totalRevenue, revenueDelta, totalOrders, ordersDelta,
                totalCustomers, customersDelta, totalProducts, productsDelta);
    }

    // ===================================================================
    // REVENUE SERIES — last 14 din ka daily revenue
    // ===================================================================
    @Override
    public List<RevenuePointResponse> getRevenueSeries() {
        List<Order> allOrders = orderRepository.findAll();
        List<RevenuePointResponse> series = new ArrayList<>();

        // "d MMM" format -> jaise "24 Jul" (display ke liye clean format)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM");

        // 13 din PEHLE se lekar AAJ tak, ek-ek din loop karte hain
        // (i=13 se shuru, i=0 tak — matlab 14 din total: 13 din pehle...aaj)
        for (int i = 13; i >= 0; i--) {
            LocalDate targetDate = LocalDate.now().minusDays(i);

            // Us din ke SAARE orders filter karte hain (placedAt ki date,
            // targetDate ke barabar honi chahiye)
            BigDecimal dayRevenue = allOrders.stream()
                    .filter(o -> o.getPlacedAt().toLocalDate().isEqual(targetDate))
                    .map(Order::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            series.add(new RevenuePointResponse(targetDate.format(formatter), dayRevenue));
        }

        return series;
    }

    // ===================================================================
    // ORDERS BY STATUS — Donut Chart ke liye
    // ===================================================================
    @Override
    public Map<String, Long> getOrdersByStatus() {
        List<Order> allOrders = orderRepository.findAll();

        // Collectors.groupingBy() — Java Streams ka powerful feature:
        // "har order ko uske STATUS ke hisaab se GROUPS mein baant do,
        // aur har group mein KITNE orders hain, wo count kar do"
        //
        // o -> o.getStatus().name() -> "grouping key" hai (jaise "PENDING",
        // "DELIVERED") — har order iska status-name check karke uske
        // group mein chala jata hai
        //
        // Collectors.counting() -> har group ke ANDAR, kitne elements hain,
        // wo count karta hai
        //
        // Result: Map<String, Long> jaisa { "PENDING": 5, "DELIVERED": 20,
        //          "CANCELLED": 2, ... }
        return allOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));
    }

    // ===================================================================
    // TOP PRODUCTS — asal aggregation, mock data NAHI
    // ===================================================================
    @Override
    public List<TopProductResponse> getTopProducts(int limit) {

        List<OrderItem> allOrderItems = orderItemRepository.findAll();

        // Step 1: Saare order-items ko "productId" ke hisaab se GROUP
        // karte hain. Collectors.groupingBy(OrderItem::getProductId) se
        // humein milta hai: Map<Long, List<OrderItem>> — jaise
        // { 1L: [item1, item5, item9], 2L: [item2, item7], ... }
        // (matlab Product ID=1 ke items teen alag orders mein aaye the)
        Map<Long, List<OrderItem>> groupedByProduct = allOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getProductId));

        List<TopProductResponse> topProducts = new ArrayList<>();

        // Step 2: Har group (matlab har product) ke liye, uski TOTAL
        // quantity aur TOTAL revenue calculate karte hain
        for (Map.Entry<Long, List<OrderItem>> entry : groupedByProduct.entrySet()) {
            Long productId = entry.getKey();
            List<OrderItem> itemsForThisProduct = entry.getValue();

            // mapToInt().sum() -> saari quantities ka jod
            long totalUnitsSold = itemsForThisProduct.stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            // Har item ka (price * quantity) nikal ke sabko jodte hain
            BigDecimal totalRevenue = itemsForThisProduct.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Title/image OrderItem mein hi SNAPSHOT ki tarah store the
            // (yaad hai Phase B6 mein discuss kiya tha — order-time ka
            // title/image copy hota hai), isliye Product Entity dobara
            // dhoondhne ki zarurat nahi — pehle hi item ke andar hai
            String title = itemsForThisProduct.get(0).getTitle();
            String image = itemsForThisProduct.get(0).getImage();

            topProducts.add(new TopProductResponse(productId, title, image, totalUnitsSold, totalRevenue));
        }

        // Step 3: Revenue ke hisaab se SABSE ZYADA-se-KAM order mein sort
        // karte hain, aur sirf top N (jaise 5) rakhte hain
        return topProducts.stream()
                .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue())) // "b compareTo a" = DESCENDING order
                .limit(limit) // sirf pehle "limit" (jaise 5) elements rakhte hain, baaki hata dete hain
                .collect(Collectors.toList());
    }

    // ===================================================================
    // RECENT ORDERS — sabse naye N orders
    // ===================================================================
    @Override
    public List<OrderResponse> getRecentOrders(int limit) {
        // findAllByOrderByPlacedAtDesc() — abhi-abhi Repository mein add
        // kiya tha — saare orders naye-se-purane order mein
        List<Order> orders = orderRepository.findAllByOrderByPlacedAtDesc();

        // Sirf pehle "limit" (jaise 5) orders rakhte hain
        return orders.stream()
                .limit(limit)
                .map(this::mapOrderToResponse)
                .collect(Collectors.toList());
    }
    
 // ===================================================================
    // SALES BY CATEGORY — Reports > Sales tab ke liye
    // ===================================================================
    @Override
    public List<CategorySalesResponse> getSalesByCategory() {

        List<OrderItem> allOrderItems = orderItemRepository.findAll();

        // Har OrderItem ke "productId" se, uski CATEGORY dhoondhni padegi —
        // OrderItem khud sirf title/price/image ka SNAPSHOT rakhta hai,
        // category-info nahi (yaad hai, Phase B6 mein discuss kiya tha),
        // isliye Product Entity dobara dhoondhna padta hai
        java.util.Map<String, java.math.BigDecimal> revenueByCategory = new java.util.HashMap<>();
        java.util.Map<String, Long> unitsByCategory = new java.util.HashMap<>();

        for (OrderItem item : allOrderItems) {
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                String categoryName = product.getCategory().getName();
                java.math.BigDecimal lineRevenue = item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity()));

                revenueByCategory.merge(categoryName, lineRevenue, java.math.BigDecimal::add);
                unitsByCategory.merge(categoryName, (long) item.getQuantity(), Long::sum);
            });
        }

        List<CategorySalesResponse> result = new ArrayList<>();
        for (String categoryName : revenueByCategory.keySet()) {
            result.add(new CategorySalesResponse(
                    categoryName,
                    revenueByCategory.get(categoryName),
                    unitsByCategory.getOrDefault(categoryName, 0L)
            ));
        }

        // Revenue ke hisaab se sabse zyada-se-kam order mein sort
        result.sort((a, b) -> b.getRevenue().compareTo(a.getRevenue()));
        return result;
    }

    // ===================================================================
    // PRIVATE HELPERS
    // ===================================================================

    // 2 numbers ke beech ka PERCENTAGE CHANGE calculate karta hai.
    // Formula: ((naya - purana) / purana) * 100
    private double calculatePercentChange(double current, double previous) {
        if (previous == 0) {
            // Agar pichla mahina 0 tha (jaise bilkul naya business hai),
            // to "percentage change" ka koi mathematical matlab nahi hai
            // (division by zero ho jayega). Agar current bhi 0 hai to
            // 0% change bolte hain, warna 100% "growth" maan lete hain
            // (kyunki 0 se kuch bhi banna = poora naya growth)
            return current == 0 ? 0.0 : 100.0;
        }
        double change = ((current - previous) / previous) * 100;
        // 1 decimal place tak round karte hain (jaise 12.456 -> 12.5),
        // taaki UI mein clean number dikhe
        return Math.round(change * 10.0) / 10.0;
    }

    // Order Entity ko OrderResponse DTO mein convert karta hai — ye
    // BILKUL WAHI logic hai jo OrderServiceImpl.mapToResponse() mein hai.
    // (NOTE: Ye ek chhoti code-duplication hai — ideally hum isko
    // ek shared/common jagah rakhte, jaise ek "OrderMapper" utility class.
    // Abhi ke liye simplicity ke liye copy kiya hai, future mein chaho
    // to refactor kar sakte ho ek common Mapper class banake.)
    private OrderResponse mapOrderToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setStatus(order.getStatus().name());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setTotal(order.getTotal());
        response.setPlacedAt(order.getPlacedAt());

        var addr = order.getAddress();
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