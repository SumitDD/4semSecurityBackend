package dto;

import entities.Rental;
import java.util.Date;


public class CreateRentalDTO {

    public String userName;
    public int rentalDays;
    public String brand;
    public String model;
    public int year;
    public double pricePerDay;
 
    public CreateRentalDTO(Rental order) {
        this.userName = order.getUser().getUserName();
        this.rentalDays = order.getRentalDays();
        this.brand = order.getCar().getBrand();
        this.model = order.getCar().getModel();
        this.year = order.getCar().getYear();
        this.pricePerDay = order.getCar().getPricePrDay();
    }

     public CreateRentalDTO(String userName, int rentalDays, String brand, String model, int year, double pricePerDay) {
        this.userName = userName;
        this.rentalDays = rentalDays;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.pricePerDay = pricePerDay;
    }

}