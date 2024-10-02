import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.*;

public class CompanyApp extends Application {
    private Stage primaryStage;
    private Connection con;
    private Scene dueBillsScene, meterReadersScene, cashiersScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/electricity", "root", "");

            VBox dueBillsContent = createDueBillsContent();
            VBox meterReadersContent = createMeterReadersContent();
            VBox cashiersContent = createCashiersContent();

            dueBillsScene = createScene(dueBillsContent, "Due Bills");
            meterReadersScene = createScene(meterReadersContent, "Meter Readers");
            cashiersScene = createScene(cashiersContent, "Cashiers");

            primaryStage.setScene(dueBillsScene);
            primaryStage.setTitle("Electricity Billing Management");
            primaryStage.show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createDueBillsContent() {
        VBox dueBillsContent = new VBox(10);
        dueBillsContent.setPadding(new javafx.geometry.Insets(10));
        dueBillsContent.setAlignment(Pos.CENTER);

        Label dueBillsLabel = new Label("Due Bills");
        dueBillsLabel.setFont(Font.font("Arial", 20));
        ListView<String> dueBillsList = new ListView<>();
        dueBillsList.setPrefHeight(300);
        refreshDueBills(dueBillsList);

        dueBillsContent.getChildren().addAll(dueBillsLabel, dueBillsList);
        return dueBillsContent;
    }

    private VBox createMeterReadersContent() {
        VBox meterReadersContent = new VBox(10);
        meterReadersContent.setPadding(new javafx.geometry.Insets(10));
        meterReadersContent.setAlignment(Pos.CENTER);

        Label meterReadersLabel = new Label("Meter Readers");
        meterReadersLabel.setFont(Font.font("Arial", 20));

        ListView<String> meterReadersList = new ListView<>();
        meterReadersList.setPrefHeight(300);
        refreshMeterReaders(meterReadersList);

        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");

        addButton.setOnAction(e -> showAddMeterReaderDialog(meterReadersList));

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        removeButton.setOnAction(e -> showRemoveMeterReaderDialog(meterReadersList));

        HBox actionButtons = new HBox(10);
        actionButtons.getChildren().addAll(addButton, removeButton);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setSpacing(10);

        meterReadersContent.getChildren().addAll(meterReadersLabel, meterReadersList, actionButtons);
        return meterReadersContent;
    }

    private VBox createCashiersContent() {
        VBox cashiersContent = new VBox(10);
        cashiersContent.setPadding(new javafx.geometry.Insets(10));
        cashiersContent.setAlignment(Pos.CENTER);

        Label cashiersLabel = new Label("Cashiers");
        cashiersLabel.setFont(Font.font("Arial", 20));

        ListView<String> cashiersList = new ListView<>();
        cashiersList.setPrefHeight(300);
        refreshCashiers(cashiersList);

        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");

        addButton.setOnAction(e -> showAddCashierDialog(cashiersList));

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        removeButton.setOnAction(e -> showRemoveCashierDialog(cashiersList));

        HBox actionButtons = new HBox(10);
        actionButtons.getChildren().addAll(addButton, removeButton);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setSpacing(10);

        cashiersContent.getChildren().addAll(cashiersLabel, cashiersList, actionButtons);
        return cashiersContent;
    }

    private Scene createScene(VBox content, String title) {
        BorderPane root = new BorderPane();
        root.setTop(createTopPanel(title));
        root.setCenter(content);
        root.setBottom(createBottomPanel());

        return new Scene(root, 800, 600, Color.BLACK);
    }

    private HBox createTopPanel(String title) {
        HBox topPanel = new HBox();
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setStyle("-fx-background-color: #000000; -fx-padding: 10;");
        Text welcomeText = new Text("Welcome to the " + title + " Portal");
        welcomeText.setFill(Color.ORANGE);
        welcomeText.setFont(Font.font("Arial", 24));
        topPanel.getChildren().add(welcomeText);

        return topPanel;
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox();
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color: #000000; -fx-padding: 10;");

        Button dueBillsButton = new Button("Due Bills");
        Button meterReadersButton = new Button("Meter Readers");
        Button cashiersButton = new Button("Cashiers");

        dueBillsButton.setOnAction(e -> primaryStage.setScene(dueBillsScene));
        meterReadersButton.setOnAction(e -> primaryStage.setScene(meterReadersScene));
        cashiersButton.setOnAction(e -> primaryStage.setScene(cashiersScene));

        dueBillsButton.setStyle("-fx-background-color: #FFA500;-fx-pref-width: 150px;-fx-pref-height: 50px; -fx-text-fill: #FFFFFF;");
        meterReadersButton.setStyle("-fx-background-color: #FFA500;-fx-pref-width: 150px;-fx-pref-height: 50px; -fx-text-fill: #FFFFFF;");
        cashiersButton.setStyle("-fx-background-color: #FFA500;-fx-pref-width: 150px;-fx-pref-height: 50px; -fx-text-fill: #FFFFFF;");

        HBox.setHgrow(dueBillsButton, Priority.ALWAYS);
        HBox.setHgrow(meterReadersButton, Priority.ALWAYS);
        HBox.setHgrow(cashiersButton, Priority.ALWAYS);

        bottomPanel.getChildren().addAll(dueBillsButton, meterReadersButton, cashiersButton);

        return bottomPanel;
    }

