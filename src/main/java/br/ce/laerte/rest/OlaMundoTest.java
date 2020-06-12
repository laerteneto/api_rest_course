package br.ce.laerte.rest;

import static io.restassured.RestAssured.*;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundoTest {

	@Test
	public void testOlaMundo() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me:80/ola");
		Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		Assert.assertEquals(response.statusCode(), 200);

		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	}

	@Test
	public void deveConhecerOutrasFormasRestAssured() {
		Response response = request(Method.GET, "http://restapi.wcaquino.me:80/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);

		get("http://restapi.wcaquino.me:80/ola").then().statusCode(200);

		given()
		.when()
			.get("http://restapi.wcaquino.me:80/ola")
		.then()
			.statusCode(200);
	}
	
	@Test
	public void devoConhecerMatchersHamcrest() {
		Assert.assertThat("Maria", Matchers.is("Maria"));
	}
	
	@Test
	public void devoValidarBody() {
		given()
		.when()
			.get("http://restapi.wcaquino.me:80/ola")
		.then()
			.statusCode(200)
			.body(Matchers.is("Ola Mundo!"))
			.body(Matchers.containsString("Mundo!"))
			.body(Matchers.is(Matchers.not(Matchers.nullValue())))
		;
			
	}

}
