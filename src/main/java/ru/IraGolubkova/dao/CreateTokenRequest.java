package ru.IraGolubkova.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@With

public class CreateTokenRequest {

    private final String totalprice;
    private final String depositpaid;
    private final String bookingdates;

    public CreateTokenRequest(String username, String password, String lastname, String firstname, String checkin, String checkout, String additionalneeds, String totalprice, String depositpaid, String bookingdates) {
        this.username = username;
        this.password = password;
        this.lastname = lastname;
        this.firstname = firstname;
        this.checkout = checkout;
        this.checkin = checkin;
        this.additionalneeds = additionalneeds;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.bookingdates = bookingdates;
    }
    @JsonProperty("firstname")
    public String firstname;
    @JsonProperty("lastname")
    public String lastname;
    @JsonProperty("additionalneeds")
    public String additionalneeds;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("checkin")
    private String checkin;
    @JsonProperty("checkout")
    private String checkout;


}

/*
{
    "username" : "admin",
    "password" : null
}
как правило, это тоже самое, что
{
    "username" : "admin"
}
 */


