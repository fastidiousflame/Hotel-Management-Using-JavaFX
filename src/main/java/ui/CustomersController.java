package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;
import controller.BookingController;

/**
 * CustomersController.java
 * FXML Controller for Customers.fxml
 */
public class CustomersController {

    // ── @FXML fields ──────────────────────────────────────────────
    @FXML private TextField tfSearch;
    @FXML private TableView<Customer>           customerTable;
    @FXML private TableColumn<Customer, String> colCustId;
    @FXML private TableColumn<Customer, String> colCustName;
    @FXML private TableColumn<Customer, String> colCustContact;
    @FXML private TableColumn<Customer, String> colCustEmail;
    @FXML private TableColumn<Customer, String> colCustId2;

    // ── Shared context ────────────────────────────────────────────
    private BookingController bookCtrl;

    public void setContext(BookingController bc) {
        this.bookCtrl = bc;

        // Wire columns using PropertyValueFactory (JavaFX property binding)
        colCustId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        colCustEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCustId2.setCellValueFactory(new PropertyValueFactory<>("idProof"));

        customerTable.setItems(bookCtrl.getAllCustomers());
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ── Handlers ─────────────────────────────────────────────────

    @FXML
    private void onSearch() {
        String q = tfSearch.getText().toLowerCase();
        customerTable.setItems(bookCtrl.getAllCustomers().filtered(c ->
            c.getName().toLowerCase().contains(q) ||
            c.getContactNumber().toLowerCase().contains(q)
        ));
    }
}