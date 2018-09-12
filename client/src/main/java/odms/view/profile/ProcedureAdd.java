package odms.view.profile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.profile.Profile;
import odms.commons.model.enums.OrganEnum;

@Slf4j
public class ProcedureAdd {

    private static ProceduresDisplay parentView;
    private static Profile searchedDonor;

    private odms.controller.profile.ProcedureAdd controller = new odms.controller.profile.ProcedureAdd(this);

    @FXML
    private TextField summaryField;

    @FXML
    private DatePicker dateOfProcedureDatePicker;

    @FXML
    private TextField descriptionField;

    @FXML
    private Label warningLabel;

    @FXML
    private Button addButton;

    @FXML
    private ListView<String> affectedOrgansListView;

    private ObservableList<String> donatedOrgans;

    @FXML
    public void handleAddButtonClicked(ActionEvent actionEvent) {
        try {
            controller.add(getSearchedDonor());
            parentView.refreshProcedureTable();
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            warningLabel.setVisible(true);
        }
    }

    @FXML
    private void onEnter(ActionEvent event) {
        handleAddButtonClicked(event);
    }

    /**
     * Run whenever this view is called
     */
    @FXML
    public void initialize() {
        warningLabel.setVisible(false);
        affectedOrgansListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<String> organs = new ArrayList<>();
        searchedDonor.getOrgansDonating().forEach(organEnum -> {
            organs.add(organEnum.getNamePlain());
        });
        donatedOrgans = FXCollections
                .observableArrayList();
        donatedOrgans.addAll(organs);
        affectedOrgansListView.setItems(donatedOrgans);
    }

    public void setup(ProceduresDisplay parentView, Profile currentProfile) {
        this.parentView = parentView;
        searchedDonor = currentProfile;
    }

    public Profile getSearchedDonor() {
        return searchedDonor;
    }

    public String getSummaryField() {
        return summaryField.getText();
    }

    public LocalDate getDateOfProcedureDatePicker() {
        return dateOfProcedureDatePicker.getValue();
    }

    public String getDescriptionField() {
        return descriptionField.getText();
    }

    public ArrayList getAffectedOrgansListView() {
        return new ArrayList<>(affectedOrgansListView.getSelectionModel().getSelectedItems());
    }
}
