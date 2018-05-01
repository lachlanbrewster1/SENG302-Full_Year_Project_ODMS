package GUI;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import odms.controller.GuiMain;
import odms.profile.Profile;
import odms.tools.TestDataCreator;
import org.junit.*;
import org.testfx.api.FxToolkit;

import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ClinicianProfileGUITest extends TestFxMethods {

    private GuiMain guiMain;

    // Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() throws TimeoutException {
        GUITestSetup.headless();
    }

    @Before
    public void loginUser() {
        loginAsClinician();
    }

    /**
     * Checks that the correct donor's profile is opened from the search table.
     */
    @Test
    public void openSearchedProfileTest() {
        clickOn("#searchTab");
        TableView searchTable = getTableView("#searchTable");
        Profile firstDonor = (Profile) searchTable.getItems().get(0);

        doubleClickOn(row("#searchTable", 0));
        //opening the first donor
        Scene scene = getTopScene();
        Label donorName = (Label) scene.lookup("#donorFullNameLabel");
        assertEquals(firstDonor.getFullName(), donorName.getText()); //checks name label is equal
    }

    /**
     * Tests that a donor's profile can be opened by a clinician and that the name can be successfully
     * updated. The name is checked in the database and in the GUI to make sure it updates.
     * Changes the donor back to the original.
     */
    @Test
    public void editDonorNameTest() {
        // Open up the first donor
        clickOn("#searchTab");
        doubleClickOn(row("#searchTable", 0));
        Scene scene = getTopScene();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer.parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        String originalGivenNames = ((Label) scene.lookup("#givenNamesLabel")).getText().substring(14);
        String originalLastNames = ((Label) scene.lookup("#lastNamesLabel")).getText().substring(11);
        // Opening edit tab
        clickOn((scene.lookup("#editButton")));

        // Editing donor
        Scene scene2 = getTopScene();
        TextField givenNames = (TextField) scene2.lookup("#givenNamesField");
        TextField lastNames = (TextField) scene2.lookup("#lastNamesField");
        clickOn(givenNames).eraseText(originalGivenNames.length()).write("Bob");
        clickOn(lastNames).eraseText(originalLastNames.length()).write("Seger");

        Button saveButton = (Button) scene2.lookup("#saveButton");
        clickOn(saveButton);

        Stage stage = getAlertDialogue();
        DialogPane dialogPane = (DialogPane) stage.getScene().getRoot();
        Button yesButton = (Button) dialogPane.lookupButton(ButtonType.YES);
        clickOn(yesButton);

        // Checks database has been updated
        assertEquals("Bob", GuiMain.getCurrentDatabase().getProfile(userId).getGivenNames());
        assertEquals("Seger", GuiMain.getCurrentDatabase().getProfile(userId).getLastNames());

        // Checks GUI has been updated.
        scene2 = getTopScene();
        Label updatedGivenNames = (Label) scene2.lookup("#givenNamesLabel");
        Label updatedLastNames = (Label) scene2.lookup("#lastNamesLabel");
        assertEquals("Bob", updatedGivenNames.getText().substring(14));
        assertEquals("Seger", updatedLastNames.getText().substring(11));

        // Reset user through GUI
        // Open edit donor back up
        Scene donorScene = getTopScene();
        // EditDonorButton = (Button) searchedDonorScene.lookup("#editDonorButton2");
        clickOn(donorScene.lookup("#editButton"));

        scene2 = getTopScene();
        givenNames = (TextField) scene2.lookup("#givenNamesField");
        lastNames = (TextField) scene2.lookup("#lastNamesField");
        clickOn(givenNames).eraseText(3).write(originalGivenNames);
        clickOn(lastNames).eraseText(5).write(originalLastNames);

        Button saveButton2 = (Button) scene2.lookup("#saveButton");
        clickOn(saveButton2);

        Stage stage2 = getAlertDialogue();
        DialogPane dialogPane3 = (DialogPane) stage2.getScene().getRoot();
        yesButton = (Button) dialogPane3.lookupButton(ButtonType.YES);
        clickOn(yesButton);
    }

    @Test
    public void editPhoneTest() {

    }

    @Test
    public void editEmailTest() {

    }

    @Test
    public void editAddressTest() {

    }

    @Test
    public void editRegionTest() {

    }

    @Test
    public void editIRDTest() {

    }

    @Test
    public void editDateOfBirthTest() {

    }

    @Test
    public void editDateOfDeathTest() {

    }

    @Test
    public void editAgeTest() {

    }

    @Test
    public void editGenderTest() {

    }

    @Test
    public void editHeightTest() {

    }

    @Test
    public void editWeightTest() {

    }

    @Test
    public void editBloodTypeTest() {

    }

    @Test
    public void editSmokerTest() {

    }

    @Test
    public void editAlcoholConsumptionTest() {

    }

    @Test
    public void editBloodPressureTest() {

    }

    @Test
    public void editChronicDiseasesTest() {

    }

    @Test
    public void editOrgansToDonateTest() {

    }

    @Test
    public void editPastDonationsTest() {

    }

    @Test
    public void validateBMITest() {

    }
}
