package com.ale.productmanagement.Controller;

import com.ale.productmanagement.DTO.ProductDTO;
import com.ale.productmanagement.Service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productInventory/productManagement/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDTO> getProducts(){
        return productService.getAllProducts();
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ProductDTO dto) {
        productService.saveProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PutMapping("/inventory/{id}")
    public ResponseEntity<Void> updateProductStock(@PathVariable Long id, @RequestParam int stock) {
        productService.updateProductStock(id, stock);
        return ResponseEntity.ok().build();
    }



}
