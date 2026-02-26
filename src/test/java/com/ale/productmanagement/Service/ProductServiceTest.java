package com.ale.productmanagement.Service;

import com.ale.productmanagement.CatalogClient;
import com.ale.productmanagement.DTO.ProductDTO;
import com.ale.productmanagement.Entity.Product;
import com.ale.productmanagement.Repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CatalogClient catalogClient;

    @InjectMocks
    private ProductService productService;

    private ProductDTO sampleDTO;
    private ProductDTO catalogResponse;

    @BeforeEach
    void setUp() {
        sampleDTO = new ProductDTO();
        sampleDTO.setName("Notebook");
        sampleDTO.setDescription("Gaming notebook");
        sampleDTO.setPrice(4999.99);
        sampleDTO.setStock(10);

        catalogResponse = new ProductDTO();
        catalogResponse.setId(1L);
        catalogResponse.setName("Notebook");
        catalogResponse.setDescription("Gaming notebook");
        catalogResponse.setPrice(4999.99);
    }

    // --- saveProduct ---

    @Test
    void saveProduct_shouldCallCatalogAndPersistProduct() {
        when(catalogClient.createProduct(any(ProductDTO.class))).thenReturn(catalogResponse);

        productService.saveProduct(sampleDTO);

        verify(catalogClient, times(1)).createProduct(any(ProductDTO.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void saveProduct_withPositiveStock_shouldSetStatusTrue() {
        when(catalogClient.createProduct(any(ProductDTO.class))).thenReturn(catalogResponse);

        productService.saveProduct(sampleDTO); // stock = 10

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertTrue(captor.getValue().isStatus());
    }

    @Test
    void saveProduct_withZeroStock_shouldSetStatusFalse() {
        sampleDTO.setStock(0);
        when(catalogClient.createProduct(any(ProductDTO.class))).thenReturn(catalogResponse);

        productService.saveProduct(sampleDTO);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertFalse(captor.getValue().isStatus());
    }

    @Test
    void saveProduct_shouldSendOnlyBasicFieldsToCatalog() {
        when(catalogClient.createProduct(any(ProductDTO.class))).thenReturn(catalogResponse);

        productService.saveProduct(sampleDTO);

        ArgumentCaptor<ProductDTO> captor = ArgumentCaptor.forClass(ProductDTO.class);
        verify(catalogClient).createProduct(captor.capture());
        ProductDTO sent = captor.getValue();
        assertEquals("Notebook", sent.getName());
        assertEquals("Gaming notebook", sent.getDescription());
        assertEquals(4999.99, sent.getPrice());
    }

    // --- updateProductStock ---

    @Test
    void updateProductStock_shouldFetchFromCatalogAndPersist() {
        when(catalogClient.getProductById(1L)).thenReturn(catalogResponse);

        productService.updateProductStock(1L, 25);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals(25, captor.getValue().getStock());
        assertTrue(captor.getValue().isStatus());
    }

    // --- getAllProducts ---

    @Test
    void getAllProducts_shouldMapProductsToDTO() {
        Product p1 = new Product(1L, "Notebook", "Gaming notebook", 4999.99, 10, true);
        Product p2 = new Product(2L, "Mouse", "Wireless mouse", 199.99, 0, false);
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Notebook", result.get(0).getName());
        assertEquals(10, result.get(0).getStock());
        assertTrue(result.get(0).getStatus());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Mouse", result.get(1).getName());
        assertEquals(0, result.get(1).getStock());
        assertFalse(result.get(1).getStatus());
    }

    @Test
    void getAllProducts_whenRepositoryIsEmpty_shouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

