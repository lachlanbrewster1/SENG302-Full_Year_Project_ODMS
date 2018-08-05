package odms.view.profile;

import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import odms.controller.condition.ConditionAddController;
import odms.model.profile.Profile;
import odms.view.CommonView;

public class ProfileAddConditionView extends CommonView {
    private static Profile searchedDonor;
    private static ProfileMedicalHistoryView parent;
    private ConditionAddController controller = new ConditionAddController(this);

    @FXML
    private javafx.scene.control.TextField nameField;

    @FXML
    private DatePicker dateDiagnosedDatePicker;

    @FXML
    private DatePicker dateCuredDatePicker;

    @FXML
    private CheckBox chronicCheckBox;

    @FXML
    private CheckBox curedCheckBox;

    @FXML
    private Label warningLabel;

    @FXML
    private Button addButton;

    @FXML
    public void handleAddButtonClicked(ActionEvent actionEvent) {
        try {
            controller.add();
            parent.refreshConditionTable();
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            warningLabel.setVisible(true);
        }
    }

    @FXML
    public void handleCuredChecked(ActionEvent actionEvent) {
        if (curedCheckBox.isSelected()) {
            dateCuredDatePicker.setDisable(false);
            chronicCheckBox.setSelected(false);
        } else {
            dateCuredDatePicker.setDisable(true);
        }
    }

    @FXML
    public void handleChronicChecked(ActionEvent actionEvent) {
        if (chronicCheckBox.isSelected()) {
            dateCuredDatePicker.setDisable(true);
            curedCheckBox.setSelected(false);
        }
    }

    @FXML
    public void initialize() {
        LocalDate now = LocalDate.now();
        dateDiagnosedDatePicker.setValue(now);
        dateCuredDatePicker.setValue(now);
        dateCuredDatePicker.setDisable(true);
    }

    public void setup(ProfileMedicalHistoryView view, Profile p) {
        parent = view;
        searchedDonor = p;
    }

    public Profile getCurrentProfile() {
        return searchedDonor;
    }

    public String getNameFieldText() {
        return nameField.getText();
    }

    public LocalDate getDateDiagnosed() {
        return dateDiagnosedDatePicker.getValue();
    }

    public Boolean getIsCured() {
        return curedCheckBox.isSelected();
    }

    public Boolean getIsChronic() {
        return chronicCheckBox.isSelected();
    }

    public LocalDate getDateCured() {
        return dateCuredDatePicker.getValue();
    }

}
