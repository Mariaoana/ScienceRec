
package sciencerec;

import java.net.URL;
import java.util.ResourceBundle;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
//
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 */
public class ChangeLogInInfoController implements Initializable {
    @FXML
    private Button btnLogOut;
    @FXML
    private Button btnBack;
    @FXML
    private Label lblEmail;
    @FXML
    private TextField txEmail;
    @FXML
    private PasswordField txOldPassword;
    @FXML
    private Label lblOldPassword;
    @FXML
    private PasswordField txNewPassword;
    @FXML
    private Label lblNewPassword;
    @FXML
    private PasswordField txRepeatNewPwd;
    @FXML
    private Label lblRepeatNewPwd;
    @FXML
    private CheckBox chkChange;
    @FXML
    private Label lblUserType;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScienceRecMain.getInstance().getPrimaryStage().setTitle("ScienceRec - Change Login Info");
        //
        lblUserType.setText("User: " + CurrentUser.getInstance().getPrenume() + " " + 
                CurrentUser.getInstance().getNume() + " (" +  CurrentUser.getInstance().getUserTypeDesc() + ")");
        txEmail.setText(CurrentUser.getInstance().getUserEmail());
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
    private void HandlerChange(ActionEvent event) {
        // Verifica daca parolele noi introduse coincid
        String pwd1 = String.valueOf(txNewPassword.getText()).trim();
        String pwd2 = String.valueOf(txRepeatNewPwd.getText()).trim();
        if (!pwd1.equals(pwd2)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Eroare");
            alert.setHeaderText("Error Dialog");
            alert.setContentText("Error, different new passwords!");
            alert.showAndWait();
            return;
        }
        String email = "";
        if (chkChange.isSelected()) {
            // Verifica daca noua adresa de email este corecta
            email = txEmail.getText().trim();
            if (!email.matches(Utilities.regexEmail)) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Eroare");
                alert.setHeaderText("Error Dialog");
                alert.setContentText("Error, wrong email address!");
                alert.showAndWait();
                return;
            }
        }
        // Verifica daca parola veche este corecta
        String pwdOld = String.valueOf(txOldPassword.getText()).trim(); 
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "";
            ResultSet rs = null;
            if (email.length() > 0) {
                // Verifica daca adresa de email noua exista
                sql = "SELECT COUNT(email) cnt FROM user WHERE email = '" + email +
                        "' AND id_user <> " + CurrentUser.getInstance().getLoginId() + ";";
                rs = stmt.executeQuery(sql); // Selectare email
                int cnt = 0;
                while (rs.next()) {
                    cnt = rs.getInt("cnt");
                }
                rs.close();
                if (cnt > 0) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Eroare");
                    alert.setHeaderText("Error Dialog");
                    alert.setContentText("Error, email address already exists!");
                    alert.showAndWait();
                    return;
                }
            }
            // Genereaza hash parola veche
            String strHexHash = Utilities.HexHash(pwdOld);
            sql = "SELECT pwd_hash FROM user WHERE id_user = " + CurrentUser.getInstance().getLoginId() + ";";
            rs = stmt.executeQuery(sql); // Selectare parola
            String strHexHash1 = "";
            while (rs.next()) {
                strHexHash1 = rs.getString("pwd_hash");
            }         
            rs.close();
            // Se verifica daca hashcode pentru parola introdusa de utilizator este identic
            // cu hashcode parola din DB
            if (!strHexHash1.equals(strHexHash)) { 
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Eroare");
                alert.setHeaderText("Error Dialog");
                alert.setContentText("Error, wrong old password!");
                alert.showAndWait();
                return;
            }            
            strHexHash = Utilities.HexHash(pwd1);
            sql = "UPDATE user SET pwd_hash = '" + strHexHash + "'";
            if (email.length() > 0) {
                sql += ", email = '" + email + "'";
            }
            sql += " WHERE id_user = " + CurrentUser.getInstance().getLoginId() + ";";
            stmt.executeUpdate(sql);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Information Dialog");
            alert.setContentText("Login Info Successfully Changed!");
            alert.showAndWait();
            return;            
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
    }

    @FXML
    private void HandlerEmailChange(ActionEvent event) {
        txEmail.setEditable(chkChange.isSelected());
    }
}

