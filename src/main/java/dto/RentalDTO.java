package dto;

import entities.Rental;
import java.util.Date;

public class RentalDTO {

    public long id;
    public String userName;
    public int rentalDays;
    public Date rentalDate;
    public double totalRentalPrice;
    public String brand;
    public String model;
    public int year;
    public double pricePerDay;

 


    public RentalDTO(Rental order) {
        this.id = order.getId();
        this.userName = order.getUser().getUserName();
        this.rentalDate = order.getRentalDate();
        this.rentalDays = order.getRentalDays();
        this.totalRentalPrice = order.getTotalRentPrice();
        this.brand = order.getCar().getBrand();
        this.model = order.getCar().getModel();
        this.year = order.getCar().getYear();
        this.pricePerDay = order.getCar().getPricePrDay();

    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RentalDTO other = (RentalDTO) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    






}