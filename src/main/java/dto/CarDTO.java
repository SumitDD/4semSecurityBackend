package dto;

import entities.Car;


public class CarDTO {

    public String brand;
    public String model;
    public int year;
    public double pricePerDay;


    public CarDTO(Car car) {
        this.brand = car.getBrand();
        this.model = car.getModel();
        this.year = car.getYear();
        this.pricePerDay = car.getPricePrDay();
    }

 



}