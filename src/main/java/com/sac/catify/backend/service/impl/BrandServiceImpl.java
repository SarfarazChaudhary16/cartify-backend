package com.sac.catify.backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sac.catify.backend.dto.BrandRequest;
import com.sac.catify.backend.dto.BrandResponse;
import com.sac.catify.backend.entity.Brand;
import com.sac.catify.backend.exception.ApiException;
import com.sac.catify.backend.repository.BrandRepository;
import com.sac.catify.backend.service.BrandService;

@Service
public class BrandServiceImpl implements BrandService {

	
	// BrandRepository ko "final" isliye banaya hai kyunki hum chahte hain ki
    // ye field ek baar constructor mein set hone ke baad kabhi badal na sake
    // (immutability — accidental reassignment se bachne ke liye good practice hai)
    private final BrandRepository brandRepository;
    
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }
    
//---------------------------------------------------------------------------------------------------
	@Override
	public BrandResponse createBrand(BrandRequest request) {


		// Step 1: Ek naya, KHAALI Brand object banate hain (JPA/Hibernate
        // ke liye pehle empty object create karna zaroori hai, phir setters
        // se fields bharte hain — ye entity classes ka standard pattern hai)
		Brand brand = new Brand();
		
		// Step 2: Request (jo frontend se aaya) ke fields ko is naye Brand
        // object mein copy kar rahe hain, ek-ek karke
        brand.setName(request.getName());
        
       // Step 3: Slug handle karna — ye thoda tricky logic hai isliye dhyan se:
        // Agar user ne slug field khaali chhoda (ya bilkul nahi bheja, null aaya),
        // to hum khud NAME se ek URL-friendly slug generate kar denge.
        // Warna, jo bhi slug user ne diya hai, wahi use karenge.
        //
        // request.getSlug() == null           -> field bilkul nahi bheji (JSON mein absent)
        // request.getSlug().isBlank()         -> field bheji lekin khaali string ("") ya sirf spaces (" ") hai
        // Dono conditions ko "||" (OR) se jodha hai — matlab in dono mein se
        // KOI BHI EK true ho, to humein khud slug generate karna hai.
        String slug = (request.getSlug() == null || request.getSlug().isBlank())
                ? generateSlug(request.getName())   // true wala case: khud generate karo
                : request.getSlug();                 // false wala case: jo diya hai wahi use karo
        brand.setSlug(slug);

        brand.setLogo(request.getLogo());
        brand.setDescription(request.getDescription());
        

        // Step 4: Ab is Brand object ko database mein save karte hain.
        // save() JpaRepository ka built-in method hai — Spring Data JPA khud
        // andar hi "INSERT INTO brands (...) VALUES (...)" SQL query bana ke
        // chala deta hai. Hume ek line bhi SQL likhni nahi padi.
        //
        // save() ka return value bhi Brand object hi hota hai, lekin ab isme
        // database se generated "id" bhi bhar chuki hoti hai (jo pehle null thi,
        // kyunki naya object tha aur ID auto-increment se milti hai save hone ke baad hi)
        Brand savedBrand = brandRepository.save(brand);

        
        // Step 5: Ab is saved Entity (jisme sensitive/unnecessary internal
        // details ho sakti hain future mein) ko ek clean "response shape"
        // (BrandResponse DTO) mein convert karke return karte hain.
        // Neeche mapToResponse() helper method mein ye conversion logic hai.
        return mapToResponse(savedBrand);
	}

//========================================================================================================
	
	@Override
	public List<BrandResponse> getAllBrands() {


		// brandRepository.findAll() -> "SELECT * FROM brands" query chalata hai,
        // aur database ki saari rows ko List<Brand> (Entity objects) ki tarah return karta hai.
        //
        // .stream() -> Is List ko ek "Stream" mein convert karte hain. Stream
        // Java ka ek feature hai jisse hum List ke har element pe operations
        // (jaise transform, filter) ek chain (pipeline) ki tarah likh sakte hain,
        // for-loop likhne ke bajaye.
        //
        // .map(this::mapToResponse) -> Stream ke HAR EK Brand object ko
        // mapToResponse() method se guzarte hain, jo use BrandResponse mein
        // convert kar deta hai. "this::mapToResponse" ek method reference hai —
        // ye likhna bilkul "brand -> mapToResponse(brand)" likhne jaisa hi hai,
        // bas short-hand syntax hai.
        //
        // .collect(Collectors.toList()) -> Stream se wapas ek normal
        // List<BrandResponse> bana lete hain (kyunki Stream khud ek "List"
        // nahi hoti, ek "processing pipeline" hoti hai — final step mein
        // use wapas List collection mein "collect" karna padta hai)
        return brandRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
	}

