package model;

import javafx.beans.property.*;

/**
 * Room.java — Data model for a hotel room.
 * Uses JavaFX Properties so TableView auto-updates when data changes.
 */
public class Room {

    public enum RoomType { SINGLE, DOUBLE, DELUXE, SUITE }

    private final IntegerProperty roomNumber = new SimpleIntegerProperty();
    private final ObjectProperty<RoomType> roomType = new SimpleObjectProperty<>();
    private final DoubleProperty pricePerDay = new SimpleDoubleProperty();
    private final BooleanProperty available = new SimpleBooleanProperty(true);
    private final StringProperty floor = new SimpleStringProperty();
    private final IntegerProperty capacity = new SimpleIntegerProperty();

    public Room(int roomNumber, RoomType roomType, double pricePerDay, String floor, int capacity) {
        this.roomNumber.set(roomNumber);
        this.roomType.set(roomType);
        this.pricePerDay.set(pricePerDay);
        this.floor.set(floor);
        this.capacity.set(capacity);
        this.available.set(true);
    }

    // --- Getters / Setters ---
    public int getRoomNumber() { return roomNumber.get(); }
    public IntegerProperty roomNumberProperty() { return roomNumber; }

    public RoomType getRoomType() { return roomType.get(); }
    public ObjectProperty<RoomType> roomTypeProperty() { return roomType; }
    public void setRoomType(RoomType t) { roomType.set(t); }

    public double getPricePerDay() { return pricePerDay.get(); }
    public DoubleProperty pricePerDayProperty() { return pricePerDay; }
    public void setPricePerDay(double p) { pricePerDay.set(p); }

    public boolean isAvailable() { return available.get(); }
    public BooleanProperty availableProperty() { return available; }
    public void setAvailable(boolean a) { available.set(a); }

    public String getFloor() { return floor.get(); }
    public StringProperty floorProperty() { return floor; }

    public int getCapacity() { return capacity.get(); }
    public IntegerProperty capacityProperty() { return capacity; }

    /** Convenience: readable availability string for TableView */
    public String getStatus() { return available.get() ? "Available" : "Occupied"; }

    @Override
    public String toString() {
        return "Room " + getRoomNumber() + " (" + getRoomType() + ")";
    }
}