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
        ProductDTO catalogRequest = new ProductDTO();
        catalogRequest.setName(productDTO.getName());
        catalogRequest.setDescription(productDTO.getDescription());
        catalogRequest.setPrice(productDTO.getPrice());

        ProductDTO catalogResponse = catalogClient.createProduct(catalogRequest);

        Product product = new Product();
        product.setName(catalogResponse.getName());
        product.setDescription(catalogResponse.getDescription());
        product.setPrice(catalogResponse.getPrice());
        product.setStock(productDTO.getStock());
        product.setStatus(productDTO.getStock() > 0);

        productRepository.save(product);
    }

    public void updateProductStock(Long id, int stock){
        ProductDTO catalogResponse = catalogClient.getProductById(id);
        Product product = new Product(catalogResponse.getId(), catalogResponse.getName(), catalogResponse.getDescription(), catalogResponse.getPrice());
        product.setStock(stock);
        product.setStatus(true);
        productRepository.save(product);

    }

    public List<ProductDTO> getAllProducts(){
        return productRepository.findAll().stream().map(product -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setStock(product.getStock());
            dto.setStatus(product.isStatus());
            return dto;
        }).toList();
    }


}
