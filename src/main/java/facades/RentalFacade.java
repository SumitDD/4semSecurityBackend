package facades;

import dto.CarsDTO;
import dto.CreateRentalDTO;
import dto.RentalDTO;
import dto.RentalsDTO;
import entities.Car;
import entities.Rental;
import entities.User;
import errorhandling.NotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static java.util.concurrent.TimeUnit.DAYS;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class RentalFacade {

    private static RentalFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private RentalFacade() {
    }

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

    public RentalDTO createRental(CreateRentalDTO createRentalDTO) throws Exception {

        EntityManager em = emf.createEntityManager();
        Car car;
        Rental rental;
        User user;
        double totalRentalPrice;

        try {

            user = em.find(User.class, createRentalDTO.userName);
            Query query = em.createQuery("SELECT c FROM Car c WHERE c.model = :model ");
            query.setParameter("model", createRentalDTO.model);
            car = (Car) query.getSingleResult();

            if (!car.getRentals().isEmpty()) {

                Rental latestRental = car.getRentals().get(car.getRentals().size() - 1);
                Date rentalDate = latestRental.getRentalDate();
                LocalDate localRentalDate = Instant.ofEpochMilli(rentalDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate finishedDate = localRentalDate.plusDays(latestRental.getRentalDays());

                LocalDate todaysDate = LocalDate.now();
                if (finishedDate.isAfter(todaysDate)) {
                    throw new Exception("");

                }
            }
                totalRentalPrice = createRentalDTO.rentalDays * car.getPricePrDay();
                rental = new Rental(createRentalDTO.rentalDays, totalRentalPrice);
                user.addRental(rental);
                car.addRental(rental);
                em.getTransaction().begin();
                em.persist(rental);
                em.merge(user);
                em.merge(car);

                em.getTransaction().commit();
        }finally {
            em.close();
        }
        return new RentalDTO(rental);

    }

    public CarsDTO getCars() {
        EntityManager em = emf.createEntityManager();
        List<Car> cars;
        List<Car> availableCars = new ArrayList();
        try {
            cars = em.createQuery("SELECT c FROM Car c").getResultList();
            for (Car car : cars) {
                if (car.getRentals().isEmpty()) {
                    availableCars.add(car);
                } else {
                    Rental latestRental = car.getRentals().get(car.getRentals().size() - 1);
                    Date rentalDate = latestRental.getRentalDate();
                    LocalDate localRentalDate = Instant.ofEpochMilli(rentalDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate finishedDate = localRentalDate.plusDays(latestRental.getRentalDays());

                    LocalDate todaysDate = LocalDate.now();

                    if (finishedDate.isBefore(todaysDate)) {
                        availableCars.add(car);
                    }

                }

            }

        } finally {
            em.close();
        }
        return new CarsDTO(availableCars);
    }

    public RentalsDTO getAllRentals() {
        EntityManager em = emf.createEntityManager();
        List<Rental> rentals;
        try {
            rentals = em.createQuery("SELECT r FROM Rental r").getResultList();
        } finally {
            em.close();
        }
        return new RentalsDTO(rentals);
    }

    public RentalsDTO getAllRentalsForOneUser(String userName) {
        EntityManager em = emf.createEntityManager();
        List<Rental> rentals;
        try {
            Query query = em.createQuery("SELECT r FROM Rental r WHERE r.user.userName = :username");
            query.setParameter("username", userName);
            rentals = query.getResultList();
        } finally {
            em.close();
        }
        return new RentalsDTO(rentals);
    }

    public RentalDTO deleteRental(long id) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        Rental rental;
        try {
            rental = em.find(Rental.class, id);
            if (rental == null) {
                throw new NotFoundException("No rental was found");
            }

            em.getTransaction().begin();
            rental.getCar().getRentals().remove(rental);
            em.remove(rental);
            em.getTransaction().commit();
            return new RentalDTO(rental);
        } finally {
            em.close();
        }
    }

    public RentalDTO editRental(RentalDTO rentalDTO) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        Rental rental;
        Car car;
        double totalRentalPrice;
        try {

            rental = em.find(Rental.class, rentalDTO.id);
            if (rental == null) {
                throw new NotFoundException("No rental was found");
            }
            rental.setRentalDays(rentalDTO.rentalDays);
            Query query = em.createQuery("SELECT c FROM Car c WHERE c.model = :model");
            query.setParameter("model", rentalDTO.model);
            car = (Car) query.getSingleResult();
            rental.setCar(car);
            totalRentalPrice = rentalDTO.rentalDays * car.getPricePrDay();
            rental.setTotalRentPrice(totalRentalPrice);
            rental.setRentalDate(rentalDTO.rentalDate);
            em.getTransaction().begin();
            em.merge(rental);
            em.getTransaction().commit();
            return new RentalDTO(rental);
        } finally {
            em.close();
        }
    }

}
