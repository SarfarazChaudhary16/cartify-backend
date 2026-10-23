package com.sac.catify.backend.service.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.ProductImageResponse;
import com.sac.catify.backend.dto.ProductRequest;
import com.sac.catify.backend.dto.ProductResponse;
import com.sac.catify.backend.dto.ProductVariantResponse;
import com.sac.catify.backend.entity.Brand;
import com.sac.catify.backend.entity.Category;
import com.sac.catify.backend.entity.Product;
import com.sac.catify.backend.entity.ProductImage;
import com.sac.catify.backend.entity.ProductVariant;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.BrandRepository;
import com.sac.catify.backend.repository.CategoryRepository;
import com.sac.catify.backend.repository.ProductRepository;
import com.sac.catify.backend.service.ProductService;


//Ye class kya karegi, high-level: Product entity ke saath Brand, Category, 
//Images, Variants, Colors, Specs sab related hain — is class ka kaam hai in 
//sabko jodna aur alag karna (Entity ↔ DTO conversion), taaki Controller ko 
//seedha clean data mile.

//ProductServiceImpl is the implementation of the ProductService interface. It 
//contains all the business logic related to product management such as creating, 
//updating, deleting, searching, and retrieving products. It communicates with 
//repositories to access the database and converts entities into DTOs before sending 
//the response to the controller.


@Service
public class ProductServiceImpl implements ProductService {

