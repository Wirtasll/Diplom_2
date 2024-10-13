import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChangeUserTest {

    public final static String BASE_URI = "https://stellarburgers.nomoreparties.site" ;
    private String name;
    private String email;
    private String password;
    private User user;
    private ApiUser apiUser;
    private String accessToken;

    private final String newName = "Vynayyqw";
    private final String newEmail = "Zaxzzaqwsz@yandex.ru";
    private final String newPassword = "1qazxsw2";
    User changeUser = new User();


    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
        name = "Vanyas";
        email = "Wirtasll@yandex.ru";
        password = "qwertyqwerty";
        user = new User(email, password, name);
        apiUser = new ApiUser();
        ApiUser.postCreateNewUser(user);
        accessToken = ApiUser.checkRequestAuthLogin(user).then().extract().path("accessToken");
    }
    @Test
    @DisplayName("Изменение имени пользователя с авторизацией.")
    public void changeUserNameWithAuthorizationTest() {
        changeUser.setName(newName);
        user.setName(newName);
        Response response = apiUser.sendPatchRequestWithAuthorizationApiAuthUser(changeUser, accessToken);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией.")
    public void changeUserEmailWithAuthorizationTest() {
        changeUser.setName(newEmail);
        user.setEmail(newEmail);
        Response response = apiUser.sendPatchRequestWithAuthorizationApiAuthUser(changeUser, accessToken);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией.")
    public void changeUserPasswordWithAuthorizationTest() {
        changeUser.setPassword(newPassword);
        user.setPassword(newPassword);
        Response response = apiUser.sendPatchRequestWithAuthorizationApiAuthUser(changeUser, accessToken);
        apiUser.checkSuccessResponseAuthUser(response, email, name);
    }

    // Удаление созданного пользователя
    @After
    public void deleteUserTest() {
        if (accessToken != null) {
            apiUser.deleteUser(accessToken);
        }
    }
}
