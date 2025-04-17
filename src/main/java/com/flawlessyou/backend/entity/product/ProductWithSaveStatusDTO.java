package com.flawlessyou.backend.entity.product;
public class ProductWithSaveStatusDTO {
    private Product product;
    private boolean isSaved;
    
    public ProductWithSaveStatusDTO(Product product, boolean isSaved) {
        this.product = product;
        this.isSaved = isSaved;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public boolean isSaved() {
        return isSaved;
    }
    public void setSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

   
}