package ru.IraGolubkova.tests;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;

public class DeleteBookingIdTests {
    static String token;
    String id;

    @BeforeAll
    static void beforeAll() {
        token = given() //предусловия, подготовка
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body("{\n"
                        + "    \"username\" : \"admin\",\n"
                        + "    \"password\" : \"password123\"\n"
                        + "}")
                .expect()
                .statusCode(200)
                .body("token", is(CoreMatchers.not(nullValue())))
                .when()  //шаг
                .post("https://restful-booker.herokuapp.com/auth")//шаг(и)
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
                .body("{\n"
                        + "    \"firstname\" : \"Marat\",\n"
                        + "    \"lastname\" : \"Ivanov\",\n"
                        + "    \"totalprice\" : 555,\n"
                        + "    \"depositpaid\" : true,\n"
                        + "    \"bookingdates\" : {\n"
                        + "        \"checkin\" : \"2018-01-01\",\n"
                        + "        \"checkout\" : \"2019-01-01\"\n"
                        + "    },\n"
                        + "    \"additionalneeds\" : \"Breakfast\"\n"
                        + "}")
                .expect()
                .statusCode(200)
                .when()
                .post("https://restful-booker.herokuapp.com/booking")
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
                .delete("https://restful-booker.herokuapp.com/booking/" + id)
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
                .delete("https://restful-booker.herokuapp.com/booking/" + id)
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
                .delete("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then().statusCode(201);
    }
}


