
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;

class Project {
    String descriere;
    int id;

    public Project(String desc, int id) {
        this.descriere = desc;
        this.id = id;
    }

    @Override
    public String toString() {
        return descriere;
    }
}

/**
 * FXML Controller class
 */
public class ManageProjectsController implements Initializable {

    @FXML
    private Button btnLogOut;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnCreate;
    @FXML
    private ListView<Project> lstProjects;
    @FXML
    private Button btnDelete;
    @FXML
    private Label lblDescriere;
    @FXML
    private TextField txtDescriere;
    @FXML
    private ComboBox<String> cmbProjectType;
    @FXML
    private Label lblProjectType;
    @FXML
    private Label lblDescriere1;
    @FXML
    private ListView<User> lstUsers;
    @FXML
    private Label lblUsers;
    @FXML
    private Button btnAsignUser;
    //  
    private Map<String, Integer> mpProjectTypes = new HashMap();
    private Map<Integer, String> mpProjectTypes1 = new HashMap();
    @FXML
    private ListView<String> lstAssignments;
    @FXML
    private Label lblAssignments;
    @FXML
    private Label lblUserType;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScienceRecMain.getInstance().getPrimaryStage().setTitle("ScienceRec - Manage Projects");
        lblUserType.setText("User: " + CurrentUser.getInstance().getPrenume() + " " + 
                CurrentUser.getInstance().getNume() + " (" +  CurrentUser.getInstance().getUserTypeDesc() + ")");
        //
        lstProjects.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends Project> ov, Project old_val, 
            Project new_val) -> {
                if (new_val != null)
                    updateListViewAssignments(new_val.id);
            });        
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
        ObservableList<String> projectTypes = FXCollections.observableArrayList();
        for (String key : mpProjectTypes.keySet())
            projectTypes.add(key);
        cmbProjectType.setItems(projectTypes);
        cmbProjectType.getSelectionModel().select("License");
        updateListViews();
    }    

    private void updateListViews() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "SELECT id_user, nume, prenume FROM user WHERE id_creator = "
                    + CurrentUser.getInstance().getLoginId() + ";";            
            ResultSet rs = stmt.executeQuery(sql);
            ObservableList<User> userItems = FXCollections.observableArrayList();
            int idUser;
            String nume = "", prenume = "";
            while (rs.next()) {
                idUser = rs.getInt("id_user");
                nume = rs.getString("nume");
                prenume = rs.getString("prenume");
                User user = new User(nume + " " + prenume, idUser);
                userItems.add(user);
            }
            rs.close();
            lstUsers.setItems(userItems);
            //
            sql = "SELECT id_project, id_project_type, descriere FROM project WHERE id_coordonator = " +
                    CurrentUser.getInstance().getLoginId() + ";";
            rs = stmt.executeQuery(sql);
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
            lstProjects.setItems(projectItems);
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
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            ResultSet rs = null;
            String projectType = cmbProjectType.getSelectionModel().getSelectedItem();
            String descriere = txtDescriere.getText().trim();
            String sql = "INSERT INTO project (id_project_type, descriere, id_coordonator) VALUES (" +
                    mpProjectTypes.get(projectType) + ", '" + descriere + "', " + 
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
        Project project = lstProjects.getSelectionModel().getSelectedItem();
        if (project == null)
            return;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "DELETE FROM project WHERE id_project = " + project.id + ";";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateListViews();
    }

    @FXML
    private void HandlerAssignUser(ActionEvent event) {
        Project project = lstProjects.getSelectionModel().getSelectedItem();
        if (project == null)
            return;
        User user = lstUsers.getSelectionModel().getSelectedItem();
        if (user == null)
            return;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            ResultSet rs = null;
            String sql = "INSERT INTO project_user (id_project, id_user) VALUES (" + 
                    project.id + "," + user.id +");";
            stmt.execute(sql);
            updateListViewAssignments(project.id);
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
    
    private void updateListViewAssignments(int projectId) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(Utilities.sqlURL);
            stmt = conn.createStatement();
            String sql = "SELECT u.nume, u.prenume FROM user u JOIN project_user pu ON u.id_user = pu.id_user WHERE pu.id_project = " +
                    projectId + ";";            
            ResultSet rs = stmt.executeQuery(sql);
            ObservableList<String> items = FXCollections.observableArrayList();
            String nume = "", prenume = "";
            while (rs.next()) {
                nume = rs.getString("nume");
                prenume = rs.getString("prenume");
                items.add(nume + " " + prenume);
            }
            rs.close();
            lstAssignments.setItems(items);
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
}

