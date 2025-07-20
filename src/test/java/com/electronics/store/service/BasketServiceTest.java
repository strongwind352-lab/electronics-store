package com.electronics.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.electronics.store.model.Basket;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.repository.BasketRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BasketServiceTest {

  private static final String CUSTOMER_USER_ID = "customer";
  @InjectMocks private BasketService basketService;
  @Mock private BasketRepository basketRepository;
  private Basket customerBasket;
  private Product laptop;

  @Mock private ProductService productService;

    @BeforeEach
    void setUp() {
    laptop =
        new Product(1L, "Laptop Pro", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.00), 10);
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    Authentication authentication = mock(Authentication.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn(CUSTOMER_USER_ID);
    customerBasket = new Basket(1L, CUSTOMER_USER_ID, new ArrayList<>());
    when(basketRepository.findByUserId(CUSTOMER_USER_ID)).thenReturn(Optional.of(customerBasket));
  }

  @Test
  @DisplayName("Should retrieve an existing basket for the authenticated user")
  void getOrCreateBasket_shouldGetExistingBasketForAuthenticatedUser() {
    Basket basket = basketService.getOrCreateBasket();
    assertNotNull(basket);
    assertEquals(customerBasket.getId(), basket.getId());
    assertEquals(customerBasket.getUserId(), basket.getUserId());
    verify(basketRepository, times(1)).findByUserId(CUSTOMER_USER_ID);
    verify(basketRepository, never()).save(customerBasket);
  }

  @Test
  @DisplayName("Should create a new basket if none exists for the authenticated user")
  void getOrCreateBasket_shouldCreateNewBasketIfNoneExistsForAuthenticatedUser() {
    // Arrange
    when(basketRepository.findByUserId(CUSTOMER_USER_ID)).thenReturn(Optional.empty());
    Basket newBasket = new Basket(2L, CUSTOMER_USER_ID, new ArrayList<>());
    when(basketRepository.save(ArgumentMatchers.any(Basket.class))).thenReturn(newBasket);

    // Act
    Basket createdBasket = basketService.getOrCreateBasket();

    // Assert
    assertNotNull(createdBasket);
    assertEquals(newBasket.getId(), createdBasket.getId());
    assertEquals(newBasket.getUserId(), createdBasket.getUserId());
    verify(basketRepository, times(1)).findByUserId(CUSTOMER_USER_ID);
    verify(basketRepository, times(1)).save(any(Basket.class));
  }

  @Test
  @DisplayName("Should throw IllegalStateException if no authenticated user for basket operations")
  void getOrCreateBasket_shouldThrowIllegalStateExceptionIfNoAuthenticatedUser() {
    // Arrange
    SecurityContext unauthenticatedSecurityContext = mock(SecurityContext.class);
    SecurityContextHolder.clearContext();
    SecurityContextHolder.setContext(unauthenticatedSecurityContext);
    Authentication unauthenticated = mock(Authentication.class);
    when(unauthenticatedSecurityContext.getAuthentication()).thenReturn(unauthenticated);
    when(unauthenticated.isAuthenticated()).thenReturn(false);

    // Act
    IllegalStateException illegalStateException =
        assertThrows(IllegalStateException.class, () -> basketService.getOrCreateBasket());

    // Assert
    assertEquals("Authentication required", illegalStateException.getMessage());
    verify(unauthenticated, never()).getName();
    verify(basketRepository, never()).findByUserId(any(String.class));
    verify(basketRepository, never()).save(any(Basket.class));
  }

  @Test
  @DisplayName("Should add a product to an empty basket and decrement product stock")
  void addProductToBasket_shouldAddProductToEmptyBasketAndDecrementStock() {
    // Arrange
    doNothing().when(productService).decrementProductStock(1L, 3);
    when(basketRepository.save(any(Basket.class))).thenReturn(customerBasket);

    // Act
    Basket updatedBasket = basketService.addProductToBasket(1L, 3);

    // Assert
    assertNotNull(updatedBasket);
    assertEquals(customerBasket.getId(), updatedBasket.getId());
    assertEquals(customerBasket.getUserId(), updatedBasket.getUserId());
    assertEquals(1, updatedBasket.getItems().size());
    assertEquals(1, updatedBasket.getItems().get(0).getProductId());
    assertEquals(3, updatedBasket.getItems().get(0).getQuantity());
    verify(productService, times(1)).decrementProductStock(1L, 3);
    verify(basketRepository, times(1)).save(customerBasket);
  }

    @Test
    void addProductToBasket() {
      }

    @Test
    void removeProductFromBasket() {
      }

    @Test
    void calculateReceipt() {
      }
}