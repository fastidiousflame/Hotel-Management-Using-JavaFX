package ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import controller.BookingController;
import controller.RoomController;

public class DashboardController {

    @FXML private Label statTotal;
    @FXML private Label statAvail;
    @FXML private Label statOccupied;
    @FXML private Label statRevenue;
    @FXML private Label statGuests;
    @FXML private ListView<String> recentList;

    private RoomController    roomCtrl;
    private BookingController bookCtrl;
    private ObservableList<String> activityLog;
    private MainApp mainApp;

    public void setContext(RoomController rc, BookingController bc,
                           ObservableList<String> log, MainApp app) {
        this.roomCtrl = rc; this.bookCtrl = bc;
        this.activityLog = log; this.mainApp = app;

        recentList.setItems(activityLog);
        recentList.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                String color = item.contains("BOOKING")    ? "#3A9A60"
                             : item.contains("CHECKOUT")   ? "#C04060"
                             : item.contains("Room added") ? "#6090C8"
                             : "#5A3070";
                setStyle("-fx-text-fill:" + color + ";" +
                         "-fx-font-family:'Courier New';" +
                         "-fx-font-size:12;" +
                         "-fx-padding:3 6;");
            }
        });
        refresh();
    }

    public void refresh() {
        if (roomCtrl == null) return;
        statTotal.setText(String.valueOf(roomCtrl.totalRooms()));
        statAvail.setText(String.valueOf(roomCtrl.availableCount()));
        statOccupied.setText(String.valueOf(roomCtrl.occupiedCount()));
        statRevenue.setText("\u20B9" + String.format("%.0f", bookCtrl.totalRevenue()));
        statGuests.setText(String.valueOf(bookCtrl.getAllCustomers().size()));
    }

    @FXML private void onNewBooking() { mainApp.selectTab(2); }
    @FXML private void onAddRoom()    { mainApp.selectTab(1); }
    @FXML private void onExportLog()  { mainApp.exportLog(); }
}