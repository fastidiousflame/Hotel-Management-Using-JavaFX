package ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import controller.BookingController;
import controller.RoomController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Booking;

public class MainApp extends Application {

    private final RoomController    roomCtrl = new RoomController();
    private final BookingController bookCtrl = new BookingController(roomCtrl);

    final ObservableList<String> activityLog = FXCollections.observableArrayList();
    private final DateTimeFormatter logFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DashboardController   dashCtrl;
    private RoomsController       roomsCtrl;
    private BookingFxController   bookingCtrl;
    private CheckoutController    checkoutCtrl;
    private CustomersController   customersCtrl;
    private ActivityLogController logCtrl;

    private TabPane tabPane;

    // ── Color palette ──────────────────────────────────────────
    static final String C_BG      = "#F5F0F8";
    static final String C_PANEL   = "#FFFFFF";
    static final String C_BORDER  = "#E0D0EC";
    static final String C_ROSE    = "#C0788C";
    static final String C_ROSE_DK = "#A86070";
    static final String C_PURPLE  = "#7B3F8C";
    static final String C_TEXT    = "#2A1040";
    static final String C_SUBTEXT = "#9B7AAC";
    static final String C_GREEN   = "#3A9A60";
    static final String C_RED     = "#C04060";
    static final String C_BLUE    = "#6090C8";

    @Override
    public void start(Stage stage) throws Exception {
        log("Royal Meridian HMS started");

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
            loadTab("  Dashboard  ", "fxml/Dashboard.fxml"),
            loadTab("  Rooms  ",     "fxml/Rooms.fxml"),
            loadTab("  Booking  ",   "fxml/Booking.fxml"),
            loadTab("  Checkout  ",  "fxml/Checkout.fxml"),
            loadTab("  Guests  ",    "fxml/Customers.fxml"),
            loadTab("  Log  ",       "fxml/ActivityLog.fxml")
        );

        dashCtrl.setContext(roomCtrl, bookCtrl, activityLog, this);
        roomsCtrl.setContext(roomCtrl, this);
        bookingCtrl.setContext(roomCtrl, bookCtrl, this);
        checkoutCtrl.setContext(roomCtrl, bookCtrl, this);
        customersCtrl.setContext(bookCtrl);
        logCtrl.setContext(activityLog, this);

        VBox root = new VBox(buildHeader(), tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        root.setStyle("-fx-background-color:" + C_BG + ";");

        Scene scene = new Scene(root, 1200, 740);
        String css = getClass().getResource("/fxml/hotel.css").toExternalForm();
        scene.getStylesheets().add(css);

        // Fix column header label color — must be done inline
        scene.getStylesheets().add("data:text/css," +
            ".tab-pane > .tab-header-area > .tab-header-background{" +
                "-fx-background-color:#EDE5F5;" +
            "}" +
            ".table-view .column-header .label{" +
                "-fx-text-fill:#7B3F8C;" +
                "-fx-font-weight:bold;" +
                "-fx-font-family:'Segoe UI';" +
            "}" +
            ".table-view{" +
                "-fx-table-cell-border-color:#F0E8F8;" +
            "}"
        );

        stage.setScene(scene);
        stage.setTitle("Royal Meridian — Hotel Management System");
        stage.setMinWidth(950);
        stage.setMinHeight(620);
        stage.show();
    }

