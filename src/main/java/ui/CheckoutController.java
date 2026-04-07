package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import controller.BookingController;
import controller.RoomController;

public class CheckoutController {

    @FXML private TextField tfBookingId;
    @FXML private Label     msgCheckout;
    @FXML private javafx.scene.layout.VBox receiptBox;
    @FXML private Label     receiptContent;

    @FXML private TableView<Booking>           activeTable;
    @FXML private TableColumn<Booking, String> colActBkId;
    @FXML private TableColumn<Booking, String> colActGuest;
    @FXML private TableColumn<Booking, String> colActRoom;
    @FXML private TableColumn<Booking, String> colActCheckIn;
    @FXML private TableColumn<Booking, String> colActCheckOut;
    @FXML private TableColumn<Booking, String> colActBill;

    private BookingController   bookCtrl;
    private RoomsController     roomsCtrl;    // to refresh rooms table
    private BookingFxController bookingCtrl;  // to refresh room combo
    private MainApp             mainApp;

    public void setContext(RoomController rc, BookingController bc, MainApp app) {
        this.bookCtrl    = bc;
        this.mainApp     = app;
        this.roomsCtrl   = app.getRoomsController();
        this.bookingCtrl = app.getBookingController();

        colActBkId.setCellValueFactory(d -> d.getValue().bookingIdProperty());
        colActGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCustomerName()));
        colActRoom.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoomNumber())));
        colActCheckIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckIn().toString()));
        colActCheckOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckOut().toString()));
        colActBill.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f", d.getValue().getTotalBill())));

        activeTable.setItems(bookCtrl.getActiveBookings());
        activeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        activeTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) tfBookingId.setText(n.getBookingId());
        });
    }

    @FXML
    private void onCheckout() {
        String bid = tfBookingId.getText().trim();
        if (bid.isEmpty()) { err("Enter a Booking ID."); return; }

        Booking target = bookCtrl.getActiveBookings().stream()
                .filter(b -> b.getBookingId().equals(bid))
                .findFirst().orElse(null);

        if (bookCtrl.checkout(bid)) {
            ok("Checkout successful for " + bid + ". Room is now available.");
            mainApp.log("CHECKOUT: " + bid);
            tfBookingId.clear();
            mainApp.refreshDashboard();
            activeTable.refresh();

            // ── Refresh rooms table status ──
            if (roomsCtrl != null)   roomsCtrl.refreshTable();

            // ── FIX: Refresh booking combo so freed room reappears ──
            if (bookingCtrl != null) bookingCtrl.refreshRoomCombo();

            if (target != null) showReceipt(target);
        } else {
            err("Booking ID not found or already checked out.");
            receiptBox.setVisible(false);
            receiptBox.setManaged(false);
        }
    }

    private void showReceipt(Booking b) {
        receiptContent.setText(
            "Booking ID  : " + b.getBookingId() + "\n" +
            "Guest       : " + b.getCustomerName() + "\n" +
            "Room        : #" + b.getRoomNumber() + " (" + b.getRoomType() + ")\n" +
            "Check-In    : " + b.getCheckIn() + "\n" +
            "Check-Out   : " + b.getCheckOut() + "\n" +
            "Nights      : " + b.getNights() + "\n" +
            "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\n" +
            "TOTAL BILL  : \u20B9" + String.format("%.2f", b.getTotalBill()) + "\n" +
            "Thank you for choosing Royal Meridian!"
        );
        receiptBox.setManaged(true);
        receiptBox.setVisible(true);
    }

    private void ok(String msg)  { msgCheckout.setStyle("-fx-text-fill:#3A9A60; -fx-font-weight:bold;"); msgCheckout.setText("\u2714  " + msg); }
    private void err(String msg) { msgCheckout.setStyle("-fx-text-fill:#C04060; -fx-font-weight:bold;"); msgCheckout.setText("\u2716  " + msg); }
}