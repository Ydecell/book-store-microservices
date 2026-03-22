package com.daniil.bookstore.userservice;

import com.daniil.bookstore.userservice.client.CartClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class UserserviceApplicationTests {
	@MockitoBean
	private CartClient cartClient;

	@Test
	void contextLoads() {
	}

}
