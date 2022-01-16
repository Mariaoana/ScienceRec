
package sciencerec;

import java.sql.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

class EduMat {
    String descriere;
    int id;
    int idStatus;

    public EduMat(String desc, int id) {
        this.descriere = desc;
        this.id = id;
    }

    public EduMat(String desc, int id, int idStatus) {
        this.descriere = desc;
        this.id = id;
        this.idStatus = idStatus;
    }
    
    @Override
    public String toString() {
        return descriere;
    }
}

/**
 * FXML Controller class
 */
public class MyEduMatController implements Initializable {

    @FXML
    private Button btnLogOut;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnCreate;
    @FXML
    private ListView<EduMat> lstMyEduMats;
    @FXML
    private Button btnDelete;
    @FXML
    private Label lblTitlu;
    @FXML
    private TextField txtTitlu;
    @FXML
    private ComboBox<String> cmbEduMatType;
    @FXML
    private Label lblEduMatType;
    @FXML
    private Label lblMyEduMats;
    @FXML
    private Label lblUserType;
    @FXML
    private ComboBox<String> cmbEduMatCategory;
    @FXML
    private Label lblEduMatCategory;
    @FXML
    private Label lblRevista;
    @FXML
    private TextField txtRevista;
    @FXML
    private Label lblEditura;
    @FXML
    private TextField txtEditura;
    @FXML
    private Label lblAnAparitie;
    @FXML
    private Spinner<Integer> spnAnAparitie;

    private Map<String, Integer> mpEduMatTypes = new HashMap();
    private Map<String, Integer> mpEduMatCategories = new HashMap();
    private Map<Integer, String> mpEduMatTypes1 = new HashMap();
    private Map<Integer, String> mpEduMatCategories1 = new HashMap();
    
