package rest;


import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test
@Disabled

public class RenameMeResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
//    @BeforeEach
//    public void setUp() {
//        EntityManager em = emf.createEntityManager();
//        r1 = new RenameMe("Some txt", "More text");
//        r2 = new RenameMe("aaa", "bbb");
//        try {
//            em.getTransaction().begin();
//            em.createNamedQuery("RenameMe.deleteAllRows").executeUpdate();
//            em.persist(r1);
//            em.persist(r2);
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }
//    }

    @Test
    public void testServerIsUp() {
        given().when().get("/info").then().statusCode(200);
    }

    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
                .contentType("application/json")
                .get("/info/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Hello anonymous"));
    }

    
    @Test
    @Order(1)
    public void testParrallel() throws Exception {
        given()
                .contentType("application/json")
                .get("/info/parrallel").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("peopleName", equalTo("Luke Skywalker"))
                .body("planetName", equalTo("Yavin IV"))
                .body("speciesName", equalTo("Ewok"))
                .body("starshipName", equalTo("Star Destroyer"))
                .body("vehicleName", equalTo("Sand Crawler"));

 
    }
    @Test
    @Order(2)
    public void testCached() throws Exception {
        given()
                .contentType("application/json")
                .get("/info/cached").then()            
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("peopleName", equalTo("Luke Skywalker"))
                .body("planetName", equalTo("Yavin IV"))
                .body("speciesName", equalTo("Ewok"))
                .body("starshipName", equalTo("Star Destroyer"))
                .body("vehicleName", equalTo("Sand Crawler"));
    }
}
