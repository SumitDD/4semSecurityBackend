package utils;


import entities.Car;
import entities.DriverLicenseImage;
import entities.Rental;
import entities.Role;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SetupTestUsers {

  public static void setUpUsers() {

    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    EntityManager em = emf.createEntityManager();
    
    // IMPORTAAAAAAAAAANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // This breaks one of the MOST fundamental security rules in that it ships with default users and passwords
    // CHANGE the three passwords below, before you uncomment and execute the code below
    // Also, either delete this file, when users are created or rename and add to .gitignore
    // Whatever you do DO NOT COMMIT and PUSH with the real passwords

    User user = new User("passat", "Passat20212630");
    User admin = new User("golf", "Golf20162640");
    User both = new User("golf_passat", "golfpassat2630");
    Car car = em.find(Car.class, (long)1);
    DriverLicenseImage driverLicense = new DriverLicenseImage("xxxMyLicenseImage.com");
    Rental rental = new Rental(10, 1500);
    if(admin.getUserPass().equals("test")||user.getUserPass().equals("test")||both.getUserPass().equals("test"))
      throw new UnsupportedOperationException("You have not changed the passwords");

    em.getTransaction().begin();
    Role userRole = new Role("user");
    Role adminRole = new Role("admin");
    user.addRole(userRole);
    user.setDriverLicense(driverLicense);
    admin.addRole(adminRole);
    both.addRole(userRole);
    both.addRole(adminRole);
    user.addRental(rental);
    car.addRental(rental);
    em.persist(userRole);
    em.persist(adminRole);
    em.persist(user);
    em.persist(admin);
    em.persist(both);
    
    em.getTransaction().commit();
    System.out.println("PW: " + user.getUserPass());
    System.out.println("Testing user with OK password: " + user.verifyPassword("test"));
    System.out.println("Testing user with wrong password: " + user.verifyPassword("test1"));
    System.out.println("Created TEST Users");
   
  }
  
    public static void main(String[] args) {
        setUpUsers();
    }

}
