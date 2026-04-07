package ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * ActivityLogController.java
 * FXML Controller for ActivityLog.fxml
 */
public class ActivityLogController {

    // ── @FXML fields ──────────────────────────────────────────────
    @FXML private ListView<String> logView;

    // ── Shared context ────────────────────────────────────────────
    private ObservableList<String> activityLog;
    private MainApp mainApp;

    public void setContext(ObservableList<String> log, MainApp app) {
        this.activityLog = log;
        this.mainApp     = app;

        logView.setItems(activityLog);
        logView.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                // Colour by event type
                String color = item.contains("BOOKING")  ? "#1A7A4A"
                             : item.contains("CHECKOUT") ? "#C0392B"
                             : item.contains("Room added") ? "#1565C0"
                             : "#1A1A2E";
                setStyle("-fx-text-fill:" + color + "; -fx-font-family:'Courier New';"
                        + "-fx-font-size:12; -fx-background-color:transparent; -fx-padding:4 8;");
            }
        });
    }

    // ── Handlers ─────────────────────────────────────────────────

    @FXML
    private void onExportLog() {
        mainApp.exportLog();
    }

    @FXML
    private void onClearLog() {
        activityLog.clear();
        mainApp.log("Log cleared by user");
    }
}