package view;

import controller.ParkingLotController;
import entity.Vehicle;
import func.ParkingFeeCalculator;
import service.VehicleService;
import utils.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.*;


public class ParkingLotManagerUI extends JFrame {
    private ParkingLotController controller;
    private DefaultTableModel tableModel;
    private JTextField licensePlateField;
    private JTextField searchField;
    private VehicleService vehicleService;

    public ParkingLotManagerUI() {
        controller = new ParkingLotController();
        vehicleService = new VehicleService();

        setTitle("Quản lý bãi đậu xe");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Bảng hiển thị xe trong bãi
        String[] columnNames = {"Id","Biển số xe", "Thời gian vào", "Thời gian ra", "Phí gửi xe"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        for (Vehicle d : this.vehicleService.getAll()) {
            Object[] rowData = {
                   d.getId(), d.getLicensePlate(), d.getEntryTime(), d.getExitTime(), d.getParkingFee()
            };
            tableModel.addRow(rowData);
        }
        // Thêm các xe chưa có exitTime vào table model
//        List<Vehicle> vehiclesWithoutExitTime = controller.getVehiclesWithoutExitTime();
//        for (Vehicle vehicle : vehiclesWithoutExitTime) {
//            tableModel.addRow(new Object[]{vehicle.getLicensePlate(), vehicle.getEntryTime(), "", 0.0});
//        }

        // Panel nhập liệu
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel licensePlateLabel = new JLabel("Biển số xe:");
        licensePlateField = new JTextField(10);

        JButton addVehicleButton = new JButton("Thêm");
        addVehicleButton.addActionListener(e -> addVehicle());

        JButton editVehicleButton = new JButton("Chỉnh sửa");
        editVehicleButton.addActionListener(e -> editVehicle());

        JButton removeVehicleButton = new JButton("Xóa");
        removeVehicleButton.addActionListener(e -> removeVehicle());

        JButton calculateFeeButton = new JButton("Tính phí");
        calculateFeeButton.addActionListener(e -> calculateFee());

        JButton showHistoryButton = new JButton("Danh sách");
        showHistoryButton.addActionListener(e -> showHistory());

        inputPanel.add(licensePlateLabel);
        inputPanel.add(licensePlateField);
        inputPanel.add(addVehicleButton);
        inputPanel.add(editVehicleButton);
        inputPanel.add(removeVehicleButton);
        inputPanel.add(calculateFeeButton);
        inputPanel.add(showHistoryButton);

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchField = new JTextField(10);

        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(e -> searchVehicle());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetButton());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        // Panel chức năng
        JPanel functionPanel = new JPanel();
        functionPanel.setLayout(new FlowLayout());

        JButton statisticsButton = new JButton("Thống kê");
        statisticsButton.addActionListener(e -> showStatistics());

        functionPanel.add(statisticsButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.SOUTH);
        panel.add(functionPanel, BorderLayout.WEST);

        add(panel);
    }

//    public void loadTable() {
//        DefaultTableModel dtm = (DefaultTableModel) this.table.getModel();
//        dtm.setRowCount(0);
//
//
//    }

