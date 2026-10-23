package com.sac.catify.backend.config;

import com.sac.catify.backend.entity.Brand;
import com.sac.catify.backend.entity.Category;
import com.sac.catify.backend.entity.Product;
import com.sac.catify.backend.entity.ProductColor;
import com.sac.catify.backend.entity.ProductImage;
import com.sac.catify.backend.repository.BrandRepository;
import com.sac.catify.backend.repository.CategoryRepository;
import com.sac.catify.backend.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Component batata hai Spring ko: "is class ka ek object bana ke apne
// container mein rakh lo" — bilkul jaisa @Service/@Repository ke saath
// hota hai, bas ye ek generic "kisi bhi tarah ki managed class" ke liye hai.
//
// "implements CommandLineRunner" — YE SABSE IMPORTANT LINE HAI. Jab
// application start hoti hai, Spring Boot poori application (saare
// Beans, database-connection, security, sab kuch) load karne ke BAAD,
// har us class ka "run()" method automatically call karta hai jo
// CommandLineRunner implement karti hai. Isliye humein iske liye koi
// alag "trigger" (jaise koi API endpoint) banane ki zarurat nahi —
// ye khud-ba-khud, app start hote hi, EK HI BAAR chal jata hai.
@Component
public class DataSeeder implements CommandLineRunner {

    // 3 repositories chahiye — Category/Brand/Product teeno ko database
    // mein daalne ke liye
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public DataSeeder(CategoryRepository categoryRepository,
                       BrandRepository brandRepository,
                       ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
    }

    // ===================================================================
    // run() — Spring Boot ye method AUTOMATICALLY call karta hai
    // ===================================================================
    // "String... args" — ye Java ka "varargs" syntax hai, matlab command-line
    // arguments (agar koi ho) yahan array ki tarah aa jate hain. Humein
    // inki zarurat nahi, bas method-signature CommandLineRunner interface
    // ki requirement hai.
    @Override
    public void run(String... args) throws Exception {

        // ===== SABSE IMPORTANT SAFETY-CHECK =====
        // Agar Categories PEHLE SE database mein hain (matlab ye
        // pehle bhi chal chuka hai), to DOBARA seed NAHI karte —
        // warna HAR baar jab tum application restart karoge, wahi
        // products BAAR-BAAR duplicate ban jate! Ye check sunishchit
        // karta hai ki seeding SIRF EK HI BAAR ho, pehli baar jab
        // database khaali ho.
        if (categoryRepository.count() > 0) {
            System.out.println("Data already seeded — skipping DataSeeder.");
            return; // method yahin se turant bahar nikal jata hai, aage kuch nahi chalega
        }

        System.out.println("Seeding demo catalog data...");

        Map<String, Category> categories = seedCategories();
        Map<String, Brand> brands = seedBrands();
        seedProducts(categories, brands);

        System.out.println("Catalog seeding complete!");
    }

