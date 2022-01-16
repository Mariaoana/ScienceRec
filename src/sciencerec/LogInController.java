
package sciencerec;

import java.net.URL;
import java.util.ResourceBundle;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
//
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 */

public class LogInController implements Initializable {
    @FXML
    private Button btnLogIn;
    @FXML
    private Label lblEmail;
    @FXML
    private TextField txEmail;
    @FXML
    private PasswordField txPassword;
    @FXML
    private Label lblPassword;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScienceRecMain.getInstance().getPrimaryStage().setTitle("ScienceRec - Login");
    }    

    @FXML
    private void HandlerLogIn(ActionEvent event) {        
        if (event.getSource() == btnLogIn) {
            // Generam HashCode pentru parola aleasa de fiecare utilizator
            // Nu se tine parola in clar in baza de date pentru ca poate fi furata.
            // Se tine hash code si la logare se compara hash code generat cu cel stocat in baza de date.
            String email = txEmail.getText().trim();
            String pwd = String.valueOf(txPassword.getText()).trim();
            Connection conn = null;
            Statement stmt = null;
            try {
                // Genereaza hashcode in hexa pentru parola introdusa de client
                String strHexHash = Utilities.HexHash(pwd);
                conn = DriverManager.getConnection(Utilities.sqlURL); // Ne conectam la serverul SQL
                stmt = conn.createStatement();
                // Obtine din baza de date hash code stocat pentru parola clientului pe baza 
                // e-mail unic
                String sql = "SELECT u.id_user, u.id_user_type, u.nume, u.prenume, u.pwd_hash, ut.descriere FROM user u " +
                    "JOIN user_type ut ON u.id_user_type = ut.id_user_type WHERE u.email = '" + email + "';";
                ResultSet rs = stmt.executeQuery(sql);
                String nume = "";
                String prenume = "";
                String pwdHash = "";
                int idUser = 0;
                int idUserType = 0;
                String descriere = "";
                while (rs.next()) {
                    idUser = rs.getInt("id_user");
                    idUserType = rs.getInt("id_user_type");
                    pwdHash = rs.getString("pwd_hash");
                    nume = rs.getString("nume");
                    prenume = rs.getString("prenume");
                    descriere = rs.getString("descriere");
                }
                rs.close();
                // Se verifica daca hash code pentru parola introdusa de utilizator este identic
                // cu cel din baza de date
                if (pwdHash.equals(strHexHash)) {
                    CurrentUser.getInstance().setLoginId(idUser);
                    CurrentUser.getInstance().setNume(nume);
                    CurrentUser.getInstance().setPrenume(prenume);
                    CurrentUser.getInstance().setUserEmail(email);
                    CurrentUser.getInstance().setUserTypeDesc(descriere);
                    CurrentUser.getInstance().setUserTypeId(idUserType);
                    //
                    ScienceRecMain.getInstance().setScene("Activities.fxml");
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Eroare");
                    alert.setHeaderText("Error Dialog");
                    alert.setContentText("Eroare de Logare: Email sau Parola gresita!");
                    alert.showAndWait();
                }
            } catch (NoSuchAlgorithmException nsae) {
                nsae.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
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
    }
}

