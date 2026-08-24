package com.example.productservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateProductRequest(
         String name,
         String description,
         BigDecimal price
){
}
