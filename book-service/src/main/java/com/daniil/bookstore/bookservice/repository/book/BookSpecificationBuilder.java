package com.daniil.bookstore.bookservice.repository.book;

import com.daniil.bookstore.bookservice.dto.book.BookSearchParameters;
import com.daniil.bookstore.bookservice.model.Book;
import com.daniil.bookstore.bookservice.repository.SpecificationBuilder;
import com.daniil.bookstore.bookservice.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = (root, query, cb) -> null;
        spec = addSpecifications(spec, "title", searchParameters.titles());
        spec = addSpecifications(spec, "author", searchParameters.authors());
        return spec;
    }

    private Specification<Book> addSpecifications(Specification<Book> spec,
                                                  String field, String[] values) {
        if (values != null && values.length > 0) {
            return spec.and(bookSpecificationProviderManager.getSpecificationProvider(field)
                    .getSpecification(values));
        }
        return spec;
    }
}
