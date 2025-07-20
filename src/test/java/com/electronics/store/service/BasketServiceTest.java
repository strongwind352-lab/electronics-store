package com.electronics.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.electronics.store.model.Basket;
import com.electronics.store.repository.BasketRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

  private static final String CUSTOMER_USER_ID = "customer";
  @InjectMocks private BasketService basketService;
  @Mock private BasketRepository basketRepository;
  private Basket customerBasket;

    @BeforeEach
    void setUp() {
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
    void addProductToBasket() {
      }

    @Test
    void removeProductFromBasket() {
      }

    @Test
    void calculateReceipt() {
      }
}