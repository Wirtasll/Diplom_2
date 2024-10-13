import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class GetOrderTest {


    private String email;
    private String password;
    private String name;
    private ApiUser apiUser;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = ChangeUserTest.BASE_URI;
        name = "Vanyas";
        email = "Wirtasll@yandex.ru";
        password = "qwertyqwerty";
        user = new User(email, password, name);
        apiUser = new ApiUser();
        ApiUser.postCreateNewUser(user);
        accessToken = ApiUser.checkRequestAuthLogin(user).then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Получение списка заказов с авторизацией.")
    public void getUserOrderWithAuthorizationTest() {
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .header("authorization", accessToken)
                .when()
                .get("/api/orders");
        response.then().log().all()
                .assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("orders", Matchers.notNullValue())
                .and().body("total", Matchers.any(Integer.class))
                .and().body("totalToday", Matchers.any(Integer.class));
    }

    @Test
    @DisplayName("Получение списка заказов без авторизации.")
    public void getUserOrderWithoutAuthorizationTest() {
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .when()
                .get("/api/orders");
        response.then().log().all()
                .assertThat().statusCode(401).and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("You should be authorised"));
    }

    // Удаление созданного пользователя
    @After
    public void deleteUserTest() {
        if (accessToken != null) {
            apiUser.deleteUser(accessToken);
        }
    }
}
