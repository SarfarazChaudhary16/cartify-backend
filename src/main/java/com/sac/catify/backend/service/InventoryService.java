package com.sac.catify.backend.service;

import java.util.List;

import com.sac.catify.backend.dto.InventoryHistoryResponse;
import com.sac.catify.backend.dto.ProductResponse;

public interface InventoryService {

    // Admin Inventory page ka "Stock Levels" tab — saare products (jinme
    // stock/id/title dikhta hai) — hum yahan SEEDHA ProductResponse
    // REUSE kar rahe hain (Phase B4 mein bana chuka hai), kyunki
    // "Product ki stock-level dikhana" bilkul wahi data hai jo Product
    // module mein already hai — koi naya DTO banane ki zarurat nahi thi
    List<ProductResponse> getAllProductsWithStock();

    // "Adjust Stock" button — Product ka stock badalta hai, AUR ek
    // InventoryHistory record banata hai (audit-log)
    ProductResponse adjustStock(Long productId, int change, String reason);

    // Admin Inventory page ka "History Log" tab
    List<InventoryHistoryResponse> getHistory();
}
