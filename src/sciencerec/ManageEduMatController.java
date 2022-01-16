
package sciencerec;

import java.sql.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

/**
 * FXML Controller class
 */
public class ManageEduMatController implements Initializable {

    @FXML
    private Button btnLogOut;
    @FXML
    private Button btnBack;
    @FXML
    private ComboBox<String> cmbEduMatType;
    @FXML
    private Label lblEduMatType;
    @FXML
    private Label lblUserType;
    @FXML
    private ComboBox<String> cmbEduMatCategory;
    @FXML
    private Label lblEduMatCategory;
    @FXML
    private Label lblEduMat;
    @FXML
    private ListView<Project> lstMyProjects;
    @FXML
    private Label lblMyProjects;
    @FXML
    private Button btnAssign;
    @FXML
    private ListView<String> lstAssignedEduMats;
    @FXML
    private Label lblAssignedEduMats;
    @FXML
    private ListView<EduMat> lstEduMats;
    
    private Map<String, Integer> mpEduMatTypes = new HashMap();
    private Map<String, Integer> mpEduMatCategories = new HashMap();
    private Map<Integer, String> mpEduMatTypes1 = new HashMap();
    private Map<Integer, String> mpEduMatCategories1 = new HashMap();
    private Map<String, Integer> mpProjectTypes = new HashMap();
    private Map<Integer, String> mpProjectTypes1 = new HashMap();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScienceRecMain.getInstance().getPrimaryStage().setTitle("ScienceRec - Manage Educational Materials");
        lblUserType.setText("User: " + CurrentUser.getInstance().getPrenume() + " " + 
                CurrentUser.getInstance().getNume() + " (" +  CurrentUser.getInstance().getUserTypeDesc() + ")");
        //
        mpEduMatTypes.put("All", 0);
        mpEduMatTypes.put("Book", Utilities.BOOK);
        mpEduMatTypes.put("Journal Article", Utilities.JOURNAL_ARTICLE);
        mpEduMatTypes.put("Conference Article", Utilities.CONFERENCE_ARTICLE);
        mpEduMatTypes.put("License Thesis", Utilities.LICENSE_THESIS);
        mpEduMatTypes.put("Master Thesis", Utilities.MASTER_THESIS);
        mpEduMatTypes.put("PhD Thesis", Utilities.PHD_THESIS);
        //
        mpEduMatTypes1.put(0, "All");
        mpEduMatTypes1.put(Utilities.BOOK, "Book");
        mpEduMatTypes1.put(Utilities.JOURNAL_ARTICLE, "Journal Article");
        mpEduMatTypes1.put(Utilities.CONFERENCE_ARTICLE, "Conference Article");
        mpEduMatTypes1.put(Utilities.LICENSE_THESIS, "License Thesis");
        mpEduMatTypes1.put(Utilities.MASTER_THESIS, "Master Thesis");
        mpEduMatTypes1.put(Utilities.PHD_THESIS, "PhD Thesis");
        //
        mpEduMatCategories.put("All", 0);
        mpEduMatCategories.put("Alg. and Data Struct.", Utilities.ALG_DATA_STRUCT);
        mpEduMatCategories.put("Machine Learning", Utilities.MACHINE_LEARNING);
        mpEduMatCategories.put("Data Mining", Utilities.DATA_MINING);
        mpEduMatCategories.put("Virtual Reality", Utilities.VIRTUAL_REALITY);
        mpEduMatCategories.put("Java Programming", Utilities.JAVA_PROGRAMMING);
        //
        mpEduMatCategories1.put(0, "All");
        mpEduMatCategories1.put(Utilities.ALG_DATA_STRUCT, "Alg. and Data Struct.");
        mpEduMatCategories1.put(Utilities.MACHINE_LEARNING, "Machine Learning");
        mpEduMatCategories1.put(Utilities.DATA_MINING, "Data Mining");
        mpEduMatCategories1.put(Utilities.VIRTUAL_REALITY, "Virtual Reality");
        mpEduMatCategories1.put(Utilities.JAVA_PROGRAMMING, "Java Programming");
        //
        mpProjectTypes.put("License", Utilities.LICENSE);
        mpProjectTypes.put("Master", Utilities.MASTER);
        mpProjectTypes.put("Doctorate", Utilities.DOCTORATE);
        mpProjectTypes.put("Research", Utilities.RESEARCH);
        //
        mpProjectTypes1.put(Utilities.LICENSE, "License");
        mpProjectTypes1.put(Utilities.MASTER, "Master");
        mpProjectTypes1.put(Utilities.DOCTORATE, "Doctorate");
        mpProjectTypes1.put(Utilities.RESEARCH, "Research");
        //
        ObservableList<String> eduMatTypes = FXCollections.observableArrayList();
        for (String key : mpEduMatTypes.keySet())
            eduMatTypes.add(key);
        cmbEduMatType.setItems(eduMatTypes);
        cmbEduMatType.getSelectionModel().select("All");
        //
        ObservableList<String> eduMatCategories = FXCollections.observableArrayList();
        for (String key : mpEduMatCategories.keySet())
            eduMatCategories.add(key);
        cmbEduMatCategory.setItems(eduMatCategories);
        cmbEduMatCategory.getSelectionModel().select("All");
        //
        lstMyProjects.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends Project> ov, Project old_val, 
            Project new_val) -> {
                if (new_val != null)
                    updateAssignedEduMats(new_val.id);
            }); 
        //
        updateEduMatListView(0, 0);
        updateMyProjectsListView();
    }

    private void updateEduMatListView(int idType, int idCategory) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "";
            if (idType > 0 && idCategory > 0) {
                sql = "SELECT id_material_pedagogic, id_tip_material_pedagogic, titlu, autori, revista, editura, an_aparitie, id_category FROM material_pedagogic WHERE id_category = " +
                    idCategory + " AND id_tip_material_pedagogic = " + idType + ";";
            }
            else if (idType > 0) {
                sql = "SELECT id_material_pedagogic, id_tip_material_pedagogic, titlu, autori, revista, editura, an_aparitie, id_category FROM material_pedagogic WHERE id_tip_material_pedagogic = " +
                    idType + ";";                
            }
            else if (idCategory > 0) {
                sql = "SELECT id_material_pedagogic, id_tip_material_pedagogic, titlu, autori, revista, editura, an_aparitie, id_category FROM material_pedagogic WHERE id_category = " +
                    idCategory + ";";
            }
            else {
                sql = "SELECT id_material_pedagogic, id_tip_material_pedagogic, titlu, autori, revista, editura, an_aparitie, id_category FROM material_pedagogic;";
            }
            ResultSet rs = stmt.executeQuery(sql);
            ObservableList<EduMat> eduMatItems = FXCollections.observableArrayList();
            String titlu = "", autori = "", revista = "", editura = "";
            int idMaterialPedagogic, idCat, idTipMaterialPedagogic, anAparitie;
            while (rs.next()) {
                idMaterialPedagogic = rs.getInt("id_material_pedagogic");
                idTipMaterialPedagogic = rs.getInt("id_tip_material_pedagogic");
                titlu = rs.getString("titlu");
                autori = rs.getString("autori");
                revista = rs.getString("revista");
                editura = rs.getString("editura");
                anAparitie = rs.getInt("an_aparitie");
                idCat = rs.getInt("id_category");
                EduMat em = new EduMat("(" + mpEduMatTypes1.get(idTipMaterialPedagogic) +
                        ", " + mpEduMatCategories1.get(idCategory) + ") " + 
                        titlu + ", " + autori + ", " + revista + ", " + editura + 
                        ", " + anAparitie, idMaterialPedagogic);
                eduMatItems.add(em);
            }
            rs.close();
            lstEduMats.setItems(eduMatItems);
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
    
    private void updateMyProjectsListView() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "SELECT id_project, id_project_type, descriere FROM project WHERE id_coordonator = " +
                    CurrentUser.getInstance().getLoginId() + ";";
            ResultSet rs = stmt.executeQuery(sql);
            ObservableList<Project> projectItems = FXCollections.observableArrayList();
            int idProject, idProjectType;
            String descriere = "";
            while (rs.next()) {
                idProject = rs.getInt("id_project");
                idProjectType = rs.getInt("id_project_type");
                descriere = rs.getString("descriere");
                Project project = new Project(mpProjectTypes1.get(idProjectType) + ": " + descriere, idProject);
                projectItems.add(project);
            }
            rs.close();
            lstMyProjects.setItems(projectItems);
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
    private void HandlerEduMatType(ActionEvent event) {
        String sEduMatCategory = cmbEduMatCategory.getSelectionModel().getSelectedItem();
        String sEduMatType = cmbEduMatType.getSelectionModel().getSelectedItem();
        updateEduMatListView(mpEduMatTypes.get(sEduMatType), mpEduMatCategories.get(sEduMatCategory));   
    }

    @FXML
    private void HandlerEduMatCategory(ActionEvent event) {
        String sEduMatCategory = cmbEduMatCategory.getSelectionModel().getSelectedItem();
        String sEduMatType = cmbEduMatType.getSelectionModel().getSelectedItem();
        updateEduMatListView(mpEduMatTypes.get(sEduMatType), mpEduMatCategories.get(sEduMatCategory));
    }

    private void updateAssignedEduMats(int projectId) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "SELECT mp.id_material_pedagogic, mp.id_tip_material_pedagogic, mp.titlu, mp.autori, mp.revista, mp.editura, mp.an_aparitie, mp.id_category FROM material_pedagogic mp JOIN project_material_pedagogic pmp ON mp.id_material_pedagogic = pmp.id_material_pedagogic AND pmp.id_project = " +
                    projectId + ";";
            ResultSet rs = stmt.executeQuery(sql);
            ObservableList<String> eduMatItems = FXCollections.observableArrayList();
            int idMaterialPedagogic, idTipMaterialPedagogic, anAparitie, idCategory;
            String titlu = "", autori = "", revista = "", editura = "", desc = "";
            while (rs.next()) {
                idMaterialPedagogic = rs.getInt("id_material_pedagogic");
                idTipMaterialPedagogic = rs.getInt("id_tip_material_pedagogic");
                titlu = rs.getString("titlu");
                autori = rs.getString("autori");
                revista = rs.getString("revista");
                editura = rs.getString("editura");
                anAparitie = rs.getInt("an_aparitie");
                idCategory = rs.getInt("id_category");
                desc = "(" + mpEduMatTypes1.get(idTipMaterialPedagogic) +
                        ", " + mpEduMatCategories1.get(idCategory) + ") " + 
                        titlu + ", " + autori + ", " + revista + ", " + editura + ", " + anAparitie;
                eduMatItems.add(desc);
            }
            rs.close();
            lstAssignedEduMats.setItems(eduMatItems);
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
    private void HandlerAssign(ActionEvent event) {
        EduMat em = lstEduMats.getSelectionModel().getSelectedItem();
        if (em == null)
            return;
        Project project = lstMyProjects.getSelectionModel().getSelectedItem();
        if (project == null)
            return;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "INSERT INTO project_material_pedagogic (id_project, id_material_pedagogic) VALUES (" +
                    project.id + ", " + em.id + ");";
            stmt.execute(sql);
            //
            sql = "SELECT id_user FROM project_user WHERE id_project = " + project.id + ";";
            ResultSet rs = stmt.executeQuery(sql);
            ObservableList<Project> projectItems = FXCollections.observableArrayList();
            int idUser;
            List<Integer> lstUsers = new ArrayList();
            while (rs.next()) {
                idUser = rs.getInt("id_user");
                lstUsers.add(idUser);
            }
            rs.close();
            for(int i : lstUsers) {
                sql = "INSERT INTO student_material_pedagogic (id_user, id_material_pedagogic, id_status) VALUES (" +
                        i + ", " + em.id + ", 2);";
                stmt.execute(sql);
            }
            updateAssignedEduMats(project.id);
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
}

