package com.daniil.bookstore.cartservice;

import com.daniil.bookstore.cartservice.client.BookClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class CartServiceApplicationTests {
	@MockitoBean
	private BookClient bookClient;

	@Test
	void contextLoads() {
	}

}
