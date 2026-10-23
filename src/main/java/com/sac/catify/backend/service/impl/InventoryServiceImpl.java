package com.sac.catify.backend.service.impl;

import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.InventoryHistoryResponse;
import com.sac.catify.backend.dto.ProductImageResponse;
import com.sac.catify.backend.dto.ProductResponse;
import com.sac.catify.backend.dto.ProductVariantResponse;
import com.sac.catify.backend.entity.InventoryHistory;
import com.sac.catify.backend.entity.Product;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.InventoryHistoryRepository;
import com.sac.catify.backend.repository.ProductRepository;
import com.sac.catify.backend.service.InventoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;

    public InventoryServiceImpl(ProductRepository productRepository,
                                 InventoryHistoryRepository inventoryHistoryRepository) {
        this.productRepository = productRepository;
        this.inventoryHistoryRepository = inventoryHistoryRepository;
    }

    @Override
    public List<ProductResponse> getAllProductsWithStock() {
        // Yahan hum ProductServiceImpl jaisa hi mapping-logic use kar rahe
        // hain (dobara likhna pada kyunki mapToResponse private hai
        // ProductServiceImpl ke andar — DUPLICATE code hai, jaisa
        // AdminAnalyticsServiceImpl mein bhi ek chhota sa duplication tha.
        // Future mein chaho to ek SHARED "ProductMapper" utility class
        // banake dono jagah se ye repetition hata sakte ho.)
        return productRepository.findAll().stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse adjustStock(Long productId, int change, String reason) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ApiException.notFound("Product not found with id: " + productId));

        int previousStock = product.getStock();
        int newStock = previousStock + change;

        // ===== VALIDATION — stock kabhi NEGATIVE nahi ho sakta =====
        // Jaise agar current stock 5 hai, aur Admin "-10" (remove 10)
        // karne ki koshish kare, to newStock = -5 ban jayega, jo
        // physically MEANINGLESS hai (stock kabhi negative nahi ho sakta).
        // Frontend mein bhi is tarah ka guard-rail tha (Math.max(0, ...)),
        // lekin BACKEND mein bhi ye check hona ZAROORI hai — kyunki
        // frontend ki validation ko koi bhi bypass kar sakta hai (jaise
        // Postman se seedha request bhej ke). "Never trust the client"
        // — ye backend security ka ek core principle hai.
        if (newStock < 0) {
            throw ApiException.badRequest("Stock cannot go below 0. Current stock: " + previousStock);
        }

        // Step 1: Product ka stock update karte hain
        product.setStock(newStock);
        Product updatedProduct = productRepository.save(product);

        // Step 2: InventoryHistory mein ek audit-record banate hain —
        // Entity ka constructor (Phase B2 mein banaya tha) khud
        // "createdAt = now()" set kar deta hai
        InventoryHistory historyEntry = new InventoryHistory(
                product.getId(), product.getTitle(), change, previousStock, newStock, reason);
        inventoryHistoryRepository.save(historyEntry);

        return mapProductToResponse(updatedProduct);
    }

    @Override
    public List<InventoryHistoryResponse> getHistory() {
        return inventoryHistoryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapHistoryToResponse)
                .collect(Collectors.toList());
    }

    // ===================================================================
    // PRIVATE HELPERS
    // ===================================================================

    // Ye bilkul ProductServiceImpl.mapToResponse() jaisa hi hai (isi
    // wajah se maine upar comment kiya tha ki ye ek CHHOTI duplication hai)
    private ProductResponse mapProductToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSlug(product.getSlug());
        response.setTitle(product.getTitle());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setCompareAtPrice(product.getCompareAtPrice());
        response.setRating(product.getRating());
        response.setReviewCount(product.getReviewCount());
        response.setStock(product.getStock());
        response.setMaterial(product.getMaterial());
        response.setNew(product.isNew());
        response.setBestSeller(product.isBestSeller());
        response.setFlashSale(product.isFlashSale());
        response.setDiscountPercent(product.getDiscountPercent());
        response.setCreatedAt(product.getCreatedAt());
        response.setBrandId(product.getBrand().getId());
        response.setBrandName(product.getBrand().getName());
        response.setBrandSlug(product.getBrand().getSlug());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());
        response.setCategorySlug(product.getCategory().getSlug());

        response.setImages(product.getImages().stream()
                .map(img -> new ProductImageResponse(img.getId(), img.getUrl(), img.getAlt()))
                .collect(Collectors.toList()));

        response.setVariants(product.getVariants().stream()
                .map(v -> new ProductVariantResponse(v.getId(), v.getColor(), v.getSize(), v.getSku(), v.getStock()))
                .collect(Collectors.toList()));

        response.setColors(product.getColors());
        response.setSpecifications(product.getSpecifications());
        response.setTags(product.getTags());
        response.setSizes(product.getSizes());

        return response;
    }

    private InventoryHistoryResponse mapHistoryToResponse(InventoryHistory history) {
        return new InventoryHistoryResponse(
                history.getId(),
                history.getProductId(),
                history.getProductTitle(),
                history.getChange(),
                history.getPreviousStock(),
                history.getNewStock(),
                history.getReason(),
                history.getCreatedAt()
        );
    }
}
