package com.sac.catify.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sac.catify.backend.dto.ProductRequest;
import com.sac.catify.backend.dto.ProductResponse;
import com.sac.catify.backend.service.ProductService;

import jakarta.validation.Valid;

//@RestController ek combo annotation hai jo 2 kaam karta hai:
//1. @Controller — is class ko Spring ko batata hai ki "ye ek web request
// handle karne wali class hai"
//2. @ResponseBody — batata hai ki har method ka return value seedha
// HTTP response BODY mein JSON ki tarah likh diya jaye (na ki koi
// HTML template render ho, jaisa purane-zamane ke JSP/Thymeleaf mein hota tha)

//@RequestMapping class-level pe base URL define karta hai. Iska matlab
//is controller ke ANDAR jitne bhi endpoints hain, sab "/api/products" se
//shuru honge (jaise neeche "/{id}" likha hai, to poora URL ban jayega
//"/api/products/{id}")

@RestController
@RequestMapping("/api/products")
public class ProductController {

	
	    private final ProductService productService;
	 
	    // Single constructor hai, isliye @Autowired zaroori nahi (Spring khud
	    // is constructor ko use karke ProductService ka object inject kar dega)
	    public ProductController(ProductService productService) {
	        this.productService = productService;
	    }
	    
	    
	// ============================================================
    // POST /api/products
	// Admin > Add Product form submit hone pe ye endpoint hit hota hai
	// ============================================================
	 @PostMapping
	 public ResponseEntity<ProductResponse> createProduct(
			 
			    // @RequestBody batata hai: incoming HTTP request ka JSON body
	            // automatically ProductRequest object mein convert (deserialize)
	            // ho jayega — hume manually JSON parse nahi karni padti.
	            //
	            // @Valid batata hai: is object ko save/process karne se PEHLE,
	            // Spring khud ProductRequest ke andar lage @NotBlank/@NotNull/
	            // @Positive annotations (jo humne Phase mein banaye the) ko check
	            // kar lega. Agar koi validation fail ho (jaise price khaali ho),
	            // to Spring automatically HTTP 400 Bad Request error de dega,
	            // hume method ke andar if-else likhne ki zarurat nahi padi.
			 
			 @Valid @RequestBody ProductRequest request) {
		 
		 ProductResponse response = productService.createProduct(request);
		 
		    // ResponseEntity hume control deta hai ki response ka HTTP status
	        // code kya ho. HttpStatus.CREATED matlab "201 Created" — ye standard
	        // convention hai jab koi NAYI cheez successfully ban jati hai
	        // (sirf "200 OK" bhejna bhi chalta, lekin 201 zyada precise/correct hai)
		return new ResponseEntity<>(response, HttpStatus.CREATED); 
		 
	 }
	 
//------------------------------------------------------------------------------------------------------	 
	    // ============================================================
	    // GET /api/products
	    // Frontend Catalog page (/products) — saare products list karta hai
	    // ============================================================
	 @GetMapping
	 public ResponseEntity<List<ProductResponse>> getAllProducts() {
		 
		 List<ProductResponse> products = productService.getAllProducts();
		 
		 return ResponseEntity.ok(products);
	 }

//------------------------------------------------------------------------------------------------------
	 
//	 Ye API Product ID ke basis par database se ek specific product ki
//	 details fetch karti hai aur usse frontend ko response ke roop me bhejti hai.
	    // ============================================================
	    // GET /api/products/{id}
	    // Admin > Edit Product page load karte waqt existing data laane ke liye
	    // ============================================================
	 @GetMapping("/{id}")
	 public ResponseEntity<ProductResponse> getProductById(
			 
			// @PathVariable batata hai ki URL ke andar jo "{id}" likha hai
	        // (jaise koi /api/products/5 call kare), uska "5" value is
	        // "id" parameter mein automatically aa jayega
			@PathVariable Long id) {
		 
		 return ResponseEntity.ok(productService.getProductById(id));
	 }
//------------------------------------------------------------------------------------------
	 
//	 Ye API Product ki ID se nahi, balki uske slug (URL-friendly name) 
//	 se product ki details database se nikalti hai aur frontend ko bhejti hai.
	 
//	 A slug is a URL-friendly, human-readable unique string used 
//	 to identify a resource in a web application.
	    // ============================================================
	    // GET /api/products/slug/{slug}
	    // Frontend Product Detail page (/product/:slug) — slug se product dhoondhta hai
	    // ============================================================
	 @GetMapping("/slug/{slug}")
	 public ResponseEntity<ProductResponse> getProductBySlug(
			 
			 @PathVariable String slug) {
		 
		 return ResponseEntity.ok(productService.getProductBySlug(slug));
	 }
//-----------------------------------------------------------------------------------------	 
	 // ============================================================
	 // GET /api/products/category/{categoryId}
     // Frontend Category page (/category/:categorySlug) ke liye
     // ============================================================
	 @GetMapping("/category/{categoryId}")
	 public ResponseEntity<List<ProductResponse>> getProductsByCategory(
			 
			 @PathVariable Long categoryId) {
		 
		 return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
	 }
//-------------------------------------------------------------------------------------------------------------	 
	 
//	 Ye API Brand ID ke basis par us brand ke saare products database se 
//	 fetch karti hai aur frontend ko list ke form me bhejti hai.
	// ============================================================
    // GET /api/products/brand/{brandId}
    // Frontend Brand page (/brand/:brandSlug) ke liye
    // ============================================================
	    @GetMapping("/brand/{brandId}")
	    public ResponseEntity<List<ProductResponse>> getProductsByBrand(
	    		
	    		@PathVariable Long brandId) {
		 
	        return ResponseEntity.ok(productService.getProductsByBrand(brandId));
	    }
//-----------------------------------------------------------------------------------------------------
	    
//	    searchProducts() is a GET API that searches products based on a 
//	    keyword provided by the user and returns a list of matching products.
	    // ============================================================
	    // GET /api/products/search?keyword=shoes
	    // Frontend Search page (/search?q=...) ke liye
	    // ============================================================
	 @GetMapping("/serach")
	 public ResponseEntity<List<ProductResponse>> searchProducts(
			 
			 // @RequestParam batata hai ki URL ke "?keyword=..." wale query
	         // parameter ki value is variable mein aayegi. Jaise agar URL hai
	         // "/api/products/search?keyword=shoes", to keyword = "shoes" milega.
			 @RequestParam String keyword) {
		 
		 return ResponseEntity.ok(productService.searchProducts(keyword));
	 }
	 
//---------------------------------------------------------------------------------------------------------	 
	    // ============================================================
	    // GET /api/products/new-arrivals
	    // Home page ke "New Arrivals" section ke liye
	    // ============================================================
	    @GetMapping("/new-arrivals")
	    public ResponseEntity<List<ProductResponse>> getNewArrivals() {
	        return ResponseEntity.ok(productService.getNewArrivals());
	    }
	    
//------------------------------------------------------------------------------------------------------	    
	    // ============================================================
	    // GET /api/products/best-sellers
	    // Home page ke "Best Sellers" section ke liye
	    // ============================================================
	    @GetMapping("/best-sellers")
	    public ResponseEntity<List<ProductResponse>> getBestSellers() {
	        return ResponseEntity.ok(productService.getBestSellers());
	    }
	    
//--------------------------------------------------------------------------------------------
	    
