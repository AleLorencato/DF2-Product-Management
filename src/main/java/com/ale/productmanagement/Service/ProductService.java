package com.ale.productmanagement.Service;

import com.ale.productmanagement.DTO.ProductDTO;
import com.ale.productmanagement.Entity.Product;
import com.ale.productmanagement.Repository.ProductRepository;
import com.ale.productmanagement.CatalogClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private CatalogClient catalogClient;

    public ProductService(ProductRepository productRepository, CatalogClient catalogClient) {
        this.productRepository = productRepository;
        this.catalogClient = catalogClient;
    }

    public void saveProduct(ProductDTO productDTO){
        ProductDTO catalogResponse = catalogClient.createProduct(productDTO);

        Product product = new Product();
        product.setName(catalogResponse.getName());
        product.setDescription(catalogResponse.getDescription());
        product.setPrice(catalogResponse.getPrice());
        product.setStock(0);
        product.setStatus(true);

        productRepository.save(product);
    }

    public void updateProductStock(Long id, int stock){
        return;
    }

    public List<ProductDTO> getAllProducts(){
        return catalogClient.getAllProducts();

    }


}