    private HBox buildHeader() {
        // Hotel name
        Label hotelName = new Label("ROYAL MERIDIAN");
        hotelName.setStyle(
            "-fx-font-family:'Georgia';" +
            "-fx-font-size:22;" +
            "-fx-text-fill:#7B3F8C;" +
            "-fx-font-weight:bold;" +
            "-fx-letter-spacing:3;"
        );

        Label tagline = new Label("LUXURY HOTEL MANAGEMENT");
        tagline.setStyle(
            "-fx-font-family:'Segoe UI';" +
            "-fx-font-size:9;" +
            "-fx-text-fill:#B090C0;" +
            "-fx-letter-spacing:4;"
        );

        VBox titles = new VBox(3, hotelName, tagline);
        titles.setAlignment(Pos.CENTER_LEFT);

        // Vertical divider
        Separator divider = new Separator(javafx.geometry.Orientation.VERTICAL);
        divider.setStyle("-fx-background-color:#D4B8E0; -fx-pref-width:1; -fx-padding:0 12;");
        HBox.setMargin(divider, new Insets(8, 16, 8, 20));

        // Subtitle
        Label subtitle = new Label("FRONT DESK PORTAL");
        subtitle.setStyle(
            "-fx-font-family:'Segoe UI';" +
            "-fx-font-size:11;" +
            "-fx-text-fill:#B090C0;" +
            "-fx-letter-spacing:2;"
        );

        // Clock pill
        Label clock = new Label();
        clock.setStyle(
            "-fx-font-family:'Segoe UI';" +
            "-fx-font-size:13;" +
            "-fx-text-fill:#7B3F8C;" +
            "-fx-background-color:#EDE5F5;" +
            "-fx-padding:7 18;" +
            "-fx-background-radius:20;" +
            "-fx-border-color:#D4B8E0;" +
            "-fx-border-radius:20;" +
            "-fx-border-width:1;"
        );
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(1), e ->
            clock.setText(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("EEE  dd MMM   HH:mm:ss")))));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(0, titles, divider, subtitle, spacer, clock);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 28, 14, 28));
        header.setStyle(
            "-fx-background-color:#FFFFFF;" +
            "-fx-border-color:#E0D0EC;" +
            "-fx-border-width:0 0 1.5 0;"
        );
        return header;
    }

    @SuppressWarnings("unchecked")
    private Tab loadTab(String tabTitle, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath));
        Node content = loader.load();

        Object ctrl = loader.getController();
        if (ctrl instanceof DashboardController)  dashCtrl      = (DashboardController)  ctrl;
        if (ctrl instanceof RoomsController)       roomsCtrl     = (RoomsController)       ctrl;
        if (ctrl instanceof BookingFxController)   bookingCtrl   = (BookingFxController)   ctrl;
        if (ctrl instanceof CheckoutController)    checkoutCtrl  = (CheckoutController)    ctrl;
        if (ctrl instanceof CustomersController)   customersCtrl = (CustomersController)   ctrl;
        if (ctrl instanceof ActivityLogController) logCtrl       = (ActivityLogController) ctrl;

        return new Tab(tabTitle, content);
    }

    public void refreshDashboard() { if (dashCtrl != null) dashCtrl.refresh(); }

    public RoomsController getRoomsController()     { return roomsCtrl;   }
    public BookingFxController getBookingController() { return bookingCtrl; }
    public void selectTab(int index) { tabPane.getSelectionModel().select(index); }

    public void log(String msg) {
        String entry = "[" + LocalDateTime.now().format(logFmt) + "]  " + msg;
        Platform.runLater(() -> activityLog.add(0, entry));
    }

    public void exportLog() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Activity Log");
        fc.setInitialFileName("meridian_log_"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fc.showSaveDialog(null);
        if (file == null) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("═══════════════════════════════════════════");
            pw.println("   ROYAL MERIDIAN — ACTIVITY LOG");
            pw.println("   Exported: " + LocalDateTime.now().format(logFmt));
            pw.println("═══════════════════════════════════════════");
            activityLog.forEach(pw::println);
            log("Log exported to " + file.getName());
            showAlert("Export Successful", "Log saved to:\n" + file.getAbsolutePath(),
                      Alert.AlertType.INFORMATION);
        } catch (IOException ex) {
            showAlert("Export Failed", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void showInvoiceAlert(Booking b) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Booking Confirmed — Royal Meridian");
        a.setHeaderText("Booking Confirmed");
        a.setContentText(
            "Booking ID  : " + b.getBookingId() + "\n" +
            "Guest       : " + b.getCustomerName() + "\n" +
            "Room        : #" + b.getRoomNumber() + " (" + b.getRoomType() + ")\n" +
            "Check-In    : " + b.getCheckIn() + "\n" +
            "Check-Out   : " + b.getCheckOut() + "\n" +
            "Nights      : " + b.getNights() + "\n" +
            "─────────────────────────────\n" +
            "TOTAL BILL  : \u20B9" + String.format("%.2f", b.getTotalBill()) + "\n\n" +
            "Thank you for choosing Royal Meridian!"
        );
        styleAlert(a);
        a.showAndWait();
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title); a.setHeaderText(null);
        styleAlert(a); a.showAndWait();
    }

    private void styleAlert(Alert a) {
        a.getDialogPane().setStyle(
            "-fx-background-color:#FFFFFF;" +
            "-fx-font-family:'Segoe UI';" +
            "-fx-font-size:13;" +
            "-fx-text-fill:#2A1040;"
        );
    }

    public static void main(String[] args) { launch(args); }
}