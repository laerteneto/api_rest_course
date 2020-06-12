package br.ce.laerte.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;


public class UserXMLTest {
	
	public static RequestSpecification reqSpec;
	public static ResponseSpecification resSpec;
	
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "http://restapi.wcaquino.me";
		// RestAssured.port = 443;
		// RestAssured.basePath = "";
		
		// Padrao das requests
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.log(LogDetail.ALL);
		reqSpec = reqBuilder.build();
		
		// Padrao para resposes
		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectStatusCode(200);
		resSpec = resBuilder.build();
		
		// Aplicando requests e response esperadas para todos os testes
		RestAssured.requestSpecification = reqSpec;
		RestAssured.responseSpecification = resSpec;
		
	}
	
	@Test
	public void devoTrabalharComXML() {
		
		given()
		.when()
			.get("usersXML/3")
		.then()
			//.statusCode(200)
			.body("user.name", is("Ana Julia"))
			.body("user.@id", is("3")) // obtendo user de id 3
			.body("user.filhos.name.size()", is(2))
			.body("user.filhos.name[0]", is("Zezinho"))
			.body("user.filhos.name[1]", is("Luizinho"))
			.body("user.filhos.name", hasItem("Luizinho"))
			.body("user.filhos.name", hasItems("Luizinho", "Zezinho"))
		;
	}
	
	@Test
	public void devoTrabalharComXMLNaRaiz() {
		given()
		.when()
			.get("usersXML/3")
		.then()
			//.statusCode(200)
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3")) // obtendo user de id 3
			
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			
			.detachRootPath("filhos")
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))
			
			.appendRootPath("filhos")
			.body("name", hasItem("Luizinho"))
			.body("name", hasItems("Luizinho", "Zezinho"))
		;
	}
	
	@Test
	public void devoTrabalharComXMLAvancado() {
		given()
		.when()
			.get("usersXML")
		.then()
			.statusCode(200)
			.body("users.user.size()", is(3)) // quantos usuarios na lista
			.body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))	 // pessoas com idade < 25
			.body("users.user.@id", hasItems("1", "2", "3")) // ids disponíveis
			.body("users.user.find{it.age == 25}.name", is("Maria Joaquina")) // nome do user com age=25
			.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))// verificar usuarios que possuem "n" no nome
			.body("user.user.salary.find{it != null}.toDouble()", is(1234.5678d)) // salario como o valor 1234.5678
			.body("user.user.age.collect{it.toInteger() * 2 }", hasItems(40, 50, 60))// multiplicar idades por 2
			.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA")) // nome que retorna com maria utilizando uppercase		
		;
	}
	
	@Test
	public void devoTrabalharComXMLEJava() {
		ArrayList<NodeImpl> nomes = 
			given()
			.when()
				.get("usersXML")
			.then()
				.statusCode(200)
				.extract().path("users.user.name.findAll{it.toString().contains('n')}")		
			;
		Assert.assertEquals(2, nomes.size());
		Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
		Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
	}
	
	@Test
	public void devoTrabalharComXpath() { 
		given()
		.when()
			.get("usersXML")
		.then()
			.statusCode(200)
			.body(hasXPath("count(/users/user)", is("3"))) // verificar numero de users
			
			.body(hasXPath("/users/user[@id=1]")) // ver se o user de id igual a 1 existe
			.body(hasXPath("//user[@id=1]"))      // ver se o user de id igual a 1 existe
			
			.body(hasXPath("//name[text() = 'Luizinho']/../../name", is("Ana Julia")))// retornar o nome da mae do Zezinho
			.body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"),containsString("Luizinho")))) // retornar o nome dos filhos a partir da mae
			
			.body(hasXPath("/users/user/name", is("João da Silva"))) //primeiro nome
			.body(hasXPath("//name", is("João da Silva"))) //primeiro nome
			
			.body(hasXPath("/users/user[2]/name", is("Maria Joaquina"))) //segundo nome
			
			.body(hasXPath("/users/user[last()]/name", is("Ana Julia"))) //ultimo registro
		
			.body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2"))) //nomes q possuem 'n'
			
			.body(hasXPath("//user[age < 24]/name", is("Ana Julia"))) //nome que possue menos de 24 anos
			.body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina"))) //nome que possue mais que 20 anos e menor que 30
		;	
	}
	
}