    // Teen repositories chahiye kyunki Product banate waqt Brand aur Category
    // dono ko "lookup" karna padta hai (jaise "brandId=3 diya hai, to Brand
    // entity dhoondh ke usse Product ke saath link karo")
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    // Single constructor hai isliye @Autowired ki zarurat nahi.
    public ProductServiceImpl(ProductRepository productRepository,
                               BrandRepository brandRepository,
                               CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        // Brand aur Category ko unki ID se dhoondhte hain — agar na milein to error
        // (exception handling proper Phase B15 mein banayenge, abhi RuntimeException se kaam chalayenge)
        Brand brand = brandRepository.findById(request.getBrandId())
        		.orElseThrow(() -> ApiException.notFound("Brand not found with id: " + request.getBrandId()));

        Category category = categoryRepository.findById(request.getCategoryId())
        		.orElseThrow(() -> ApiException.notFound("Category not found with id: " + request.getCategoryId()));

        // Naya Product object banate hain
        Product product = new Product();
        product.setTitle(request.getTitle());

        // Agar frontend se slug khaali aaya (admin form mein "auto-generate if left blank"
        // wala feature yaad hai?), to hum khud title se generate kar dete hain
        String slug = (request.getSlug() == null || request.getSlug().isBlank())
                ? generateSlug(request.getTitle())
                : request.getSlug();
        product.setSlug(slug);

        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCompareAtPrice(request.getCompareAtPrice());
        product.setStock(request.getStock());
        product.setMaterial(request.getMaterial());
        product.setBrand(brand);
        product.setCategory(category);
        product.setNew(request.isNew());
        product.setBestSeller(request.isBestSeller());
        product.setFlashSale(request.isFlashSale());
        product.setDiscountPercent(request.getDiscountPercent());

        // Naya product hamesha rating=0, reviewCount=0 se shuru hota hai
        // (Entity mein already default values hain: rating=0.0, reviewCount=0)

        // Simple lists (tags, sizes, colors, specifications) seedha copy kar sakte hain,
        // null-check zaroori hai kyunki request mein ye optional ho sakte hain
        if (request.getTags() != null) product.setTags(request.getTags());
        if (request.getSizes() != null) product.setSizes(request.getSizes());
        if (request.getColors() != null) product.setColors(request.getColors());
        if (request.getSpecifications() != null) product.setSpecifications(request.getSpecifications());

        // ===== Images: Request DTO se Entity list banani hai =====
        // Images ke andar "product" field bhi set karna hoga (ManyToOne relationship,
        // yaad hai ProductImage entity? Usme "product" field tha jo owning side hai)
        if (request.getImages() != null) {
            List<ProductImage> imageEntities = new ArrayList<>();
            for (ProductRequest.ProductImageRequest imgReq : request.getImages()) {
                ProductImage image = new ProductImage(imgReq.getUrl(), imgReq.getAlt(), product);
                imageEntities.add(image);
            }
            product.setImages(imageEntities);
        }

        // ===== Variants: same pattern jaisa images ka =====
        if (request.getVariants() != null) {
            List<ProductVariant> variantEntities = new ArrayList<>();
            for (ProductRequest.ProductVariantRequest varReq : request.getVariants()) {
                ProductVariant variant = new ProductVariant(
                        varReq.getColor(), varReq.getSize(), varReq.getSku(), varReq.getStock(), product);
                variantEntities.add(variant);
            }
            product.setVariants(variantEntities);
        }

        // save() ek hi call mein Product + uske saare images + variants save kar dega,
        // kyunki Product entity mein cascade = CascadeType.ALL laga hua tha
        // (yaad hai Phase B2 mein explain kiya tha?) — hume alag se
        // imageRepository.save() ya variantRepository.save() nahi karna padega
        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
        		.orElseThrow(() -> ApiException.notFound("Product not found with slug: " + slug));
        return mapToResponse(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
        		.orElseThrow(() -> ApiException.notFound("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        // findAll() JpaRepository ka built-in method hai — "SELECT * FROM products"
        // .stream().map(...).collect(...) -> ye Java Streams API hai. Iska matlab:
        // "List<Product> lo, HAR EK Product ko mapToResponse() se DTO mein badlo,
        // phir sabko wapas ek List<ProductResponse> mein jama kar do."
        // Ye ek for-loop ka hi shorter/cleaner tareeka hai.
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByBrand(Long brandId) {
        return productRepository.findByBrandId(brandId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getNewArrivals() {
        return productRepository.findByIsNewTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getBestSellers() {
        return productRepository.findByIsBestSellerTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getFlashSaleProducts() {
        return productRepository.findByIsFlashSaleTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        // Pehle existing product dhoondhte hain
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found with id: " + id));

        Brand brand = brandRepository.findById(request.getBrandId())
        		.orElseThrow(() -> ApiException.notFound("Brand not found with id: " + request.getBrandId()));

        Category category = categoryRepository.findById(request.getCategoryId())
        		.orElseThrow(() -> ApiException.notFound("Category not found with id: " + request.getCategoryId()));

        // Existing product ke fields ko naye data se update karte hain
        product.setTitle(request.getTitle());
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            product.setSlug(request.getSlug());
        }
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCompareAtPrice(request.getCompareAtPrice());
        product.setStock(request.getStock());
        product.setMaterial(request.getMaterial());
        product.setBrand(brand);
        product.setCategory(category);
        product.setNew(request.isNew());
        product.setBestSeller(request.isBestSeller());
        product.setFlashSale(request.isFlashSale());
        product.setDiscountPercent(request.getDiscountPercent());

        if (request.getTags() != null) product.setTags(request.getTags());
        if (request.getSizes() != null) product.setSizes(request.getSizes());
        if (request.getColors() != null) product.setColors(request.getColors());
        if (request.getSpecifications() != null) product.setSpecifications(request.getSpecifications());

        // Images/Variants ko update karne ka simplest tareeka: purani list clear() karo,
        // phir nayi list se dobara bharo. orphanRemoval=true (Entity mein set kiya tha)
        // ki wajah se clear() karte hi purani rows database se bhi delete ho jayengi.
        if (request.getImages() != null) {
            product.getImages().clear();
            for (ProductRequest.ProductImageRequest imgReq : request.getImages()) {
                product.getImages().add(new ProductImage(imgReq.getUrl(), imgReq.getAlt(), product));
            }
        }

        if (request.getVariants() != null) {
            product.getVariants().clear();
            for (ProductRequest.ProductVariantRequest varReq : request.getVariants()) {
                product.getVariants().add(new ProductVariant(
                        varReq.getColor(), varReq.getSize(), varReq.getSku(), varReq.getStock(), product));
            }
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        // existsById() check karke pehle confirm karte hain product hai bhi ya nahi,
        // warna deleteById() silently kuch nahi karega agar ID galat ho (koi error nahi dega)
        if (!productRepository.existsById(id)) {
        	
        	throw ApiException.notFound("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // ===== Private Helper Methods =====

    // Title se URL-friendly slug banata hai, jaise "Nike Air Max 90!" -> "nike-air-max-90"
    // Frontend ke slugify() function (AdminProductFormPage.tsx mein tha) ka hi Java version
    private String generateSlug(String title) {
        return title.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")  // saare special characters/spaces ko "-" se replace
                .replaceAll("(^-|-$)", "");       // shuru/end ke extra "-" hata do
    }

    // Product Entity -> ProductResponse DTO conversion.
    // Ye method bahut baar use hota hai (har method ke andar), isliye ek hi jagah
    // define kiya hai taaki code repeat na ho (DRY principle - "Don't Repeat Yourself")
    
//    mapToResponse() converts a database Entity into a Response DTO. 
//    It hides internal fields, prevents exposing sensitive data, 
//    avoids duplicate conversion code, and sends only the required data 
//    back to the controller and then to the frontend.
    
    private ProductResponse mapToResponse(Product product) {
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

        // Brand/Category ke poore Entity ki jagah sirf zaroori fields flatten kar rahe hain
        response.setBrandId(product.getBrand().getId());
        response.setBrandName(product.getBrand().getName());
        response.setBrandSlug(product.getBrand().getSlug());

        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());
        response.setCategorySlug(product.getCategory().getSlug());

        // Images list ko ProductImageResponse list mein convert (Stream API se, upar jaisa hi pattern)
        response.setImages(product.getImages().stream()
                .map(img -> new ProductImageResponse(img.getId(), img.getUrl(), img.getAlt()))
                .collect(Collectors.toList()));

        response.setVariants(product.getVariants().stream()
                .map(v -> new ProductVariantResponse(v.getId(), v.getColor(), v.getSize(), v.getSku(), v.getStock()))
                .collect(Collectors.toList()));

        // Colors/Specifications Embeddable hain, seedha set kar sakte hain (koi conversion nahi chahiye)
        response.setColors(product.getColors());
        response.setSpecifications(product.getSpecifications());
        response.setTags(product.getTags());
        response.setSizes(product.getSizes());

        return response;
    }
    
}   
//FLOW
    
           
//    Frontend (AdminProductFormPage) 
//    │
//    │  POST /api/products  (JSON body: title, price, brandId, images[], variants[]...)
//    ▼
//Controller (ProductController)  ← ye Phase mein aage banega
//    │
//    │  request ko ProductRequest DTO mein convert karta hai (@RequestBody)
//    │  createProduct(request) ko call karta hai
//    ▼
//ProductServiceImpl.createProduct(request)
//    │
//    ├─► Step 1: brandRepository.findById(request.getBrandId())
//    │           → Database se Brand row nikalta hai. Agar na mile → Exception
//    │
//    ├─► Step 2: categoryRepository.findById(request.getCategoryId())
//    │           → Same as Brand, Category nikalta hai
//    │
//    ├─► Step 3: naya Product object banate hain (empty "new Product()")
//    │           → request ke saare fields (title, price, stock...) isme set karte hain
//    │           → agar slug khaali hai to generateSlug() se khud bana lete hain
//    │
//    ├─► Step 4: request.getImages() ko loop karke ProductImage entities banate hain
//    │           → har image mein "product" (jo abhi banaya) ko link karte hain
//    │           → product.setImages(imageEntities)
//    │
//    ├─► Step 5: same pattern variants ke liye (ProductVariant entities banake link)
//    │
//    ├─► Step 6: productRepository.save(product)
//    │           → YE SABSE IMPORTANT STEP HAI:
//    │           → Hibernate ek hi save() call mein:
//    │              a) products table mein INSERT karta hai
//    │              b) cascade=ALL ki wajah se product_images table mein
//    │                 saari images bhi automatically INSERT ho jati hain
//    │              c) product_variants table mein saare variants bhi INSERT ho jate hain
//    │           → (Humein alag se imageRepository.save() ya variantRepository.save()
//    │              bilkul nahi likhna pada — cascade ne poora kaam kar diya)
//    │
//    └─► Step 7: mapToResponse(savedProduct)
//                → Entity (jisme Brand/Category poore objects the) ko
//                  ProductResponse DTO mein "flatten" karte hain
//                → sirf zaroori fields nikalte hain (brandName, brandSlug, waghera)
//                → ye DTO Controller ko wapas milta hai
//    │
//    ▼
//Controller response ko JSON बनाके frontend ko wapas bhejta hai
//    │
//    ▼
//Frontend (AdminProductFormPage) → toast dikhata hai "Product created" → list pe redirect 

