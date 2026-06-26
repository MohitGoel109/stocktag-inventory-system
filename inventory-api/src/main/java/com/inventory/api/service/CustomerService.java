package com.inventory.api.service;

import com.inventory.api.dto.CustomerDto;
import com.inventory.api.entity.Customer;
import com.inventory.api.exception.ConflictException;
import com.inventory.api.exception.NotFoundException;
import com.inventory.api.repository.CustomerRepository;
import com.inventory.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<CustomerDto> search(String term) {
        if (term == null || term.isBlank()) {
            return findAll();
        }
        return customerRepository.searchByName(term.trim()).stream().map(this::toDto).toList();
    }

    public CustomerDto findById(Long id) {
        return toDto(getEntity(id));
    }

    @Transactional
    public CustomerDto create(CustomerDto dto) {
        Customer customer = Customer.builder()
                .name(dto.name().trim())
                .mobileNumber(dto.mobileNumber())
                .email(dto.email())
                .build();
        return toDto(customerRepository.save(customer));
    }

    @Transactional
    public CustomerDto update(Long id, CustomerDto dto) {
        Customer customer = getEntity(id);
        customer.setName(dto.name().trim());
        customer.setMobileNumber(dto.mobileNumber());
        customer.setEmail(dto.email());
        return toDto(customerRepository.save(customer));
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = getEntity(id);
        long orderCount = orderRepository.countByCustomerId(id);
        if (orderCount > 0) {
            throw new ConflictException(
                    "Cannot delete '" + customer.getName() + "' — they have " + orderCount +
                    " order(s) on record.");
        }
        customerRepository.delete(customer);
    }

    private Customer getEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id " + id));
    }

    private CustomerDto toDto(Customer c) {
        return new CustomerDto(c.getId(), c.getName(), c.getMobileNumber(), c.getEmail());
    }
}
