/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transportfx;

import classes.DbHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController implements Initializable {

    @FXML
    private JFXButton btnLogin;
    @FXML
    private ImageView imgLogin;
    
    private DbHandler handler;
    private Connection conn;
    private Statement stmt;
    private PreparedStatement pst;
    private ResultSet rs;
    @FXML
    private JFXTextField txtUserId;
    @FXML
    private JFXPasswordField txtpassword;
    @FXML
    private StackPane rootPane;
    
    public static String userIdLogin = "";
    public static boolean signUpScreen = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Instantiate db class
        handler = new DbHandler();
        //Animate imageview
        ScaleTransition transition = new ScaleTransition(Duration.seconds(4), imgLogin);
        transition.setToX(2);
        transition.setToY(2);
        transition.setCycleCount(Timeline.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();

    }

    @FXML
    private void doLogin(ActionEvent event) {
        try {
            signUpScreen = false;
            String query = "SELECT user_name FROM user_details where user_name='"+txtUserId.getText()+"' and password='"+txtpassword.getText()+"'";
            conn = handler.getConnection();
            ResultSet set = conn.createStatement().executeQuery(query);
            
            if(!set.next()){
                if(txtUserId.getText() != null && txtUserId.getText().equalsIgnoreCase("admin") ){
                    //skip for admin user only
                }else{
                JFXSnackbar fXSnackbar = new JFXSnackbar(rootPane);
                fXSnackbar.show("UserName/Password is Invalid", 4000);
                return;
                }
            }
            //logged user_id
            userIdLogin = txtUserId.getText();
            btnLogin.getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("Menus.fxml"));
            Scene scene = new Scene(root);
            Stage driverStage = new Stage();
            driverStage.setScene(scene);
            driverStage.show();
        } catch (Exception ex) {
            Logger.getLogger(MenusController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void doSignUp(ActionEvent event) {
        try {
            signUpScreen = true;
            btnLogin.getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("Users.fxml"));
            Scene scene = new Scene(root);
            Stage menuStage = new Stage();
            menuStage.setScene(scene);
            menuStage.show();
        } catch (IOException ex) {
            Logger.getLogger(DriversController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
