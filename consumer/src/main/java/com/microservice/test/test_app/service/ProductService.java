package com.microservice.test.test_app.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservice.test.test_app.entity.Product;
import com.microservice.test.test_app.repository.ProductRepository;

@Service
public class ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	private ProductRepository repo;

	public List<Product> getAllProducts() {
		log.debug("Start getAllProducts");
		List<Product> allProducts = repo.findAll();
		log.debug("End getAllProducts. Fetched " +allProducts.size() +" products");

		return allProducts;
	}

}