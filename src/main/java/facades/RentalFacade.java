package facades;


import dto.CreateRentalDTO;
import dto.RentalDTO;
import entities.Car;
import entities.Rental;
import entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class RentalFacade {

    private static RentalFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private RentalFacade() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static RentalFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new RentalFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public RentalDTO createRental(CreateRentalDTO createRentalDTO){
            
        EntityManager em = emf.createEntityManager();
        Car car;
        Rental rental;
        User user;
        double totalRentalPrice;
        
        try{
            
            user = em.find(User.class, createRentalDTO.userName);
            Query query = em.createQuery("SELECT c FROM Car c WHERE c.model = :model ");
            query.setParameter("model", createRentalDTO.model);
            car = (Car) query.getSingleResult();
            totalRentalPrice = createRentalDTO.rentalDays * car.getPricePrDay();
            rental = new Rental(createRentalDTO.rentalDays, totalRentalPrice);
            user.addRental(rental);
            car.addRental(rental);
            em.getTransaction().begin();
            em.persist(rental);
            em.merge(user);
            em.merge(car);
            
            em.getTransaction().commit();
            
        } finally {
            em.close();
        }
        return new RentalDTO(rental);
        
        
    }
  

}
