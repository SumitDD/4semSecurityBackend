package facades;

import dto.CarDTO;
import dto.CarsDTO;
import dto.CreateRentalDTO;
import dto.RentalDTO;
import dto.RentalsDTO;
import entities.Car;
import entities.Rental;
import utils.EMF_Creator;

import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import security.errorhandling.AuthenticationException;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class RentalFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    private static RentalFacade rentalFacade;
    private static User u1, u2, admin, both;
    private static Car c1, c2;
    private static Rental r;

    public RentalFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getUserFacade(emf);
        rentalFacade = RentalFacade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
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
            em.persist(admin);
            em.persist(both);
            em.persist(c2);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void testVerifyUser() throws AuthenticationException {
        User user = facade.getVeryfiedUser("admin", "testadmin");
        assertEquals("admin", admin.getUserName());
    }

    @Test
    public void testMakeRental() {
        RentalDTO rentalDTO = rentalFacade.createRental(new CreateRentalDTO(u1.getUserName(), 10, c1.getBrand(), c1.getModel(), c1.getYear(), c1.getPricePrDay()));
        double totalRentalPrice = 1900;
        assertEquals(totalRentalPrice, rentalDTO.totalRentalPrice);

    }

    @Test
    public void testGetCars() {
        CarsDTO cars = rentalFacade.getCars();
        assertEquals(1, cars.cars.size(), "Expects the size of one ");
        assertThat(cars.cars, containsInAnyOrder(new CarDTO(c2)));
    }

    @Test
    public void testGetAllRentals() {
        RentalsDTO rentals = rentalFacade.getAllRentals();
        assertEquals(1, rentals.rentals.size(), "Expects the size of one ");
        assertThat(rentals.rentals, containsInAnyOrder(new RentalDTO(r)));
    }
       @Test
    public void testGetRentalsForOneUser() {
        RentalsDTO rentals = rentalFacade.getAllRentalsForOneUser(u1.getUserName());
        assertEquals(1, rentals.rentals.size(), "Expects the size of one ");
        assertThat(rentals.rentals, containsInAnyOrder(new RentalDTO(r)));
    }
   

    @Test
    public void testDeleteRental() {
        RentalDTO rentalDTO = rentalFacade.deleteRental(r.getId());
        assertEquals(u1.getUserName(), r.getUser().getUserName(), "Expects the username: user");
    }

    @Test
    public void testEditDog() {
        r.setRentalDays(15);
        RentalDTO rentalDTO = rentalFacade.editRental(new RentalDTO(r));
        assertEquals(2850.0, rentalDTO.totalRentalPrice, "Expects a new total price of 2850'");
    }

}
