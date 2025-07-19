package com.electronics.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Product name must not be blank")
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Product category must be specified")
    private ProductCategory category;

    @NotNull(message = "Price must be specified")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be greater than 0")
    private BigDecimal price;

    @Min(value = 0, message = "Product stock must be positive")
    private int stock;

    public boolean isAvailable() {
        return this.stock >0;
    }
}
