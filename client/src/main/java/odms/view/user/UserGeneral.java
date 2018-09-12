package odms.view.user;


import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.swing.text.NumberFormatter;
import odms.commons.model.enums.CountriesEnum;
import odms.commons.model.enums.UserType;
import odms.commons.model.user.User;
import odms.controller.database.DAOFactory;
import odms.controller.database.country.CountryDAO;
import odms.data.DefaultLocale;

public class UserGeneral {

    @FXML
    private Label givenNamesLabel;
    @FXML
    private Label staffIdLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label regionLabel;

    //private Redo redoController = new Redo();
    //private Undo undoController = new Undo();
    private User currentUser;



    /**
     * Button handler to undo last action.
     *
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) throws IOException {
        //todo replace with standardised?
        //undoController.undo(GuiMain.getCurrentDatabase());
        Parent parent = FXMLLoader.load(getClass().getResource("/view/ClinicianProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Button handler to redo last undo action.
     *
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) throws IOException {
        //todo replace with standardised?
        //redoController.redo(GuiMain.getCurrentDatabase());
        Parent parent = FXMLLoader.load(getClass().getResource("/view/ClinicianProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Button handler to make fields editable.
     *
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfileEdit.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        ClinicianEdit v = fxmlLoader.getController();
        v.initialize(currentUser);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Edit profile");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initializes all of the labels and checks the user type.
     * @param currentUser The current user logged in.
     */
    public void initialize(User currentUser) {
        this.currentUser = currentUser;
        givenNamesLabel.setText(givenNamesLabel.getText() + (
                        currentUser.getName() != null ? currentUser.getName() : ""));
        staffIdLabel.setText(
                staffIdLabel.getText() + (
                        currentUser.getStaffID() != null ?
                                DefaultLocale.format(currentUser.getStaffID()) : ""));
        addressLabel.setText(
                addressLabel.getText() +
                        (currentUser.getWorkAddress() != null ? currentUser.getWorkAddress() : "")
        );
        regionLabel.setText(
                regionLabel.getText() +
                        (currentUser.getRegion() != null ? currentUser.getRegion() : "")
        );
    }
}