    // ===================================================================
    // CATEGORIES SEED KARNA
    // ===================================================================
    private Map<String, Category> seedCategories() {

        // Ek Map banate hain jisme category ka NAAM "key" hai aur SAVED
        // Category object "value" hai. Isse Products banate waqt humein
        // seedha "categories.get("Sneakers & Footwear")" likh ke us
        // category ka poora object mil jayega — Product ke saath link
        // karna aasan ho jata hai.
        Map<String, Category> categoryMap = new HashMap<>();

        // List of raw data — {name, slug, image-url} har category ke liye
        List<Category> categoryList = List.of(
                new Category("Sneakers & Footwear", "sneakers-footwear",
                        "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?w=800"),
                new Category("Apparel", "apparel",
                        "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800"),
                new Category("Bags & Backpacks", "bags-backpacks",
                        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800"),
                new Category("Watches", "watches",
                        "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800"),
                new Category("Audio", "audio",
                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800"),
                new Category("Eyewear", "eyewear",
                        "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800")
        );

        // Har category ko database mein SAVE karte hain, aur uska SAVED
        // version (jisme ab database-generated ID bhi hai) Map mein
        // daal dete hain — is SAVED version ki zarurat hai kyunki
        // Product banate waqt humein Category ki ACTUAL ID chahiye hogi
        for (Category category : categoryList) {
            Category saved = categoryRepository.save(category);
            categoryMap.put(saved.getName(), saved);
        }

        return categoryMap;
    }

    // ===================================================================
    // BRANDS SEED KARNA
    // ===================================================================
    private Brand seedSingleBrand(String name, String slug, String logo, String description) {
        Brand brand = new Brand(name, slug, logo, description);
        return brandRepository.save(brand);
    }

    private Map<String, Brand> seedBrands() {
        Map<String, Brand> brandMap = new HashMap<>();

        brandMap.put("Northline", seedSingleBrand("Northline", "northline",
                "https://api.dicebear.com/9.x/initials/svg?seed=Northline",
                "Performance footwear built for everyday movement."));

        brandMap.put("Loom & Co.", seedSingleBrand("Loom & Co.", "loom-co",
                "https://api.dicebear.com/9.x/initials/svg?seed=Loom",
                "Soft, sustainable everyday apparel."));

        brandMap.put("Fieldstone", seedSingleBrand("Fieldstone", "fieldstone",
                "https://api.dicebear.com/9.x/initials/svg?seed=Fieldstone",
                "Durable bags designed for travel and daily carry."));

        brandMap.put("Chronos", seedSingleBrand("Chronos", "chronos",
                "https://api.dicebear.com/9.x/initials/svg?seed=Chronos",
                "Precision timepieces with a minimalist soul."));

        brandMap.put("Verve", seedSingleBrand("Verve", "verve",
                "https://api.dicebear.com/9.x/initials/svg?seed=Verve",
                "Audio gear tuned for clarity and comfort."));

        brandMap.put("Solstice", seedSingleBrand("Solstice", "solstice",
                "https://api.dicebear.com/9.x/initials/svg?seed=Solstice",
                "Eyewear that blends protection with everyday style."));

        return brandMap;
    }

    /// ===================================================================
    // PRODUCTS SEED KARNA — 50 products, 6 categories mein baटे hue
    // ===================================================================
    private void seedProducts(Map<String, Category> categories, Map<String, Brand> brands) {

        Category sneakers = categories.get("Sneakers & Footwear");
        Category apparel = categories.get("Apparel");
        Category bags = categories.get("Bags & Backpacks");
        Category watches = categories.get("Watches");
        Category audio = categories.get("Audio");
        Category eyewear = categories.get("Eyewear");

        Brand northline = brands.get("Northline");
        Brand loomCo = brands.get("Loom & Co.");
        Brand fieldstone = brands.get("Fieldstone");
        Brand chronos = brands.get("Chronos");
        Brand verve = brands.get("Verve");
        Brand solstice = brands.get("Solstice");

        // ===== SNEAKERS & FOOTWEAR (9 products) =====
        createProduct("Northline Strider Runner", "northline-strider-runner",
                "A lightweight everyday runner with responsive cushioning.",
                new BigDecimal("7299"), new BigDecimal("8999"), 30, sneakers, northline,
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
                List.of("UK 7", "UK 8", "UK 9", "UK 10"),
                List.of(new ProductColor("Black", "#000000"), new ProductColor("White", "#FFFFFF")),
                true, false);

        createProduct("Northline Trailblazer High-Top", "northline-trailblazer-high-top",
                "A rugged high-top sneaker built for trail and city.",
                new BigDecimal("6499"), null, 22, sneakers, northline,
                "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800",
                List.of("UK 8", "UK 9", "UK 10"),
                List.of(new ProductColor("Olive", "#556B2F")),
                false, true);

        createProduct("Northline Velocity Racer", "northline-velocity-racer",
                "An aerodynamic racing-inspired sneaker with mesh paneling.",
                new BigDecimal("5999"), new BigDecimal("6999"), 26, sneakers, northline,
                "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=800",
                List.of("UK 6", "UK 7", "UK 8", "UK 9"),
                List.of(new ProductColor("Red", "#C1121F"), new ProductColor("Grey", "#808080")),
                true, false);

        createProduct("Northline Summit Hiker", "northline-summit-hiker",
                "Waterproof hiking boots with reinforced toe protection.",
                new BigDecimal("8499"), null, 18, sneakers, northline,
                "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800",
                List.of("UK 8", "UK 9", "UK 10", "UK 11"),
                List.of(new ProductColor("Brown", "#5C4033")),
                false, false);

        createProduct("Northline Pulse Trainer", "northline-pulse-trainer",
                "A gym-ready cross-trainer with a stability-first sole.",
                new BigDecimal("4999"), new BigDecimal("5999"), 35, sneakers, northline,
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
                List.of("UK 7", "UK 8", "UK 9"),
                List.of(new ProductColor("Navy", "#1B1F3B")),
                false, true);

        createProduct("Northline Cloudwalk Slip-On", "northline-cloudwalk-slip-on",
                "A featherlight slip-on sneaker for effortless everyday wear.",
                new BigDecimal("3999"), null, 40, sneakers, northline,
                "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800",
                List.of("UK 6", "UK 7", "UK 8", "UK 9", "UK 10"),
                List.of(new ProductColor("Beige", "#E8DCC5"), new ProductColor("Black", "#000000")),
                true, false);

        createProduct("Northline Ridge Canvas Sneaker", "northline-ridge-canvas-sneaker",
                "A classic canvas sneaker with a vulcanized rubber sole.",
                new BigDecimal("2999"), new BigDecimal("3499"), 50, sneakers, northline,
                "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=800",
                List.of("UK 6", "UK 7", "UK 8", "UK 9"),
                List.of(new ProductColor("White", "#FFFFFF")),
                false, false);

        createProduct("Northline Apex Basketball Hi", "northline-apex-basketball-hi",
                "High-top basketball shoe with ankle support and grip soles.",
                new BigDecimal("7999"), null, 15, sneakers, northline,
                "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800",
                List.of("UK 8", "UK 9", "UK 10", "UK 11"),
                List.of(new ProductColor("Black/Red", "#1A1A1A")),
                true, true);

        createProduct("Northline Coastal Sandal", "northline-coastal-sandal",
                "A sport sandal with adjustable straps and cushioned footbed.",
                new BigDecimal("1999"), null, 45, sneakers, northline,
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
                List.of("UK 7", "UK 8", "UK 9", "UK 10"),
                List.of(new ProductColor("Grey", "#808080"), new ProductColor("Blue", "#1E3A8A")),
                false, false);

        // ===== APPAREL (8 products) =====
        createProduct("Loom & Co. Drift Fleece Hoodie", "loom-co-drift-fleece-hoodie",
                "A heavyweight fleece hoodie with a relaxed fit.",
                new BigDecimal("2199"), new BigDecimal("2799"), 45, apparel, loomCo,
                "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800",
                List.of("S", "M", "L", "XL"),
                List.of(new ProductColor("Charcoal", "#36454F"), new ProductColor("Cream", "#FFFDD0")),
                true, false);

        createProduct("Loom & Co. Essential Crew Tee", "loom-co-essential-crew-tee",
                "A 100% combed-cotton crew-neck tee, cut for a clean fit.",
                new BigDecimal("899"), null, 60, apparel, loomCo,
                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=800",
                List.of("S", "M", "L", "XL"),
                List.of(new ProductColor("White", "#FFFFFF"), new ProductColor("Black", "#000000")),
                false, false);

        createProduct("Loom & Co. Harbor Denim Jacket", "loom-co-harbor-denim-jacket",
                "A washed denim jacket with a relaxed, boxy silhouette.",
                new BigDecimal("3499"), new BigDecimal("3999"), 25, apparel, loomCo,
                "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=800",
                List.of("S", "M", "L"),
                List.of(new ProductColor("Indigo", "#3F5D7D")),
                true, true);

        createProduct("Loom & Co. Weekend Jogger Pants", "loom-co-weekend-jogger-pants",
                "Tapered joggers in brushed cotton-blend fleece.",
                new BigDecimal("1799"), null, 38, apparel, loomCo,
                "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=800",
                List.of("S", "M", "L", "XL"),
                List.of(new ProductColor("Grey", "#808080"), new ProductColor("Black", "#000000")),
                false, false);

        createProduct("Loom & Co. Meadow Linen Shirt", "loom-co-meadow-linen-shirt",
                "A breathable linen-blend shirt, perfect for warm weather.",
                new BigDecimal("1599"), new BigDecimal("1999"), 30, apparel, loomCo,
                "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=800",
                List.of("S", "M", "L", "XL"),
                List.of(new ProductColor("Sky Blue", "#87CEEB"), new ProductColor("White", "#FFFFFF")),
                true, false);

        createProduct("Loom & Co. Ridgeline Puffer Vest", "loom-co-ridgeline-puffer-vest",
                "A packable puffer vest with recycled insulation.",
                new BigDecimal("2999"), null, 20, apparel, loomCo,
                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=800",
                List.of("M", "L", "XL"),
                List.of(new ProductColor("Forest Green", "#228B22")),
                false, true);

        createProduct("Loom & Co. Cambridge Wool Sweater", "loom-co-cambridge-wool-sweater",
                "A merino-wool sweater with a classic crew neckline.",
                new BigDecimal("2799"), new BigDecimal("3299"), 22, apparel, loomCo,
                "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=800",
                List.of("S", "M", "L"),
                List.of(new ProductColor("Camel", "#C19A6B")),
                true, false);

        createProduct("Loom & Co. Flex Training Shorts", "loom-co-flex-training-shorts",
                "Stretch-woven training shorts with a zip pocket.",
                new BigDecimal("1299"), null, 42, apparel, loomCo,
                "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=800",
                List.of("S", "M", "L", "XL"),
                List.of(new ProductColor("Black", "#000000"), new ProductColor("Navy", "#1B1F3B")),
                false, false);

        // ===== BAGS & BACKPACKS (8 products) =====
        createProduct("Fieldstone Atlas 28L Backpack", "fieldstone-atlas-28l-backpack",
                "A weatherproof 28L backpack with a padded laptop sleeve.",
                new BigDecimal("3299"), new BigDecimal("3999"), 18, bags, fieldstone,
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800",
                List.of(),
                List.of(new ProductColor("Forest Green", "#228B22"), new ProductColor("Black", "#000000")),
                true, true);

        createProduct("Fieldstone Voyager Duffel Bag", "fieldstone-voyager-duffel-bag",
                "A rugged canvas duffel bag with reinforced handles.",
                new BigDecimal("2799"), null, 15, bags, fieldstone,
                "https://images.unsplash.com/photo-1547949003-9792a18a2645?w=800",
                List.of(),
                List.of(new ProductColor("Khaki", "#C3B091")),
                false, false);

        createProduct("Fieldstone Commuter Sling Bag", "fieldstone-commuter-sling-bag",
                "A compact crossbody sling with anti-theft zip pockets.",
                new BigDecimal("1599"), new BigDecimal("1899"), 30, bags, fieldstone,
                "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=800",
                List.of(),
                List.of(new ProductColor("Black", "#000000")),
                true, false);

        createProduct("Fieldstone Basecamp 45L Travel Pack", "fieldstone-basecamp-45l-travel-pack",
                "A carry-on-friendly travel backpack with a clamshell opening.",
                new BigDecimal("4999"), new BigDecimal("5999"), 12, bags, fieldstone,
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800",
                List.of(),
                List.of(new ProductColor("Charcoal", "#36454F")),
                false, true);

        createProduct("Fieldstone Everyday Tote", "fieldstone-everyday-tote",
                "A durable canvas tote with leather-trimmed handles.",
                new BigDecimal("1299"), null, 35, bags, fieldstone,
                "https://images.unsplash.com/photo-1547949003-9792a18a2645?w=800",
                List.of(),
                List.of(new ProductColor("Natural", "#F5F0E6")),
                false, false);

        createProduct("Fieldstone Trail Waist Pack", "fieldstone-trail-waist-pack",
                "A lightweight waist pack for running and hiking essentials.",
                new BigDecimal("999"), new BigDecimal("1299"), 40, bags, fieldstone,
                "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=800",
                List.of(),
                List.of(new ProductColor("Grey", "#808080")),
                true, false);

        createProduct("Fieldstone Metro Laptop Messenger", "fieldstone-metro-laptop-messenger",
                "A slim messenger bag with a padded 15-inch laptop compartment.",
                new BigDecimal("2499"), null, 20, bags, fieldstone,
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800",
                List.of(),
                List.of(new ProductColor("Brown", "#5C4033")),
                false, false);

        createProduct("Fieldstone Alpine Rolltop Backpack", "fieldstone-alpine-rolltop-backpack",
                "A roll-top backpack with a water-resistant tarpaulin shell.",
                new BigDecimal("3799"), new BigDecimal("4299"), 16, bags, fieldstone,
                "https://images.unsplash.com/photo-1547949003-9792a18a2645?w=800",
                List.of(),
                List.of(new ProductColor("Mustard", "#D4A017")),
                true, true);

        // ===== WATCHES (8 products) =====
        createProduct("Chronos Meridian Chronograph Watch", "chronos-meridian-chronograph-watch",
                "A stainless-steel chronograph with a sapphire crystal face.",
                new BigDecimal("8999"), new BigDecimal("10999"), 12, watches, chronos,
                "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800",
                List.of(),
                List.of(new ProductColor("Brown Strap", "#5C4033")),
                true, false);

        createProduct("Chronos Aurora Minimalist Watch", "chronos-aurora-minimalist-watch",
                "A slim-profile minimalist watch with a mesh strap.",
                new BigDecimal("5499"), null, 20, watches, chronos,
                "https://images.unsplash.com/photo-1495856458515-0637185db551?w=800",
                List.of(),
                List.of(new ProductColor("Silver", "#C0C0C0")),
                false, false);

        createProduct("Chronos Voyager Dive Watch", "chronos-voyager-dive-watch",
                "A 200m water-resistant dive watch with a rotating bezel.",
                new BigDecimal("11999"), new BigDecimal("13999"), 8, watches, chronos,
                "https://images.unsplash.com/photo-1523170335258-f5ed11844a49?w=800",
                List.of(),
                List.of(new ProductColor("Black", "#000000")),
                true, true);

        createProduct("Chronos Heritage Automatic Watch", "chronos-heritage-automatic-watch",
                "A self-winding automatic movement with an exhibition caseback.",
                new BigDecimal("14999"), null, 6, watches, chronos,
                "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800",
                List.of(),
                List.of(new ProductColor("Gold", "#D4AF37")),
                false, false);

        createProduct("Chronos Pulse Smartwatch Hybrid", "chronos-pulse-smartwatch-hybrid",
                "An analog-hybrid smartwatch with activity tracking.",
                new BigDecimal("6999"), new BigDecimal("7999"), 18, watches, chronos,
                "https://images.unsplash.com/photo-1495856458515-0637185db551?w=800",
                List.of(),
                List.of(new ProductColor("Black", "#000000"), new ProductColor("Rose Gold", "#B76E79")),
                true, false);

        createProduct("Chronos Everyday Field Watch", "chronos-everyday-field-watch",
                "A rugged field watch with luminous hands and a canvas strap.",
                new BigDecimal("4499"), null, 25, watches, chronos,
                "https://images.unsplash.com/photo-1523170335258-f5ed11844a49?w=800",
                List.of(),
                List.of(new ProductColor("Olive Strap", "#556B2F")),
                false, false);

        createProduct("Chronos Skyline Dress Watch", "chronos-skyline-dress-watch",
                "An ultra-thin dress watch designed for formal occasions.",
                new BigDecimal("9499"), new BigDecimal("10499"), 10, watches, chronos,
                "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800",
                List.of(),
                List.of(new ProductColor("White Face", "#FFFFFF")),
                true, false);

        createProduct("Chronos Terra Rugged Chrono", "chronos-terra-rugged-chrono",
                "A tool-watch styled chronograph with a rubber strap.",
                new BigDecimal("7499"), null, 14, watches, chronos,
                "https://images.unsplash.com/photo-1495856458515-0637185db551?w=800",
                List.of(),
                List.of(new ProductColor("Black/Orange", "#1A1A1A")),
                false, true);

        // ===== AUDIO (9 products) =====
        createProduct("Verve Aria ANC Headphones", "verve-aria-anc-headphones",
                "Over-ear headphones with active noise cancellation.",
                new BigDecimal("8999"), new BigDecimal("10999"), 25, audio, verve,
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800",
                List.of(),
                List.of(new ProductColor("Black", "#000000")),
                true, true);

        createProduct("Verve Pulse True Wireless Earbuds", "verve-pulse-true-wireless-earbuds",
                "Compact true-wireless earbuds with punchy bass.",
                new BigDecimal("3999"), null, 40, audio, verve,
                "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800",
                List.of(),
                List.of(new ProductColor("White", "#FFFFFF"), new ProductColor("Black", "#000000")),
                false, true);

        createProduct("Verve Boom Portable Speaker", "verve-boom-portable-speaker",
                "A waterproof Bluetooth speaker with 20-hour playtime.",
                new BigDecimal("4499"), new BigDecimal("5299"), 30, audio, verve,
                "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800",
                List.of(),
                List.of(new ProductColor("Blue", "#1E3A8A"), new ProductColor("Red", "#C1121F")),
                true, false);

        createProduct("Verve Studio Wired Headphones", "verve-studio-wired-headphones",
                "Reference-tuned wired headphones for studio monitoring.",
                new BigDecimal("5999"), null, 16, audio, verve,
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800",
                List.of(),
                List.of(new ProductColor("Black", "#000000")),
                false, false);

        createProduct("Verve Echo Sport Earbuds", "verve-echo-sport-earbuds",
                "Sweat-resistant sport earbuds with ear-hook stability.",
                new BigDecimal("2999"), new BigDecimal("3499"), 35, audio, verve,
                "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800",
                List.of(),
                List.of(new ProductColor("Green", "#228B22")),
                true, false);

        createProduct("Verve Mini Bluetooth Speaker", "verve-mini-bluetooth-speaker",
                "A pocket-sized speaker with surprisingly deep bass.",
                new BigDecimal("1999"), null, 45, audio, verve,
                "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800",
                List.of(),
                List.of(new ProductColor("Yellow", "#F4C430"), new ProductColor("Black", "#000000")),
                false, false);

        createProduct("Verve Glow LED Party Speaker", "verve-glow-led-party-speaker",
                "A party speaker with sync-to-music LED lighting.",
                new BigDecimal("6499"), new BigDecimal("7499"), 12, audio, verve,
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800",
                List.of(),
                List.of(new ProductColor("Black", "#000000")),
                true, true);

        createProduct("Verve Clarity Gaming Headset", "verve-clarity-gaming-headset",
                "A gaming headset with a detachable noise-cancelling mic.",
                new BigDecimal("4999"), null, 20, audio, verve,
                "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800",
                List.of(),
                List.of(new ProductColor("Black/Red", "#1A1A1A")),
                false, false);

        createProduct("Verve Home Theater Soundbar", "verve-home-theater-soundbar",
                "A compact soundbar with virtual surround sound.",
                new BigDecimal("9999"), new BigDecimal("11999"), 10, audio, verve,
                "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800",
                List.of(),
                List.of(new ProductColor("Black", "#000000")),
                true, false);

        // ===== EYEWEAR (8 products) =====
        createProduct("Solstice Horizon Sunglasses", "solstice-horizon-sunglasses",
                "Polarized UV400 sunglasses with a lightweight acetate frame.",
                new BigDecimal("2499"), null, 28, eyewear, solstice,
                "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800",
                List.of(),
                List.of(new ProductColor("Tortoise", "#8B5A2B")),
                true, false);

        createProduct("Solstice Drift Polarized Shades", "solstice-drift-polarized-shades",
                "Sporty wraparound sunglasses for outdoor activity.",
                new BigDecimal("2999"), new BigDecimal("3499"), 20, eyewear, solstice,
                "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800",
                List.of(),
                List.of(new ProductColor("Matte Black", "#28282B")),
                false, false);

        createProduct("Solstice Aviator Classic", "solstice-aviator-classic",
                "A timeless aviator silhouette with gradient lenses.",
                new BigDecimal("2199"), null, 32, eyewear, solstice,
                "https://images.unsplash.com/photo-1508296695146-257a814070b4?w=800",
                List.of(),
                List.of(new ProductColor("Gold", "#D4AF37")),
                true, true);

        createProduct("Solstice Round Retro Frames", "solstice-round-retro-frames",
                "Round acetate frames inspired by 70s retro styling.",
                new BigDecimal("1899"), new BigDecimal("2299"), 25, eyewear, solstice,
                "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800",
                List.of(),
                List.of(new ProductColor("Amber", "#FFBF00")),
                false, false);

        createProduct("Solstice Blue-Light Reading Glasses", "solstice-blue-light-reading-glasses",
                "Blue-light-filtering lenses for screen-heavy days.",
                new BigDecimal("1499"), null, 40, eyewear, solstice,
                "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800",
                List.of(),
                List.of(new ProductColor("Black", "#000000"), new ProductColor("Clear", "#F5F5F5")),
                true, false);

        createProduct("Solstice Cat-Eye Sunglasses", "solstice-cat-eye-sunglasses",
                "A bold cat-eye frame with polarized UV protection.",
                new BigDecimal("2699"), new BigDecimal("3199"), 18, eyewear, solstice,
                "https://images.unsplash.com/photo-1508296695146-257a814070b4?w=800",
                List.of(),
                List.of(new ProductColor("Black", "#000000")),
                false, true);

        createProduct("Solstice Titanium Rimless Frames", "solstice-titanium-rimless-frames",
                "Ultra-light titanium rimless frames for all-day comfort.",
                new BigDecimal("3499"), null, 14, eyewear, solstice,
                "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800",
                List.of(),
                List.of(new ProductColor("Silver", "#C0C0C0")),
                true, false);

        createProduct("Solstice Kids UV Protection Shades", "solstice-kids-uv-protection-shades",
                "Durable, flexible sunglasses designed for active kids.",
                new BigDecimal("999"), new BigDecimal("1299"), 30, eyewear, solstice,
                "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800",
                List.of(),
                List.of(new ProductColor("Blue", "#1E3A8A"), new ProductColor("Pink", "#FF69B4")),
                false, false);
    }

    // ===================================================================
    // PRIVATE HELPER — ek Product banake save karta hai
    // ===================================================================
    // Ye method upar wale 12 calls mein REPEAT hone wali logic (Product
    // object banana, image attach karna, sizes/colors set karna, save
    // karna) ko EK JAGAH rakhta hai — DRY principle (Don't Repeat Yourself),
    // jo humne pehle bhi kayi jagah follow kiya hai.
    private void createProduct(String title, String slug, String description,
                                BigDecimal price, BigDecimal compareAtPrice, int stock,
                                Category category, Brand brand, String imageUrl,
                                List<String> sizes, List<ProductColor> colors,
                                boolean isNew, boolean isBestSeller) {

        Product product = new Product(slug, title, description, price, stock, brand, category);
        product.setCompareAtPrice(compareAtPrice);
        product.setNew(isNew);
        product.setBestSeller(isBestSeller);

        // Sizes list khaali ho sakti hai (jaise watches/bags ke liye
        // size-selection nahi hoti) — List.of() (khaali list) bhi
        // bilkul valid hai yahan, koi error nahi aayega
        product.setSizes(new ArrayList<>(sizes));
        product.setColors(new ArrayList<>(colors));

        // Ek image attach karte hain — ProductImage ka constructor
        // "product" reference bhi maangta hai (ManyToOne relationship
        // ki wajah se, Phase B2 mein discuss kiya tha)
        List<ProductImage> images = new ArrayList<>();
        images.add(new ProductImage(imageUrl, title, product));
        product.setImages(images);

        // save() ek hi call mein Product + uski image save kar dega
        // (cascade=ALL ki wajah se, Phase B2 mein Product Entity mein set kiya tha)
        productRepository.save(product);
    }
}
