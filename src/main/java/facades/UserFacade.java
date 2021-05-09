package facades;

import entities.DriverLicenseImage;
import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import security.errorhandling.AuthenticationException;
import security.errorhandling.RegisterException;


public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }
    
      public User createUser(String username, String password1, String password2, String imgUrl) throws RegisterException {
        if (password1.equals(password2) && password1.length()>= 12) {
            EntityManager em = emf.createEntityManager();
            User user = new User(username, password1);
            Role userRole = em.find(Role.class, "user");
            DriverLicenseImage license = new DriverLicenseImage(imgUrl);
            user.setDriverLicense(license);
            user.addRole(userRole);
            em.getTransaction().begin();
            em.persist(userRole);
            em.persist(user);
            em.getTransaction().commit();

            try {
                user = em.find(User.class, username);
                if (user == null || !user.verifyPassword(password1)) {
                    throw new RegisterException("Make sure the two passwords are identical and upload a driverlicense!");
                }
            } finally {
                em.close();
            }
            return user;

        } else {
            throw new RegisterException("Make sure the two passwords are identical and upload a driverlicense!");
        }
    }

}
