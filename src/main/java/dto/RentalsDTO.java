package dto;

import entities.Car;
import entities.Rental;
import java.util.ArrayList;
import java.util.List;


public class RentalsDTO {

    public List<RentalDTO> rentalOrders = new ArrayList();

    public RentalsDTO(List<Rental> rentals) {
        for (Rental r : rentals) {
            this.rentalOrders.add(new RentalDTO(r));
        }
    }



}