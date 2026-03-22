package com.daniil.bookstore.cartservice.controller;

import com.daniil.bookstore.cartservice.dto.cartitem.CreateCartItemRequestDto;
import com.daniil.bookstore.cartservice.dto.cartitem.UpdateCartItemRequestDto;
import com.daniil.bookstore.cartservice.dto.shoppingcart.ShoppingCartDto;
import com.daniil.bookstore.cartservice.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping Cart", description = "Endpoints for managing the shopping cart")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get the current user's shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shopping cart retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShoppingCartDto.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Shopping cart not found",
                    content = @Content)
    })
    @GetMapping
    public ShoppingCartDto getShoppingCart() {
        Long userId = getCurrentUserId();
        return shoppingCartService.getShoppingCartForUser(userId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Add an item to the shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added to cart",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShoppingCartDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartDto addItemToCart(@Valid @RequestBody CreateCartItemRequestDto requestDto) {
        Long userId = getCurrentUserId();
        return shoppingCartService.addItemToCart(requestDto, userId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Update an item in the shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item updated in cart",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShoppingCartDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Item not found in cart",
                    content = @Content)
    })
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartDto updateCartItem(@PathVariable Long cartItemId,
                                          @Valid @RequestBody UpdateCartItemRequestDto requestDto) {
        Long userId = getCurrentUserId();
        return shoppingCartService.updateCartItem(cartItemId, requestDto, userId);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Remove an item from the shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removed from cart",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Item not found in cart",
                    content = @Content)
    })
    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCartItem(@PathVariable Long cartItemId) {
        Long userId = getCurrentUserId();
        shoppingCartService.removeCartItem(cartItemId, userId);
    }

    @PostMapping("/internal/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createShoppingCart(@PathVariable Long userId) {
        shoppingCartService.createShoppingCart(userId);
    }

    @GetMapping("/internal/{userId}")
    public ShoppingCartDto getShoppingCartInternal(@PathVariable Long userId) {
        return shoppingCartService.getShoppingCartForUser(userId);
    }

    @DeleteMapping("/internal/{userId}/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearShoppingCart(@PathVariable Long userId) {
        shoppingCartService.clearShoppingCart(userId);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
}
