import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CashierApp extends Application {
    private Connection con;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // Database connection setup
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/electricity", "root", "");

        showLoginView();
    }

    private void showLoginView() {
        Label loginLabel = new Label("Enter Client ID:");
        loginLabel.setStyle("-fx-text-fill: #FFA500; -fx-font-size: 16px;");

        TextField idtf = new TextField();
        idtf.setPromptText("Client ID");
        idtf.setStyle("-fx-background-color: #333333; -fx-text-fill: #FFFFFF; -fx-prompt-text-fill: #AAAAAA;");
        idtf.setPrefWidth(300);
        idtf.setPrefHeight(40);

        Label passLabel = new Label("Enter Password:");
        passLabel.setStyle("-fx-text-fill: #FFA500; -fx-font-size: 16px;");

        PasswordField passf = new PasswordField();
        passf.setPromptText("Password");
        passf.setStyle("-fx-background-color: #333333; -fx-text-fill: #FFFFFF; -fx-prompt-text-fill: #AAAAAA;");
        passf.setPrefWidth(300);
        passf.setPrefHeight(40);

        Button loginb = new Button("Login");
        loginb.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;");
        loginb.setPrefWidth(300);
        loginb.setPrefHeight(50);

        loginb.setOnAction(e -> {
            int cid = Integer.parseInt(idtf.getText());
            try {
                PreparedStatement ps = con.prepareStatement("SELECT cashier_pass FROM cashier WHERE cashier_id = ?");
                ps.setInt(1, cid);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (passf.getText().equals(rs.getString(1))) {
                        showHomeView(cid);
                    } else {
                        loginb.setText("WRONG PASS");
                    }
                } else {
                    loginLabel.setText("Incorrect ID, try again");
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        });

        VBox vbox = new VBox(15, loginLabel, idtf, passLabel, passf, loginb);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #000000; -fx-padding: 20;");
        
        Scene loginScene = new Scene(vbox, 500, 400);
        primaryStage.setTitle("Cashier Portal");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

   private void showHomeView(int cid) throws Exception {
    // Create a small back button and place it in the upper left corner
    Button backButton = new Button("Back");
    backButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 12px;");
    backButton.setPrefWidth(80);
    backButton.setPrefHeight(30);
    backButton.setOnAction(e -> returnToLogin());

    HBox topLeftCorner = new HBox(backButton);
    topLeftCorner.setAlignment(Pos.TOP_LEFT);
    topLeftCorner.setStyle("-fx-padding: 10;");

    // Labels and Input Fields
    Label consLabel = new Label("Enter Consumer No.:");
    consLabel.setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold; -fx-font-size: 14px;");

    TextField consField = new TextField();
    consField.setPromptText("Consumer Number");
    consField.setStyle("-fx-background-color: #333333; -fx-text-fill: #FFA500; -fx-font-size: 14px; -fx-prompt-text-fill: #888888;");
    consField.setPrefWidth(200);

    Button getButton = new Button("Get Details");
    getButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 14px;");
    getButton.setPrefWidth(120);

    Label nameLabel = new Label("Name: ");
    nameLabel.setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold; -fx-font-size: 14px;");

    Label dueLabel = new Label("Due Amount: ");
    dueLabel.setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold; -fx-font-size: 14px;");

    Label duedateLabel = new Label("Due Date: ");
    duedateLabel.setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold; -fx-font-size: 14px;");

    // Payment Options
    RadioButton cashRadio = new RadioButton("Cash");
    cashRadio.setStyle("-fx-text-fill: #FFA500; -fx-font-size: 14px;");
    cashRadio.setSelected(true);

    RadioButton chequeRadio = new RadioButton("Cheque");
    chequeRadio.setStyle("-fx-text-fill: #FFA500; -fx-font-size: 14px;");

    ToggleGroup paymentToggleGroup = new ToggleGroup();
    cashRadio.setToggleGroup(paymentToggleGroup);
    chequeRadio.setToggleGroup(paymentToggleGroup);

    Button payButton = new Button("PAY");
    payButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;");
    payButton.setPrefWidth(100);

    // GridPane for arranging components
    GridPane grid = new GridPane();
    grid.setVgap(15);
    grid.setHgap(15);
    grid.setAlignment(Pos.CENTER);
    grid.setStyle("-fx-padding: 20; -fx-background-color: #000000;");

    grid.add(consLabel, 0, 0);
    grid.add(consField, 1, 0);
    grid.add(getButton, 2, 0);

    grid.add(nameLabel, 0, 1, 3, 1);
    grid.add(dueLabel, 0, 2, 3, 1);
    grid.add(duedateLabel, 0, 3, 3, 1);

    HBox paymentOptions = new HBox(20, cashRadio, chequeRadio);
    paymentOptions.setAlignment(Pos.CENTER);
    grid.add(paymentOptions, 0, 4, 3, 1);

    grid.add(payButton, 0, 5, 3, 1);
    GridPane.setHalignment(payButton, HPos.CENTER);

    // Event Handlers
    getButton.setOnAction(e -> {
        String consNumber = consField.getText().trim();
        if (consNumber.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please enter a valid Consumer Number.");
            return;
        }
        try {
            int consid = Integer.parseInt(consNumber);
            PreparedStatement ps = con.prepareStatement("SELECT cname FROM consumer_detail WHERE consumer_no = ?");
            ps.setInt(1, consid);
            ResultSet rs = ps.executeQuery();

            PreparedStatement ps1 = con.prepareStatement("SELECT SUM(dueamount), MIN(duedate) FROM history WHERE consumer_no = ? AND dueamount <> 0 AND status = false");
            ps1.setInt(1, consid);
            ResultSet rs1 = ps1.executeQuery();

            if (rs.next()) {
                String consumerName = rs.getString("cname");
                nameLabel.setText("Name: " + consumerName);
            } else {
                showAlert(AlertType.ERROR, "Not Found", "Consumer not found.");
                nameLabel.setText("Name: ");
                dueLabel.setText("Due Amount: ");
                duedateLabel.setText("Due Date: ");
                return;
            }

            if (rs1.next() && rs1.getDouble(1) > 0) {
                double dueAmount = rs1.getDouble(1);
                Date dueDate = rs1.getDate(2);
                dueLabel.setText("Due Amount: Rs. " + String.format("%.2f", dueAmount));
                duedateLabel.setText("Due Date: " + dueDate.toString());
            } else {
                showAlert(AlertType.INFORMATION, "No Dues", "All bills are paid for this consumer.");
                dueLabel.setText("Due Amount: Rs. 0.00");
                duedateLabel.setText("Due Date: N/A");
            }
        } catch (NumberFormatException ex) {
            showAlert(AlertType.ERROR, "Input Error", "Consumer Number must be a valid integer.");
        } catch (SQLException ex) {
            showAlert(AlertType.ERROR, "Database Error", "Error fetching consumer details: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    payButton.setOnAction(e -> {
        String consNumber = consField.getText().trim();
        if (consNumber.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please enter a valid Consumer Number.");
            return;
        }
        try {
            int consid = Integer.parseInt(consField.getText());
            String dueAmountText = dueLabel.getText().replace("Due Amount: $", "").trim();
            if (dueAmountText.isEmpty() || dueAmountText.equals("0.00")) {
                showAlert(AlertType.INFORMATION, "No Dues", "There is no due amount to pay.");
                return;
            }
            //double dueAmount = Double.parseDouble(dueAmountText);

            if (cashRadio.isSelected()) {
                PreparedStatement ps = con.prepareStatement("UPDATE history SET status = true WHERE consumer_no = ? AND status = false");
                ps.setInt(1, consid);
                int updatedRows = ps.executeUpdate();
                if (updatedRows > 0) {
                    showAlert(AlertType.INFORMATION, "Payment Successful", "Cash payment of $" + String.format("%.2f", dueAmountText) + " received for Consumer No: " + consid);
                    showHomeView(cid);
                } else {
                    showAlert(AlertType.ERROR, "Payment Error", "Failed to update payment status.");
                }
            } else if (chequeRadio.isSelected()) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Cheque Payment");
                dialog.setHeaderText("Enter Cheque Number:");
                dialog.setContentText("Cheque Number:");

                DialogPane dialogPane = dialog.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("alert.css").toExternalForm());
                dialogPane.getStyleClass().add("myDialog");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String chequeNumber = result.get().trim();
                    if (chequeNumber.isEmpty()) {
                        showAlert(AlertType.ERROR, "Input Error", "Cheque Number cannot be empty.");
                        return;
                    }
                    PreparedStatement ps = con.prepareStatement("INSERT INTO chequepay (cheque_no, consumer_no, cashier_id, status) VALUES (?, ?, ?, 'pending')");
                    ps.setString(1, chequeNumber);
                    ps.setInt(2, consid);
                    ps.setInt(3, cid);
                    ps.executeUpdate();
                    showAlert(AlertType.INFORMATION, "Cheque Submitted", "Cheque payment submitted for verification.");
                    showHomeView(cid);
                }
            } else {
                showAlert(AlertType.ERROR, "Payment Method", "Please select a payment method.");
            }
        } catch (NumberFormatException ex) {
            showAlert(AlertType.ERROR, "Input Error", "Consumer Number and Due Amount must be valid numbers.");
            System.out.println(ex.getLocalizedMessage());
        } catch (SQLException ex) {
            showAlert(AlertType.ERROR, "Database Error", "Error processing payment: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    // Pay Bill and Verify Cheques buttons at the bottom
    Button payBillButton = new Button("Pay Bills");
    payBillButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;");
    payBillButton.setPrefHeight(50);
    payBillButton.setPrefWidth(150);
    payBillButton.setOnAction(e -> {
        try {
            showHomeView(cid);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    });

    Button verifyChequeButton = new Button("Verify Cheques");
    verifyChequeButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;");
    verifyChequeButton.setPrefHeight(50);
    verifyChequeButton.setPrefWidth(150);
    verifyChequeButton.setOnAction(e -> {
        try {
            showVerifyChequeView(cid);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    });

    HBox bottomPanel = new HBox(30, payBillButton, verifyChequeButton);
    bottomPanel.setAlignment(Pos.CENTER);
    bottomPanel.setStyle("-fx-padding: 20; -fx-background-color: #000000;");

    BorderPane layout = new BorderPane();
    layout.setTop(topLeftCorner);
    layout.setCenter(grid);
    layout.setBottom(bottomPanel);
    layout.setStyle("-fx-background-color: #000000;");

    Scene homeScene = new Scene(layout, 700, 600);
    primaryStage.setTitle("Cashier Portal - Home");
    primaryStage.setScene(homeScene);
    primaryStage.show();
}


  private void showVerifyChequeView(int cid) throws Exception {
    // Create a small back button and place it in the upper left corner
    Button backButton = new Button("Back");
    backButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 12px;");
    backButton.setPrefWidth(80);
    backButton.setPrefHeight(30);
    backButton.setOnAction(e -> {
        try {
            showHomeView(cid);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    });

    HBox topLeftCorner = new HBox(backButton);
    topLeftCorner.setAlignment(Pos.TOP_LEFT);
    topLeftCorner.setStyle("-fx-padding: 10;");

    // List of pending cheques
    ListView<String> pendingChequeList = new ListView<>();
    populatePendingCheques(cid, pendingChequeList);

    // Upload .txt file button
    Button uploadButton = new Button("Upload Cheque Status File");
    uploadButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;");
    uploadButton.setOnAction(e -> handleFileUpload(cid, pendingChequeList));

    VBox centerBox = new VBox(10, pendingChequeList, uploadButton);
    centerBox.setAlignment(Pos.CENTER);
    centerBox.setStyle("-fx-padding: 20;");

    // Pay Bill and Verify Cheques buttons at the bottom
    Button payBillButton = new Button("Pay Bills");
    payBillButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;");
    payBillButton.setPrefHeight(50);
    payBillButton.setPrefWidth(150);
    payBillButton.setOnAction(e -> {
        try {
            showHomeView(cid);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    });

    Button verifyChequeButton = new Button("Verify Cheques");
    verifyChequeButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;");
    verifyChequeButton.setPrefHeight(50);
    verifyChequeButton.setPrefWidth(150);
    verifyChequeButton.setOnAction(e -> {
        try {
            showVerifyChequeView(cid);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    });

    HBox bottomPanel = new HBox(30, payBillButton, verifyChequeButton);
    bottomPanel.setAlignment(Pos.CENTER);
    bottomPanel.setStyle("-fx-padding: 20; -fx-background-color: #000000;");


    BorderPane layout = new BorderPane();
    layout.setTop(topLeftCorner);
    layout.setCenter(centerBox);
    layout.setBottom(bottomPanel);
    layout.setStyle("-fx-background-color: #000000;");

    Scene homeScene = new Scene(layout, 700, 600);
    primaryStage.setTitle("Cashier Portal - Verify Cheques");
    primaryStage.setScene(homeScene);
    primaryStage.show();
}

private void populatePendingCheques(int cid, ListView<String> pendingChequeList) {
    try {
        String query = "SELECT c.cheque_no, cust.cname, cust.consumer_no " +
                       "FROM chequepay c " +
                       "JOIN consumer_detail cust ON c.consumer_no = cust.consumer_no " +
                       "WHERE c.status = 'Pending' AND c.cashier_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, cid);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String chequeNo = rs.getString("cheque_no");
            String consumerName = rs.getString("cname");
            String consumerNo = rs.getString("consumer_no");
            String displayText = "Cheque No: " + chequeNo + " | Consumer: " + consumerName + " | Consumer No: " + consumerNo;
            pendingChequeList.getItems().add(displayText);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


private void handleFileUpload(int cid, ListView<String> pendingChequeList) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Cheque Status File");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
    File file = fileChooser.showOpenDialog(primaryStage);
    if (file != null) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    updateChequeStatus(cid, parts[0], parts[1]);
                    pendingChequeList.getItems().remove("Cheque No: " + parts[0]);
                }
            }
            showAlert(Alert.AlertType.INFORMATION, "Update Success", "Cheque statuses updated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

private void updateChequeStatus(int cid, String chequeNo, String status) {
    try {
        PreparedStatement ps = con.prepareStatement("UPDATE chequepay SET status = ? WHERE cheque_no = ? AND cashier_id = ?");
        ps.setString(1, status.equals("cleared") ? "Cleared" : "Bounced");
        ps.setString(2, chequeNo);
        ps.setInt(3, cid);
        ps.executeUpdate();
        ResultSet rs=con.createStatement().executeQuery("SELECT consumer_no FROM chequepay WHERE cheque_no='"+chequeNo+"'");
        rs.next();
        PreparedStatement historyPs = con.prepareStatement("UPDATE history SET status=? WHERE consumer_no=?");
        historyPs.setInt(2, rs.getInt(1));
        historyPs.setBoolean(1, status.equals("cleared") ? true : false);
        historyPs.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    private void returnToLogin() {
        try {
            showLoginView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("alert.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        alert.showAndWait();
    }
}
