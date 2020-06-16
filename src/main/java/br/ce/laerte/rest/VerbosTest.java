package br.ce.laerte.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.http.ContentType;

public class VerbosTest {
	
	@Test
	public void deveSalvarUsuario() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Jose\",\"age\": 50 }")
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Jose"))
			.body("age", is(50))
		;
	}
	
	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{\"age\": 50 }")
	.when()
		.post("http://restapi.wcaquino.me/users")
	.then()
		.log().all()
		.statusCode(400)
		.body("error", is("Name é um atributo obrigatório"))
	;
	}
	
	@Test
	public void deveSalvarUsuarioViaXML() {
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body("<user><name>Jose</name><age>50</age></user>")
		.when()
			.post("http://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Jose"))
			.body("user.age", is("50"))
		;
	}
	
	@Test
	public void deveAlterarUsuario() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario alterado\",\"age\": 80 }")
		.when()
			.put("http://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void devoCustomizarURL() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario alterado\",\"age\": 80 }")
		.when()
			.put("http://restapi.wcaquino.me/{entidade}/{userId}", "users", "1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void devoCustomizarURLParte2() {
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario alterado\",\"age\": 80 }")
			.pathParam("entidade", "users")
			.pathParam("userId", "1")
		.when()
			.put("http://restapi.wcaquino.me/{entidade}/{userId}")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
		;
	}
	
	@Test
	public void deveRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("http://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(204)
		;
	}
	
	@Test
	public void naoDeveRemoverUsuarioInexistente() {
		given()
			.log().all()
		.when()
			.delete("http://restapi.wcaquino.me/users/1000")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
		;
	}
	
	// Para utilizar parametrizando o json, é necessário utilizar a biblioteca GSON
	// A mesma foi adicionada no pom
	@Test
	public void deveSalvarUsuarioMap() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Usuario via map");
		params.put("age", 25);
		
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(params)
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario via map"))
			.body("age", is(25))
		;
	}
	
	@Test
	public void deveSalvarUsuarioObjeto() {
		User user = new User("Usuario via objeto", 35);
		
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Usuario via objeto"))
			.body("age", is(35))
		;
	}
	
	@Test
	public void deveDeserializarObjetoAoSalvarUsuario() {
		User user = new User("Usuario desserializado", 35);
		
		User usuarioInserido = given()
				.log().all()
				.contentType(ContentType.JSON)
				.body(user)
			.when()
				.post("http://restapi.wcaquino.me/users")
			.then()
				.log().all()
				.statusCode(201)
				.extract().body().as(User.class)
			;
		Assert.assertEquals("Usuario desserializado", usuarioInserido.getName());
		Assert.assertThat(usuarioInserido.getAge(), is(35));
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
	}
	
	@Test
	public void deveSalvarUsuarioXMLUsandoObjeto() {
		User user = new User("Usuario XML", 40);
		
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Usuario XML"))
			.body("user.age", is("40"))
		;
	}
	
	@Test
	public void deveSDeserializarXMLAoSalvarUsuario() {
		User user = new User("Usuario XML", 40);
		
		User usuarioInserido = given()
				.log().all()
				.contentType(ContentType.XML)
				.body(user)
			.when()
				.post("http://restapi.wcaquino.me/usersXML")
			.then()
				.log().all()
				.statusCode(201)
				.extract().body().as(User.class)
			;
		System.out.println(usuarioInserido);
		Assert.assertEquals("Usuario XML", usuarioInserido.getName());
		Assert.assertThat(usuarioInserido.getAge(), is(40));
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
	}
	
}