package entity;

public class Vehicle {
    private Integer id;
    private String licensePlate;
    private String entryTime;
    private String exitTime;
    private double parkingFee;
    private int status;

//    public Vehicle(String licensePlate, String entryTime) {
//        this.licensePlate = licensePlate;
//        this.entryTime = entryTime;
//        this.exitTime = "";
//        this.parkingFee = 0.0;
//    }


    public Vehicle() {
    }

    public Vehicle(Integer id, String licensePlate, String entryTime, String exitTime, double parkingFee, int status) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.parkingFee = parkingFee;
        this.status = status;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public double getParkingFee() {
        return parkingFee;
    }

    public void setParkingFee(double parkingFee) {
        this.parkingFee = parkingFee;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // Getters and setters...
}

