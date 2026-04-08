package controller;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Room;
import util.FileStorage;

public class RoomController {

    private final ObservableList<Room> rooms = FXCollections.observableArrayList();

    public RoomController() {
        ObservableList<Room> loaded = FileStorage.loadRooms();

        if (loaded.isEmpty()) {
            seedDefaultRooms();
            FileStorage.saveRooms(rooms);
        } else {
            rooms.addAll(loaded);
        }
    }

    private void seedDefaultRooms() {
        rooms.addAll(
            new Room(101, Room.RoomType.SINGLE, 1500, "1st Floor", 1),
            new Room(102, Room.RoomType.SINGLE, 1500, "1st Floor", 1),
            new Room(103, Room.RoomType.DOUBLE, 2500, "1st Floor", 2),
            new Room(201, Room.RoomType.DOUBLE, 2500, "2nd Floor", 2),
            new Room(202, Room.RoomType.DELUXE, 4000, "2nd Floor", 3),
            new Room(203, Room.RoomType.DELUXE, 4000, "2nd Floor", 3),
            new Room(301, Room.RoomType.SUITE, 8000, "3rd Floor", 4),
            new Room(302, Room.RoomType.SUITE, 8500, "3rd Floor", 4),
            new Room(303, Room.RoomType.DELUXE, 4200, "3rd Floor", 3),
            new Room(401, Room.RoomType.SUITE, 12000, "Penthouse", 6)
        );
    }

    public boolean addRoom(int roomNumber, Room.RoomType type, double price, String floor, int capacity) {
        boolean exists = rooms.stream().anyMatch(r -> r.getRoomNumber() == roomNumber);
        if (exists) return false;

        rooms.add(new Room(roomNumber, type, price, floor, capacity));
        FileStorage.saveRooms(rooms);

        return true;
    }

    public Optional<Room> findRoom(int roomNumber) {
        return rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst();
    }

    public ObservableList<Room> getAllRooms() { return rooms; }

    public ObservableList<Room> getAvailableRooms() {
        return rooms.filtered(Room::isAvailable);
    }

    public long totalRooms()     { return rooms.size(); }
    public long availableCount() { return rooms.stream().filter(Room::isAvailable).count(); }
    public long occupiedCount()  { return rooms.stream().filter(r -> !r.isAvailable()).count(); }

    public double totalRevenuePotential() {
        return rooms.stream().mapToDouble(Room::getPricePerDay).sum();
    }
}