package odms.view.profile;

import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import odms.controller.procedure.ProcedureController;
import odms.controller.procedure.ProcedureEditController;
import odms.model.profile.Procedure;
import odms.model.profile.Profile;
import odms.view.CommonView;

public class ProfileProceduresView extends CommonView {
    //todo separate fxml files
    @FXML
    private TableView pendingProcedureTable;

    @FXML
    private TableView previousProcedureTable;

    @FXML
    private TableColumn pendingSummaryColumn;

    @FXML
    private TableColumn previousSummaryColumn;

    @FXML
    private TableColumn pendingDateColumn;

    @FXML
    private TableColumn previousDateColumn;

    @FXML
    private TableColumn pendingAffectsColumn;

    @FXML
    private TableColumn previousAffectsColumn;

    public ObjectProperty<Profile> currentProfile = new SimpleObjectProperty<>();
    private ObservableList<Procedure> previousProceduresObservableList;
    private ObservableList<Procedure> pendingProceduresObservableList;

    private ProcedureController controller = new ProcedureController(this);

    @FXML
    public void handleAddProcedureButtonClicked(ActionEvent actionEvent) {
        createPopup(actionEvent, "/view/ProcedureAdd.fxml", "Add Procedure");
    }

    /**
     * Removes the selected procedure and refreshes the table
     *
     * @param actionEvent
     */
    @FXML
    public void handleDeleteProcedureButtonClicked(ActionEvent actionEvent) {
        controller.delete();
        refreshProcedureTable();
    }

    /**
     * Initializes and refreshes the previous and pending procedure tables
     */
    @FXML
    private void makeProcedureTable(ArrayList<Procedure> previousProcedures,
            ArrayList<Procedure> pendingProcedures) {
        pendingDateColumn.setComparator(pendingDateColumn.getComparator().reversed());

        if (previousProcedures != null) {
            previousProceduresObservableList = FXCollections
                    .observableArrayList(previousProcedures);
        } else {
            previousProceduresObservableList = FXCollections.observableArrayList();
        }
        if (pendingProcedures != null) {
            pendingProceduresObservableList = FXCollections.observableArrayList(pendingProcedures);
        } else {
            pendingProceduresObservableList = FXCollections.observableArrayList();
        }

        if (previousProcedures != null) {
            refreshProcedureTable();
        }
        pendingProcedureTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    pendingProcedureTable.getSelectionModel().getSelectedItem() != null) {
                try {
                    createNewProcedureWindow((Procedure) pendingProcedureTable.getSelectionModel()
                            .getSelectedItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        previousProcedureTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    previousProcedureTable.getSelectionModel().getSelectedItem() != null) {
                try {
                    createNewProcedureWindow((Procedure) previousProcedureTable.getSelectionModel()
                            .getSelectedItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Creates a new edit procedure window
     */
    @FXML
    public void createNewProcedureWindow(Procedure selectedProcedure) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/ProcedureEdit.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            ProcedureEditController controller = fxmlLoader.<ProcedureEditController>getController();
            ProfileDetailedProcedureView child = new ProfileDetailedProcedureView();
            child.initialize(selectedProcedure, currentProfile,this);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the procedure table, updating it with the current values pendingProcedureTable;
     */
    @FXML
    public void refreshProcedureTable() {
        if (previousProceduresObservableList == null) {
            previousProceduresObservableList = FXCollections.observableArrayList();
        }
        if (pendingProceduresObservableList == null) {
            pendingProceduresObservableList = FXCollections.observableArrayList();
        }

        // update all procedures
        if (currentProfile.getValue().getAllProcedures() != null) {
            for (Procedure procedure : currentProfile.getValue().getAllProcedures()) {
                procedure.update();
            }
        }

        pendingProcedureTable.getItems().clear();
        previousProcedureTable.getItems().clear();
        if (controller.getPendingProcedures() != null) {
            pendingProceduresObservableList.addAll(controller.getPendingProcedures());
        }
        if (controller.getPreviousProcedures() != null) {
            previousProceduresObservableList.addAll(controller.getPreviousProcedures());
        } else {
            return;
        }

        previousProcedureTable.setItems(previousProceduresObservableList);
        previousSummaryColumn.setCellValueFactory(new PropertyValueFactory("summary"));
        previousDateColumn.setCellValueFactory(new PropertyValueFactory("date"));
        previousAffectsColumn.setCellValueFactory(new PropertyValueFactory("affectsOrgansText"));
        previousProcedureTable.getColumns()
                .setAll(previousSummaryColumn, previousDateColumn, previousAffectsColumn);

        pendingProcedureTable.setItems(pendingProceduresObservableList);
        pendingSummaryColumn.setCellValueFactory(new PropertyValueFactory("summary"));
        pendingDateColumn.setCellValueFactory(new PropertyValueFactory("date"));
        pendingAffectsColumn.setCellValueFactory(new PropertyValueFactory("affectsOrgansText"));
        pendingProcedureTable.getColumns()
                .setAll(pendingSummaryColumn, pendingDateColumn, pendingAffectsColumn);

        forceSortProcedureOrder();
    }

    /**
     * Forces the sort order of the procedure table so that most recent procedures are always at the
     * top
     */
    @FXML
    private void forceSortProcedureOrder() {
        previousProcedureTable.getSortOrder().clear();
        previousProcedureTable.getSortOrder().add(previousDateColumn);
    }

    public Procedure getSelectedPendingProcedure() {
        return (Procedure) pendingProcedureTable.getSelectionModel().getSelectedItem();
    }

    public Procedure getSelectedPreviousProcedure() {
        return (Procedure) previousProcedureTable.getSelectionModel().getSelectedItem();
    }

    public Profile getProfile() {
        return currentProfile.getValue();
    }

    public void initialize(Profile p) {
        currentProfile.setValue(p);
        makeProcedureTable(controller.getPreviousProcedures(),
                controller.getPendingProcedures());
        refreshProcedureTable();
    }




}
