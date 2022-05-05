package ru.IraGolubkova.tests.lesson34;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.IraGolubkova.dao.BookingdatesRequest;
import ru.IraGolubkova.dao.CreateTokenRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static java.lang.String.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class PartialUpdateBookingTests {
    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    private static BookingdatesRequest requestBookingdates;
    static CreateTokenRequest requestAccount;
    static Properties properties = new Properties();
    static SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
    static Faker faker = new Faker();
    static String token;
    String id;


    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        CreateTokenRequest request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");

        requestBookingdates = BookingdatesRequest.builder()
              .checkin(formater.format(valueOf(faker.date().birthday().getDate())))
              .checkout(formater.format(valueOf(faker.date().birthday().getDate())))
               .build();
        requestAccount = CreateTokenRequest.builder()
                .firstname(faker.name().fullName())
                .lastname(faker.name().lastName())
                .totalprice(valueOf(faker.hashCode()))
                .depositpaid(valueOf(true))
                .bookingdates(valueOf(requestBookingdates))
                .additionalneeds(faker.chuckNorris().fact())
                .build();
        token = given() //предусловия, подготовка
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .body("token", is(CoreMatchers.not(nullValue())))
                .when()
                .post("/auth")
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

    @AfterEach
    void tearDown() {
        given()
                .log()
                .all()
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then().statusCode(201);
    }

    @Test
    void changeOfFirstAndLastNameBookingPositiveTest() {
        given() //предусловия, подготовка
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withFirstname("Marat"), (ObjectMapper) requestAccount.withLastname("Ivanov"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Marat"))
                .body("lastname", equalTo("Ivanov"));

    }

    @Test
    void lastNameChangeBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withLastname("Ivanov"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("Ivanov"));
    }

    @Test
    void nameChangeBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withFirstname("Marat"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Marat"));

    }

    @Test
    void changeInTotalCostBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withTotalprice(valueOf(555)))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("totalprice", equalTo(555));

    }

    @Test
    void depositChangeBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withDepositpaid(valueOf(true)))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("depositpaid", equalTo(true));
    }

    @Test
    void additionalneedsChangeBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestAccount.withAdditionalneeds(valueOf(true)))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("additionalneeds", equalTo(true));
    }

    @Test
    void changeOfArrivalAndDepartureDateBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestBookingdates.withCheckin())
                .body(requestBookingdates.withCheckout())
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkin", equalTo("2018-01-01"))
                .body("bookingdates.checkout", equalTo("2019-01-01"));
    }
}