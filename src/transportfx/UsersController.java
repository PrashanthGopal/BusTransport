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
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.imageio.ImageIO;


public class UsersController implements Initializable {

    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane topAnchor;
    @FXML
    private JFXSnackbar snackEdit;
    @FXML
    private ImageView imgBack;
    @FXML
    private JFXDatePicker dtDOB;
    @FXML
    private JFXTextField txtPhone;
    @FXML
    private JFXPasswordField passwd;
    @FXML
    private JFXPasswordField passwd2;
    @FXML
    private RadioButton rdMale;
    @FXML
    private ToggleGroup gender;
    @FXML
    private RadioButton rdFemale;
    @FXML
    private RadioButton rdSingle;
    @FXML
    private ToggleGroup family;
    @FXML
    private RadioButton rdMarried;
    @FXML
    private RadioButton rdOthers;
    @FXML
    private JFXComboBox<String> comboUsrType;
    @FXML
    private JFXComboBox<Integer> comboYear;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXTextField txtusername;
    @FXML
    private TableView tableBusesList;

    private DbHandler handler;
    private Connection conn;
    private Statement stmt;
    private PreparedStatement pst;
    private ResultSet rs;
    private final String pattern = "yyyy-MM-dd HH:mm";
    @FXML
    private Tab userInfoTab;
    @FXML
    private JFXTabPane tabPaneUser;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Instantiate database class
        handler = new DbHandler();
        JFXRippler backRippler = new JFXRippler(imgBack, JFXRippler.RipplerMask.RECT, JFXRippler.RipplerPos.FRONT);
        topAnchor.getChildren().add(imgBack);
        if(LoginController.signUpScreen){
//      userInfoTab.setDisable(true);
        tabPaneUser.getTabs().remove(1);
        }

        buildUserTable();
        populateCombos();
        
        // Set default selected radio buttons
        rdMale.setSelected(true);
        rdSingle.setSelected(true);
        
    }    
    
    private void populateCombos() {
        for (int i = 2017; i > 1970; i--) {
            comboYear.getItems().addAll(i);
        }
        comboUsrType.getItems().addAll("Select", "Admin", "Data Operator","Agency Chief");
    }

    @FXML
    private void goBack(MouseEvent event) {
        try {
            imgBack.getScene().getWindow().hide();
            String screenVal = "Menus.fxml";
            if(LoginController.signUpScreen){
                screenVal = "Login.fxml";
            }
            Parent root = FXMLLoader.load(getClass().getResource(screenVal));
            Scene scene = new Scene(root);
            Stage menuStage = new Stage();
            menuStage.setScene(scene);
            menuStage.show();
        } catch (IOException ex) {
            Logger.getLogger(DriversController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void goSignUpScreen() {
        try {
            imgBack.getScene().getWindow().hide();
            String screenVal = "Menus.fxml";
            if(LoginController.signUpScreen){
                screenVal = "Login.fxml";
            }
            Parent root = FXMLLoader.load(getClass().getResource(screenVal));
            Scene scene = new Scene(root);
            Stage menuStage = new Stage();
            menuStage.setScene(scene);
            menuStage.show();
        } catch (IOException ex) {
            Logger.getLogger(DriversController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void saveUser(ActionEvent event) throws SQLException  {
        try {
            if(txtusername.getText() == null || txtusername.getText().isEmpty()){
                JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
                fXSnackbar.show("Invalid User", 4000);
                return;
            }
                    
            String insert = "INSERT INTO user_details(user_name,password,contact_no,email,gender,"
                    + "dob,utype,created_dttm) "
                    + "VALUES (?,?,?,?,?,?,?,?)";
            conn = handler.getConnection();
            pst = conn.prepareStatement(insert);
            
            // Set parameters
            pst.setString(1, txtusername.getText());
            pst.setString(2, passwd.getText());
            pst.setString(3, txtPhone.getText());
            pst.setString(4, "dummy@gmail.com");
            pst.setString(5, getGender());
            pst.setString(6, String.valueOf(dtDOB.getValue()));
            pst.setString(7, comboUsrType.getSelectionModel().getSelectedItem());
            pst.setString(8, String.valueOf(new SimpleDateFormat(pattern).format(new Date())));

            int success = pst.executeUpdate();
            if (success == 1) {
                JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
                fXSnackbar.show("User Added successful", 4000);
                buildUserTable();
                clearFields();
                if(LoginController.signUpScreen)
                    goSignUpScreen();
            } else {
                JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
                fXSnackbar.show("Failed To Add User", 4000);
            }
            //System.out.println(blob);
            //System.out.println(success);

        } catch (Exception ex) {
            JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
            fXSnackbar.show("Failed To Add User", 4000);
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void buildUserTable() {

        try {
            conn = handler.getConnection();
            String query = "SELECT user_name as UserName,contact_no as ContactNo,email,gender,dob as DOB,utype as UserType FROM user_details";

            ResultSet rst = conn.createStatement().executeQuery(query);
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            tableBusesList.getColumns().clear();
            int cols = rst.getMetaData().getColumnCount();

            for (int i = 0; i < cols; i++) {
                final int j = i;
                TableColumn col = new TableColumn(rst.getMetaData().getColumnLabel(i + 1).toUpperCase());
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
    
    private void clearFields() {
            
        txtusername.setText(null);
        passwd.setText(null);
        txtPhone.setText(null);
        dtDOB.setValue(null);
        comboUsrType.getSelectionModel().clearSelection();
        comboYear.getSelectionModel().clearSelection();
        passwd.setText(null);
        passwd2.setText(null);
        
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


    @FXML
    private void selectedDelete(ActionEvent event) {
        Object row = tableBusesList.getSelectionModel().getSelectedItem();
        if (row != null){
            String userId = String.valueOf(row).split(",")[0].substring(1);
            System.out.println("Delete userId"+ userId);
            try {
            String query = "delete FROM user_details where user_name='"+userId+"'";
            conn = handler.getConnection();
            conn.createStatement().executeUpdate(query);
            buildUserTable();
            JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
            fXSnackbar.show("User Record deleted - user Id: "+userId, 5000);

        } catch (SQLException ex) {
            Logger.getLogger(DriversController.class.getName()).log(Level.SEVERE, null, ex);
        }
        }else{
            JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
            fXSnackbar.show("Please select Record", 5000);
        }
    }

}
