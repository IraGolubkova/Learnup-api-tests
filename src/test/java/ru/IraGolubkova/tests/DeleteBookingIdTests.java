package ru.IraGolubkova.tests;

import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.IraGolubkova.dao.BookingdatesRequest;
import ru.IraGolubkova.dao.CreateAccountRequest;
import ru.IraGolubkova.dao.CreateTokenRequest;
import ru.IraGolubkova.dao.CreateTokenResponse;
import ru.IraGolubkova.tests.lesson35.BaseTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@Severity(SeverityLevel.BLOCKER)
@Story("delete a booking")
@Feature("Tests for booking delettion")

public class DeleteBookingIdTests extends BaseTest {
    protected static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    protected static CreateTokenRequest requestToken;
    protected static CreateTokenResponse responseToken;
    protected static BookingdatesRequest requestBookingDates;
    protected static CreateAccountRequest requestBooking;
    protected static Properties properties = new Properties();
    protected static Faker faker = new Faker();
    protected String id;


    @BeforeAll
    static void beforeAll() throws IOException {
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");
        requestToken = CreateTokenRequest.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build();

        responseToken = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(requestToken)
                .expect()
                .statusCode(200)
                .when()
                .post("auth")
                .prettyPeek()
                .then()
                .extract()
                .as(CreateTokenResponse.class);
        assertThat(responseToken.getToken().length(), equalTo(15));

        requestBookingDates = BookingdatesRequest.builder()
                .checkin(properties.getProperty("checkin"))
                .checkout(properties.getProperty("checkout"))
                .build();

        requestBooking = CreateAccountRequest.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.hashCode())
                .depositpaid(faker.bool().bool())
                .bookingDates(requestBookingDates)
                .additionalneeds(faker.chuckNorris().fact())
                .build();
    }

    @BeforeEach
    void setUp() {
        id = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(requestBooking)
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
    @Step("Delete en existing booking")
    void deleteBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Cookie", "token=" + responseToken.getToken())
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);


    }

    @Test
    @Step("Deleting a non-existing booking")
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
    @Step("Deleting with authorization")
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