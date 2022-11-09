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

		SerenityRest.rest().given().when().get("/list?cls=com.synectiks.search.controllers.Student").then().log().all()
				.statusCode(200);
	}

	@Title("This test get the list of all document under particular index and List of ids")
	@Test
	public void test003() {

		SerenityRest.rest().given().when().get("/getDocs?cls=com.synectiks.search.controllers.Student&ids=101&ids=105")
				.then().log().all().statusCode(200);
	}

	/**
	 * Api to create a new index in elastic if index not exists. Also add the index
	 * mappings for new entity. We can call it to update then existing index
	 * mappings too using isUpdate field.
	 * 
	 * @param cls
	 * @param mappings
	 * @param isUpdate
	 * @return
	 */
	@Title("Api to create a new index in elastic if index not exists.")
	@Test
	public void test004() {

		SerenityRest.rest().given().when()
				.get("/setIndexMapping?cls=com.synectiks.search.controllers.Student&mappings=student2&isUpdate=false")
				.then().log().all().statusCode(200);
	}
	
	
	/**
	 * API to return mappings for entity class.
	 * @param cls
	 * @param fieldsOnly if true then you will get list of fieldnames
	 * @return
	 */
	@Title("API to return mappings for entity class.")
	@Test
	public void test005() {

		SerenityRest.rest().given().when()
				.get("/getIndexMapping?cls=com.synectiks.search.controllers.Student&fieldsOnly=true")
				.then().log().all().statusCode(200);
	}
	
	/**
	 * API endpoint to get list of indexes from Elastic
	 * or entities names from a package.
	 * @param fromElastic send 'true' to get all indexes from elastic
	 * @param pkg set package to search for IESEntity sub classes.
	 * @param json set true if you need pkg response as object with index name and type.
	 * @return List
	 */
	@Title("API endpoint to get list of indexes from Elastic")
	@Test
	public void test006() {

		SerenityRest.rest().given().when()
				.get("/getIndexes")
				.then().log().all().statusCode(200);
	}
	
	
	/**
	 * API to search for elastic query json string
	 * @param elsQuery
	 * @param pageNo
	 * @param pageSize
	 * @return {@code SearchResponse} object
	 */
	@Title("API to search for elastic query json string")
	@Test
	public void test007() {

		SerenityRest.rest().given().when()
				.get("/elsQuery?cls=com.synectiks.search.controllers.Student&query={\r\n"
						+ "    \"bool\": {\r\n"
						+ "      \"filter\": {\r\n"
						+ "        \"range\": {\r\n"
						+ "          \"amount.signed\": { \"lt\": 10 }\r\n"
						+ "        }\r\n"
						+ "      }\r\n"
						+ "    }\r\n"
						+ "  }&notOnlyIds=true&asPolicyRuleRes=false&pageNo=3&pageSize=10")
				.then().log().all().statusCode(200);
	}
	
	
	/**
	 * API {@code /api/v1/search/count} for get count of entities based on
	 * filters provided as json object.
	 * <br/>
	 * For params doc {@see #searchEntities(String, String, int, int)}
	 * <br/>
	 * @return {@code Long} count of entities in {@code ResponseEntity} body
	 */
	@Title("API {@code /api/v1/search/count} for get count of entities based on")
	@Test
	public void test008() {

		SerenityRest.rest().given().when()
				.get("/count?cls=com.synectiks.search.controllers.Student&filters={}")
				.then().log().all().statusCode(200);
	}
	
	/**
	 * API {@code /api/v1/search/aggregateCounts} to get aggregate count of
	 * entities based on filters provided as json object.
	 * @param cls fully qualified name of entity class i.e.
	 * {@code com.girnarsoft.delite.lms.entities.es.TestEnquiry}
	 * @param aggregator json object with aggregation filters<br/>
	 * Aggregator json format:<br/>
	 * <pre>
	 * {
	 * 	<b>"aggreType"</b>: "count | terms | avg | ranges | min | max | sum",
	 * 	<b>"fieldType"</b>: "field-type",
	 * 	<b>"fieldName"</b>: "field-name",
	 * 	"interval": "1d" -- "[\d+][s|m|h|d|w|M|q|y]"
	 * 	"ranges": [{"from": X, "to": Y}], -- only used for numbers
	 * 	"values": ["ABC", "DEF"],
	 * 	"locale": "local-value if any",
	 * 	"format": "value format if needs formated key"
	 * }
	 * </pre>
	 * <br/>
	 * For params doc {@see #searchEntities(String, String, int, int)}
	 * <br/>
	 * @return {@code Map} of aggregation key, doc_count values
	 * in {@code ResponseEntity} body
	 */
	@Title("API {@code /api/v1/search/aggregateCounts} to get aggregate count of")
	@Test
	public void test009() {

		SerenityRest.rest().given().when()
				.get("/aggregateCounts?cls=com.synectiks.search.controllers.Student&filters={}&aggregator={\"aggreType\":\"count\",\"fieldName\":'\\''id'\\''}")
				.then().log().all().statusCode(200);
	}
}
