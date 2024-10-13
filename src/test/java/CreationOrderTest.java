import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreationOrderTest {

    private String name;
    private String email;
    private String password;
    private ApiUser apiUser;
    private User user;
    private String accessToken;
    private ApiOrder apiOrder;
    private List<String> ingredient;
    private Order order;

    @Before
    public void setUp() {
        RestAssured.baseURI = ChangeUserTest.BASE_URI;
        name = "Vanyas";
        email = "Wirtasll@yandex.ru";
        password = "qwertyqwerty";
        user = new User(email, password, name);
        apiUser = new ApiUser();
        apiOrder = new ApiOrder();
        ApiUser.postCreateNewUser(user);
        accessToken = ApiUser.checkRequestAuthLogin(user).then().extract().path("accessToken");
        ingredient = new ArrayList<>();
        order = new Order(ingredient);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией.")
    public void createOrderWithAuthorizationTest() {
        Ingredients ingredients = apiOrder.getIngredient();
        ingredient.add(ingredients.getData().get(1).get_id());
        ingredient.add(ingredients.getData().get(2).get_id());
        ingredient.add(ingredients.getData().get(3).get_id());
        ingredient.add(ingredients.getData().get(4).get_id());
        Response response = ApiOrder.createOrderWithAuthorization(order, accessToken);
        response.then().log().all()
                .assertThat().statusCode(200).and().body("success", Matchers.is(true))
                .and().body("name", Matchers.notNullValue())
                .and().body("order.number", Matchers.any(Integer.class))
                .and().body("order.ingredients", Matchers.notNullValue())
                .and().body("order._id", Matchers.notNullValue())
                .and().body("order.owner.name", Matchers.is(name))
                .and().body("order.owner.email", Matchers.is(email.toLowerCase(Locale.ROOT)))
                .and().body("order.status", Matchers.is("done"))
                .and().body("order.name", Matchers.notNullValue())
                .and().body("order.price", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации.")
    public void createOrderWithoutAuthorizationTest() {
        Ingredients ingredients = apiOrder.getIngredient();
        ingredient.add(ingredients.getData().get(1).get_id());
        ingredient.add(ingredients.getData().get(2).get_id());
        Response response = ApiOrder.createOrderWithoutAuthorization(order);
        response.then().log().all()
                .assertThat().body("success", Matchers.is(true))
                .and().body("name", Matchers.notNullValue())
                .and().body("order.number", Matchers.any(Integer.class))
                .and().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов с авторизацией.")
    public void createEmptyOrderWithAuthorization() {
        Response response = ApiOrder.createOrderWithAuthorization(order, accessToken);
        apiOrder.checkFailedResponseApiOrders(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов и без авторизации.")
    public void createEmptyOrderWithoutAuthorization() {
        Response response = ApiOrder.createOrderWithoutAuthorization(order);
        apiOrder.checkFailedResponseApiOrders(response);
    }


    @Test
    @DisplayName("Создание заказа без авторизации с неверным хэшем ингредиентов.")
    public void createOrderWithoutAuthorizationWithWrongHashTest() {
        Ingredients ingredients = apiOrder.getIngredient();
        ingredient.add(ingredients.getData().get(0).get_id() + "qwertyqwer");
        ingredient.add(ingredients.getData().get(1).get_id() + "qazzaqwsx");
        Response response = ApiOrder.createOrderWithoutAuthorization(order);
        response.then().log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией с неверным хешем ингредиентов.")
    public void createOrderWithAuthorizationWithWrongHashTest() {
        Ingredients ingredients = apiOrder.getIngredient();
        ingredient.add(ingredients.getData().get(1).get_id() + "qwertqazqw");
        ingredient.add(ingredients.getData().get(2).get_id() + "qqwwertqqwaz");
        Response response = ApiOrder.createOrderWithAuthorization(order, accessToken);
        response.then().log().all()
                .statusCode(500);
    }

    @After
    public void deleteUserTest() {
        if (accessToken != null) {
            apiUser.deleteUser(accessToken);
        }
    }
}
