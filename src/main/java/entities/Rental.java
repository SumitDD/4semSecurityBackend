
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
public class Rental implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rentalDays;
    @Temporal(TemporalType.DATE)
    private Date rentalDate;
    private double totalRentPrice;
    
    @ManyToOne
    private User user;
    
    @ManyToOne(cascade= CascadeType.PERSIST)
    private Car car;
  

    public Rental() {
    }

    public Rental(int rentalDays, double totalRentPrice) {
        this.rentalDays = rentalDays;
        this.rentalDate = new Date();
        this.totalRentPrice = totalRentPrice;
    }
    
      public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
    }

    public Date getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(Date rentalDate) {
        this.rentalDate = rentalDate;
    }

    public double getTotalRentPrice() {
        return totalRentPrice;
    }

    public void setTotalRentPrice(double totalRentPrice) {
        this.totalRentPrice = totalRentPrice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
    
    
    
    
    

    
}
