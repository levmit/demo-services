package com.microservice.test.test_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.test.test_app.entity.Product;

/**
 * Spring Data JPA repository to connect our service bean to data
 */

@Repository 
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
