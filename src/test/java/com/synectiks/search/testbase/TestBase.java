package com.synectiks.search.testbase;

import org.junit.BeforeClass;

import io.restassured.RestAssured;

public class TestBase {
	@BeforeClass
	public static void init() {
		RestAssured.baseURI = "http://localhost:8092/search";
	}
}
