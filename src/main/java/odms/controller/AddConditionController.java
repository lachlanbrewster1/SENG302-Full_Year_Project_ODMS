package odms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import odms.profile.Condition;
import odms.profile.Profile;

import java.awt.*;
import java.time.LocalDate;

public class AddConditionController {

    private Profile searchedDonor;
    private DonorProfileController controller;

    @FXML
    private javafx.scene.control.TextField nameField;

    @FXML
    private javafx.scene.control.TextField dateDiagnosedField;

    @FXML
    private TextField dateCuredField;

    @FXML
    private CheckBox chronicCheckBox;

    @FXML
    private CheckBox curedCheckBox;

    @FXML
    public void handleAddButtonClicked(ActionEvent actionEvent) {
        String name = nameField.getText();
        String dateDiagnosed = dateDiagnosedField.getText();
        Boolean isChronic = chronicCheckBox.isSelected();
        String dateCured = dateCuredField.getText();

        Condition condition = new Condition(name, dateDiagnosed, dateCured, isChronic);
        addCondition(condition);
    }

    /**
     * Adds a new condition to the current profile
     * @param condition
     */
    public void addCondition(Condition condition) {
        searchedDonor.addCondition(condition);
        controller.refreshTable();
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("/view/DonorProfile.fxml"));
//        DonorProfileController controller = fxmlLoader.<DonorProfileController>getController();
//        controller.refreshTable();
    }

    @FXML
    public void handleCuredChecked(ActionEvent actionEvent) {
    }

    public void init(DonorProfileController controller) {
        this.controller = controller;
        searchedDonor = controller.searchedDonor;
    }

}
