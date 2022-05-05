package ru.IraGolubkova.tests.lesson34;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static ru.IraGolubkova.tests.lesson34.PartialUpdateBookingTests.requestAccount;

public class DeleteBookingIdTests {
    static String token;
    String id;

    @BeforeAll
    static void beforeAll() {
        byte[] requestAccount = new byte[0];
        token = given() //предусловия, подготовка
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(requestAccount)
                .expect()
                .statusCode(200)
                .body("token", is(CoreMatchers.not(nullValue())))
                .when()  //шаг
                .post("/auth")//шаг(и)
                .prettyPeek()
                .body()
                .jsonPath()
                .get("token")
                .toString();
    }

    @BeforeEach
    void setUp() {
        //создает бронирование
        id = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(requestAccount)
                .expect()
                .statusCode(200)
                .when()
                .post("booking")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("bookingid")
                .toString();
    }

    @Test
    void deleteBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);


    }

    @Test
    void deleteBookingWithoutAuthNegativeTest() {
        given()
                .log()
                .all()
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);

    }

    @Test
    void deletingABookingWithAuthorizationPositiveTest() {
        given()
                .log()
                .all()
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then().statusCode(201);
    }
}


