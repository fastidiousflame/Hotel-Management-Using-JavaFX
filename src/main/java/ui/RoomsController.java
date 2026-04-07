package ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Room;
import controller.RoomController;

public class RoomsController {

    @FXML private TextField tfRoomNo;
    @FXML private ComboBox<Room.RoomType> cbType;
    @FXML private TextField tfPrice;
    @FXML private TextField tfFloor;
    @FXML private TextField tfCapacity;
    @FXML private Label     msgRoom;

    @FXML private TableView<Room>            roomTable;
    @FXML private TableColumn<Room, String>  colRoomNo;
    @FXML private TableColumn<Room, String>  colType;
    @FXML private TableColumn<Room, String>  colFloor;
    @FXML private TableColumn<Room, String>  colCapacity;
    @FXML private TableColumn<Room, String>  colPrice;
    @FXML private TableColumn<Room, String>  colStatus;

    private RoomController roomCtrl;
    private MainApp        mainApp;

    public void setContext(RoomController rc, MainApp app) {
        this.roomCtrl = rc;
        this.mainApp  = app;

        cbType.getItems().setAll(Room.RoomType.values());

        colRoomNo.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoomType().toString()));
        colFloor.setCellValueFactory(new PropertyValueFactory<>("floor"));
        colCapacity.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCapacity())));
        colPrice.setCellValueFactory(d -> new SimpleStringProperty(String.format("\u20B9%.0f", d.getValue().getPricePerDay())));

        // ── KEY FIX: bind directly to availableProperty so status updates LIVE ──
        colStatus.setCellValueFactory(d ->
            Bindings.when(d.getValue().availableProperty())
                    .then("Available")
                    .otherwise("Occupied")
        );
        colStatus.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setStyle(""); return; }
                setText(s);
                setStyle("-fx-text-fill:" + (s.equals("Available") ? "#3A9A60" : "#C04060")
                        + "; -fx-font-weight:bold; -fx-alignment:CENTER;");
            }
        });

        roomTable.setItems(roomCtrl.getAllRooms());
        roomTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ── Called by CheckoutController after every checkout ─────────
    public void refreshTable() {
        roomTable.refresh();
    }

    @FXML
    private void onAddRoom() {
        try {
            int no    = Integer.parseInt(tfRoomNo.getText().trim());
            double p  = Double.parseDouble(tfPrice.getText().trim());
            int cap   = Integer.parseInt(tfCapacity.getText().trim());
            String fl = tfFloor.getText().trim();
            Room.RoomType rt = cbType.getValue();
            if (rt == null || fl.isEmpty()) { err("Fill all fields."); return; }
            if (roomCtrl.addRoom(no, rt, p, fl, cap)) {
                ok("Room " + no + " added!");
                mainApp.log("Room added: #" + no + " (" + rt + ") \u20B9" + p + "/day");
                onClearForm();
                mainApp.refreshDashboard();
            } else {
                err("Room #" + no + " already exists.");
            }
        } catch (NumberFormatException ex) {
            err("Room No, Price and Capacity must be numbers.");
        }
    }

    @FXML private void onClearForm() {
        tfRoomNo.clear(); tfPrice.clear(); tfFloor.clear(); tfCapacity.clear();
        cbType.setValue(null); msgRoom.setText("");
    }

    @FXML private void onShowAll()       { roomTable.setItems(roomCtrl.getAllRooms()); roomTable.refresh(); }
    @FXML private void onShowAvailable() { roomTable.setItems(roomCtrl.getAvailableRooms()); roomTable.refresh(); }
    @FXML private void onRefresh()       { roomTable.refresh(); ok("Table refreshed."); }

    private void ok(String msg)  { msgRoom.setStyle("-fx-text-fill:#3A9A60; -fx-font-weight:bold;"); msgRoom.setText("\u2714  " + msg); }
    private void err(String msg) { msgRoom.setStyle("-fx-text-fill:#C04060; -fx-font-weight:bold;"); msgRoom.setText("\u2716  " + msg); }
}