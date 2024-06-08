package service;

import entity.Vehicle;
import repository.VehicleRepo;

import java.util.ArrayList;

public class VehicleService {

    private VehicleRepo vehicleRepo;

    public VehicleService() {
        this.vehicleRepo = new VehicleRepo();
    }

    public void insert(Vehicle vehicle) {
        this.vehicleRepo.insert(vehicle);
    }

    public ArrayList<Vehicle> getAll() {
        return this.vehicleRepo.getAll();
    }

    public ArrayList<Vehicle> getHistory() {
        return this.vehicleRepo.getHistory();
    }
    public ArrayList<Vehicle> search(String licensePlate) {
        return this.vehicleRepo.search(licensePlate);
    }

    public void delete(int id) {
        this.vehicleRepo.delete(id);
    }
    public Vehicle update(int id, Vehicle vehicle){
        this.vehicleRepo.update(id, vehicle);
        return vehicle;
    }
}
