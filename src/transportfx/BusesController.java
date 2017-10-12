package transportfx;

import classes.DbHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import models.Movements;

/**
 * FXML Controller class
 *
 * @author danml
 */
public class BusesController implements Initializable {

    @FXML
    private ImageView imgBack;
    @FXML
    private ImageView imgBus;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnEdit;
    @FXML
    private StackPane rootPane;
    @FXML
    private JFXTextField txtBusNo;
    @FXML
    private JFXTextField txtcarPlateNo;
    @FXML
    private JFXTextField txtModel;
    @FXML
    private JFXTextField txtCapacity;
    @FXML
    private JFXDatePicker dtDatePurchased;
    @FXML
    private JFXTextField txtInsuranceStatus;
    @FXML
    private JFXDatePicker dtDateInsured;
    @FXML
    private JFXDatePicker dtExpiryDate;
    @FXML
    private JFXComboBox<String> comboDriverName;
    private DbHandler handler;
    private Connection conn;
    private Statement stmt;
    private PreparedStatement ps;
    private ResultSet rs;
    private final String pattern = "yyyy-MM-dd";
    @FXML
    private JFXButton btnReset;
    @FXML
    private TableView tableBusesList;
    private TableView<Movements> tableMovements;
    private TableColumn<Movements, Integer> colId;
    private TableColumn<Movements, String> colDriver;
    private TableColumn<Movements, String> colOrigin;
    private TableColumn<Movements, String> colDestination;
    private TableColumn<Movements, String> colTime;
    private ObservableList<Movements> data;
    @FXML
    private JFXComboBox<String> comboRouteId;
    @FXML
    private Label labelDrName;
    @FXML
    private Label labelDrPhone;
    @FXML
    private Label labelRouteName;
    @FXML
    private Label labelRoutesource;
    @FXML
    private Label labelRoutedest;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Instantiate database class
        handler = new DbHandler();
        // Date converter for javafx datepicker to conform with sql date format
        sqlDateFormatter();
        //auto populate bs list tableview
        buildBusesTable();
        //Anmate bus movement
        animateBus();
        //POpulate booking comboboxes
        populateCombooxes();

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

    private void animateBus() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        KeyValue kv = new KeyValue(imgBus.xProperty(), 700);
        KeyFrame kf = new KeyFrame(Duration.seconds(10), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    @FXML
    private void saveBusInfo(ActionEvent event) {
        try {

            String insertQuery = "INSERT INTO buses(bus_no,model,capacity,purchase_date,insurance_status,driver_id,route_id) "
                    + "VALUES (?,?,?,?,?,?,?)";
            conn = handler.getConnection();
            ps = conn.prepareStatement(insertQuery);
            ps.setString(1, txtBusNo.getText().trim().toUpperCase());
            ps.setString(2, txtModel.getText().toUpperCase());
            ps.setString(3, txtCapacity.getText());
            ps.setDate(4, java.sql.Date.valueOf(dtDatePurchased.getValue()));
            ps.setString(5, txtInsuranceStatus.getText());
            ps.setString(6, comboDriverName.getSelectionModel().getSelectedItem());
            ps.setString(7, comboRouteId.getSelectionModel().getSelectedItem());

            int success = ps.executeUpdate();
            if (success == 1) {
                JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
                fXSnackbar.show("New bus saved into the records", 5000);
                clearFields();
                buildBusesTable();
            } else {
                JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
                fXSnackbar.show("Check validity of data entered and save.", 9000);
            }
        } catch (SQLException ex) {
            JFXSnackbar nackbar = new JFXSnackbar(rootPane);
            nackbar.show("Check validity of data entered and save.", 9000);
        }
    }

    void sqlDateFormatter() {
        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return formatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, formatter);

                } else {
                    return null;
                }
            }

        };
        dtDateInsured.setConverter(converter);
        dtDatePurchased.setConverter(converter);
        dtExpiryDate.setConverter(converter);
    }

    private void clearFields() {
        txtBusNo.setText("");
        txtCapacity.setText("");
        txtInsuranceStatus.setText("");
        txtModel.setText("");
        txtcarPlateNo.setText("");
        dtDateInsured.setValue(null);
        dtDatePurchased.setValue(null);
        dtExpiryDate.setValue(null);
        comboDriverName.getSelectionModel().clearSelection();
        comboRouteId.getSelectionModel().clearSelection();

    }

    @FXML
    private void resetFields(ActionEvent event) {
        clearFields();
    }

    private void buildBusesTable() {

        try {
            conn = handler.getConnection();
            String query = "SELECT * FROM buses";

            ResultSet rst = conn.createStatement().executeQuery(query);
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            tableBusesList.getColumns().clear();
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
                tableBusesList.getColumns().addAll(col);
            }
            while (rst.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int k = 1; k <= rst.getMetaData().getColumnCount(); k++) {
                    row.add(rst.getString(k));
                }
                data.add(row);
            }
            tableBusesList.setItems(data);

        } catch (SQLException ex) {
            Logger.getLogger(BusesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateCombooxes() {

        try {
            //comboDriverName.getItems().clear();
            conn = handler.getConnection();
            //driver info
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT driver.driver_id FROM driver");

            while (resultSet.next()) {
                String driverId = resultSet.getString(1);
                comboDriverName.getItems().addAll(driverId);
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

    @FXML
    private void LoadLabels(ActionEvent event) {

        String driverId = comboDriverName.getSelectionModel().getSelectedItem();
        String routeId = comboRouteId.getSelectionModel().getSelectedItem();
        try {
            //comboDriverName.getItems().clear();
            conn = handler.getConnection();
            ResultSet resultSet;
            if (driverId != null && !driverId.isEmpty()) {
                //driver info
                resultSet = conn.createStatement().executeQuery("SELECT sname,lname,contact_no FROM driver where driver_id='" + driverId + "'");
                while (resultSet.next()) {
                    String name = resultSet.getString(1) + " " + resultSet.getString(2);
                    String phone = resultSet.getString(3);

                    labelDrName.setText("Driver Name:" + name);
                    labelDrPhone.setText("Phone: " + phone);
                }
            }
            //route Id
            if (routeId != null && !routeId.isEmpty()) {
                resultSet = conn.createStatement().executeQuery("SELECT route_name,source,destination FROM Route where route_id='" + routeId + "'");
                while (resultSet.next()) {
                    String routeName = resultSet.getString(1);
                    String source = resultSet.getString(2);
                    String dest = resultSet.getString(3);

                    labelRouteName.setText("Route Name:" + routeName);
                    labelRoutesource.setText("Source:" + source);
                    labelRoutedest.setText("Destination:" + dest);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(BusesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
