package ui;

import java.time.LocalDate;

import controller.BookingController;
import controller.RoomController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.Booking;
import model.Room;

public class BookingFxController {

    @FXML private TextField         tfName;
    @FXML private TextField         tfContact;
    @FXML private TextField         tfEmail;
    @FXML private TextField         tfId;
    @FXML private ComboBox<Integer> cbRoom;
    @FXML private DatePicker        dpCheckIn;
    @FXML private DatePicker        dpCheckOut;
    @FXML private Label             billPreview;
    @FXML private Label             msgBook;

    @FXML private TableView<Booking>           bookingTable;
    @FXML private TableColumn<Booking, String> colBookingId;
    @FXML private TableColumn<Booking, String> colGuest;
    @FXML private TableColumn<Booking, String> colRoomNo;
    @FXML private TableColumn<Booking, String> colRoomType;
    @FXML private TableColumn<Booking, String> colCheckIn;
    @FXML private TableColumn<Booking, String> colCheckOut;
    @FXML private TableColumn<Booking, String> colNights;
    @FXML private TableColumn<Booking, String> colBill;
    @FXML private TableColumn<Booking, String> colBkStatus;

    private RoomController    roomCtrl;
    private BookingController bookCtrl;
    private MainApp           mainApp;

    public void setContext(RoomController rc, BookingController bc, MainApp app) {
        this.roomCtrl = rc;
        this.bookCtrl = bc;
        this.mainApp  = app;

        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));

        // ── FIX 1: Refresh combo every time the dropdown is opened ──
        // This catches newly added rooms AND rooms freed by checkout
        cbRoom.setOnShowing(e -> refreshRoomCombo());

        cbRoom.setOnAction(e    -> updateBillPreview());
        dpCheckIn.setOnAction(e  -> updateBillPreview());
        dpCheckOut.setOnAction(e -> updateBillPreview());

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
            ok("Booking " + booking.getBookingId() + " confirmed!  \u20B9"
                    + String.format("%.2f", booking.getTotalBill()));
            mainApp.log("BOOKING: " + booking.getBookingId() + " | Guest: " + name
                    + " | Room: " + rno + " | " + ci + " \u2192 " + co
                    + " | \u20B9" + String.format("%.2f", booking.getTotalBill()));
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

    // ── FIX 2: Public so CheckoutController can call it after checkout ──
    public void refreshRoomCombo() {
        Integer current = cbRoom.getValue(); // remember current selection
        cbRoom.getItems().setAll(
            roomCtrl.getAvailableRooms().stream()
                    .map(Room::getRoomNumber)
                    .toList()
        );
        // Restore selection if it's still available
        if (current != null && cbRoom.getItems().contains(current)) {
            cbRoom.setValue(current);
        }
    }

    private void updateBillPreview() {
        Integer rn = cbRoom.getValue();
        LocalDate ci = dpCheckIn.getValue(), co = dpCheckOut.getValue();
        if (rn != null && ci != null && co != null && co.isAfter(ci)) {
            roomCtrl.findRoom(rn).ifPresent(r -> {
                long nights = java.time.temporal.ChronoUnit.DAYS.between(ci, co);
                double total = nights * r.getPricePerDay();
                billPreview.setText("Estimated Bill: \u20B9" + String.format("%.2f", total)
                        + "  (" + nights + " night" + (nights > 1 ? "s" : "") + ")");
            });
        }
    }

    private void ok(String msg)  { msgBook.setStyle("-fx-text-fill:#3A9A60; -fx-font-weight:bold;"); msgBook.setText("\u2714  " + msg); }
    private void err(String msg) { msgBook.setStyle("-fx-text-fill:#C04060; -fx-font-weight:bold;"); msgBook.setText("\u2716  " + msg); }
}