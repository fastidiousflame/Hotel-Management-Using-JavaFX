package controller;

import java.time.LocalDate;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Booking;
import model.Customer;
import model.Room;
import util.FileStorage;

public class BookingController {

    private final ObservableList<Booking> bookings = FXCollections.observableArrayList();
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private final RoomController roomController;

    public BookingController(RoomController roomController) {
        this.roomController = roomController;

        // ✅ Load bookings from file
        bookings.addAll(FileStorage.loadBookings(roomController));

        // ✅ Rebuild customers list from bookings
        rebuildCustomersFromBookings();
    }

    // 🔥 FIX: rebuild customers from bookings
    private void rebuildCustomersFromBookings() {
        customers.clear();

        for (Booking b : bookings) {
            Customer c = b.getCustomer();
            if (c != null) {
                customers.add(c);
            }
        }
    }

    public Booking createBooking(String customerName, String contact, String email,
                                 String idProof, int roomNumber,
                                 LocalDate checkIn, LocalDate checkOut) {

        Optional<Room> roomOpt = roomController.findRoom(roomNumber);
        if (roomOpt.isEmpty()) return null;

        Room room = roomOpt.get();
        if (!room.isAvailable()) return null;

        Customer customer = new Customer(customerName, contact, email, idProof);
        Booking booking = new Booking(customer, room, checkIn, checkOut);

        room.setAvailable(false);

        customers.add(customer);
        bookings.add(booking);

        // ✅ Save data
        FileStorage.saveRooms(roomController.getAllRooms());
        FileStorage.saveBookings(bookings);

        return booking;
    }

    public boolean checkout(String bookingId) {
        Optional<Booking> opt = bookings.stream()
                .filter(b -> b.getBookingId().equals(bookingId) && b.getStatus().equals("ACTIVE"))
                .findFirst();

        if (opt.isEmpty()) return false;

        Booking booking = opt.get();
        booking.setStatus("CHECKED_OUT");
        booking.getRoom().setAvailable(true);

        // ✅ Save updates
        FileStorage.saveRooms(roomController.getAllRooms());
        FileStorage.saveBookings(bookings);

        return true;
    }

    public ObservableList<Booking> getAllBookings()  { return bookings; }
    public ObservableList<Customer> getAllCustomers() { return customers; }

    public ObservableList<Booking> getActiveBookings() {
        return bookings.filtered(b -> b.getStatus().equals("ACTIVE"));
    }

    public long activeCount()    { return bookings.stream().filter(b -> b.getStatus().equals("ACTIVE")).count(); }
    public long checkedOutCount(){ return bookings.stream().filter(b -> b.getStatus().equals("CHECKED_OUT")).count(); }

    public double totalRevenue() {
        return bookings.stream()
                .filter(b -> b.getStatus().equals("CHECKED_OUT"))
                .mapToDouble(Booking::getTotalBill)
                .sum();
    }
}