    @FXML
    private ListView<EduMat> lstMyRecEduMats;
    @FXML
    private Label lblMyRecEduMats;
    @FXML
    private RadioButton rbRead;
    @FXML
    private RadioButton rbNotRead;
    @FXML
    private RadioButton rbUseful;
    @FXML
    private RadioButton rbNotUseful;
    @FXML
    private Label lblAutori;
    @FXML
    private TextField txtAutori;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScienceRecMain.getInstance().getPrimaryStage().setTitle("ScienceRec - My Educational Materials");
        lblUserType.setText("User: " + CurrentUser.getInstance().getPrenume() + " " + 
                CurrentUser.getInstance().getNume() + " (" +  CurrentUser.getInstance().getUserTypeDesc() + ")");
        //
        ToggleGroup toggleGroup = new ToggleGroup();
        rbRead.setToggleGroup(toggleGroup);
        rbNotRead.setToggleGroup(toggleGroup);
        rbUseful.setToggleGroup(toggleGroup);
        rbNotUseful.setToggleGroup(toggleGroup);
        toggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
                int idStatus = -1;
                if (newVal == rbRead) {
                    idStatus = 1;
                }
                else if (newVal == rbNotRead) {
                    idStatus = 2;
                }
                else if (newVal == rbUseful) {
                    idStatus = 3;
                }
                else if (newVal == rbNotUseful) {
                    idStatus = 4;
                }
                updateRbMyRecEduMats1(idStatus);
            }
        );
        //
        spnAnAparitie.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1960, 2030, 2021));
        //
        mpEduMatTypes.put("Book", Utilities.BOOK);
        mpEduMatTypes.put("Journal Article", Utilities.JOURNAL_ARTICLE);
        mpEduMatTypes.put("Conference Article", Utilities.CONFERENCE_ARTICLE);
        mpEduMatTypes.put("License Thesis", Utilities.LICENSE_THESIS);
        mpEduMatTypes.put("Master Thesis", Utilities.MASTER_THESIS);
        mpEduMatTypes.put("PhD Thesis", Utilities.PHD_THESIS);
        //
        mpEduMatTypes1.put(Utilities.BOOK, "Book");
        mpEduMatTypes1.put(Utilities.JOURNAL_ARTICLE, "Journal Article");
        mpEduMatTypes1.put(Utilities.CONFERENCE_ARTICLE, "Conference Article");
        mpEduMatTypes1.put(Utilities.LICENSE_THESIS, "License Thesis");
        mpEduMatTypes1.put(Utilities.MASTER_THESIS, "Master Thesis");
        mpEduMatTypes1.put(Utilities.PHD_THESIS, "PhD Thesis");
        //
        mpEduMatCategories.put("Alg. and Data Struct.", Utilities.ALG_DATA_STRUCT);
        mpEduMatCategories.put("Machine Learning", Utilities.MACHINE_LEARNING);
        mpEduMatCategories.put("Data Mining", Utilities.DATA_MINING);
        mpEduMatCategories.put("Virtual Reality", Utilities.VIRTUAL_REALITY);
        mpEduMatCategories.put("Java Programming", Utilities.JAVA_PROGRAMMING);
        //
        mpEduMatCategories1.put(Utilities.ALG_DATA_STRUCT, "Alg. and Data Struct.");
        mpEduMatCategories1.put(Utilities.MACHINE_LEARNING, "Machine Learning");
        mpEduMatCategories1.put(Utilities.DATA_MINING, "Data Mining");
        mpEduMatCategories1.put(Utilities.VIRTUAL_REALITY, "Virtual Reality");
        mpEduMatCategories1.put(Utilities.JAVA_PROGRAMMING, "Java Programming");
        //
        ObservableList<String> eduMatTypes = FXCollections.observableArrayList();
        for (String key : mpEduMatTypes.keySet())
            eduMatTypes.add(key);
        cmbEduMatType.setItems(eduMatTypes);
        cmbEduMatType.getSelectionModel().select("Book");
        //
        ObservableList<String> eduMatCategories = FXCollections.observableArrayList();
        for (String key : mpEduMatCategories.keySet())
            eduMatCategories.add(key);
        cmbEduMatCategory.setItems(eduMatCategories);
        cmbEduMatCategory.getSelectionModel().select("Alg. and Data Struct.");
        //
        lstMyRecEduMats.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends EduMat> ov, EduMat old_val, 
            EduMat new_val) -> {
                if (new_val != null)
                    updateRbMyRecEduMats(new_val.id);
            });
        updateListViews();
    }
    
    private boolean noUpdate = false;
    
    private void updateRbMyRecEduMats(int idMaterialPedagogic) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "SELECT id_status FROM student_material_pedagogic WHERE id_user = " +
                    CurrentUser.getInstance().getLoginId() + " AND id_material_pedagogic = " +
                    idMaterialPedagogic + ";";
            ResultSet rs = stmt.executeQuery(sql);
            int idStatus = 0;
            while (rs.next()) {
                idStatus = rs.getInt("id_status");
            }
            rs.close();
            noUpdate = true;
            switch(idStatus) {
                case 1:
                    rbRead.setSelected(true);
                    break;
                    
                case 2:
                    rbNotRead.setSelected(true);
                    break;
                    
                case 3:
                    rbUseful.setSelected(true);
                    break;
                    
                case 4:
                    rbNotUseful.setSelected(true);
                    break;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
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
    
    private void updateRbMyRecEduMats1(int idStatus) {
        if (noUpdate) {
            noUpdate = false;
            return;
        }
        EduMat em = lstMyRecEduMats.getSelectionModel().getSelectedItem();
        if (em != null) {
            Connection conn = null;
            Statement stmt = null;
            try {
                conn = DriverManager.getConnection(Utilities.sqlURL);
                stmt = conn.createStatement();
                String sql = "UPDATE student_material_pedagogic SET id_status = " +
                        idStatus + " WHERE id_user = " + CurrentUser.getInstance().getLoginId() +
                        " AND id_material_pedagogic = " + em.id + ";";
                stmt.executeUpdate(sql);
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
    
    private void updateListViews() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "SELECT id_material_pedagogic, id_tip_material_pedagogic, titlu, autori, revista, editura, an_aparitie, id_category FROM material_pedagogic WHERE id_creator = " +
                    + CurrentUser.getInstance().getLoginId() + ";";
            ResultSet rs = stmt.executeQuery(sql);
            ObservableList<EduMat> eduMatItems = FXCollections.observableArrayList();
            int idMaterialPedagogic, idTipMaterialPedagogic, anAparitie, idCategory;
            String titlu = "", autori = "", revista = "", editura = "";
            while (rs.next()) {
                idMaterialPedagogic = rs.getInt("id_material_pedagogic");
                idTipMaterialPedagogic = rs.getInt("id_tip_material_pedagogic");
                titlu = rs.getString("titlu");
                autori = rs.getString("autori");
                revista = rs.getString("revista");
                editura = rs.getString("editura");
                anAparitie = rs.getInt("an_aparitie");
                idCategory = rs.getInt("id_category");
                EduMat em = new EduMat("(" + mpEduMatTypes1.get(idTipMaterialPedagogic) +
                        ", " + mpEduMatCategories1.get(idCategory) + ") " + 
                        titlu + ", " + autori + ", " + revista + ", " + editura + ", " + anAparitie,
                        idMaterialPedagogic);
                eduMatItems.add(em);
            }
            rs.close();
            lstMyEduMats.setItems(eduMatItems);
            //
            sql = "SELECT mp.id_material_pedagogic, mp.id_tip_material_pedagogic, mp.titlu, mp.autori, mp.revista, mp.editura, mp.an_aparitie, mp.id_category, smp.id_status FROM material_pedagogic mp JOIN " +
                    "student_material_pedagogic smp ON mp.id_material_pedagogic = smp.id_material_pedagogic WHERE id_user = " +
                    CurrentUser.getInstance().getLoginId() + ";";
            rs = stmt.executeQuery(sql);
            ObservableList<EduMat> eduMatItems1 = FXCollections.observableArrayList();
            while (rs.next()) {
                idMaterialPedagogic = rs.getInt("id_material_pedagogic");
                idTipMaterialPedagogic = rs.getInt("id_tip_material_pedagogic");
                titlu = rs.getString("titlu");
                autori = rs.getString("autori");
                revista = rs.getString("revista");
                editura = rs.getString("editura");
                anAparitie = rs.getInt("an_aparitie");
                idCategory = rs.getInt("id_category");
                EduMat em = new EduMat("(" + mpEduMatTypes1.get(idTipMaterialPedagogic) + ", " + mpEduMatCategories1.get(idCategory) + ") " + titlu + ", " + autori + ", " + revista + ", " + editura + ", " + anAparitie,
                        idMaterialPedagogic);
                eduMatItems1.add(em);
            }
            rs.close();
            lstMyRecEduMats.setItems(eduMatItems1);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
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
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            ResultSet rs = null;
            String sEduMatCategory = cmbEduMatCategory.getSelectionModel().getSelectedItem();
            int eduMatCategory = mpEduMatCategories.get(sEduMatCategory);
            String sEduMatType = cmbEduMatType.getSelectionModel().getSelectedItem();
            int eduMatType = mpEduMatTypes.get(sEduMatType);
            String titlu = txtTitlu.getText().trim();
            String autori = txtAutori.getText().trim();
            String revista = txtRevista.getText().trim();
            String editura = txtEditura.getText().trim();
            int an = spnAnAparitie.getValue();
            String sql = "INSERT INTO material_pedagogic (id_tip_material_pedagogic, titlu, autori, revista, editura, an_aparitie, id_category, id_creator) VALUES (" +
                    eduMatType + ",'" + titlu + "','" + autori + "','" + revista + "','" + editura + "'," + an + "," + eduMatCategory + "," + 
                    CurrentUser.getInstance().getLoginId() + ")";
            stmt.execute(sql);
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
        updateListViews();
    }

    @FXML
    private void HandlerDelete(ActionEvent event) {
        EduMat em = lstMyEduMats.getSelectionModel().getSelectedItem();
        if (em == null)
            return;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "DELETE FROM material_pedagogic WHERE id_material_pedagogic = " + em.id + ";";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateListViews();
    }
}

