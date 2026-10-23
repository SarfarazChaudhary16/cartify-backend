package com.sac.catify.backend.service;

import java.util.List;

import com.sac.catify.backend.dto.BrandRequest;
import com.sac.catify.backend.dto.BrandResponse;

//Brand se related saare business operations ka "contract".
//Is interface ko Controller layer use karega — Controller ko sirf itna pata
//hoga ki "createBrand", "getAllBrands" waghera methods exist karte hain,
//unka andar ka implementation (BrandServiceImpl) usse matlab nahi.
public interface BrandService {

	
	// Admin > Brands page > "Add Brand" button click hone pe ye method call hoga
    BrandResponse createBrand(BrandRequest request);
    
    // Header ke "Brands" mega-menu mein saari brands dikhani hoti hain,
    // aur Admin BrandsPage ki list bhi isi method se aayegi
    List<BrandResponse> getAllBrands();
    
    // Frontend ka route /brand/:brandSlug hai — jab customer kisi brand page
    // pe jaye, to us slug se Brand entity dhoondhni hai
    BrandResponse getBrandBySlug(String slug);
    
    // Admin > Brands page > kisi brand ka "Edit" (pencil icon) click karne pe
    BrandResponse updateBrand(Long id, BrandRequest request);

    // Admin > Brands page > "Delete" (trash icon) click karne pe
    void deleteBrand(Long id);
}
