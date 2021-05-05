package rest;

import dto.CarDTO;
import dto.CreateRentalDTO;
import dto.RentalDTO;
import entities.Car;
import entities.Rental;
import entities.Role;
import entities.User;
import facades.RentalFacade;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test

//@Disabled
public class RentalResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static RentalFacade rentalFacade;
    private static User u1, u2, admin, both;
    private static Car c1, c2;
    private static Rental r;
    private static String securityToken;

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
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createNativeQuery("DELETE FROM RENTAL").executeUpdate();
            em.createNativeQuery("DELETE FROM CAR").executeUpdate();
            em.createNativeQuery("DELETE FROM user_roles").executeUpdate();
            em.createNativeQuery("DELETE FROM roles").executeUpdate();
            em.createNativeQuery("DELETE FROM users").executeUpdate();

            u1 = new User("user", "testuser");
            admin = new User("admin", "testadmin");
            both = new User("user_admin", "testuseradmin");
            c1 = new Car("kia", "rio", 1968, 190);
            c2 = new Car("bmw", "m5", 2010, 200);
            r = new Rental(10, 1500);

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            u1.addRole(userRole);
            admin.addRole(adminRole);
            both.addRole(userRole);
            both.addRole(adminRole);
            u1.addRental(r);
            c1.addRental(r);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(u1);
            em.persist(c2);
            em.persist(admin);
            em.persist(both);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");

        //System.out.println("TOKEN ---> " + securityToken);
    }

    @Test
    public void testServerIsUp() {
        given().when().get("/rental").then().statusCode(200);
    }

    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
                .contentType("application/json")
                .get("/rental/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Hello anonymous"));
    }

    @Test
    public void testCreateRental() throws Exception {
        login("user", "testuser");
        given()
                .contentType("application/json")
                .body(new CreateRentalDTO(u1.getUserName(), 10, c2.getBrand(), c2.getModel(), c2.getYear(), c2.getPricePrDay()))
                .header("x-access-token", securityToken)
                .when()
                .put("/rental/makerental").then()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("brand", equalTo("bmw"));

    }

    @Test
    public void testGetCars() throws Exception {
        List<CarDTO> carsDTO;

        carsDTO = given()
                .contentType("application/json")
                .when()
                .get("/rental/getcars/")
                .then()
                .extract().body().jsonPath().getList("cars", CarDTO.class);

        CarDTO carDTO = new CarDTO(c2);
        assertThat(carsDTO, containsInAnyOrder(carDTO));
    }

    @Test
    public void testGetAllRentals() throws Exception {
        login("admin", "testadmin");
        List<RentalDTO> rentalsDTO;

        rentalsDTO = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/rental/getallrentals/")
                .then()
                .extract().body().jsonPath().getList("rentals", RentalDTO.class);

        RentalDTO rentalDTO = new RentalDTO(r);

        assertThat(rentalsDTO, containsInAnyOrder(rentalDTO));
    }
    
      @Test
    public void testGetAllRentalsForOneUser() throws Exception {
        login("user", "testuser");
        List<RentalDTO> rentalsDTO;

        rentalsDTO = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/rental/getallrentalsoneuser/" + u1.getUserName())
                .then()
                .extract().body().jsonPath().getList("rentals", RentalDTO.class);

        RentalDTO rentalDTO = new RentalDTO(r);
        assertThat(rentalsDTO, containsInAnyOrder(rentalDTO));
    }
    

    @Test
    public void testDeleteRental() throws Exception {
        login("admin", "testadmin");

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("/rental/deleterental/" + r.getId()).then()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("userName", equalTo(u1.getUserName()));
    }

    @Test
    public void testEditDog() throws Exception {
        login("admin", "testadmin");

        r.setRentalDays(15);

        given()
                .contentType("application/json")
                .body(new RentalDTO(r))
                .header("x-access-token", securityToken)
                .when()
                .put("/rental/editrental").then()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("rentalDays", equalTo(r.getRentalDays()));

    }

}
