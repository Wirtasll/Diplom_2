import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CreationUserTest {

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
    @DisplayName("Проверка создания уникального пользователя.")
    public void createUserTest() {
        user = new User(email, password, name);
        Response response = ApiUser.postCreateNewUser(user);
        response.then().log().all().assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("user.email", Matchers.notNullValue())
                .and().body("user.name", Matchers.notNullValue())
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue());

    }
    @Test
    @DisplayName("Проверка создания пользователя, который уже зарегистрирован.")
    public void registeredUserTest() {
        user = new User(email, password, name);
        ApiUser.postCreateNewUser(user);
        Response response = ApiUser.postCreateNewUser(user);
        response.then().log().all()
                .assertThat().statusCode(403).and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("User already exists"));
    }

    @Test
    @DisplayName("Проверка создания пользователя без одного из обязательных полей.")
    public void createUserWithoutNameTest() {
        user.setEmail(email);
        user.setPassword(password);
        Response response = ApiUser.postCreateNewUser(user);
        ApiUser.failedResponseRegister(response);
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