    private void editVehicle() {
        int selectedRow = getSelectedRow();
        if (selectedRow != -1) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            String licensePlate = tableModel.getValueAt(selectedRow, 1).toString();
            String entryTime = tableModel.getValueAt(selectedRow, 2).toString(); // Lấy thời gian vào từ bảng

            // Hiển thị dialog để người dùng nhập thông tin mới
            String newLicensePlate = JOptionPane.showInputDialog(this, "Nhập biển số xe mới:", licensePlate);
            if (newLicensePlate != null && !newLicensePlate.isEmpty()) {
                // Lấy thông tin xe cũ
                Vehicle oldVehicle = controller.getVehicle(licensePlate, entryTime);
                Vehicle vehicle = new Vehicle();
                vehicle.setLicensePlate(newLicensePlate);
                vehicle.setEntryTime(entryTime);
                vehicle.setStatus(1);

                // Cập nhật thông tin của phương tiện trong cơ sở dữ liệu
//                controller.modifyVehicle(oldVehicle, newLicensePlate);
                vehicleService.update(Integer.valueOf(id),vehicle);

                // Cập nhật dữ liệu trong bảng
//                tableModel.setValueAt(newLicensePlate, selectedRow, 1);
                List<Vehicle> vehiclesWithoutExitTime = vehicleService.getAll();
                tableModel.setRowCount(0);
                for (Vehicle vehicle2 : vehiclesWithoutExitTime) {
                    tableModel.addRow(new Object[]{vehicle2.getId(),vehicle2.getLicensePlate(), vehicle2.getEntryTime(), "", 0.0});
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phương tiện để chỉnh sửa.");
        }
    }

    private void showHistory() {
        JFrame historyFrame = new JFrame("Danh sách các xe đã đậu");
        historyFrame.setSize(800, 400);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        historyFrame.setLocationRelativeTo(this);

        JPanel historyPanel = new JPanel(new BorderLayout());

        // Bảng hiển thị lịch sử
        String[] columnNames = {"Id","Biển số xe", "Thời gian vào", "Thời gian ra", "Phí gửi xe"};
        DefaultTableModel historyTableModel = new DefaultTableModel(columnNames, 0);
        JTable historyTable = new JTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        // Thêm dữ liệu vào bảng
        for (Vehicle vehicle : vehicleService.getHistory()) {
            historyTableModel.addRow(new Object[]{
                    vehicle.getId(),
                    vehicle.getLicensePlate(),
                    vehicle.getEntryTime(),
                    vehicle.getExitTime(),
                    vehicle.getParkingFee()
            });
        }

        // Panel nhập liệu
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JButton removeVehicleButton = new JButton("Xóa phương tiện");
        removeVehicleButton.addActionListener(e -> {
            int selectedRow = historyTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = historyTableModel.getValueAt(selectedRow, 0).toString();
                vehicleService.delete(Integer.valueOf(id));
                historyTableModel.removeRow(selectedRow);
                // Cập nhật lại tableModel ở cửa sổ chính
                tableModel.setRowCount(0); // Xóa tất cả các dòng hiện có
                List<Vehicle> vehiclesWithoutExitTime = vehicleService.getHistory();
                for (Vehicle vehicle : vehiclesWithoutExitTime) {
                    tableModel.addRow(new Object[]{vehicle.getId(),vehicle.getLicensePlate(), vehicle.getEntryTime(), "", 0.0});
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một chiếc xe để loại bỏ.");
            }
        });

        JButton editVehicleButton = new JButton("Sửa phương tiện");
        editVehicleButton.addActionListener(e -> {
            int selectedRow = historyTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = historyTableModel.getValueAt(selectedRow, 0).toString();
                String licensePlate = historyTableModel.getValueAt(selectedRow, 1).toString();
                String entryTime = historyTableModel.getValueAt(selectedRow, 2).toString();
                Vehicle vehicle1 = new Vehicle();
                vehicle1.setEntryTime(entryTime);
                String newLicensePlate = JOptionPane.showInputDialog(this, "Nhập biển số xe mới:", licensePlate);
                vehicle1.setLicensePlate(newLicensePlate);
                vehicle1.setStatus(2);
                if (newLicensePlate != null && !newLicensePlate.isEmpty()) {
                    Vehicle oldVehicle = vehicleService.update(Integer.valueOf(id), vehicle1);
                    controller.modifyVehicle(oldVehicle, newLicensePlate);
                    historyTableModel.setValueAt(newLicensePlate, selectedRow, 1);

                    // Cập nhật lại tableModel ở cửa sổ chính
                    tableModel.setRowCount(0); // Xóa tất cả các dòng hiện có
//                    List<Vehicle> vehiclesWithoutExitTime = controller.getVehiclesWithoutExitTime();
                    List<Vehicle> vehiclesWithoutExitTime = vehicleService.getAll();
                    for (Vehicle vehicle : vehiclesWithoutExitTime) {
                        tableModel.addRow(new Object[]{vehicle.getId(),vehicle.getLicensePlate(), vehicle.getEntryTime(), "", 0.0});
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một chiếc xe để chỉnh sửa.");
            }
        });

        inputPanel.add(removeVehicleButton);
        inputPanel.add(editVehicleButton);

        historyPanel.add(inputPanel, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        historyFrame.add(historyPanel);
        historyFrame.setVisible(true);
    }

    private void addVehicle() {
        String licensePlate = licensePlateField.getText().trim().toUpperCase();
        if (!licensePlate.isEmpty()) {
            // Kiểm tra xem xe đã có thời gian ra hay chưa
            if (controller.isVehicleExited(licensePlate)) {
                String entryTime = DateUtils.getCurrentTime();
                Vehicle vehicle = new Vehicle();
                vehicle.setEntryTime(entryTime);
                vehicle.setLicensePlate(licensePlate);
                vehicleService.insert(vehicle);
//                Vehicle vehicle = new Vehicle(licensePlate, entryTime);
//                controller.addVehicle(vehicle);
//                tableModel.addRow(new Object[]{ vehicle.getId(), licensePlate, entryTime, "", 0.0});
                licensePlateField.setText("");
                List<Vehicle> vehiclesWithoutExitTime = vehicleService.getAll();
                tableModel.setRowCount(0);
                for (Vehicle vehicle2 : vehiclesWithoutExitTime) {
                    tableModel.addRow(new Object[]{vehicle2.getId(),vehicle2.getLicensePlate(), vehicle2.getEntryTime(), "", 0.0});
                }
            } else {
                JOptionPane.showMessageDialog(this, "Phương tiện chưa rời bãi đậu xe.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Biển số xe không được để trống.");
        }
    }

    private void removeVehicle() {
        int selectedRow = getSelectedRow();
        if (selectedRow != -1) {
//            String licensePlate = tableModel.getValueAt(selectedRow, 0).toString();
            String id = tableModel.getValueAt(selectedRow, 0).toString();
//            controller.removeVehicle(licensePlate);
            vehicleService.delete(Integer.valueOf(id));
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chiếc xe để loại bỏ.");
        }
    }

    private void calculateFee() {
        int selectedRow = getSelectedRow();
        if (selectedRow != -1) {

            String id = tableModel.getValueAt(selectedRow, 0).toString();
            String licensePlate = tableModel.getValueAt(selectedRow, 1).toString();
            String entryTime = tableModel.getValueAt(selectedRow, 2).toString(); // Lấy thời gian vào từ bảng

//            Vehicle vehicle = controller.getVehicle(licensePlate, entryTime);
            Vehicle vehicle = new Vehicle();

            // Phương tiện đã ra khỏi bãi đậu xe
            String exitTime = DateUtils.getCurrentTime();
            double fee = ParkingFeeCalculator.calculateFee(entryTime, exitTime);
            vehicle.setLicensePlate(licensePlate);
            vehicle.setExitTime(exitTime);
            vehicle.setParkingFee(fee);
            vehicle.setEntryTime(entryTime);
            vehicle.setStatus(2);
            vehicleService.update(Integer.valueOf(id),vehicle);  // Save the updated vehicle data

            tableModel.setValueAt(exitTime, selectedRow, 3);
            tableModel.setValueAt(fee, selectedRow, 4);

            // Xóa phương tiện khỏi tableModel nhưng không xóa trong XML
            tableModel.removeRow(selectedRow);

        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại xe để tính phí.");
        }
    }

    private void searchVehicle() {
        String licensePlateRegex = searchField.getText().trim().toUpperCase();
        if (!licensePlateRegex.isEmpty()) {
            // Tạo biểu thức chính quy từ chuỗi nhập vào
            Pattern pattern = Pattern.compile("^" + licensePlateRegex);

            // Lấy tất cả các phương tiện có biển số xe khớp với biểu thức chính quy
//            List<Vehicle> vehicles = controller.getAllVehicles();
            List<Vehicle> vehicles = this.vehicleService.search(licensePlateRegex);
            List<Vehicle> matchedVehicles = new ArrayList<>();
            for (Vehicle vehicle : vehicles) {
                Matcher matcher = pattern.matcher(vehicle.getLicensePlate());
                if (matcher.find()) {
                    matchedVehicles.add(vehicle);
                }
            }
            tableModel.setRowCount(0);
            List<Vehicle> vehiclesWithoutExitTime = this.vehicleService.search(licensePlateRegex);
            for (Vehicle vehicle : vehiclesWithoutExitTime) {
                tableModel.addRow(new Object[]{vehicle.getId(),vehicle.getLicensePlate(), vehicle.getEntryTime(), "" ,0.0});
            }

        }else {
            JOptionPane.showMessageDialog(this, "Trường tìm kiếm không được để trống.");
        }

    }

//    private void searchVehicle() {
//        String licensePlateRegex = searchField.getText().trim().toUpperCase();
//        if (!licensePlateRegex.isEmpty()) {
//            // Tạo biểu thức chính quy từ chuỗi nhập vào
//            Pattern pattern = Pattern.compile("^" +licensePlateRegex);
//
//            // Lấy tất cả các phương tiện có biển số xe khớp với biểu thức chính quy
////            List<Vehicle> vehicles = controller.getAllVehicles();
//            List<Vehicle> vehicles = this.vehicleService.search(licensePlateRegex);
//            List<Vehicle> matchedVehicles = new ArrayList<>();
//            for (Vehicle vehicle : vehicles) {
//                Matcher matcher = pattern.matcher(vehicle.getLicensePlate());
//                if (matcher.find()) {
//                    matchedVehicles.add(vehicle);
//                }
//            }
//
//            if (!matchedVehicles.isEmpty()) {
//                // Tạo cửa sổ mới để hiển thị thông tin phương tiện
//                JFrame searchResultFrame = new JFrame("Kết quả tìm kiếm");
//                searchResultFrame.setSize(800, 400);
//                searchResultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                searchResultFrame.setLocationRelativeTo(this);
//
//                JPanel searchResultPanel = new JPanel(new BorderLayout());
//
//                // Bảng hiển thị kết quả tìm kiếm
//                String[] columnNames = {"Id","Biển số xe", "Thời gian vào", "Thời gian ra", "Phí gửi xe"};
//                DefaultTableModel searchResultTableModel = new DefaultTableModel(columnNames, 0);
//                JTable searchResultTable = new JTable(searchResultTableModel);
//                JScrollPane scrollPane = new JScrollPane(searchResultTable);
//
//                // Thêm dữ liệu vào bảng kết quả tìm kiếm
//                for (Vehicle vehicle : matchedVehicles) {
//                    searchResultTableModel.addRow(new Object[]{
//                            vehicle.getId(),
//                            vehicle.getLicensePlate(),
//                            vehicle.getEntryTime(),
//                            vehicle.getExitTime(),
//                            vehicle.getParkingFee()
//                    });
//                }
//
//                // Panel chức năng
//                JPanel functionPanel = new JPanel();
//                functionPanel.setLayout(new FlowLayout());
//
//                JButton removeVehicleButton = new JButton("Xóa phương tiện");
//                removeVehicleButton.addActionListener(e -> {
//                    int selectedRow = searchResultTable.getSelectedRow();
//                    if (selectedRow != -1) {
//                        String id = tableModel.getValueAt(selectedRow, 0).toString();
//                        vehicleService.delete(Integer.valueOf(id));
////                        String licensePlate = searchResultTableModel.getValueAt(selectedRow, 0).toString();
////                        controller.removeVehicle(licensePlate);
//                        searchResultTableModel.removeRow(selectedRow);
//                        // Cập nhật lại tableModel ở cửa sổ chính
//                        tableModel.setRowCount(0); // Xóa tất cả các dòng hiện có
//                        List<Vehicle> vehiclesWithoutExitTime = this.vehicleService.getAll();
//                        for (Vehicle vehicle : vehiclesWithoutExitTime) {
//                            tableModel.addRow(new Object[]{vehicle.getId(),vehicle.getLicensePlate(), vehicle.getEntryTime(), "", 0.0});
//                        }
//                    } else {
//                        JOptionPane.showMessageDialog(searchResultFrame, "Vui lòng chọn một chiếc xe để loại bỏ.");
//                    }
//                });
//
//                JButton editVehicleButton = new JButton("Sửa phương tiện");
//                editVehicleButton.addActionListener(e -> {
//                    int selectedRow = searchResultTable.getSelectedRow();
//                    if (selectedRow != -1) {
//                        String id = tableModel.getValueAt(selectedRow, 0).toString();
//                        String licensePlate = tableModel.getValueAt(selectedRow, 1).toString();
//                        String entryTime = tableModel.getValueAt(selectedRow, 2).toString();
//                        Vehicle vehicle1 = new Vehicle();
//                        vehicle1.setEntryTime(entryTime);
//                        String newLicensePlate = JOptionPane.showInputDialog(this, "Nhập biển số xe mới:", licensePlate);
//                        vehicle1.setLicensePlate(newLicensePlate);
//                        vehicle1.setStatus(1);
//                        if (newLicensePlate != null && !newLicensePlate.isEmpty()) {
//                            Vehicle oldVehicle = vehicleService.update(Integer.valueOf(id), vehicle1);
//                            controller.modifyVehicle(oldVehicle, newLicensePlate);
//                            tableModel.setValueAt(newLicensePlate, selectedRow, 1);
//                            searchResultTableModel.setValueAt(newLicensePlate, selectedRow, 1);
//
//
//                            // Cập nhật lại tableModel ở cửa sổ chính
//                           // Xóa tất cả các dòng hiện có
////                            List<Vehicle> vehiclesWithoutExitTime = controller.getVehiclesWithoutExitTime();
//                            tableModel.setRowCount(0);
//                            List<Vehicle> vehiclesWithoutExitTime = this.vehicleService.getAll();
//                            for (Vehicle vehicle : vehiclesWithoutExitTime) {
//                                tableModel.addRow(new Object[]{vehicle.getId(),vehicle.getLicensePlate(), vehicle.getEntryTime(), "" ,0.0});
//                            }
//                        }
//
//                    } else {
//                        JOptionPane.showMessageDialog(this, "Vui lòng chọn một chiếc xe để chỉnh sửa.");
//                    }
//                });
//
//
//                functionPanel.add(removeVehicleButton);
//                functionPanel.add(editVehicleButton);
//
//                searchResultPanel.add(functionPanel, BorderLayout.NORTH);
//                searchResultPanel.add(scrollPane, BorderLayout.CENTER);
//                searchResultFrame.add(searchResultPanel);
//                searchResultFrame.setVisible(true);
//            } else {
//                JOptionPane.showMessageDialog(this, "Không có phương tiện nào khớp với biểu thức tìm kiếm.");
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Trường tìm kiếm không được để trống.");
//        }
//    }

    private void showStatistics() {
//        int totalVehicles = controller.getParkingLot().getAllVehicles().size();
        int totalHistory = vehicleService.getAll().size();
        int totalVehicles = vehicleService.getHistory().size();
        int total = totalHistory + totalVehicles;
        double totalFees = vehicleService.getHistory().stream().mapToDouble(Vehicle::getParkingFee).sum();

        JOptionPane.showMessageDialog(this, "Tổng số phương tiện: " + total + "\nTổng phí gửi xe: " + totalFees);
    }

    private int getSelectedRow() {
        JTable table = (JTable) tableModel.getTableModelListeners()[0];
        return table.getSelectedRow();
    }

    private void resetButton(){
        searchField.setText("");
        tableModel.setRowCount(0);
        List<Vehicle> vehiclesWithoutExitTime = this.vehicleService.getAll();
        for (Vehicle vehicle : vehiclesWithoutExitTime) {
            tableModel.addRow(new Object[]{vehicle.getId(),vehicle.getLicensePlate(), vehicle.getEntryTime(), "" ,0.0});
        }
    }
}
