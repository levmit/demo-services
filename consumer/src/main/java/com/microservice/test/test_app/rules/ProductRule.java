package com.microservice.test.test_app.rules;

import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;

@Rule(order = 1)
public class ProductRule {
	@Given("hello")
	private String hello;

	@Result
	private String result;

	@When
	public boolean when() {
		return hello != null;
	}

	@Then
	public void then() {
		result = hello + " ";
	}
}