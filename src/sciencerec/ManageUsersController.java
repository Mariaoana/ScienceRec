
package sciencerec;

import java.sql.*;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

class User {
    String name;
    int id;

    public User(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}

/**
 * FXML Controller class
 */
public class ManageUsersController implements Initializable {

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
    private Label lblPassword;
    @FXML
    private PasswordField txRepeatPwd;
    @FXML
    private Label lblRepeatPwd;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnDelete;
    @FXML
    private Label lblNume;
    @FXML
    private Label lblPrenume;
    @FXML
    private TextField txtNume;
    @FXML
    private TextField txtPrenume;
    @FXML
    private ComboBox<String> cmbUserType;
    @FXML
    private Label lblUserType;
    @FXML
    private ListView<User> lstUsers;    
    private Map<String, Integer> mpUserTypes = new HashMap();
    @FXML
    private Label lblUserType1;
    @FXML
    private Label lblMyUsers;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScienceRecMain.getInstance().getPrimaryStage().setTitle("ScienceRec - Manage Users");
        //
        lblUserType1.setText("User: " + CurrentUser.getInstance().getPrenume() + " " + 
                CurrentUser.getInstance().getNume() + " (" +  CurrentUser.getInstance().getUserTypeDesc() + ")");
        //
        mpUserTypes.put("Student", Utilities.STUDENT);
        mpUserTypes.put("Master Student", Utilities.MASTERAND);
        mpUserTypes.put("PhD Student", Utilities.DOCTORAND);
        mpUserTypes.put("Researcher", Utilities.CERCETATOR);
        ObservableList<String> userTypes = FXCollections.observableArrayList();
        for (String key : mpUserTypes.keySet())
            userTypes.add(key);
        cmbUserType.setItems(userTypes);
        cmbUserType.getSelectionModel().select("Student");
        updateListView();
    }    

    private void updateListView() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "SELECT id_user, nume, prenume FROM user WHERE id_creator = "
                    + CurrentUser.getInstance().getLoginId() + ";";            
            ResultSet rs = stmt.executeQuery(sql);
            ObservableList<User> items = FXCollections.observableArrayList();
            int idUser;
            String nume = "", prenume = "";
            while (rs.next()) {
                idUser = rs.getInt("id_user");
                nume = rs.getString("nume");
                prenume = rs.getString("prenume");
                items.add(new User(nume + " " + prenume, idUser));
            }
            rs.close();
            lstUsers.setItems(items);
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
            String userType = cmbUserType.getSelectionModel().getSelectedItem();
            String nume = txtNume.getText().trim();
            String prenume = txtPrenume.getText().trim();
            String strHexHash = Utilities.HexHash(pwd1);
            String sql = "INSERT INTO user (email, nume, prenume, pwd_hash, id_user_type, id_creator) VALUES ('" +
                    email + "', '" + nume + "', '" + prenume + "', '" + strHexHash + "', " + 
                    mpUserTypes.get(userType) + ", " + CurrentUser.getInstance().getLoginId() + ")";
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
        User user = lstUsers.getSelectionModel().getSelectedItem();
        if (user == null)
            return;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "DELETE FROM user WHERE id_user = " + user.id + ";";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateListView();
    }
}

