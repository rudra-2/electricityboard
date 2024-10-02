import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class ElectricityBillApp extends Application {
    private Connection con;
    private Statement st;
    private Scene mainScene;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/electricity", "root", "");
        st = con.createStatement();
        this.primaryStage = primaryStage;
        if (con != null)
            System.out.println("Connection established");
        else
            System.out.println("Connection not established");

        Label cidLabel = new Label("Enter Consumer no.:");
        TextField cidtf = new TextField();
        Button cidcheck = new Button("Check");
        Label notfoundcid = new Label();
        notfoundcid.getStyleClass().add("error-label");

        cidcheck.setOnAction(e -> {
            try {
                int consid = Integer.parseInt(cidtf.getText());
                ResultSet rs = st.executeQuery("SELECT * FROM consumer_detail WHERE consumer_no=" + consid);
                if (rs.next()) {
                    afterCheck(consid);
                } else {
                    notfoundcid.setText("Please Enter Correct Cons.No.:");
                }
            } catch (Exception ee) {
               System.out.println(ee.getLocalizedMessage());
            }
        });

        VBox v = new VBox(10, cidLabel, cidtf, cidcheck, notfoundcid);
        v.setPadding(new Insets(10));
        v.getStyleClass().add("main-container");

        mainScene = new Scene(v, 350, 200);
        mainScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setTitle("Consumer");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    Scene home;
     void afterCheck(int consid) throws Exception {
        ResultSet rs = st.executeQuery("SELECT cname FROM consumer_detail WHERE consumer_no=" + consid);
        Statement st1=con.createStatement();
        ResultSet rs1=st1.executeQuery("SELECT sum(dueamount),min(duedate),min(status) FROM history WHERE consumer_no="+consid+" AND dueamount<>0");
        rs.next();
        rs1.next();
            double dueamount = rs1.getDouble(1);
            String cname = rs.getString(1);
            boolean status=rs1.getBoolean(3);
            System.out.println(status);
            Label showname = new Label("Name: " + cname);
            Label showdue = new Label("Total Due: " + dueamount);
            Label duedate = new Label("Due Date: " + rs1.getDate(2));
            Label paybillLabel = new Label("PAYMENT");
            Menu m = new Menu("Select option");
            MenuItem m1 = new MenuItem("UPI");
            MenuItem m2 = new MenuItem("CARD");
            MenuBar mb = new MenuBar();
            m.getItems().addAll(m1, m2);
            mb.getMenus().add(m);
            TextField paytf = new TextField();
            paytf.setPromptText("Enter Payment Id");
            Button payb = new Button("PAY");
            Label paidl = new Label();
            Label updateDetailsLabel = new Label("Update Details:");
            Button updateDetailsButton = new Button("Update");

            if (status == true) {
                m.setDisable(true);
                payb.setDisable(true);
                paytf.setDisable(true);
            }

            m1.setOnAction(e -> m.setText("upi"));
            m2.setOnAction(e -> m.setText("card"));

            payb.setOnAction(e -> {
                String paymentMethod = m.getText();
                if (paymentMethod.equals("upi")) {
                    paidl.setText("Payment received");
                    try {
                        st.executeUpdate("UPDATE history SET dueamount=0,status=true WHERE consumer_no=" + consid);
                        showdue.setText("Current Due: 0");
                        primaryStage.setScene(home);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                } else if (paymentMethod.equals("card")) {
                    try {
                        Integer.parseInt(paytf.getText());
                        paidl.setText("Payment received");
                        st.executeUpdate("UPDATE history SET dueamount=0,status=true WHERE consumer_no=" + consid);
                        showdue.setText("Current Due: 0");
                        primaryStage.setScene(home);
                    } catch (Exception ee) {
                        paidl.setText("Enter Correct Number");
                    }
                } else {
                    paidl.setText("Please select a payment method");
                }
            });

            updateDetailsButton.setOnAction(e -> {
                try {
                    updateDetails(consid);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            });

            Button payback = new Button("Back");
            payback.setOnAction(e ->{
                primaryStage.setTitle("Consumer Login");
                primaryStage.setScene(mainScene);
            } );

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(showname, 0, 0);
            grid.add(updateDetailsLabel, 1, 0);
            grid.add(updateDetailsButton, 2, 0);
            grid.add(showdue, 0, 1);
            grid.add(duedate, 1, 1);
            grid.add(paybillLabel, 0, 2);
            grid.add(mb, 0, 3);
            grid.add(paytf, 0, 4);
            grid.add(payb, 0, 5);
            grid.add(paidl, 0, 6);
            grid.add(payback, 0, 7);

            home = new Scene(grid, 500, 350);
            home.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

            primaryStage.setScene(home);
            primaryStage.setTitle("Bill Details");
       
    }

    void updateDetails(int consid) throws Exception {
        Label currmobo = new Label("Current Mobile No: ");
        ResultSet rs = st.executeQuery("select cmobile from consumer_detail where consumer_no=" + consid);

        Label mobo;
        if (rs.next()) {
            mobo = new Label("" + rs.getLong(1));
        } else {
            mobo = new Label("NO CONSUMER");
        }

        Label newmobo = new Label("Enter New Mobile no.:");
        TextField newmobotf = new TextField();
        newmobotf.setPromptText("Enter Number Only");
        Button newmobob = new Button("Update");
        Label updonLabel=new Label();
        newmobob.setOnAction(e -> {
            try {
                PreparedStatement ps = con.prepareStatement("UPDATE consumer_detail set cmobile=? where consumer_no=?");
                ps.setLong(1, Long.parseLong(newmobotf.getText()));
                ps.setInt(2, consid);
                ps.executeUpdate();
                updonLabel.setText("Update Done");
                updateDetails(consid);
            } catch (Exception ee) {
                updonLabel.setText("Enter Valid Number :");
            }
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(currmobo, 0, 0);
        grid.add(mobo, 1, 0);
        grid.add(newmobo, 0, 1);
        grid.add(newmobotf, 1, 1);
        grid.add(newmobob, 1, 2);

        Button payback = new Button("Back");
        payback.setOnAction(e -> {primaryStage.setScene(home);
        primaryStage.setTitle("Bill Details");});
        grid.add(payback, 0, 2);

        Scene upScene = new Scene(grid, 450, 350);
        upScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(upScene);
        primaryStage.setTitle("Update");
    }

    @Override
    public void stop() throws Exception {
        if (st != null) st.close();
        if (con != null) con.close();
        super.stop();
    }
}
