
package sciencerec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 */
public class EditProfileController implements Initializable {

    @FXML
    private Button btnLogOut;
    @FXML
    private Button btnBack;
    @FXML
    private TextField txtNume;
    @FXML
    private TextField txtPrenume;
    @FXML
    private TextField txtTelefon;
    @FXML
    private TextField txtSpecializare;
    @FXML
    private TextField txtHobiuri;
    @FXML
    private TextField txtCreator;
    @FXML
    private ImageView ivPhoto;
    @FXML
    private Button btnLoad;
    @FXML
    private Button btnSave;
    @FXML
    private Label lblUserType;

    //lblUserType
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScienceRecMain.getInstance().getPrimaryStage().setTitle("ScienceRec - Change Login Info");
        //
        lblUserType.setText("User: " + CurrentUser.getInstance().getPrenume() + " " + 
                CurrentUser.getInstance().getNume() + " (" +  CurrentUser.getInstance().getUserTypeDesc() + ")");
        //
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "SELECT nume, prenume, telefon, specializare, hobiuri, imagine, id_creator FROM user WHERE id_user = " +
                CurrentUser.getInstance().getLoginId() + ";";
            ResultSet rs = stmt.executeQuery(sql);
            String nume = "", prenume = "", telefon = "", specializare= "", hobiuri= "";
            int id_creator = 0;
            while (rs.next()) {
                nume = rs.getString("nume");
                prenume = rs.getString("prenume");
                telefon = rs.getString("telefon");
                specializare = rs.getString("specializare");
                hobiuri = rs.getString("hobiuri");
                id_creator = rs.getInt("id_creator");
                InputStream input = rs.getBinaryStream("imagine");
                if (input != null && input.available() > 1) {
                    Image image = new Image(input);
                    ivPhoto.setImage(image);
                }
            }
            rs.close();
            if (nume != null)
                txtNume.setText(nume);
            if (prenume != null)
                txtPrenume.setText(prenume);
            if (telefon != null)
                txtTelefon.setText(telefon);
            if (specializare != null)
                txtSpecializare.setText(specializare);
            if (hobiuri != null)
                txtHobiuri.setText(hobiuri);
            if (id_creator > 0) {
                sql = "SELECT nume, prenume FROM user WHERE id_user = " + id_creator + ";";
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    nume = rs.getString("nume");
                    prenume = rs.getString("prenume");
                }
                rs.close();
                if (nume != null && prenume != null)
                    txtCreator.setText(nume + " " + prenume);
            }
            rs.close();
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
    private void HandlerLoad(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Image");
        File cdir = new File(System.getProperty("user.dir"));
        if(cdir.isDirectory()) {
            fc.setInitialDirectory(cdir);
        }
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JPEG files", "*.jar", "*.jpeg", "*.jpg"),
            new FileChooser.ExtensionFilter("PNG files", "*.png")
        );
        File file = fc.showOpenDialog(null);
        if (file != null) {
            String path = file.getAbsolutePath();
            InputStream is = null;
            try {
                is = new FileInputStream(path);
            } 
            catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
            Image image = new Image(is);
            ivPhoto.setImage(image);
        }
    }

    @FXML
    private void HandlerSave(ActionEvent event) {
        
        String nume = txtNume.getText().trim();
        String prenume = txtPrenume.getText().trim();
        String telefon = txtTelefon.getText().trim();
        String specializare = txtSpecializare.getText().trim();
        String hobiuri = txtHobiuri.getText().trim();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            String sql = "UPDATE user SET nume=?, prenume=?, telefon=?, specializare=?, " +
                    "imagine=?, hobiuri=? WHERE id_user = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, telefon);
            pstmt.setString(4, specializare);
            Image img = ivPhoto.getImage();
            if (img != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(ivPhoto.getImage(), null), "png", baos);
                pstmt.setBytes(5, baos.toByteArray());
            }
            else {
                pstmt.setBytes(5, null);
            }
            pstmt.setString(6, hobiuri);
            pstmt.setInt(7, CurrentUser.getInstance().getLoginId());
            pstmt.executeUpdate();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Information Dialog");
            alert.setContentText("Profole Info Successfully Changed!");
            alert.showAndWait();
            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
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

