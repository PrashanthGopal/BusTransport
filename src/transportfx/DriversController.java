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
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import models.driver;

public class DriversController implements Initializable {

    @FXML
    private ImageView imgBack;
    @FXML
    private AnchorPane topAnchor;
    @FXML
    private ImageView imgProfile;
    @FXML
    private JFXButton btnChoose;
    @FXML
    private StackPane rootPane;
    @FXML
    private ToggleGroup gender;
    @FXML
    private ToggleGroup family;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnGenerate;
    @FXML
    private JFXComboBox<String> comboLicence;
    @FXML
    private JFXComboBox<Number> comboYear;
    @FXML
    private JFXButton btnClear;
    @FXML
    private JFXSnackbar snackEdit;
    @FXML
    private JFXTextField txtFirstName;
    @FXML
    private JFXTextField txtMiddleName;
    @FXML
    private JFXTextField txtSurName;
    @FXML
    private JFXDatePicker dtDOB;
    @FXML
    private JFXTextField txtPhone;
    @FXML
    private RadioButton rdMale;
    @FXML
    private RadioButton rdFemale;
    @FXML
    private RadioButton rdSingle;
    @FXML
    private RadioButton rdMarried;
    @FXML
    private RadioButton rdOthers;
    private DbHandler handler;
    private Connection conn;
    private Statement stmt;
    private PreparedStatement pst;
    private ResultSet rs;
    private JFXComboBox<String> comboPLate;
    @FXML
    private TableView<driver> tableDriversInfo;
    @FXML
    private TableColumn<driver, String> colNames;
    @FXML
    private TableColumn<driver, String> colDob;
    @FXML
    private TableColumn<driver, String> colGender;
    @FXML
    private TableColumn<driver, String> colPhone;
    @FXML
    private TableColumn<driver, String> colStatus;
    @FXML
    private TableColumn<driver, String> colLicense;
    @FXML
    private TableColumn<driver, String> colIssued;

    private ObservableList<driver> data;
    @FXML
    private JFXTextField txtDriverId;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Instantiate db class
        handler = new DbHandler();

        JFXRippler backRippler = new JFXRippler(imgBack, JFXRippler.RipplerMask.RECT, JFXRippler.RipplerPos.FRONT);
        topAnchor.getChildren().add(imgBack);
        populateCombos();
        // Set default selected radio buttons
        rdMale.setSelected(true);
        rdSingle.setSelected(true);
        //populate table
        buildDriversTable();

//        Rectangle clip=new Rectangle(imgProfile.getFitWidth(), imgProfile.getFitHeight());
//        clip.setArcHeight(120);
//        clip.setArcWidth(120);
//        imgProfile.setClip(clip);
//        SnapshotParameters parameters=new SnapshotParameters();
//        parameters.setFill(Color.TRANSPARENT);
//        WritableImage img=imgProfile.snapshot(parameters, null);
//        imgProfile.setClip(null);
//        imgProfile.setImage(img);
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
    private void choosePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filterJPG = new FileChooser.ExtensionFilter("JPG files(*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter filterPNG = new FileChooser.ExtensionFilter("PNG files(*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(filterPNG, filterJPG);
        //show open dialog
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
                Rectangle clip = new Rectangle(imgProfile.getFitWidth(), imgProfile.getFitHeight());
                clip.setArcHeight(180);
                clip.setArcWidth(180);
                imgProfile.setImage(image);
                imgProfile.setClip(clip);
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                WritableImage img = imgProfile.snapshot(parameters, null);
                imgProfile.setClip(null);
                imgProfile.setImage(img);

            } catch (IOException ex) {
                Logger.getLogger(DriversController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @FXML
    private void saveDriver(ActionEvent event) throws SQLException {

        try {
            String insert = "INSERT INTO driver(sname,mname,lname,dob,gender,"
                    + "marital_status,license_type,license_issued_date,photo,contact_no,driver_id) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            conn = handler.getConnection();
            pst = conn.prepareStatement(insert);
            //Create blob object from image
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(imgProfile.getImage(), null), "png", byteOutput);
            Blob blob = conn.createBlob();
            blob.setBytes(1, byteOutput.toByteArray());
            // Set parameters
            pst.setString(1, txtSurName.getText().toUpperCase());
            pst.setString(2, txtMiddleName.getText().toUpperCase());
            pst.setString(3, txtFirstName.getText().toUpperCase());
            pst.setDate(4, java.sql.Date.valueOf(dtDOB.getValue()));
            pst.setString(5, getGender());
            pst.setString(6, getMarital());
            pst.setString(7, comboLicence.getSelectionModel().getSelectedItem());
            pst.setString(8, comboYear.getSelectionModel().getSelectedItem().toString());
            pst.setBlob(9, blob);
            pst.setString(10, txtPhone.getText().trim());
            pst.setString(11, txtDriverId.getText().trim());
            //save
            //System.out.println("Size is :" +byteOutput.size());

            int success = pst.executeUpdate();
            if (success == 1) {
                JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
                fXSnackbar.show("Driver Added successful", 4000);
                buildDriversTable();
                clearFields();
            } else {
                JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
                fXSnackbar.show("Failed To Add Driver", 4000);
            }
            //System.out.println(blob);
            //System.out.println(success);

        } catch (IOException ex) {
            JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
            fXSnackbar.show("Failed To Add Driver", 4000);
            Logger.getLogger(DriversController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearFields() {
        comboLicence.getSelectionModel().clearSelection();
        comboYear.getSelectionModel().clearSelection();
        txtFirstName.setText(null);
        txtMiddleName.setText(null);
        txtSurName.setText(null);
        dtDOB.setValue(null);
        txtPhone.setText(null);
        txtDriverId.setText(null);
    }

    private void populateCombos() {
        for (int i = 2017; i > 1970; i--) {
            comboYear.getItems().addAll(i);
        }
        comboLicence.getItems().addAll("Select", "Permanent", "Temporary");
    }

    private String getGender() {
        String ge = "";
        if (rdMale.isSelected()) {
            ge = "MALE";
        } else if (rdFemale.isSelected()) {
            ge = "FEMALE";
        }
        return ge;
    }

    private String getMarital() {
        String marital = "";
        if (rdMarried.isSelected()) {
            marital = "MARRIED";
        } else if (rdSingle.isSelected()) {
            marital = "SINGLE";
        } else if (rdOthers.isSelected()) {
            marital = "OTHERS";
        }
        return marital;
    }

    private void buildDriversTable() {
        try {
            String query = "SELECT driver_id,sname,mname,lname,dob,gender,license_issued_date,license_type,marital_status,contact_no FROM driver";
            conn = handler.getConnection();
            data = FXCollections.observableArrayList();
            ResultSet set = conn.createStatement().executeQuery(query);
            while (set.next()) {
                String id = set.getString(1);
                String names = set.getString(2) + " " + set.getString(3) + " " + set.getString(4);
                String dob = set.getString(5);
                String geda = set.getString(6);
                String status = set.getString(9);
                String license = set.getString(8);
                String issueD = set.getString(7);
                String contactNo = set.getString(10);

                data.add(new driver(id, names, dob, geda, contactNo, status, license, issueD));
            }
            colNames.setCellValueFactory(new PropertyValueFactory<>("names"));
            colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
            colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            colLicense.setCellValueFactory(new PropertyValueFactory<>("license"));
            colIssued.setCellValueFactory(new PropertyValueFactory<>("issued"));
            colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

            tableDriversInfo.setItems(null);
            tableDriversInfo.setItems(data);

        } catch (SQLException ex) {
            Logger.getLogger(DriversController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
