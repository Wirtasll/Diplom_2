import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginUserTest {

    private String name;
    private String email;
    private String password;
    private User user;
    private ApiUser apiUser;

    @Before
    public void setUp() {
        RestAssured.baseURI = ChangeUserTest.BASE_URI;
        user = new User(email, password, name);
        apiUser = new ApiUser();
        name = "Vanyas";
        email = "Wirtasll@yandex.ru";
        password = "qwertyqwerty";

    }

    @Test
    @DisplayName("Авторизация пользователя под существующим пользователем.")
    public void authorizationTest() {
        user = new User(email, password, name);
        ApiUser.postCreateNewUser(user);
        Response response = ApiUser.checkRequestAuthLogin(user);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().body("user.email", Matchers.notNullValue())
                .and().body("user.name", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным логином.")
    public void authorizationIncorrectLoginTest() {
        user = new User(email, password);
        user.setEmail("1223Qwertyqwertasdqw" + email);
        Response response = ApiUser.checkRequestAuthLogin(user);
        apiUser.failedResponseAuthLogin(response);
    }

    @Test
    @DisplayName("Авторизация с неверным паролем.")
    public void authorizationIncorrectPasswordTest() {
        user = new User(email, password);
        user.setPassword("111111qqqqaxzxdeetzzz" + password);
        Response response = ApiUser.checkRequestAuthLogin(user);
        apiUser.failedResponseAuthLogin(response);
    }


    // Удаление созданного пользователя
    @After
    public void deleteUserTest(){
        String accessToken = ApiUser.checkRequestAuthLogin(user).then().extract().path("accessToken");
        if (accessToken != null) {
            apiUser.deleteUser(accessToken);
        }
    }
}
