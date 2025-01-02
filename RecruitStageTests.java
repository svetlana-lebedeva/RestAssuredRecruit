import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import javax.json.Json;
import javax.json.JsonObject;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecruitStageTests {

    private static String token;
    private static String applicationId;
    private static String candidateId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://recruit-stage.portnov.com/recruit/api/v1";
    }

    @Test
    @Order(1)
    public void testCreateCandidate() {
        JsonObject candidate = Json.createObjectBuilder()
                .add("firstName", "Svetlana")
                .add("middleName", "Y")
                .add("lastName", "Lebedeva")
                .add("email", "lebedeva15.svetlana@example.com")
                .add("password", "welcome")
                .add("address", "123 Main Street")
                .add("city", "San Francisco")
                .add("state", "CA")
                .add("zip", "94545")
                .add("summary", "This is my summary")
                .build();

        Response createCandidateResponse = given()
                .contentType(ContentType.JSON)
                .body(candidate.toString())
                .log().all()
                .when()
                .post("/candidates")
                .then()
                .log().all()
                .statusCode(201)
                .extract().response();
        candidateId = createCandidateResponse.path("id").toString();
    }

    @Test
    @Order(2)
    public void testLogin(){
        JsonObject credentials = Json.createObjectBuilder()
                .add("email", "lebedeva15.svetlana@example.com")
                .add("password", "welcome")
                .build();
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(credentials.toString())
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract().response();

        token = loginResponse.path("token");
    }

    @Test
    @Order(3)
    public void testApplyToPosition(){
        JsonObject application = Json.createObjectBuilder()
                .add("candidateId", candidateId)
                .add("positionId", 1)
                .add("dateApplied", "2025-01-01")
                .build();
        Response createApplicationResponse = (Response) given()
                .contentType("application/json")
                .body(application.toString())
                .header("Authorization", "Bearer " + token)
                .log().all()
                .when()
                .post("/applications")
                .then()
                .log().all()
                .statusCode(201)
                .extract().response();

        applicationId = createApplicationResponse.path("id").toString();
    }

    @Test
    @Order(4)
    public void testVerifyApplication(){
        given()
                .header("Authorization", "Bearer " + token)
                .log().all()
                .when()
                .get("/applications/" + applicationId)
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    @Order(5)
    public void testDeleteApplication(){
        given()
                .header("Authorization", "Bearer " + token)
                .log().all()
                .when()
                .delete("/applications/" + applicationId)
                .then()
                .log().all()
                .statusCode(204);
    }
}



