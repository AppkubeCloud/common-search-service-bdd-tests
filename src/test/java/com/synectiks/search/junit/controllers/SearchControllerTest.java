package com.synectiks.search.junit.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import com.synectiks.search.service.entities.Student;
import com.synectiks.search.testbase.TestBase;
import com.synectiks.search.utils.TestUtils;
import com.synectiks.security.entities.Permission;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Manual;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.Title;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SerenityRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearchControllerTest extends TestBase {

	public static long version = TestUtils.getRandomInt();
	public static int st_id = (int) Math.random();
	public static String st_name = "Test Student_" + TestUtils.getRandomString();
	public static float st_fee = 8494.3f;

	@Title("This test add new document in student index name")
	@Test
	public void test001() {
		Student st = new Student();
		st.setId(st_id);
		st.setName(st_name);
		st.setFee(st_fee);
		SerenityRest.rest().given().when().contentType(ContentType.JSON).header(new Header("index_name", "student"))
				.log().all().body(st).post("/saveDocs").then().log().all().statusCode(200);
	}

	@Title("This test get the list of all document under particular index")
	@Test
	public void test002() {

		SerenityRest.rest().given().when().get("/list?cls=com.synectiks.search.controllers.Student")
		.then().log().all()
				.statusCode(200);
	}
	
	@Title("This test get the list of all document under particular index and List of ids")
	@Test
	public void test003() {

		SerenityRest.rest().given().when().get("/getDocs?cls=com.synectiks.search.controllers.Student&ids=101&ids=105")
		.then().log().all()
				.statusCode(200);
	}
}
