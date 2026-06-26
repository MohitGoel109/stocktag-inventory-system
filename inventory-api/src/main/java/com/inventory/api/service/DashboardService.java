package com.inventory.api.service;

import com.inventory.api.dto.DashboardStatsDto;
import com.inventory.api.dto.ProductDto;
import com.inventory.api.entity.Order;
import com.inventory.api.repository.CategoryRepository;
import com.inventory.api.repository.CustomerRepository;
import com.inventory.api.repository.OrderRepository;
import com.inventory.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;

    public DashboardStatsDto getStats() {
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .map(Order::getTotalPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ProductDto> lowStock = productService.findLowStock();

        return new DashboardStatsDto(
                productRepository.count(),
                categoryRepository.count(),
                customerRepository.count(),
                orderRepository.count(),
                totalRevenue,
                lowStock
        );
    }
}
