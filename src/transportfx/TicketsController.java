/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transportfx;

import classes.DbHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author danml
 */
public class TicketsController implements Initializable {

    @FXML
    private ImageView imgBack;
    private Connection conn;
    private DbHandler handler;
    @FXML
    private StackPane rootPane;
    @FXML
    private TableView tableTickets;
    @FXML
    private ImageView imgBus;
    @FXML
    private JFXTextField txtPNo;
    @FXML
    private JFXTextField txtPName;
    @FXML
    private JFXTextField txtPAddress;
    @FXML
    private JFXTextField txtContactNo;
    @FXML
    private JFXRadioButton rdMale;
    @FXML
    private JFXRadioButton rdFemale;
    @FXML
    private JFXTextField txtTcktId;
    @FXML
    private JFXComboBox<String> comboOrigin;
    @FXML
    private JFXComboBox<String> comboDest;
    @FXML
    private JFXTextField txtNoTckts;
    @FXML
    private JFXTextField txtAmnt;
    @FXML
    private JFXTextField txtRouteName;
    @FXML
    private JFXComboBox<String> comboRouteId;
    @FXML
    private JFXComboBox<String> comboBusNo;
    @FXML
    private JFXDatePicker dtBoardDate;
    @FXML
    private JFXButton bookTicket;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Instantian Dbhandler class
        handler = new DbHandler();
        // populate combos
        populateCombos();

        //Populate tables
        buildDataTable();
    }

    @FXML
    private void goBack(MouseEvent event) {
        try {
            imgBack.getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("Menus.fxml"));
            Scene scene = new Scene(root);
            Stage menuStage = new Stage();
            menuStage.setScene(scene);
            menuStage.show();
        } catch (IOException ex) {
            Logger.getLogger(DriversController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearField() {
        txtPNo.setText(null);
        txtPName.setText(null);
        txtPAddress.setText(null);
        txtContactNo.setText(null);
        txtTcktId.setText(null);
        comboOrigin.getSelectionModel().clearSelection();
        comboDest.getSelectionModel().clearSelection();
        txtNoTckts.setText(null);
        txtAmnt.setText(null);
        txtRouteName.setText(null);
        comboRouteId.getSelectionModel().clearSelection();
        comboBusNo.getSelectionModel().clearSelection();
        dtBoardDate.setTime(null);
    }

    @FXML
    private void bookTicket(ActionEvent event) throws SQLException {
        int iPassgSuc = insertToPassg();
        int iTicketSuc = insertToTicket();

        if (iTicketSuc == 1 && iPassgSuc == 1) {
            JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
            fXSnackbar.show("Ticket book successful", 4000);
            buildDataTable();
            clearField();

        } else {
            JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
            fXSnackbar.show("Failed To Book Tickets", 4000);
            buildDataTable();
        }
    }

    private int insertToPassg() {
        int success = 0;
        try {
            // Establish connection
            conn = handler.getConnection();
            String sql = "INSERT INTO passenger(pno,pname,paddress,pcontact_no,pemail) VALUES (?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtPNo.getText());
            pst.setString(2, txtPName.getText());
            pst.setString(3, txtPAddress.getText());
            pst.setString(4, txtContactNo.getText());
            pst.setString(5, "Demo@emailId");

            success = pst.executeUpdate();

        } catch (Exception e) {
            success = 0;
            Logger.getLogger(TicketsController.class.getName()).log(Level.SEVERE, null, e);
        }
        return success;
    }

    private int insertToTicket() {
        int success = 0;
        try {
            //Get variables
            String from = comboOrigin.getSelectionModel().getSelectedItem();
            String to = comboDest.getSelectionModel().getSelectedItem();
            String routeId = comboRouteId.getSelectionModel().getSelectedItem();
            String busNo = comboBusNo.getSelectionModel().getSelectedItem();
            // Establish connection
            conn = handler.getConnection();
            String sql = "INSERT INTO tickets(ticket_id,origin,destination,amount,ticket_date,pno,route_id,bus_no,no_tickets) VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtTcktId.getText());
            pst.setString(2, from);
            pst.setString(3, to);
            pst.setInt(4, Integer.valueOf(txtAmnt.getText()));
            pst.setDate(5, java.sql.Date.valueOf(dtBoardDate.getValue()));
            pst.setString(6, txtPNo.getText());
            pst.setString(7, routeId);
            pst.setString(8, busNo);
            pst.setInt(9, Integer.valueOf(txtNoTckts.getText()));
            success = pst.executeUpdate();

        } catch (Exception e) {
            success = 0;
            Logger.getLogger(TicketsController.class.getName()).log(Level.SEVERE, null, e);
        }
        return success;
    }

    private void populateCombos() {

        comboOrigin.getItems().addAll("Mysuru", "Bengaluru", "Ernakulam", "Puducherry", "Mangaluru",
                "Kundapura", "Hubli", "Pune", "Hyderabad", "Shirdi", "Shivamogga", "Chennai", "Vijayawada");
        comboDest.getItems().addAll("Mysuru", "Bengaluru", "Ernakulam", "Puducherry", "Mangaluru",
                "Kundapura", "Hubli", "Pune", "Hyderabad", "Shirdi", "Shivamogga", "Chennai", "Vijayawada");
        populateRouteBusComboxes();

    }

    private void populateRouteBusComboxes() {

        try {
            //comboDriverName.getItems().clear();
            conn = handler.getConnection();
            //driver info
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT bus_no FROM buses");

            while (resultSet.next()) {
                String busNo = resultSet.getString(1);
                comboBusNo.getItems().addAll(busNo);
            }
            //route Id
            resultSet = conn.createStatement().executeQuery("SELECT route_id FROM Route");
            while (resultSet.next()) {
                String routeId = resultSet.getString(1);
                comboRouteId.getItems().add(routeId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BusesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buildDataTable() {
        try {
            conn = handler.getConnection();
            String query = "SELECT t.ticket_id as Ticket_id,t.origin,t.destination,t.amount,t.ticket_date as Boarding_Date,"
                    + "t.no_tickets as Num_Tickets,t.bus_no as Bus_Number,r.route_id as route_id,r.route_name as route_Name,"
                    + "p.pname as passenger_Name,p.pcontact_no as passenger_Phone FROM tickets t,route r, buses b,passenger p "
                    + "where t.pno = p.pno and t.route_id = r.route_id and t.bus_no = b.bus_no";

            ResultSet rst = conn.createStatement().executeQuery(query);
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            tableTickets.getColumns().clear();
            int cols = rst.getMetaData().getColumnCount();

            for (int i = 0; i < cols; i++) {
                final int j = i;
//                rst.getMetaData().getColumnLabel(cols)
                TableColumn col = new TableColumn(rst.getMetaData().getColumnLabel(i + 1).toUpperCase());
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });

                col.setPrefWidth(130);
                tableTickets.getColumns().addAll(col);
            }
            while (rst.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int k = 1; k <= rst.getMetaData().getColumnCount(); k++) {
                    row.add(rst.getString(k));
                }
                data.add(row);
            }
            tableTickets.setItems(data);
        } catch (SQLException ex) {
            Logger.getLogger(TicketsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