	    // ============================================================
	    // GET /api/products/flash-sale
	    // Home page ke "Flash Sale" section ke liye
	    // ============================================================
	    @GetMapping("/flash-sale")
	    public ResponseEntity<List<ProductResponse>> getFlashSaleProducts() {
	        return ResponseEntity.ok(productService.getFlashSaleProducts());
	    }
	    
//---------------------------------------------------------------------------------------------------------------
	    
	    // ============================================================
	    // PUT /api/products/{id}
	    // Admin > Edit Product form submit hone pe
	    // (PUT method REST convention mein "poori resource ko update karo" ke
	    // liye use hota hai — POST "naya banao" ke liye, PUT "existing ko badlo" ke liye)
	    // ============================================================
	    @PutMapping("/{id}")
	    public ResponseEntity<ProductResponse> updateProduct(
	            @PathVariable Long id,
	            @Valid @RequestBody ProductRequest request) {
	        return ResponseEntity.ok(productService.updateProduct(id, request));
	    }
	    
//--------------------------------------------------------------------------------------------------------------------------
	    
	    // ============================================================
	    // DELETE /api/products/{id}
	    // Admin > Products page > Delete button (single ya bulk delete se bhi
	    // isi endpoint ko multiple baar call kiya ja sakta hai frontend se)
	    // ============================================================
	    @DeleteMapping("/{id}")
	    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
	        productService.deleteProduct(id);
	        // Delete ke baad koi data return karne ki zarurat nahi hoti,
	        // isliye body khaali (Void/null) rakhte hain, bas status code
	        // "204 No Content" bhejte hain — ye REST convention hai jo batata
	        // hai "operation successful hui, lekin wapas dikhane ke liye kuch nahi hai"
	        return ResponseEntity.noContent().build();
	    }
	    
}

