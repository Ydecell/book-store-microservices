package com.daniil.bookstore.bookservice.dto.book;

public record BookSearchParameters(String[] titles, String[] authors) {
}
