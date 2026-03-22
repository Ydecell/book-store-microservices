package com.daniil.bookstore.cartservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.daniil.bookstore.cartservice.client.BookClient;
import com.daniil.bookstore.cartservice.dto.book.BookDto;
import com.daniil.bookstore.cartservice.dto.cartitem.CreateCartItemRequestDto;
import com.daniil.bookstore.cartservice.dto.cartitem.UpdateCartItemRequestDto;
import com.daniil.bookstore.cartservice.dto.shoppingcart.ShoppingCartDto;
import com.daniil.bookstore.cartservice.mapper.ShoppingCartMapper;
import com.daniil.bookstore.cartservice.model.CartItem;
import com.daniil.bookstore.cartservice.model.ShoppingCart;
import com.daniil.bookstore.cartservice.repository.cartitem.CartItemRepository;
import com.daniil.bookstore.cartservice.repository.shoppingcart.ShoppingCartRepository;
import com.daniil.bookstore.commonsecurity.exception.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookClient bookClient;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private ShoppingCart shoppingCart;
    private ShoppingCartDto shoppingCartDto;
    private static final Long USER_ID = 100L;

    @BeforeEach
    void setUp() {
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUserId(USER_ID);
        shoppingCart.setCartItems(new HashSet<>());

        shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);
        shoppingCartDto.setUserId(USER_ID);
    }

    @Test
    @DisplayName("getShoppingCartForUser valid userId returns ShoppingCartDto")
    void getShoppingCartForUser_ValidUserId_ReturnsDto() {
        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.getShoppingCartForUser(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
    }

    @Test
    @DisplayName("getShoppingCartForUser invalid userId throws EntityNotFoundException")
    void getShoppingCartForUser_InvalidUserId_ThrowsEntityNotFoundException() {
        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCartForUser(USER_ID));

        assertEquals("Shopping cart not found for user id " + USER_ID, ex.getMessage());
    }

    @Test
    @DisplayName("addItemToCart new item creates cart item and returns cart")
    void addItemToCart_NewItem_CreatesCartItemAndReturnsDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(10L);
        bookDto.setTitle("Dune");
        bookDto.setPrice(new BigDecimal("9.99"));

        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(10L);
        requestDto.setQuantity(1);

        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));
        when(bookClient.getBookById(10L)).thenReturn(bookDto);
        when(cartItemRepository.findByShoppingCartIdAndBookId(1L, 10L))
                .thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.addItemToCart(requestDto, USER_ID);

        assertNotNull(result);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("addItemToCart existing item increments quantity")
    void addItemToCart_ExistingItem_IncrementsQuantity() {
        BookDto bookDto = new BookDto();
        bookDto.setId(10L);
        bookDto.setTitle("Dune");
        bookDto.setPrice(new BigDecimal("9.99"));

        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setBookId(10L);
        existingItem.setQuantity(2);
        existingItem.setShoppingCart(shoppingCart);

        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(10L);
        requestDto.setQuantity(3);

        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));
        when(bookClient.getBookById(10L)).thenReturn(bookDto);
        when(cartItemRepository.findByShoppingCartIdAndBookId(1L, 10L))
                .thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(existingItem)).thenReturn(existingItem);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        shoppingCartService.addItemToCart(requestDto, USER_ID);

        assertEquals(5, existingItem.getQuantity());
    }

    @Test
    @DisplayName("addItemToCart cart not found throws EntityNotFoundException")
    void addItemToCart_CartNotFound_ThrowsEntityNotFoundException() {
        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(10L);
        requestDto.setQuantity(1);

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addItemToCart(requestDto, USER_ID));
    }

    @Test
    @DisplayName("updateCartItem valid ids updates quantity and returns cart")
    void updateCartItem_ValidIds_UpdatesQuantityAndReturnsDto() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(2);
        cartItem.setShoppingCart(shoppingCart);

        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(5);

        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(1L, 1L))
                .thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.updateCartItem(1L, requestDto, USER_ID);

        assertNotNull(result);
        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @Test
    @DisplayName("updateCartItem cart item not found throws EntityNotFoundException")
    void updateCartItem_ItemNotFound_ThrowsEntityNotFoundException() {
        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(99L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItem(99L,
                        new UpdateCartItemRequestDto(), USER_ID));
    }

    @Test
    @DisplayName("removeCartItem valid ids deletes cart item")
    void removeCartItem_ValidIds_DeletesCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(shoppingCart);

        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(1L, 1L))
                .thenReturn(Optional.of(cartItem));

        shoppingCartService.removeCartItem(1L, USER_ID);

        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    @DisplayName("removeCartItem item not found throws EntityNotFoundException")
    void removeCartItem_ItemNotFound_ThrowsEntityNotFoundException() {
        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(99L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.removeCartItem(99L, USER_ID));
    }

    @Test
    @DisplayName("createShoppingCart creates and saves new cart")
    void createShoppingCart_ValidUserId_SavesNewCart() {
        shoppingCartService.createShoppingCart(USER_ID);

        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    @DisplayName("clearShoppingCart removes all items from cart")
    void clearShoppingCart_ValidUserId_DeletesAllItems() {
        CartItem item = new CartItem();
        shoppingCart.getCartItems().add(item);

        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));

        shoppingCartService.clearShoppingCart(USER_ID);

        verify(cartItemRepository, times(1)).deleteAll(shoppingCart.getCartItems());
    }

    @Test
    @DisplayName("clearShoppingCart cart not found throws EntityNotFoundException")
    void clearShoppingCart_CartNotFound_ThrowsEntityNotFoundException() {
        when(shoppingCartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.clearShoppingCart(USER_ID));
    }
}