//============================================================================================================
	
	@Override
	public BrandResponse getBrandBySlug(String slug) {


		// findBySlug() humne khud BrandRepository interface mein define kiya
        // tha (Phase B2 mein) — Spring Data JPA ne khud iska SQL generate kiya:
        // "SELECT * FROM brands WHERE slug = ?"
        //
        // Ye Optional<Brand> return karta hai — matlab result "ho sakta hai
        // mile, ho sakta hai na mile" (jaise galat slug URL mein daal diya ho).
        //
        // .orElseThrow(...) -> Agar Optional ke andar Brand hai, to use nikal
        // ke return kar deta hai. Agar KHAALI hai (koi match nahi mila), to
        // humne jo lambda diya hai wo chalega aur ek Exception throw ho jayegi.
        // Ye null-check ka safe/modern tareeka hai — agar hum .get() seedha
        // call karte bina check kiye, to agar value na ho to
        // "NoSuchElementException" crash ho jata bina proper message ke.
		Brand brand = brandRepository.findBySlug(slug)
				
				.orElseThrow(() -> ApiException.notFound("Brand not found with slug: " + slug));

        return mapToResponse(brand);
	}

//==================================================================================================================
	
	@Override
	public BrandResponse updateBrand(Long id, BrandRequest request) {


		// Step 1: Pehle check karte hain ki jis ID ka brand update karna hai,
        // wo database mein EXIST karta hai ya nahi. findById() bhi Optional
        // return karta hai (JpaRepository ka built-in method hai, hume khud
        // banane ki zarurat nahi padi).
        Brand brand = brandRepository.findById(id)
        		
        		.orElseThrow(() -> ApiException.notFound("Brand not found with id: " + id));

        // Step 2: Existing brand object (jo abhi database se aaya hai, saari
        // purani values ke saath) ke fields ko NAYI values se overwrite karte hain.
        // Dhyan do: hum naya Brand object nahi bana rahe (createBrand() ki
        // tarah), balki EXISTING object ko modify kar rahe hain — isse
        // Hibernate automatically samajh jata hai ki isko UPDATE query
        // chalani hai, INSERT nahi (kyunki is object ki ID already set hai).
        brand.setName(request.getName());
        
        // Slug ko sirf tab update karte hain jab user ne naya slug bheja ho
        // (khaali nahi chhoda). Agar khaali chhoda, to purana slug hi wapas
        // rehne dete hain (change nahi karte) — is baar humne createBrand()
        // jaisa "auto-generate" nahi kiya, kyunki update ke waqt agar slug
        // khaali aaye to iska matlab shayad user isse "touch" hi nahi karna
        // chahta, purana hi rakhna chahta hai.
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            brand.setSlug(request.getSlug());
        }
        
        brand.setLogo(request.getLogo());
        brand.setDescription(request.getDescription());
        
        // Step 3: save() ko yahan bhi call karte hain — lekin is baar ye
        // INSERT nahi karega, UPDATE karega. Kaise pata chalta hai Hibernate ko?
        // Kyunki humne "brand" object ko findById() se database se LOADED
        // kiya tha (uski ID already thi), naya object "new Brand()" nahi banaya.
        // Hibernate dekhta hai "iski ID already hai, matlab ye row database
        // mein already exist karti hai" -> isliye UPDATE query chalata hai.
        Brand updatedBrand = brandRepository.save(brand);

        return mapToResponse(updatedBrand);
	}

