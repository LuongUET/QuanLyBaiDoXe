package repository;

import entity.Vehicle;

import java.sql.*;
import java.util.ArrayList;

import utils.DBConnectionUtils;

public class VehicleRepo {
    public void insert(Vehicle vehicle){
        try {

            Connection conn= DBConnectionUtils.getConnection();
            String sql="INSERT INTO vehicle"+"(license_plate,entry_time,exit_time,parking_fee)"+"VALUES(?,?,?,?)";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, vehicle.getLicensePlate());
            ps.setString(2, vehicle.getEntryTime());
            ps.setString(3, vehicle.getExitTime());
            ps.setDouble(4, vehicle.getParkingFee());
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Vehicle> getAll(){
        ArrayList<Vehicle> list=new ArrayList<>();
        try {
            Connection conn=DBConnectionUtils.getConnection();
            String sql="SELECT * FROM vehicle where status =1";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.execute();
            ResultSet rs=ps.getResultSet();
            while(rs.next()) {
                int id=rs.getInt("id");
                String licensePlate=rs.getString("license_plate");
                String entryTime=rs.getString("entry_time");
                String exitTime=rs.getString("exit_time");
                double parkingFee=rs.getDouble("parking_fee");
                int status=rs.getInt("status");
                Vehicle vehicle =new Vehicle(id, licensePlate,entryTime, exitTime, parkingFee, status);
                list.add(vehicle);
            }
            System.out.println("Truy vấn thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Vehicle> getHistory(){
        ArrayList<Vehicle> list=new ArrayList<>();
        try {
            Connection conn=DBConnectionUtils.getConnection();
            String sql="SELECT * FROM vehicle where status =2";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.execute();
            ResultSet rs=ps.getResultSet();
            while(rs.next()) {
                int id=rs.getInt("id");
                String licensePlate=rs.getString("license_plate");
                String entryTime=rs.getString("entry_time");
                String exitTime=rs.getString("exit_time");
                double parkingFee=rs.getDouble("parking_fee");
                int status=rs.getInt("status");
                Vehicle vehicle =new Vehicle(id, licensePlate,entryTime, exitTime, parkingFee, status);
                list.add(vehicle);
            }
            System.out.println("Truy vấn thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Vehicle> search(String licensePlate1){
        ArrayList<Vehicle> list=new ArrayList<>();
        try {
            Connection conn=DBConnectionUtils.getConnection();
            String sql="SELECT * FROM vehicle where license_plate = ? and status = 1";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, licensePlate1);
            ps.execute();
            ResultSet rs=ps.getResultSet();
            while(rs.next()) {
                int id=rs.getInt("id");
                String licensePlate=rs.getString("license_plate");
                String entryTime=rs.getString("entry_time");
                String exitTime=rs.getString("exit_time");
                double parkingFee=rs.getDouble("parking_fee");
                int status=rs.getInt("status");
                Vehicle vehicle =new Vehicle(id, licensePlate,entryTime, exitTime, parkingFee, status);
                list.add(vehicle);
            }
            System.out.println("Truy vấn thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void delete(int id){
        try {
            Connection conn=DBConnectionUtils.getConnection();
            String sql="DELETE FROM vehicle WHERE id =?";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Truy vấn thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(int id, Vehicle vehicle) {
        try {
            Connection conn = DBConnectionUtils.getConnection();
            String sql = "UPDATE vehicle SET " + "license_plate =?,entry_time=?,exit_time=?,parking_fee=?, status =?   WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, vehicle.getLicensePlate());
            ps.setString(2, vehicle.getEntryTime());
            ps.setString(3, vehicle.getExitTime());
            ps.setDouble(4, vehicle.getParkingFee());
            ps.setInt(5, vehicle.getStatus());
            ps.setInt(6 , id);
            ps.execute();
//            conn.close();
            System.out.println("Truy vấn thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
