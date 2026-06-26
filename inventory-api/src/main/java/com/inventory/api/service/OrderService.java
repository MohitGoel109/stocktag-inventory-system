package com.inventory.api.service;

import com.inventory.api.dto.OrderDto;
import com.inventory.api.dto.OrderItemDto;
import com.inventory.api.entity.Customer;
import com.inventory.api.entity.Order;
import com.inventory.api.entity.OrderItem;
import com.inventory.api.entity.Product;
import com.inventory.api.exception.BadRequestException;
import com.inventory.api.exception.NotFoundException;
import com.inventory.api.repository.CustomerRepository;
import com.inventory.api.repository.OrderRepository;
import com.inventory.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public List<OrderDto> findAll() {
        return orderRepository.findAll().stream().map(this::toDto).toList();
    }

    public OrderDto findById(Long id) {
        return toDto(getEntity(id));
    }

    /**
     * Places an order: validates stock for every line item up front, then
     * atomically deducts stock and persists the order with its line items.
     * If any item has insufficient stock, the whole order is rejected —
     * the original system deducted stock row-by-row with no rollback safety,
     * which could leave inventory in a corrupted partial state.
     */
    @Transactional
    public OrderDto placeOrder(OrderDto dto) {
        if (dto.items() == null || dto.items().isEmpty()) {
            throw new BadRequestException("An order must contain at least one product");
        }

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found with id " + dto.customerId()));

        // Pass 1: validate all products exist and have enough stock before changing anything.
        List<Product> lockedProducts = dto.items().stream().map(item -> {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new NotFoundException("Product not found with id " + item.productId()));
            if (item.quantity() == null || item.quantity() <= 0) {
                throw new BadRequestException("Quantity for '" + product.getName() + "' must be greater than zero");
            }
            if (product.getQuantity() < item.quantity()) {
                throw new BadRequestException(
                        "Not enough stock for '" + product.getName() + "' (available: " +
                        product.getQuantity() + ", requested: " + item.quantity() + ")");
            }
            return product;
        }).toList();

        // Pass 2: build the order, deduct stock, compute total.
        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .customer(customer)
                .totalPaid(BigDecimal.ZERO)
                .build();

        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);

        for (int i = 0; i < dto.items().size(); i++) {
            OrderItemDto itemDto = dto.items().get(i);
            Product product = lockedProducts.get(i);

            product.setQuantity(product.getQuantity() - itemDto.quantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDto.quantity())
                    .unitPrice(product.getPrice())
                    .build();
            order.addItem(orderItem);

            total.set(total.get().add(product.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity()))));
        }

        order.setTotalPaid(total.get());
        return toDto(orderRepository.save(order));
    }

    private String generateOrderCode() {
        String datePart = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        return "ORD-" + datePart;
    }

    private Order getEntity(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id " + id));
    }

    private OrderDto toDto(Order o) {
        List<OrderItemDto> items = o.getItems().stream()
                .map(i -> new OrderItemDto(
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getLineTotal()))
                .toList();

        return new OrderDto(
                o.getId(),
                o.getOrderCode(),
                o.getCustomer().getId(),
                o.getCustomer().getName(),
                o.getOrderDate(),
                o.getTotalPaid(),
                items
        );
    }
}
