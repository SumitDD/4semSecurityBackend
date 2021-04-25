package dto;

import entities.Car;
import java.util.ArrayList;
import java.util.List;


public class CarsDTO {


    public List<CarDTO> cars = new ArrayList();

    public CarsDTO(List<Car> products){
        for (Car car : products) {
            this.cars.add(new CarDTO(car));
        }

    }

}