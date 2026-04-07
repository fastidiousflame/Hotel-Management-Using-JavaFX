package model;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Booking.java — Links a Customer to a Room for a date range.
 * Calculates total bill automatically from nights × pricePerDay.
 */
public class Booking {

    private final StringProperty bookingId = new SimpleStringProperty();
    private final ObjectProperty<Customer> customer = new SimpleObjectProperty<>();
    private final ObjectProperty<Room> room = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> checkIn = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> checkOut = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty("ACTIVE"); // ACTIVE | CHECKED_OUT
    private final DoubleProperty totalBill = new SimpleDoubleProperty();

    private static int bookingCounter = 5000;

    public Booking(Customer customer, Room room, LocalDate checkIn, LocalDate checkOut) {
        this.bookingId.set("BKG-" + (++bookingCounter));
        this.customer.set(customer);
        this.room.set(room);
        this.checkIn.set(checkIn);
        this.checkOut.set(checkOut);
        recalcBill();
    }

    /** Recalculates total bill based on nights and room price */
    public void recalcBill() {
        long nights = ChronoUnit.DAYS.between(getCheckIn(), getCheckOut());
        if (nights < 1) nights = 1;
        totalBill.set(nights * getRoom().getPricePerDay());
    }

    public long getNights() {
        return Math.max(1, ChronoUnit.DAYS.between(getCheckIn(), getCheckOut()));
    }

    // --- Getters / Properties ---
    public String getBookingId() { return bookingId.get(); }
    public StringProperty bookingIdProperty() { return bookingId; }

    public Customer getCustomer() { return customer.get(); }
    public ObjectProperty<Customer> customerProperty() { return customer; }

    public Room getRoom() { return room.get(); }
    public ObjectProperty<Room> roomProperty() { return room; }

    public LocalDate getCheckIn() { return checkIn.get(); }
    public ObjectProperty<LocalDate> checkInProperty() { return checkIn; }

    public LocalDate getCheckOut() { return checkOut.get(); }
    public ObjectProperty<LocalDate> checkOutProperty() { return checkOut; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    public void setStatus(String s) { status.set(s); }

    public double getTotalBill() { return totalBill.get(); }
    public DoubleProperty totalBillProperty() { return totalBill; }

    // Convenience display methods for TableView
    public String getCustomerName() { return customer.get().getName(); }
    public int getRoomNumber() { return room.get().getRoomNumber(); }
    public String getRoomType() { return room.get().getRoomType().toString(); }
}