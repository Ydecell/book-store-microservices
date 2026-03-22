package com.daniil.bookstore.cartservice.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookClient bookClient;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCartForUser(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found "
                        + "for user id " + userId));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addItemToCart(CreateCartItemRequestDto requestDto, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found "
                        + "for user id " + userId));

        BookDto book = bookClient.getBookById(requestDto.getBookId());

        CartItem cartItem = cartItemRepository
                .findByShoppingCartIdAndBookId(shoppingCart.getId(), book.getId())
                .orElseGet(() -> {
                    CartItem newCartItem = new CartItem();
                    newCartItem.setShoppingCart(shoppingCart);
                    newCartItem.setBookId(book.getId());
                    newCartItem.setBookTitle(book.getTitle());
                    newCartItem.setPrice(book.getPrice());
                    newCartItem.setQuantity(requestDto.getQuantity());
                    shoppingCart.getCartItems().add(newCartItem);
                    return newCartItem;
                });

        if (cartItem.getId() != null) {
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());
        }

        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    @Transactional
    public ShoppingCartDto updateCartItem(Long cartItemId, UpdateCartItemRequestDto requestDto,
                                          Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found "
                        + "for user id " + userId));

        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found for id "
                        + cartItemId + " and cart id " + cart.getId()));

        cartItem.setQuantity(requestDto.getQuantity());
        return shoppingCartMapper.toDto(cartItemRepository.save(cartItem).getShoppingCart());
    }

    @Override
    @Transactional
    public void removeCartItem(Long cartItemId, Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Shopping cart not found for user id " + userId));

        CartItem cartItem = cartItemRepository
                .findByIdAndShoppingCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cart item not found or does not belong to user"));

        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void clearShoppingCart(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found "
                        + "for user id " + userId));
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
    }

    @Override
    public void createShoppingCart(Long userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCartRepository.save(shoppingCart);
    }
}