    private void refreshDueBills(ListView<String> dueBillsList) {
        dueBillsList.getItems().clear();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT consumer_no, dueamount, duedate FROM history WHERE status = 0");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int consumerNo = rs.getInt("consumer_no");
                double dueAmount = rs.getDouble("dueamount");
                Date dueDate = rs.getDate("duedate");
                dueBillsList.getItems().add("Consumer No: " + consumerNo + ", Amount: " + dueAmount + ", Due Date: " + dueDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshMeterReaders(ListView<String> meterReadersList) {
        meterReadersList.getItems().clear();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT reader_id, reader_name, rmobile, workarea FROM reader_detail");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int readerId = rs.getInt("reader_id");
                String readerName = rs.getString("reader_name");
                int rmobile = rs.getInt("rmobile");
                String workarea = rs.getString("workarea");
                meterReadersList.getItems().add("ID: " + readerId + ", Name: " + readerName + ", Mobile: " + rmobile + ", Area: " + workarea);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshCashiers(ListView<String> cashiersList) {
        cashiersList.getItems().clear();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT cashier_id, cashier_name FROM cashier");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int cashierId = rs.getInt("cashier_id");
                String cashierName = rs.getString("cashier_name");
                cashiersList.getItems().add("ID: " + cashierId + ", Name: " + cashierName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddMeterReaderDialog(ListView<String> meterReadersList) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Meter Reader");
        dialog.setHeaderText("Enter Meter Reader Details:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        idField.setPromptText("Reader ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Reader Name");
        TextField mobileField = new TextField();
        mobileField.setPromptText("Mobile");
        TextField areaField = new TextField();
        areaField.setPromptText("Work Area");
        TextField passField = new TextField();
        passField.setPromptText("Password");

        grid.add(new Label("Reader ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Mobile:"), 0, 2);
        grid.add(mobileField, 1, 2);
        grid.add(new Label("Work Area:"), 0, 3);
        grid.add(areaField, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(passField, 1, 4);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String id = idField.getText();
                String name = nameField.getText();
                String mobile = mobileField.getText();
                String area = areaField.getText();
                String pass = passField.getText();

                try {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO reader_detail (reader_id, reader_name, rmobile, workarea, pass) VALUES (?, ?, ?, ?, ?)");
                    ps.setInt(1, Integer.parseInt(id));
                    ps.setString(2, name);
                    ps.setInt(3, Integer.parseInt(mobile));
                    ps.setString(4, area);
                    ps.setString(5, pass);
                    ps.executeUpdate();
                    refreshMeterReaders(meterReadersList);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showRemoveMeterReaderDialog(ListView<String> meterReadersList) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove Meter Reader");
        dialog.setHeaderText("Enter Meter Reader ID to Remove:");
        dialog.setContentText("Reader ID:");

        dialog.showAndWait().ifPresent(id -> {
            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM reader_detail WHERE reader_id = ?");
                ps.setInt(1, Integer.parseInt(id));
                ps.executeUpdate();
                refreshMeterReaders(meterReadersList);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void showAddCashierDialog(ListView<String> cashiersList) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Cashier");
        dialog.setHeaderText("Enter Cashier Details:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        idField.setPromptText("Cashier ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Cashier Name");
        TextField passField = new TextField();
        passField.setPromptText("Password");

        grid.add(new Label("Cashier ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passField, 1, 2);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String id = idField.getText();
                String name = nameField.getText();
                String pass = passField.getText();

                try {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO cashier (cashier_id, cashier_name, cashier_pass) VALUES (?, ?, ?)");
                    ps.setInt(1, Integer.parseInt(id));
                    ps.setString(2, name);
                    ps.setString(3, pass);
                    ps.executeUpdate();
                    refreshCashiers(cashiersList);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showRemoveCashierDialog(ListView<String> cashiersList) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove Cashier");
        dialog.setHeaderText("Enter Cashier ID to Remove:");
        dialog.setContentText("Cashier ID:");

        dialog.showAndWait().ifPresent(id -> {
            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM cashier WHERE cashier_id = ?");
                ps.setInt(1, Integer.parseInt(id));
                ps.executeUpdate();
                refreshCashiers(cashiersList);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void stop() throws Exception {
        if (con != null) {
            con.close();
        }
    }
}
