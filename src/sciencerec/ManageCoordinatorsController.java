
package sciencerec;

import java.sql.*;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * FXML Controller class
 */
public class ManageCoordinatorsController implements Initializable {
    @FXML
    private Button btnLogOut;
    @FXML
    private Button btnBack;
    @FXML
    private Label lblEmail;
    @FXML
    private TextField txEmail;
    @FXML
    private PasswordField txPassword;
    @FXML
    private Button btnCreate;
    @FXML
    private ListView<Coordinator> lstCoordinators;
    @FXML
    private Button btnDelete;
    @FXML
    private Label lblPassword;
    @FXML
    private PasswordField txRepeatPwd;
    @FXML
    private Label lblRepeatPwd;
    @FXML
    private TextField txtNume;
    @FXML
    private Label lblNume;
    @FXML
    private TextField txtPrenume;
    @FXML
    private Label lblPrenume;
    @FXML
    private Label lblUserType;

    class Coordinator {
        String name;
        int id;

        public Coordinator(String name, int id) {
            this.name = name;
            this.id = id;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScienceRecMain.getInstance().getPrimaryStage().setTitle("ScienceRec - Manage Coordinators");
        lblUserType.setText("User: " + CurrentUser.getInstance().getPrenume() + " " + 
                CurrentUser.getInstance().getNume() + " (" +  CurrentUser.getInstance().getUserTypeDesc() + ")");
        updateListView();
    }    

    private void updateListView() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "SELECT id_user, nume, prenume FROM user WHERE id_user_type = "
                    + Utilities.COORDONATOR + ";";
            ResultSet rs = stmt.executeQuery(sql);
            ObservableList<Coordinator> items = FXCollections.observableArrayList();
            int idUser;
            String nume = "", prenume = "";
            while (rs.next()) {
                idUser = rs.getInt("id_user");
                nume = rs.getString("nume");
                prenume = rs.getString("prenume");
                items.add(new Coordinator(nume + " " + prenume, idUser));
            }
            rs.close();
            lstCoordinators.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
    }
    
    @FXML
    private void HandlerLogOut(ActionEvent event) {
        ScienceRecMain.getInstance().setScene("LogIn.fxml");
    }

    @FXML
    private void HandlerBack(ActionEvent event) {
        ScienceRecMain.getInstance().setScene("Activities.fxml");
    }

    @FXML
    private void HandlerCreate(ActionEvent event) {
        // Verifica daca parolele noi introduse coincid
        String pwd1 = String.valueOf(txPassword.getText()).trim();
        String pwd2 = String.valueOf(txRepeatPwd.getText()).trim();
        if (!pwd1.equals(pwd2)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Eroare");
            alert.setHeaderText("Error Dialog");
            alert.setContentText("Error, different passwords!");
            alert.showAndWait();
            return;
        }
        // Verifica daca noua adresa de email este corecta
        String email = txEmail.getText().trim();
        if (!email.matches(Utilities.regexEmail)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Eroare");
            alert.setHeaderText("Error Dialog");
            alert.setContentText("Error, wrong email address!");
            alert.showAndWait();
            return;
        }
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            ResultSet rs = null;
            String nume = txtNume.getText().trim();
            String prenume = txtPrenume.getText().trim();
            String strHexHash = Utilities.HexHash(pwd1);
            String sql = "INSERT INTO user (email, nume, prenume, pwd_hash, id_user_type, id_creator) VALUES ('" +
                    email + "', '" + nume + "', '" + prenume + "', '" + strHexHash + "', " + 
                    Utilities.COORDONATOR + ", " + CurrentUser.getInstance().getLoginId() + ")";
            stmt.execute(sql);
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        updateListView();
    }

    @FXML
    private void HandlerDelete(ActionEvent event) {
        Coordinator coord = lstCoordinators.getSelectionModel().getSelectedItem();
        if (coord == null)
            return;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "DELETE FROM user WHERE id_user = " + coord.id + ";";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateListView();
    }
}

