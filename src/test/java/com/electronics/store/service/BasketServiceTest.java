package com.electronics.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.electronics.store.exception.InsufficientStockException;
import com.electronics.store.exception.ProductNotFoundException;
import com.electronics.store.model.Basket;
import com.electronics.store.model.BasketItem;
import com.electronics.store.model.Deal;
import com.electronics.store.model.DealType;
import com.electronics.store.model.Product;
import com.electronics.store.model.ProductCategory;
import com.electronics.store.model.Receipt;
import com.electronics.store.model.ReceiptItem;
import com.electronics.store.repository.BasketRepository;
import com.electronics.store.repository.DealRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
  private Product mouse;

  @Mock private ProductService productService;

  @Mock private DealRepository dealRepository;

    @BeforeEach
    void setUp() {
    laptop =
        new Product(1L, "Laptop Pro", ProductCategory.ELECTRONICS, BigDecimal.valueOf(1200.00), 10);

    mouse =
        new Product(
            2L, "Wireless Mouse", ProductCategory.ELECTRONICS, BigDecimal.valueOf(25.00), 50);
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
  @DisplayName("Should add more quantity to an existing product in the basket and decrement stock")
  void addProductToBasket_shouldAddProductToExistingBasketItemAndDecrementStock() {
    // Arrange
    customerBasket.getItems().add(new BasketItem(1L, 9));
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
    assertEquals(12, updatedBasket.getItems().get(0).getQuantity());
    verify(productService, times(1)).decrementProductStock(1L, 3);
    verify(basketRepository, times(1)).save(customerBasket);
  }

  @Test
  @DisplayName(
      "Should throw InsufficientStockException when adding product with insufficient stock")
  void
      addProductToBasket_shouldThrowInsufficientStockExceptionWhenAddingProductWithInsufficientStock() {
    // Arrange
    doThrow(new InsufficientStockException("Not Enough Stock"))
        .when(productService)
        .decrementProductStock(1L, 99);

    // Act
    InsufficientStockException insufficientStockException =
        assertThrows(
            InsufficientStockException.class, () -> basketService.addProductToBasket(1L, 99));

    // Assert
    assertEquals("Not Enough Stock", insufficientStockException.getMessage());
    verify(productService, times(1)).decrementProductStock(1L, 99);
    verify(basketRepository, never()).save(any(Basket.class));
  }

  @Test
  @DisplayName("Should throw ProductNotFoundException when adding a non-existent product")
  void addProductToBasket_shouldThrowProductNotFoundExceptionWhenAddingNonExistentProduct() {
    // Arrange
    doThrow(new ProductNotFoundException("Product with ID 999 not found."))
        .when(productService)
        .decrementProductStock(999L, 3);
    when(basketRepository.save(any(Basket.class))).thenReturn(customerBasket);

    // Act
    ProductNotFoundException productNotFoundException =
        assertThrows(
            ProductNotFoundException.class, () -> basketService.addProductToBasket(999L, 3));

    // Assert
    assertEquals("Product with ID 999 not found.", productNotFoundException.getMessage());
    verify(productService, times(1)).decrementProductStock(999L, 3);
    verify(basketRepository, never()).save(any(Basket.class));
  }

  @Test
  @DisplayName("Should remove a product partially from basket and increment product stock")
  void removeProductFromBasket_shouldRemoveProductPartiallyFromBasketAndIncrementStock() {
    // Arrange
    customerBasket.getItems().add(new BasketItem(1L, 10));
    doNothing().when(productService).decrementProductStock(1L, 4);
    when(basketRepository.save(any(Basket.class))).thenReturn(customerBasket);

    // Act
    Basket updatedBasket = basketService.removeProductFromBasket(1L, 4);

    // Assert
    assertNotNull(updatedBasket);
    assertEquals(customerBasket.getId(), updatedBasket.getId());
    assertEquals(customerBasket.getUserId(), updatedBasket.getUserId());
    assertEquals(1, updatedBasket.getItems().size());
    assertEquals(1, updatedBasket.getItems().get(0).getProductId());
    assertEquals(6, updatedBasket.getItems().get(0).getQuantity());
    verify(productService, times(1)).incrementProductStock(1L, 4);
    verify(basketRepository, times(1)).save(any(Basket.class));
  }

  @Test
  @DisplayName("Should remove a product entirely from basket and increment all its stock")
  void removeProductFromBasket_shouldRemoveProductEntirelyFromBasketAndIncrementAllStock() {
    // Arrange
    customerBasket.getItems().add(new BasketItem(1L, 10));
    doNothing().when(productService).decrementProductStock(1L, 10);
    when(basketRepository.save(any(Basket.class))).thenReturn(customerBasket);

    // Act
    Basket updatedBasket = basketService.removeProductFromBasket(1L, 10);

    // Assert
    assertNotNull(updatedBasket);
    assertEquals(customerBasket.getId(), updatedBasket.getId());
    assertEquals(customerBasket.getUserId(), updatedBasket.getUserId());
    assertEquals(0, updatedBasket.getItems().size());
    verify(productService, times(1)).incrementProductStock(1L, 10);
    verify(basketRepository, times(1)).save(any(Basket.class));
  }

  @Test
  @DisplayName("Should calculate receipt correctly with no deals applied")
  void calculateReceipt_shouldCalculateReceiptWithNoDealsApplied() {
    // Arrange
    customerBasket.getItems().add(new BasketItem(laptop.getId(), 1));
    customerBasket.getItems().add(new BasketItem(mouse.getId(), 2));
    when(productService.findProductById(1L)).thenReturn(laptop);
    when(productService.findProductById(2L)).thenReturn(mouse);
    when(dealRepository.findByProductId(anyLong())).thenReturn(Optional.empty());

    // Act
    Receipt receipt = basketService.calculateReceipt();

    // Assert
    assertNotNull(receipt);
    assertEquals(2, receipt.getItems().size());
    assertTrue(receipt.getDealsApplied().isEmpty());

    Optional<ReceiptItem> laptopReceiptItemOptional =
        receipt.getItems().stream()
            .filter(item -> item.getProductId().equals(laptop.getId()))
            .findFirst();
    assertTrue(laptopReceiptItemOptional.isPresent());
    assertNull(laptopReceiptItemOptional.get().getDealApplied());

    Optional<ReceiptItem> mouseReceiptItemOptional =
        receipt.getItems().stream()
            .filter(item -> item.getProductId().equals(laptop.getId()))
            .findFirst();
    assertTrue(mouseReceiptItemOptional.isPresent());
    assertNull(mouseReceiptItemOptional.get().getDealApplied());
    assertEquals(BigDecimal.valueOf(1250.0), receipt.getTotalPrice());

    verify(productService, times(2)).findProductById(anyLong());
    verify(dealRepository, times(2)).findByProductId(anyLong());
  }

  @Test
  @DisplayName("Should calculate receipt with BOGO50 deal applied for an even quantity of items")
  void calculateReceipt_shouldCalculateReceiptWithBOGO50DealEvenQuantity() {
    // Arrange
    customerBasket.getItems().add(new BasketItem(laptop.getId(), 2));
    when(productService.findProductById(1L)).thenReturn(laptop);
    Deal bogo50Deal =
            new Deal(1L, laptop.getId(), DealType.BOGO50, LocalDateTime.now().plusHours(7));
    when(dealRepository.findByProductId(1L)).thenReturn(Optional.of(bogo50Deal));

    // Act
    Receipt receipt = basketService.calculateReceipt();

    // Assert
    assertNotNull(receipt);
    assertEquals(1, receipt.getItems().size());
    assertEquals(1, receipt.getDealsApplied().size());
    assertEquals("BOGO50 for Laptop Pro", receipt.getDealsApplied().get(0));

    ReceiptItem laptopItem = receipt.getItems().get(0);
    assertEquals(1L, laptopItem.getProductId());
    assertEquals(2, laptopItem.getQuantity());
    assertEquals(0, laptopItem.getPriceAfterDeal().compareTo(BigDecimal.valueOf(600.00)));
    assertEquals("BOGO50", laptopItem.getDealApplied());
    assertEquals(0, receipt.getTotalPrice().compareTo(BigDecimal.valueOf(1800.00)));

    verify(productService, times(1)).findProductById(anyLong());
    verify(dealRepository, times(1)).findByProductId(anyLong());
  }

  @Test
  @DisplayName("Should calculate receipt with BOGO50 deal applied for an odd quantity of items")
  void calculateReceipt_shouldCalculateReceiptWithBOGO50DealOddQuantity() {
    // Arrange
    customerBasket.getItems().add(new BasketItem(laptop.getId(), 3));
    when(productService.findProductById(1L)).thenReturn(laptop);
    Deal bogo50Deal =
        new Deal(1L, laptop.getId(), DealType.BOGO50, LocalDateTime.now().plusHours(7));
    when(dealRepository.findByProductId(1L)).thenReturn(Optional.of(bogo50Deal));

    // Act
    Receipt receipt = basketService.calculateReceipt();

    // Assert
    assertNotNull(receipt);
    assertEquals(1, receipt.getItems().size());
    assertEquals(1, receipt.getDealsApplied().size());
    assertEquals("BOGO50 for Laptop Pro", receipt.getDealsApplied().get(0));

    ReceiptItem laptopItem = receipt.getItems().get(0);
    assertEquals(1L, laptopItem.getProductId());
    assertEquals(3, laptopItem.getQuantity());
    assertEquals(0, laptopItem.getPriceAfterDeal().compareTo(BigDecimal.valueOf(600.00)));
    assertEquals("BOGO50", laptopItem.getDealApplied());
    assertEquals(0, receipt.getTotalPrice().compareTo(BigDecimal.valueOf(3000.00)));

    verify(productService, times(1)).findProductById(anyLong());
    verify(dealRepository, times(1)).findByProductId(anyLong());
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