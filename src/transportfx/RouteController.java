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
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextArea;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author pgopalakrishnaiah
 */
public class RouteController implements Initializable  {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView imgBack;
    @FXML
    private JFXTextField txtRouteId;
    @FXML
    private JFXComboBox<String> comboFrom;
    @FXML
    private JFXComboBox<String> comboTo;
    @FXML
    private JFXTextField txtRoteName;
    @FXML
    private JFXTextArea txtStops;
    @FXML
    private TableView tableTickets;
    @FXML
    private JFXButton btnRoute;
    
    private Connection conn;
    private DbHandler handler;
    
    @FXML
    private JFXDatePicker dtArrvalTime;
    @FXML
    private JFXDatePicker dtDeptureTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//    dtDeptureTime.setTime(LocalTime.now());
    // populate combos
     populateCombos();
     //Instantian Dbhandler class
        handler = new DbHandler();
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

    @FXML
    private void AddRoute(ActionEvent event){
        try{
        //Get variables
        String from = comboFrom.getSelectionModel().getSelectedItem();
        String to = comboTo.getSelectionModel().getSelectedItem();
        String routeId = txtRouteId.getText().trim();
        String routeName = txtRoteName.getText().trim();
        String stops = txtStops.getText().trim();
        // Establish connection
        conn = handler.getConnection();
        String sql = "INSERT INTO route(route_id,route_name,source,destination,stops,departure_origin,arrival_dest) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, routeId);
        pst.setString(2, routeName);
        pst.setString(3, from);
        pst.setString(4, to);
        pst.setString(5, stops);
        pst.setString(6,dtDeptureTime.getTime().toString());
        pst.setString(7,dtArrvalTime.getTime().toString());
                
       int success = pst.executeUpdate();
        if (success == 1) {
            JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
            fXSnackbar.show("Route Added successful", 4000);
            buildDataTable();
            clearFields();
        }else{
            JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
            fXSnackbar.show("Failed To Add Route", 4000);
        }
        }
        catch(Exception e){
        Logger.getLogger(TicketsController.class.getName()).log(Level.SEVERE, null, e);
        JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
            fXSnackbar.show("Failed To Add Route", 4000);
    }
    }
 private void populateCombos() {

        comboFrom.getItems().addAll("Mysuru", "Bengaluru", "Ernakulam", "Puducherry", "Mangaluru",
                "Kundapura", "Hubli", "Pune", "Hyderabad", "Shirdi","Shivamogga","Chennai","Vijayawada");
        comboTo.getItems().addAll("Mysuru", "Bengaluru", "Ernakulam", "Puducherry", "Mangaluru",
                "Kundapura", "Hubli", "Pune", "Hyderabad", "Shirdi","Shivamogga","Chennai","Vijayawada");

    }   
 private void buildDataTable() {
        try {
            conn = handler.getConnection();
            String query = "SELECT * FROM route";

            ResultSet rst = conn.createStatement().executeQuery(query);
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            tableTickets.getColumns().clear();
            int cols = rst.getMetaData().getColumnCount();

            for (int i = 0; i < cols; i++) {
                final int j = i;
                TableColumn col = new TableColumn(rst.getMetaData().getColumnName(i + 1).toUpperCase());
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
 private void clearFields() {
     txtRoteName.setText(null);
     txtRouteId.setText(null);
     txtStops.setText(null);
     comboFrom.getSelectionModel().clearSelection();
     comboTo.getSelectionModel().clearSelection();
     dtDeptureTime.setValue(null);
     dtArrvalTime.setValue(null);
     dtDeptureTime.setTime(null);
     dtArrvalTime.setTime(null);
    }
}
