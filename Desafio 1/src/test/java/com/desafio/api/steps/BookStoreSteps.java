// src/test/java/com/desafio/api/steps/BookStoreSteps.java
package com.desafio.api.steps;

import com.desafio.api.models.UserCreationRequest;
import com.desafio.api.utils.APIConfig;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookStoreSteps {
    private Response response;
    private String username;
    private String password;
    private String userId;
    private String token;
    private List<String> reservedIsbns;

    // Estados do cenário (dados dinâmicos)
    private Map<String, Object> scenarioData = new HashMap<>();

    // Inicialização (Para o Background: Configuração inicial da API)
    @Given("a base URI da API é {string}")
    public void a_base_uri_da_api_é(String baseUri) {
        APIConfig.setup();
        scenarioData.put("baseUri", baseUri);
    }

    // --- Passo 1: Criar Usuário ---

    @Given("que um novo usuário com nome {string} e senha {string} será criado")
    public void que_um_novo_usuario_com_nome_e_senha_será_criado(String user, String pass) {
        this.username = user;
        this.password = pass;
        // Prepara o corpo da requisição (payload)
        UserCreationRequest userPayload = new UserCreationRequest(username, password);
        scenarioData.put("userPayload", userPayload);
    }

    @When("eu realizar a requisição POST para criar o usuário")
    public void eu_realizar_a_requisição_post_para_criar_o_usuario() {
        // Endpoint: https://demoqa.com/Account/v1/User
        response = given()
                .contentType(ContentType.JSON)
                .body(scenarioData.get("userPayload"))
                .when()
                .post("/Account/v1/User");
    }

    @Then("a resposta deve ter o status {int} e o ID do usuário deve ser capturado")
    public void a_resposta_deve_ter_o_status_e_o_id_do_usuario_deve_ser_capturado(Integer expectedStatus) {
        response.then().statusCode(expectedStatus);

        // Captura o userID para uso futuro
        userId = response.jsonPath().getString("userID");
        scenarioData.put("userId", userId);
        System.out.println("User ID Criado: " + userId);

        // Validação adicional do corpo
        response.then().body("username", equalTo(username));
    }

    // --- Passo 2: Gerar Token ---

    @Given("que o usuário e a senha capturados serão usados para gerar um token")
    public void que_o_usuario_e_a_senha_capturados_serão_usados_para_gerar_um_token() {
        // O corpo da requisição de token é o mesmo da criação de usuário
        UserCreationRequest tokenPayload = new UserCreationRequest(username, password);
        scenarioData.put("tokenPayload", tokenPayload);
    }

    @When("eu realizar a requisição POST para gerar o token de acesso")
    public void eu_realizar_a_requisição_post_para_gerar_o_token_de_acesso() {
        // Endpoint: https://demoqa.com/Account/v1/GenerateToken
        response = given()
                .contentType(ContentType.JSON)
                .body(scenarioData.get("tokenPayload"))
                .when()
                .post("/Account/v1/GenerateToken");
    }

    @Then("a resposta deve ter o status {int} e o token deve ser capturado")
    public void a_resposta_deve_ter_o_status_e_o_token_deve_ser_capturado(Integer expectedStatus) {
        response.then().statusCode(expectedStatus);

        // Captura o token para uso futuro
        token = response.jsonPath().getString("token");
        scenarioData.put("token", token);
        System.out.println("Token Capturado: " + token.substring(0, 20) + "...");

        // Validação adicional
        response.then().body("status", equalTo("Success"))
                .body("result", equalTo("User authorized successfully."));
    }

    // --- Passo 3: Autorização ---

    @When("eu realizar a requisição POST para verificar a autorização do usuário")
    public void eu_realizar_a_requisição_post_para_verificar_a_autorização_do_usuario() {
        // Endpoint: https://demoqa.com/Account/v1/Authorized
        UserCreationRequest authPayload = new UserCreationRequest(username, password);

        response = given()
                .contentType(ContentType.JSON)
                .body(authPayload)
                .when()
                .post("/Account/v1/Authorized");
    }

    @Then("a resposta deve ter o status {int} e o corpo deve indicar {string}")
    public void a_resposta_deve_ter_o_status_e_o_corpo_deve_indicar(Integer expectedStatus, String expectedBody) {
        response.then().statusCode(expectedStatus);

        // O Hamcrest 'equalTo' é usado para validar que o corpo é exatamente o booleano 'true' ou 'false'
        response.then().body(equalTo(expectedBody));
    }

    // --- Passo 4: Listar Livros ---

    @When("eu realizar a requisição GET para listar todos os livros")
    public void eu_realizar_a_requisição_get_para_listar_todos_os_livros() {
        // Endpoint: https://demoqa.com/BookStore/v1/Books
        response = given()
                .when()
                .get("/BookStore/v1/Books");
    }

    @Then("a resposta deve ter o status {int} e dois ISBNs de livros devem ser capturados")
    public void a_resposta_deve_ter_o_status_e_dois_isbns_de_livros_devem_ser_capturados(Integer expectedStatus) {
        response.then().statusCode(expectedStatus);

        // Captura a lista de ISBNs (pelo menos 2)
        reservedIsbns = response.jsonPath().getList("books.isbn");

        // Captura apenas os dois primeiros ISBNs para reservar
        if (reservedIsbns.size() >= 2) {
            reservedIsbns = reservedIsbns.subList(0, 2);
        } else {
            throw new AssertionError("Não há pelo menos 2 livros disponíveis para reserva.");
        }
        scenarioData.put("reservedIsbns", reservedIsbns);
        System.out.println("ISBNs capturados para reserva: " + reservedIsbns);

        // Validação adicional (Hamcrest)
        response.then().body("books", hasSize(greaterThanOrEqualTo(2)));
    }

    // --- Passo 5: Reservar Livros ---

    @Given("que os dois ISBNs de livros e o User ID capturados serão usados para reservar")
    public void que_os_dois_isbns_de_livros_e_o_user_id_capturados_serão_usados_para_reservar() {
        // Constrói o corpo da requisição de reserva
        List<Map<String, String>> books = List.of(
                Map.of("isbn", reservedIsbns.get(0)),
                Map.of("isbn", reservedIsbns.get(1))
        );

        Map<String, Object> reservationPayload = new HashMap<>();
        reservationPayload.put("userId", userId);
        reservationPayload.put("collectionOfIsbns", books);

        scenarioData.put("reservationPayload", reservationPayload);
    }

    @When("eu realizar a requisição POST para adicionar os livros ao usuário")
    public void eu_realizar_a_requisição_post_para_adicionar_os_livros_ao_usuario() {
        // Endpoint: https://demoqa.com/BookStore/v1/Books
        response = given()
                .header("Authorization", "Bearer " + token) // Autorização com o token
                .contentType(ContentType.JSON)
                .body(scenarioData.get("reservationPayload"))
                .when()
                .post("/BookStore/v1/Books");
    }

    @Then("a resposta deve ter o status {int} e a lista de livros deve ser validada")
    public void a_resposta_deve_ter_o_status_e_a_lista_de_livros_deve_ser_validada(Integer expectedStatus) {
        response.then().statusCode(expectedStatus);

        // Validação adicional (Hamcrest): Verifica se a lista retornada contém os dois ISBNs reservados
        response.then().body("books.isbn", containsInAnyOrder(reservedIsbns.get(0), reservedIsbns.get(1)));
    }

    // --- Passo 6: Listar Detalhes do Usuário ---

    @Given("que o User ID capturado será usado para listar os detalhes")
    public void que_o_user_id_capturado_será_usado_para_listar_os_detalhes() {
        // Nada a fazer, o userId já está em 'scenarioData'
    }

    @When("eu realizar a requisição GET para listar os detalhes do usuário")
    public void eu_realizar_a_requisição_get_para_listar_os_detalhes_do_usuario() {
        // Endpoint: https://demoqa.com/Account/v1/User/{userID}
        response = given()
                .header("Authorization", "Bearer " + token) // Autorização com o token
                .pathParam("userID", userId)
                .when()
                .get("/Account/v1/User/{userID}");
    }

    @Then("a resposta deve ter o status {int} e a lista de livros do usuário deve conter os dois livros reservados")
    public void a_resposta_deve_ter_o_status_e_a_lista_de_livros_do_usuario_deve_conter_os_dois_livros_reservados(Integer expectedStatus) {
        response.then().statusCode(expectedStatus);

        // Validação final: Verifica se os ISBNs reservados estão presentes no detalhe do usuário
        response.then().body("username", equalTo(username))
                .body("userId", equalTo(userId))
                .body("books.isbn", containsInAnyOrder(reservedIsbns.toArray()));

        System.out.println("Fluxo de automação concluído com sucesso!");
    }
}
