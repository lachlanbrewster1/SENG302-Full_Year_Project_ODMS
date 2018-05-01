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

public class ClinicianProfileControllerTest extends ApplicationTest {

    private GuiMain guiMain;
    private Parent root;

    // Runs tests in background if headless is set to true. This gets it working with the CI.
    @BeforeClass
    public static void headless() {
        //GUITestSetup.headless();
    }

    @After()
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
        FxToolkit.cleanupStages();
    }

    @Before
    public void loginUser() {
        logInClinician();
    }

    /**
     * Initializes the main gui
     * @param stage current stage
     * @throws Exception throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception{
        guiMain = new GuiMainDummy();
        //guiMain.setCurrentDatabase(new TestDataCreator().getDatabase());

        guiMain.start(stage);
        stage.toFront();
    }

    /**
     * logs in the clinician and opens up the search tab
     */
    public void logInClinician() {
        clickOn("#usernameField").write("0");
        clickOn("#loginButton");
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
        Scene scene = getTopModalStage();
        Label donorName = (Label) scene.lookup("#donorFullNameLabel");
        assertEquals(firstDonor.getFullName(), donorName.getText()); //checks name label is equal
    }

    /**
     * Tests that a donor's profile can be opened by a clinician and that the name can be successfully
     * updated. The name is checked in the database and in the GUI to make sure it updates.
     * Changes the donor back to the original.
     */
    @Test
    public void editSearchedProfileTest() {
        // Open up the first donor
        clickOn("#searchTab");
        doubleClickOn(row("#searchTable", 0));
        Scene scene = getTopModalStage();

        Label userIdLabel = (Label) scene.lookup("#userIdLabel");
        Integer userId = Integer.parseInt(userIdLabel.getText().substring(10)); //gets id of user being edited.

        String originalGivenNames = ((Label) scene.lookup("#givenNamesLabel")).getText().substring(14);
        String originalLastNames = ((Label) scene.lookup("#lastNamesLabel")).getText().substring(11);
        // Opening edit tab
        clickOn((scene.lookup("#editButton")));

        // Editing donor
        Scene scene2 = getTopModalStage();
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
        assertEquals("Bob", guiMain.getCurrentDatabase().getProfile(userId).getGivenNames());
        assertEquals("Seger", guiMain.getCurrentDatabase().getProfile(userId).getLastNames());

        // Checks GUI has been updated.
        scene2 = getTopModalStage();
        Label updatedGivenNames = (Label) scene2.lookup("#givenNamesLabel");
        Label updatedLastNames = (Label) scene2.lookup("#lastNamesLabel");
        assertEquals("Bob", updatedGivenNames.getText().substring(14));
        assertEquals("Seger", updatedLastNames.getText().substring(11));

        // Reset user through GUI
        // Open edit donor back up
        Scene donorScene = getTopModalStage();
        // EditDonorButton = (Button) searchedDonorScene.lookup("#editDonorButton2");
        clickOn(donorScene.lookup("#editButton"));

        scene2 = getTopModalStage();
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

    /**
     * @param tableSelector The id of the table to be used
     * @return Returns a table view node from the given ID
     */
    private TableView<?> getTableView(String tableSelector) {
        Node node = lookup(tableSelector).queryTableView();
        if (!(node instanceof TableView)) {
        }
        return (TableView<?>) node;
    }

    /**
     * @param tableSelector The id of the table that contains the cell wanted
     * @param row           row number
     * @param column        column number
     * @return returns the cell data.
     */
    protected Object cellValue(String tableSelector, int row, int column) {
        return getTableView(tableSelector).getColumns().get(column).getCellData(row);
    }

    /**
     * @param tableSelector Id of table that contains the row
     * @param row           row number
     * @return returns a table row
     */
    protected TableRow<?> row(String tableSelector, int row) {

        TableView<?> tableView = getTableView(tableSelector);

        List<Node> current = tableView.getChildrenUnmodifiable();
        while (current.size() == 1) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        current = ((Parent) current.get(1)).getChildrenUnmodifiable();
        while (!(current.get(0) instanceof TableRow)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(row);
        return (TableRow<?>) node;
    }

    /**
     * @param tableSelector ID of the table that contains the cell wanted
     * @param row           row number
     * @param column        column number
     * @return the cell of the table
     */
    protected TableCell<?, ?> cell(String tableSelector, int row, int column) {
        List<Node> current = row(tableSelector, row).getChildrenUnmodifiable();
        while (current.size() == 1 && !(current.get(0) instanceof TableCell)) {
            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
        }

        Node node = current.get(column);
        return (TableCell<?, ?>) node;
    }



    /**
     * gets current stage with all windows.
     * @return All of the current windows
     */
    private javafx.scene.Scene getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        return (javafx.scene.Scene) allWindows.get(0).getScene();
    }

    /**
     * gets current stage with all windows. Used to check that an alert controller has been created and is visible
     * @return All of the current windows
     */
    private javafx.stage.Stage getAlertDialogue() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        return (javafx.stage.Stage) allWindows
                .stream()
                .filter(window -> window instanceof javafx.stage.Stage)
                .filter(window -> ((javafx.stage.Stage) window).getModality() == Modality.APPLICATION_MODAL)
                .findFirst()
                .orElse(null);
    }
}
