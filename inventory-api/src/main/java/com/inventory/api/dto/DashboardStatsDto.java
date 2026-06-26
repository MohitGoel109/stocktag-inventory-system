package com.inventory.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsDto(
        long totalProducts,
        long totalCategories,
        long totalCustomers,
        long totalOrders,
        BigDecimal totalRevenue,
        List<ProductDto> lowStockProducts
) {}
