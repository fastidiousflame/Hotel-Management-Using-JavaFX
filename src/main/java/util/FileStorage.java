package util;

import javafx.collections.*;
import model.*;

import java.io.*;

public class FileStorage {

    // ================= ROOMS =================
    public static void saveRooms(ObservableList<Room> rooms) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("rooms.csv"))) {
            for (Room r : rooms) {
                pw.println(
                    r.getRoomNumber() + "," +
                    r.getRoomType() + "," +
                    r.getPricePerDay() + "," +
                    r.getFloor() + "," +
                    r.getCapacity() + "," +
                    r.isAvailable()
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static ObservableList<Room> loadRooms() {
        ObservableList<Room> list = FXCollections.observableArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader("rooms.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                Room r = new Room(
                    Integer.parseInt(p[0]),
                    Room.RoomType.valueOf(p[1]),
                    Double.parseDouble(p[2]),
                    p[3],
                    Integer.parseInt(p[4])
                );
                r.setAvailable(Boolean.parseBoolean(p[5]));
                list.add(r);
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ================= BOOKINGS =================
    public static void saveBookings(ObservableList<Booking> bookings) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("bookings.csv"))) {
            for (Booking b : bookings) {
                pw.println(
                    b.getBookingId() + "," +
                    b.getCustomerName() + "," +
                    b.getRoomNumber() + "," +
                    b.getCheckIn() + "," +
                    b.getCheckOut() + "," +
                    b.getStatus() + "," +
                    b.getTotalBill()
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}