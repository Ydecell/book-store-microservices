package com.daniil.bookstore.orderservice;

import com.daniil.bookstore.orderservice.client.CartClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class OrderServiceApplicationTests {

	@MockitoBean
	private CartClient cartClient;

	@Test
	void contextLoads() {
	}

}
