package model;

import javafx.beans.property.*;

/**
 * Customer.java — Stores guest information.
 * JavaFX Properties used for live TableView binding.
 */
public class Customer {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty contactNumber = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty idProof = new SimpleStringProperty(); // Passport / Aadhar / etc.
    private final StringProperty customerId = new SimpleStringProperty();

    private static int idCounter = 1000;

    public Customer(String name, String contactNumber, String email, String idProof) {
        this.customerId.set("CUST-" + (++idCounter));
        this.name.set(name);
        this.contactNumber.set(contactNumber);
        this.email.set(email);
        this.idProof.set(idProof);
    }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getContactNumber() { return contactNumber.get(); }
    public StringProperty contactNumberProperty() { return contactNumber; }

    public String getEmail() { return email.get(); }
    public StringProperty emailProperty() { return email; }

    public String getIdProof() { return idProof.get(); }
    public StringProperty idProofProperty() { return idProof; }

    public String getCustomerId() { return customerId.get(); }
    public StringProperty customerIdProperty() { return customerId; }

    @Override
    public String toString() { return getName() + " (" + getCustomerId() + ")"; }
}