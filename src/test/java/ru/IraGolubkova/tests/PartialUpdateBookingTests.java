package ru.IraGolubkova.tests;

import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;

public class PartialUpdateBookingTests {
    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void existingBookingPositiveTest() {
        given() //предусловия, подготовка
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body("{\n"
                        + "    \"firstname\" : \"James\",\n"
                        + "    \"lastname\" : \"Brown\",\n"
                        + "}")
                .expect()
                .statusCode(200)
                .body("id", is(CoreMatchers.not(nullValue())))
                .when()  //шаг
                .patch("https://restful-booker.herokuapp.com/booking/id")
                .prettyPeek()
                .then()//проверки
                .statusCode(200);


    }

    @Test
    void bookingWhichIsNotTests() {
        given() //предусловия, подготовка
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body("{\n"
                        + "    \"firstname\" : \"James\",\n"
                        + "    \"lastname\" : \"Brown\",\n"
                        + "}")
                .when()  //шаг
                .patch("https://restful-booker.herokuapp.com/booking/id")
                .prettyPeek()
                .then()//проверки
                .statusCode(200)
                .body("reason", CoreMatchers.equalTo("Bad Request"));


    }
}
