package com.restaurant.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositorySmokeTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    void repositoriesShouldBeLoadedBySpringContext() {
        assertThat(userRepository).isNotNull();
        assertThat(roleRepository).isNotNull();
        assertThat(foodItemRepository).isNotNull();
        assertThat(orderRepository).isNotNull();
        assertThat(paymentRepository).isNotNull();
        assertThat(invoiceRepository).isNotNull();
    }
}