//===========================================================================================================
	
	@Override
	public void deleteBrand(Long id) {


		// existsById() sirf ek boolean (true/false) return karta hai — "SELECT
        // COUNT(*) > 0 FROM brands WHERE id = ?" jaisi query chalti hai andar.
        //
        // Hum ye check pehle isliye karte hain kyunki agar hum seedha
        // deleteById(id) call kar dein aur wo ID exist hi na kare, to
        // deleteById() koi exception NAHI dega (chup-chaap kuch nahi karega) —
        // jo confusing behavior hai. Isliye khud manually check karke, agar
        // na mile to explicit error dete hain, taaki Controller/frontend ko
        // pata chale ki delete fail hua (kyunki wo product hi exist nahi karta).
        if (!brandRepository.existsById(id)) {
        	
        	throw ApiException.notFound("Brand not found with id: " + id);
        }
        
        
        // Ab confirm ho gaya ki brand exist karta hai, to safely delete kar sakte hain.
        // deleteById() -> "DELETE FROM brands WHERE id = ?" query chalata hai.
        //
        // Note: Agar is brand ke saath products linked hain (Product entity
        // ka "brand" field @JoinColumn(nullable = false) hai, Phase B2 mein
        // banaya tha), to MySQL foreign-key constraint ki wajah se ye delete
        // FAIL ho jayega agar products abhi bhi is brand ko point kar rahe
        // hain. Ye jaan-boojh kar hai — accidental data-loss (products ka
        // "orphan" ho jana, bina brand ke reh jana) rokta hai.
        brandRepository.deleteById(id);	
	}

	
	// ===================================================================
    // PRIVATE HELPER METHODS
    // Ye methods sirf isi class ke andar use hote hain (bahar se access
    // nahi hote, isliye "private"). Inka kaam hai repetitive logic ko
    // ek jagah rakhna, taaki upar wale methods clean/chhote rahein.
    // ===================================================================
	
	
	// Brand ka naam lekar ek URL-friendly "slug" banata hai.
    // Example: "Nike India!" -> "nike-india"
    private String generateSlug(String name) {
        return name
                .toLowerCase()                        // saare letters chhote (Nike -> nike)
                .trim()                                // aage-peeche ke extra spaces hata do
                .replaceAll("[^a-z0-9]+", "-")          // jo bhi letter/number NAHI hai
                                                          // (jaise "!", "&", space) — usse "-" bana do
                .replaceAll("(^-|-$)", "");              // agar shuru ya end mein "-" reh gaya
                                                          // ho (jaise agar original naam special
                                                          // character se shuru/end hota tha), use hata do
    }
    
//==================================================================================================================
    
    
    // Brand Entity (jo database se aata hai, JPA-managed object hai) ko
    // BrandResponse DTO (jo hum frontend ko bhejte hain, plain/simple object hai)
    // mein convert karta hai.
    //
    // Abhi ke liye Brand aur BrandResponse ki fields exactly same hain,
    // isliye ye conversion "seedha copy" jaisa lag raha hai. Lekin ye pattern
    // IMPORTANT hai kyunki Entity aur DTO ko ALAG rakhna best practice hai —
    // agar kal Brand Entity mein koi naya internal/sensitive field add ho
    // (jaise "createdByAdminId"), to hume bas is method mein ONE LINE add/
    // remove karni hai, frontend ko accidentally wo field leak nahi hogi
    // (kyunki DTO mein hum sirf explicitly wahi fields daalte hain jo chahiye).
    private BrandResponse mapToResponse(Brand brand) {
        return new BrandResponse(
                brand.getId(),
                brand.getName(),
                brand.getSlug(),
                brand.getLogo(),
                brand.getDescription()
        );
    }
}   
//--------------------------------------------------------------------------------------------------------------
//FLOW
    
//    Frontend (AdminBrandsPage.tsx modal "Add Brand")
//    │
//    │  POST /api/brands   Body: { name: "Nike", logo: "...", description: "..." }
//    ▼
//BrandController  (agli batch mein banega)
//    │  @RequestBody se JSON ko BrandRequest object mein convert karta hai
//    │  brandService.createBrand(request) call karta hai
//    ▼
//BrandServiceImpl.createBrand(request)
//    │
//    ├─► new Brand()                     → khaali Java object banaya
//    ├─► brand.setName("Nike")           → request se field copy
//    ├─► slug generate/copy logic         → "nike" ban jayega agar khaali diya tha
//    ├─► brand.setLogo(...) / setDescription(...)
//    │
//    ├─► brandRepository.save(brand)
//    │        │
//    │        └─► Spring Data JPA khud andar:
//    │              "INSERT INTO brands (name, slug, logo, description)
//    │               VALUES ('Nike', 'nike', '...', '...')"
//    │              chalata hai, aur DB se generated ID (jaise id=7)
//    │              wapas 'brand' object mein bhar deta hai
//    │
//    └─► mapToResponse(savedBrand)
//              → Brand Entity (id=7, name="Nike"...) ko
//                BrandResponse (same fields) mein convert karta hai
//    │
//    ▼
//Controller isi BrandResponse ko JSON bana ke wapas bhejta hai
//    │
//    ▼
//Frontend: toast "Brand added" dikhata hai, list re-render hoti hai naye brand ke saath

