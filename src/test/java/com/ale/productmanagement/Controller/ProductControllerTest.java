package com.ale.productmanagement.Controller;

import com.ale.productmanagement.DTO.ProductDTO;
import com.ale.productmanagement.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private static final String BASE_URL = "/productInventory/productManagement/v1/products";

    @Test
    void getProducts_shouldReturn200WithList() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setId(1L);
        dto.setName("Notebook");
        dto.setPrice(4999.99);
        dto.setStock(10);
        dto.setStatus(true);

        when(productService.getAllProducts()).thenReturn(List.of(dto));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Notebook"))
                .andExpect(jsonPath("$[0].stock").value(10));
    }

    @Test
    void createProduct_shouldReturn201() throws Exception {
        String json = """
                {"name":"Notebook","description":"Gaming notebook","price":4999.99,"stock":10,"status":false}
                """;

        doNothing().when(productService).saveProduct(any(ProductDTO.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(productService, times(1)).saveProduct(any(ProductDTO.class));
    }

    @Test
    void updateProductStock_shouldReturn200() throws Exception {
        doNothing().when(productService).updateProductStock(eq(1L), eq(20));

        mockMvc.perform(put(BASE_URL + "/inventory/1")
                        .param("stock", "20"))
                .andExpect(status().isOk());

        verify(productService, times(1)).updateProductStock(1L, 20);
    }
}



