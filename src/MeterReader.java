import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MeterReader extends Application {
    Connection con;
    PreparedStatement ps;
    Statement st;
    Stage pstage;
    Scene loginScene;
    String month;
    public static void main(String[] args) throws Exception {
        launch(args);
    }
    public void start(Stage pstage) throws Exception
    {
        this.pstage=pstage;
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/electricity", "root", "");
        Label loginLabel=new Label("Enter Login ID:");
        TextField logintf=new TextField();
        Label passLabel=new Label("Enter Password:");
        PasswordField passwordField=new PasswordField();
        Button loginb=new Button("LOGIN");
        loginb.setOnAction(e ->
        {
            try {
                ps=con.prepareStatement("select * from reader_detail where reader_id=? AND pass=?");
                ps.setInt(1, Integer.parseInt(logintf.getText()));
                ps.setString(2, passwordField.getText());
                ResultSet rs=ps.executeQuery();
                if(rs.next())
                {
                    showMonthPopup(Integer.parseInt(logintf.getText()));
                    //homePage(Integer.parseInt(logintf.getText()),"");
                }
                else{
                    passLabel.setText("Wrong Credentials");
                    loginb.setText("Try Again");
                }
            } catch (Exception ee) {
               ee.printStackTrace();
            }
            
        });
        VBox v=new VBox(10,loginLabel,logintf,passLabel,passwordField,loginb);
        v.setAlignment(Pos.CENTER);
        v.setStyle("-fx-padding: 20;");

        loginScene=new Scene(v,350,250);
        loginScene.getStylesheets().add("stylegroup.css");
        pstage.setScene(loginScene);
        pstage.setTitle("Meter Reader");
        pstage.show();

    }
    Scene homeScene;
    void homePage(int r_id,String month) throws Exception
    {
        ll l=new ll();
        CallableStatement cs=con.prepareCall("{call createMonth(?)}");
        cs.setString(1, month);
        cs.execute();
        int house=0;
        ps=con.prepareStatement("select count(*) from  consumer_detail inner join reader_detail  on consumer_detail.area=reader_detail.workarea where reader_detail.reader_id="+r_id);
        ResultSet rs=ps.executeQuery();
        if(rs.next())
        house=rs.getInt(1);
        
        Label housLabel=new Label("Total Houses: "+house);
        int rem=0;
        PreparedStatement ps1=con.prepareStatement("SELECT count(*) FROM prev_reads2024 INNER JOIN meter_reader ON prev_reads2024.consumer_no=meter_reader.consumer_no  WHERE month=? AND reading=0 AND meter_reader.reader_id= "+r_id);
        ps1.setString(1, month);
        ResultSet rs1=ps1.executeQuery();
        if(rs1.next())
        rem=rs1.getInt(1);
        Label remLabel;
        if(rem>5)
        {
             remLabel=new Label("Remaining Houses: "+5);
        }
        else
         remLabel=new Label("Remaining Houses: "+rem);
        
        ListView<ll.node> lv=new ListView<>();
        ObservableList<ll.node> ol=FXCollections.observableArrayList();
        if(rem>0)
        {
            String sql="SELECT consumer_no,cname FROM consumer_detail Inner join reader_detail on consumer_detail.area=reader_detail.workarea WHERE reader_detail.reader_id = ? AND consumer_detail.consumer_no IN(SELECT consumer_no FROM prev_reads2024 WHERE month=? AND reading=0);";
            PreparedStatement ps2=con.prepareStatement(sql);
            ps2.setString(2, month);
            ps2.setInt(1, r_id );
            ResultSet rs2=ps2.executeQuery();
            while(rs2.next())
            {
                ol.add(l.insert(rs2.getInt(1), rs2.getString(2)));
            }
            //l.printList();
        }
        lv.setItems(ol);
        
        // Add click event to list items
        lv.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                if(!lv.getItems().isEmpty())
                {
                    
                   //System.out.println(dealsListView.getSelectionModel().getSelectedItem().sid);
                    try {
                        int consid = lv.getSelectionModel().getSelectedItem().consumer_no;
                        putread(consid, r_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                }
                else
                {
                    showAlert("It is Empty!!", "Empty", AlertType.WARNING);
                }
               
            }
        });
        Button back=new Button("Back");
        back.setOnAction(e -> {
            try {
                start(pstage);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        GridPane gp=new GridPane();
        gp.add(housLabel, 0, 0);
        gp.add(remLabel, 0, 1);
        gp.add(lv, 0, 2);
        gp.add(back, 0, 3);

        gp.setVgap(10);
        homeScene=new Scene(gp,350,250);
        homeScene.getStylesheets().add("stylegroup.css");
        pstage.setTitle("Home Page");
        pstage.setScene(homeScene);
    }
Scene putreadScene;
    void putread(int consid,int r_id) throws Exception
    {
        st=con.createStatement();
        ResultSet rs=st.executeQuery("SELECT cname,caddress FROM consumer_detail WHERE consumer_no="+consid);
        rs.next();
        Label cnameLabel=new Label("Name: "+rs.getString(1));
        Label considLabel=new Label("Consumer No: "+consid);
        Label caddress=new Label("Address: "+rs.getString(2));
      //  rs.close();
        rs=st.executeQuery("SELECT curread FROM meter_reader WHERE consumer_no="+consid);
        rs.next();
        double prevread=rs.getDouble(1);
        Label prevreadLabel=new Label("Previous Reading: "+prevread);
        Label newreadLabel=new Label("Enter New Readings:");
        TextField newreadTextField=new TextField();
        newreadTextField.setPromptText("Enter New Readings");
        Button newreadButton=new Button("Make Bill");
        BillCalc calc=new BillCalc();
        newreadButton.setOnAction(e ->{
            try {
                con.setAutoCommit(false);
                double newread=Double.parseDouble(newreadTextField.getText());
                double difference=newread-prevread;
                
                if(difference>0)
                {

                    double totalbill=calc.calculate(difference);
                    
                    try {
                    //st.executeUpdate("UPDATE meter_reader SET prevread="+prevread+" WHERE consumer_no="+consid);
                    ps=con.prepareStatement("UPDATE meter_reader SET curread=?,reader_id=?,newamount=?,prevread=? WHERE consumer_no=?");
                    ps.setDouble(1, newread);
                    ps.setInt(2, r_id);
                    ps.setDouble(3, totalbill);
                    ps.setDouble(4, prevread);
                    ps.setInt(5,consid);
                    ps.executeUpdate();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                   boolean comm=showConfirmationDialog("Confirmation", "Check Bill Amount "+totalbill+" rs. \nUnit Consumed:"+prevread+" - "+newread+" = "+difference, "Verify it...");
                  if(comm)
                  {
                    con.commit();
                    homePage(r_id, month);
                    
                  }
                  else
                  {
                    con.rollback();
                  }
                  con.setAutoCommit(true);
                  
                }
                else
                {
                    showAlert("Invalid Entry", "New Read must be greater than previous", AlertType.ERROR);
                }
            } catch (Exception ee) {
                showAlert("Wrong Value Type","Invalid Value Entered", AlertType.WARNING);
            }

        });
        Button back=new Button("Back");
        back.setOnAction(e ->
        {
            try {
                homePage(r_id, month);
            } catch (Exception e1) {
                
                e1.printStackTrace();
            }
        });
        GridPane gp=new GridPane();
        gp.setHgap(5);
        gp.setVgap(5);
        gp.setPadding(new Insets(5, 5, 5, 5));
        gp.add(cnameLabel, 0, 0);
        gp.add(considLabel, 0, 1);
        gp.add(caddress,0,2);
        gp.add(prevreadLabel,0,3);
        gp.add(newreadLabel, 0, 4);
        gp.add(newreadTextField, 1, 4);
        gp.add(newreadButton,0,5);
        gp.add(back,0,6);

        ColumnConstraints col1 = new ColumnConstraints();
         col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
         gp.getColumnConstraints().addAll(col1, col2);

        putreadScene=new Scene(gp,300,250);
        pstage.setScene(putreadScene);
        pstage.setTitle("New Readings");
    }

 void showAlert(String title, String message, AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
}

void showMonthPopup(int r_id) throws Exception {
    // Create a list of months
    List<String> months = Arrays.asList("January", "February", "March", "April", "May", 
                                        "June", "July", "August", "September", "October", 
                                        "November", "December");

    // Create a ChoiceDialog with the list of months
    ChoiceDialog<String> dialog = new ChoiceDialog<>("January", months);
    dialog.setTitle("Select Month");
    dialog.setHeaderText(null);
    dialog.setContentText("Please select the name of the month:");

    // Show the dialog and capture the result
    Optional<String> result = dialog.showAndWait();
    result.ifPresent(month -> {
        try {
            this.month = month;
            homePage(r_id, month);
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}
 private boolean showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
    
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
class ll //Linked List
{
    class node
    {
        int consumer_no;
        String cname;
        node next;
        node(int consumer_no,String cname)
        {
            this.consumer_no=consumer_no;
            this.cname=cname;
            this.next=null;
        }
        @Override
        public String toString() {
            return "Consumer_no: " + consumer_no + " ~ Name: " + cname;
        }
        
    }
    node first=null;
    node insert(int consumer_no, String cname) {
        node newNode = new node(consumer_no, cname);
        if (first == null) {
            first = newNode;
        } else {
            node current = first;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        return newNode;
    }

   
    void delete(int consumer_no) {
        if (first == null) {
           // System.out.println("List is empty.");
            return;
        }

        if (first.consumer_no == consumer_no) {
            first = first.next;
           // System.out.println("Consumer with number " + consumer_no + " deleted.");
            return;
        }

        node current = first;
        node previous = null;
        
        while (current != null && current.consumer_no != consumer_no) {
            previous = current;
            current = current.next;
        }

        if (current == null) {
           // System.out.println("Consumer with number " + consumer_no + " not found.");
            return;
        }

        previous.next = current.next;
       // System.out.println("Consumer with number " + consumer_no + " deleted.");
    }
    void printList() {
        node current = first;
        while (current != null) {
            System.out.println("Consumer Number: " + current.consumer_no + ", Name: " + current.cname);
            current = current.next;
        }
    }
}
class BillCalc
{
    double calculate(double units)
    {
        double energycharge=0;
        int choice=0;
        if(units<=50)
        {
           choice=1;
        }
        else if(units<=100)
        {
            choice=2;
        }
        else if(units<=250)
        {
            choice=3;
        }
        else
        {
            choice=4;
        }
        switch (choice) {
            case 1:
                energycharge=units*3.05;                
                break;
            case 2:
            energycharge=50*3.05;
            units=units-50;
            energycharge+=units*3.5;
            break;
            case 3:
            energycharge=50*3.05;
            units=units-50;
            energycharge+=50*3.5;
            units=units-50;
            energycharge+=units*4.05;
            break;

            case 4:
            energycharge=50*3.05;
            units=units-50;
            energycharge+=50*3.5;
            units=units-50;
            energycharge+=150*4.5;
            units=units-150;
            energycharge+=units*5.2;
            break;


            default:
                break;
        }
        double fuelcharge=units*2.85;
        double electricityduty=energycharge*0.15;
        return energycharge+fuelcharge+electricityduty;
    }
}