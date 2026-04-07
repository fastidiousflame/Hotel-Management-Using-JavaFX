package ui;

import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import model.Room;
import controller.BookingController;
import controller.RoomController;

/**
 * BookingFxController.java
 * FXML Controller for Booking.fxml
 * (Named BookingFxController to avoid clash with controller.BookingController)
 */
public class BookingFxController {

    // ── @FXML fields ──────────────────────────────────────────────
    @FXML private TextField    tfName;
    @FXML private TextField    tfContact;
    @FXML private TextField    tfEmail;
    @FXML private TextField    tfId;
    @FXML private ComboBox<Integer> cbRoom;
    @FXML private DatePicker   dpCheckIn;
    @FXML private DatePicker   dpCheckOut;
    @FXML private Label        billPreview;
    @FXML private Label        msgBook;

    @FXML private TableView<Booking>          bookingTable;
    @FXML private TableColumn<Booking, String> colBookingId;
    @FXML private TableColumn<Booking, String> colGuest;
    @FXML private TableColumn<Booking, String> colRoomNo;
    @FXML private TableColumn<Booking, String> colRoomType;
    @FXML private TableColumn<Booking, String> colCheckIn;
    @FXML private TableColumn<Booking, String> colCheckOut;
    @FXML private TableColumn<Booking, String> colNights;
    @FXML private TableColumn<Booking, String> colBill;
    @FXML private TableColumn<Booking, String> colBkStatus;

    // ── Shared context ────────────────────────────────────────────
    private RoomController    roomCtrl;
    private BookingController bookCtrl;
    private MainApp           mainApp;

    public void setContext(RoomController rc, BookingController bc, MainApp app) {
        this.roomCtrl = rc;
        this.bookCtrl = bc;
        this.mainApp  = app;

        // Default dates
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));

        // Listeners for live bill preview
        cbRoom.setOnAction(e    -> updateBillPreview());
        dpCheckIn.setOnAction(e  -> updateBillPreview());
        dpCheckOut.setOnAction(e -> updateBillPreview());

        // Populate available room numbers
        refreshRoomCombo();

        // Wire table columns
        colBookingId.setCellValueFactory(d -> d.getValue().bookingIdProperty());
        colGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCustomerName()));
        colRoomNo.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoomNumber())));
        colRoomType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoomType()));
        colCheckIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckIn().toString()));
        colCheckOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckOut().toString()));
        colNights.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getNights())));
        colBill.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f", d.getValue().getTotalBill())));

        // Coloured status column
        colBkStatus.setCellValueFactory(d -> d.getValue().statusProperty());
        colBkStatus.setCellFactory(col -> new TableCell<>() {
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setStyle(""); return; }
                setText(s);
                setStyle("-fx-text-fill:" + (s.equals("ACTIVE") ? "#3A9A60" : "#9B7AAC")
                        + "; -fx-font-weight:bold; -fx-alignment:CENTER;");
            }
        });

        bookingTable.setItems(bookCtrl.getAllBookings());
        bookingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ── Handlers ─────────────────────────────────────────────────

    @FXML
    private void onConfirmBooking() {
        String name = tfName.getText().trim();
        String cont = tfContact.getText().trim();
        String mail = tfEmail.getText().trim();
        String idp  = tfId.getText().trim();
        Integer rno = cbRoom.getValue();
        LocalDate ci = dpCheckIn.getValue(), co = dpCheckOut.getValue();

        if (name.isEmpty() || cont.isEmpty() || rno == null || ci == null || co == null) {
            err("Please fill all required fields."); return;
        }
        if (!co.isAfter(ci)) { err("Check-out must be after check-in."); return; }

        Booking booking = bookCtrl.createBooking(name, cont, mail, idp, rno, ci, co);
        if (booking != null) {
            ok("Booking " + booking.getBookingId() + " confirmed! Bill: ₹"
                    + String.format("%.2f", booking.getTotalBill()));
            mainApp.log("BOOKING: " + booking.getBookingId() + " | Guest: " + name
                    + " | Room: " + rno + " | " + ci + " → " + co
                    + " | ₹" + String.format("%.2f", booking.getTotalBill()));
            mainApp.showInvoiceAlert(booking);
            onClearForm();
            mainApp.refreshDashboard();
        } else {
            err("Room " + rno + " is not available.");
        }
    }

    @FXML
    private void onClearForm() {
        tfName.clear(); tfContact.clear(); tfEmail.clear(); tfId.clear();
        cbRoom.setValue(null);
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));
        msgBook.setText("");
        billPreview.setText("Select room & dates to see estimate");
        refreshRoomCombo();
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void refreshRoomCombo() {
        cbRoom.getItems().setAll(
            roomCtrl.getAvailableRooms().stream()
                    .map(Room::getRoomNumber).toList()
        );
    }

    private void updateBillPreview() {
        Integer rn = cbRoom.getValue();
        LocalDate ci = dpCheckIn.getValue(), co = dpCheckOut.getValue();
        if (rn != null && ci != null && co != null && co.isAfter(ci)) {
            roomCtrl.findRoom(rn).ifPresent(r -> {
                long nights = java.time.temporal.ChronoUnit.DAYS.between(ci, co);
                double total = nights * r.getPricePerDay();
                billPreview.setText("Estimated Bill: ₹" + String.format("%.2f", total)
                        + "  (" + nights + " night" + (nights > 1 ? "s" : "") + ")");
            });
        }
    }

    private void ok(String msg)  {
        msgBook.setStyle("-fx-text-fill:#3A9A60; -fx-font-weight:bold;");
        msgBook.setText("✔  " + msg);
    }
    private void err(String msg) {
        msgBook.setStyle("-fx-text-fill:#C04060; -fx-font-weight:bold;");
        msgBook.setText("✖  " + msg);
    }
}