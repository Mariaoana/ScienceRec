
package sciencerec;

import java.sql.*;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

/**
 * FXML Controller class
 */
public class ActivitiesController implements Initializable {

    @FXML
    private Button btnLogOut;
    @FXML
    private Button btnManagCoord;
    @FXML
    private Button btnLoginInfo;
    @FXML
    private Button btnEditProfile;
    @FXML
    private Button btnManageProjects;
    @FXML
    private Button btnManageUsers;
    @FXML
    private Label lblUserType;
    @FXML
    private Button btnManageEduMat;
    @FXML
    private Button btnMyEduMat;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScienceRecMain.getInstance().getPrimaryStage().setTitle("ScienceRec - Activities");
        if (CurrentUser.getInstance().getUserTypeId() == Utilities.ADMIN) {
            btnManageUsers.setVisible(false);
            btnManageProjects.setVisible(false);
            btnManageEduMat.setVisible(false);
            btnMyEduMat.setVisible(false);
        }
        else {
            btnManagCoord.setVisible(false);
        }
        if (CurrentUser.getInstance().getUserTypeId() != Utilities.COORDONATOR) {
            btnManageUsers.setVisible(false);
            btnManageProjects.setVisible(false);
            btnManageEduMat.setVisible(false);
        }
        lblUserType.setText("User: " + CurrentUser.getInstance().getPrenume() + " " + 
                CurrentUser.getInstance().getNume() + " (" +  CurrentUser.getInstance().getUserTypeDesc() + ")");        
    }    

    @FXML
    private void HandlerLogOut(ActionEvent event) {
        ScienceRecMain.getInstance().setScene("LogIn.fxml");
    }

    @FXML
    private void HandlerChangeLogin(ActionEvent event) {
        ScienceRecMain.getInstance().setScene("ChangeLogInInfo.fxml");
    }

    @FXML
    private void HandlerEditProfile(ActionEvent event) {
        ScienceRecMain.getInstance().setScene("EditProfile.fxml");
    }

    @FXML
    private void HandlerManageCoordinators(ActionEvent event) {
        ScienceRecMain.getInstance().setScene("ManageCoordinators.fxml");
    }

    @FXML
    private void HandlerManageUsers(ActionEvent event) {
        ScienceRecMain.getInstance().setScene("ManageUsers.fxml");
    }

    @FXML
    private void HandlerManageProjects(ActionEvent event) {
        ScienceRecMain.getInstance().setScene("ManageProjects.fxml");
    }

    @FXML
    private void HandlerManageEduMat(ActionEvent event) {
        ScienceRecMain.getInstance().setScene("ManageEduMat.fxml");
    }

    @FXML
    private void HandlerMyEduMat(ActionEvent event) {
        ScienceRecMain.getInstance().setScene("MyEduMat.fxml");
    }
}

