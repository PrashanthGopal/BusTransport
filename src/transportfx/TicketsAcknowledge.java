/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transportfx;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class TicketsAcknowledge implements Initializable {

    @FXML
    private StackPane rootPane;
    @FXML
    private ImageView imgBack;
    @FXML
    private ImageView imgBus;
    @FXML
    private Label lblFrom;
    @FXML
    private Label lblPassg;
    @FXML
    private Label lblPassg1;
    @FXML
    private Label lblTo;
    @FXML
    private Label lblBoardingDate;
    @FXML
    private Label lblRoute;
    @FXML
    private Label lblNoOfTckts;
    @FXML
    private Label lblPassg11;
    @FXML
    private Label lblBookBy;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LoadLabels();
    }    

    @FXML
    private void goBack(MouseEvent event) {
        try {
//            LoginController.userIdLogin = null;
            TicketsController.passgName = null;
            TicketsController.boardingDate = null;
            TicketsController.source = null;
            TicketsController.destination = null;
            TicketsController.noOfTckts = null;
            TicketsController.route = null;
        
            imgBack.getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("TicketsBook.fxml"));
            Scene scene = new Scene(root);
            Stage menuStage = new Stage();
            menuStage.setScene(scene);
            menuStage.show();
        } catch (IOException ex) {
            Logger.getLogger(DriversController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void LoadLabels() {
        
        lblBookBy.setText(LoginController.userIdLogin);
        lblPassg.setText(TicketsController.passgName);
        lblBoardingDate.setText(TicketsController.boardingDate);
        lblFrom.setText(TicketsController.source);
        lblTo.setText(TicketsController.destination);
        lblNoOfTckts.setText(TicketsController.noOfTckts);
        lblRoute.setText(TicketsController.route);
          
    }
    
}
