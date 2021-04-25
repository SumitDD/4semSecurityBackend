
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private String model;
    private int year;
    private double pricePrDay;
    
    @OneToMany(mappedBy = "car", cascade = CascadeType.PERSIST)
    private List<Rental> rentals;

    public Car() {
    }

    public Car(String brand, String model, int year, double pricePrDay) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.pricePrDay = pricePrDay;
        this.rentals = new ArrayList();
        
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPricePrDay() {
        return pricePrDay;
    }

    public void setPricePrDay(double pricePrDay) {
        this.pricePrDay = pricePrDay;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void addRental(Rental rental) {
        if(rental != null){
            this.rentals.add(rental);
            rental.setCar(this);
        }
    }
    
    


}
