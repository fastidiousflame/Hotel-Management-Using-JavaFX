package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;

import controller.RoomController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Booking;
import model.Customer;
import model.Room;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // ================= BOOKINGS (WITH FULL CUSTOMER DATA) =================
    public static void saveBookings(ObservableList<Booking> bookings) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("bookings.csv"))) {
            for (Booking b : bookings) {
                Customer c = b.getCustomer();

                pw.println(
                        b.getBookingId() + "," +
                        c.getName() + "," +
                        c.getContactNumber() + "," +
                        c.getEmail() + "," +
                        c.getIdProof() + "," +
                        b.getRoomNumber() + "," +
                        b.getCheckIn() + "," +
                        b.getCheckOut() + "," +
                        b.getStatus() + "," +
                        b.getTotalBill()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Booking> loadBookings(RoomController roomCtrl) {
        ObservableList<Booking> list = FXCollections.observableArrayList();

        try (BufferedReader br = new BufferedReader(new FileReader("bookings.csv"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                String bookingId = p[0];
                String name = p[1];
                String contact = p[2];
                String email = p[3];
                String idProof = p[4];
                int roomNo = Integer.parseInt(p[5]);
                LocalDate checkIn = LocalDate.parse(p[6]);
                LocalDate checkOut = LocalDate.parse(p[7]);
                String status = p[8];

                Room room = roomCtrl.findRoom(roomNo).orElse(null);
                if (room == null) continue;

                // ✅ FULL CUSTOMER RESTORE
                Customer customer = new Customer(name, contact, email, idProof);

                Booking booking = new Booking(customer, room, checkIn, checkOut);
                booking.setStatus(status);

                list.add(booking);
            }
        } catch (Exception ignored) {}

        return list;
    }
}