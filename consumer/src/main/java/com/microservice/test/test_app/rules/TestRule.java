package com.microservice.test.test_app.rules;

import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;

@Rule(order = 2)
public class TestRule {
	@Given("world")
	private String world;

	@Result
	private String result;

	@When
	public boolean when() {
		return world != null;
	}

	@Then
	public void then() {
		result += world;
	}
}
