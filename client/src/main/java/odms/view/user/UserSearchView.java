package odms.view.user;

import java.sql.SQLException;
import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import odms.controller.database.DAOFactory;
import odms.controller.database.ProfileDAO;
import odms.controller.user.UserSearchController;
import odms.model.enums.OrganEnum;
import odms.model.profile.Profile;
import odms.model.user.User;
import odms.view.CommonView;
import org.controlsfx.control.CheckComboBox;

public class UserSearchView extends CommonView {

    // Constant that holds the number of search results displayed on a page at a time.
    private static final int PAGESIZE = 25;
    // Constant that holds the max number of search results that can be displayed.
    private static final int MAXPAGESIZE = 200;
    private odms.model.user.User currentUser;

    private ObservableList<String> genderStrings = FXCollections.observableArrayList();
    private ObservableList<String> typeStrings = FXCollections.observableArrayList();
    private ObservableList<String> organsStrings = FXCollections.observableArrayList();

    private UserSearchController controller = new UserSearchController(this);

    @FXML
    private TextField ageField;
    @FXML
    private TextField ageRangeField;
    @FXML
    private ComboBox typeCombobox;
    @FXML
    private CheckComboBox<OrganEnum> organsCombobox;
    @FXML
    private ComboBox genderCombobox;
    @FXML
    private TextField searchField;
    @FXML
    private TextField regionField;
    @FXML
    private TableView<Profile> searchTable;
    @FXML
    private TableColumn<Profile, String> fullNameColumn;
    @FXML
    private TableColumn<Profile, String> donorReceiverColumn;
    @FXML
    private TableColumn<Profile, Integer> ageColumn;
    @FXML
    private TableColumn<Profile, String> genderColumn;
    @FXML
    private TableColumn<Profile, String> regionColumn;
    @FXML
    private CheckBox ageRangeCheckbox;
    @FXML
    private Label labelResultCount;
    @FXML
    private Label labelCurrentOnDisplay;
    @FXML
    private Label labelToManyResults;
    @FXML
    private Button buttonShowAll;
    @FXML
    private Button buttonShowNext;

    private ObservableList<Profile> donorObservableList = FXCollections.observableArrayList();

    private ArrayList<Profile> profileSearchResults = new ArrayList<>();
    private ClinicianProfileView parentView;

    /**
     * Initializes and refreshes the search table Adds a listener to each row so that when it is
     * double clicked a new donor window is opened. Calls the setTooltipToRow function.
     */
    @FXML
    private void makeSearchTable() {
        ProfileDAO database = DAOFactory.getProfileDao();
        try {
            labelResultCount.setText(database.size() + " results found");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        searchTable.getItems().clear();

        donorObservableList = FXCollections.observableArrayList();
        searchTable.setItems(donorObservableList);
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullPreferredName"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        donorReceiverColumn.setCellValueFactory(p -> {
            Profile x = p.getValue();
            return controller.donorReceiverProperty(x);
        });
        searchTable.getColumns().setAll(fullNameColumn, donorReceiverColumn, ageColumn, genderColumn, regionColumn);

        searchTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2 &&
                    searchTable.getSelectionModel().getSelectedItem() != null) {
                createNewDonorWindow(searchTable.getSelectionModel().getSelectedItem(), parentView);
            }
        });

        genderCombobox.addEventHandler(ComboBox.ON_HIDING, event -> {
            performSearchFromFilters();
        });

        organsCombobox.addEventHandler(ComboBox.ON_HIDING, event -> {
            performSearchFromFilters();
        });

        addTooltipToRow();

    }

    /**
     * adds a tooltip to each row of the table containing their organs donated.
     */
    private void addTooltipToRow() {
        searchTable.setRowFactory(tableView -> {
            final TableRow<Profile> row = new TableRow<>();

            row.hoverProperty().addListener((observable) -> {
                final Profile donor = row.getItem();
                String donations = "";
                if (row.isHover() && donor != null) {
                    if (donor.getOrgansDonated().size() > 0) {
                        donations = ". Donor: " + donor.getOrgansDonated().toString();
                    }
                    row.setTooltip(new Tooltip(donor.getFullName() + donations));
                }
            });
            return row;
        });
    }

    @FXML
    private void handleAgeRangeCheckboxChecked() {
        if (ageRangeCheckbox.isSelected()) {
            ageRangeField.setDisable(false);
            ageField.setPromptText("Lower Age");
            ageRangeField.setPromptText("Upper Age");
            ageRangeField.clear();
        } else {
            ageRangeField.setDisable(true);
            ageField.setPromptText("Age");
        }
        updateLabels();
    }

    /**
     * Button handler to display all search results in the search table
     *
     * @param event clicking on the show all button.
     */
    @FXML
    private void handleGetAllResults(ActionEvent event) {
        buttonShowAll.setVisible(false);
        buttonShowNext.setVisible(false);
        updateTable(true, false);
        labelCurrentOnDisplay.setText("displaying 1 to " + searchTable.getItems().size());
    }

    /**
     * Button handler to display next 25 search results in the search table
     *
     * @param event clicking on the show all button.
     */
    @FXML
    private void handleGetXResults(ActionEvent event) {
        updateTable(false, true);
        labelCurrentOnDisplay.setText("displaying 1 to " + searchTable.getItems().size());
    }

    /**
     * updates the display labels and button status in the search tab.
     */
    private void updateLabels() {
        labelToManyResults.setVisible(false);

        if (profileSearchResults == null || profileSearchResults.size() == 0) {
            labelCurrentOnDisplay.setText("displaying 0 to 0");
            labelResultCount.setText("0 results found");
            searchTable.setPlaceholder(new Label(
                    "0 profiles found"));
            buttonShowAll.setVisible(false);
            buttonShowNext.setVisible(false);
        } else {
            if (profileSearchResults.size() <= PAGESIZE) {
                labelCurrentOnDisplay.setText("displaying 1 to " + profileSearchResults.size());
                buttonShowAll.setVisible(false);
                buttonShowNext.setVisible(false);
            } else {
                labelCurrentOnDisplay.setText("displaying 1 to " + PAGESIZE);
                if (profileSearchResults.size() > MAXPAGESIZE) {
                    labelToManyResults.setVisible(true);
                    buttonShowAll.setVisible(false);
                    buttonShowNext.setVisible(false);
                } else {
                    buttonShowAll.setText("Show all " + profileSearchResults.size() + " results");
                    buttonShowNext.setText("Show next 25 results");
                    buttonShowAll.setVisible(true);
                    buttonShowNext.setVisible(true);
                }
            }
        }
    }

    /**
     * Clears the searchTable and updates with search results of profiles from the fuzzy search.
     */
    private void performSearchFromFilters() {
        profileSearchResults = controller.performSearch(organsCombobox.getCheckModel().getCheckedItems(),
                typeCombobox.getValue().toString(), genderCombobox.getValue().toString(), searchField.getText(), regionField.getText(),
                ageField.getText(), ageRangeField.getText(), ageRangeCheckbox.isSelected());

        updateTable(false, false);
        updateLabels();

    }

    /**
     * Clears the searchTable and updates with objects from the profileSearchResults arrayList.
     * Results displayed depend on the variable showAll, if this is false it will display only 50 or
     * less (if profileSearchResults is smaller than 50) results. If it is true all results will be
     * displayed, as long as profileSearchResults is under 200 objects in size.
     *
     * @param showAll  boolean, if true will display all objects in class variable
     *                 profileSearchResults.
     * @param showNext boolean, if true will display next 25 results.
     */
    private void updateTable(boolean showAll, boolean showNext) {
        int size = donorObservableList.size();
        searchTable.getItems().clear();
        if (profileSearchResults != null) {
            if (profileSearchResults.size() == 1) {
                labelResultCount.setText(profileSearchResults.size() + " result found");
            } else {
                labelResultCount.setText(profileSearchResults.size() + " results found");
            }

            if (showAll) {
                if (profileSearchResults.size() > 200) {
                    labelResultCount.setText(0 + " results found");
                } else {
                    donorObservableList.addAll(profileSearchResults);
                }
            } else if (showNext) {
                if (profileSearchResults.size() > (size + PAGESIZE)) {
                    donorObservableList.addAll(profileSearchResults.subList(0, size + PAGESIZE));
                    if (profileSearchResults.subList(size + PAGESIZE, profileSearchResults.size())
                            .size() < PAGESIZE) {
                        buttonShowNext.setText("Show next " + profileSearchResults
                                .subList(size + PAGESIZE, profileSearchResults.size()).size()
                                + " results");
                    }
                } else {
                    donorObservableList.addAll(profileSearchResults);
                    buttonShowNext.setVisible(false);
                    buttonShowAll.setVisible(false);
                }

            } else if (profileSearchResults.size() > PAGESIZE) {
                donorObservableList.addAll(profileSearchResults.subList(0, PAGESIZE));
            } else {
                donorObservableList.addAll(profileSearchResults);
            }

            searchTable.setItems(donorObservableList);
        }
    }

    public void setCurrentUser(odms.model.user.User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Limits the characters entered in textfield to only digits and maxLength
     *
     * @param maxLength that can be entered in the textfield
     * @return
     */
    public EventHandler<KeyEvent> numeric_Validation(final Integer maxLength) {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                TextField txt_TextField = (TextField) e.getSource();
                if (txt_TextField.getText().length() >= maxLength) {
                    e.consume();
                }
                if (e.getCharacter().matches("[0-9.]")) {
                    if (txt_TextField.getText().contains(".") && e.getCharacter().matches("[.]")) {
                        e.consume();
                    } else if (txt_TextField.getText().length() == 0 && e.getCharacter()
                            .matches("[.]")) {
                        e.consume();
                    }
                } else {
                    e.consume();
                }
            }
        };
    }

    @FXML
    public void initialize(User currentUser, ClinicianProfileView parentView) {
        this.parentView = parentView;
        if (currentUser != null) {
            ageRangeField.setDisable(true);
            ageField.addEventHandler(KeyEvent.KEY_TYPED, numeric_Validation(10));
            ageRangeField.addEventHandler(KeyEvent.KEY_TYPED, numeric_Validation(10));
            genderStrings.clear();
            genderStrings.add("any");
            genderStrings.add("male");
            genderStrings.add("female");
            genderCombobox.getItems().setAll(genderStrings);
            genderCombobox.getSelectionModel().selectFirst();

            organsStrings.clear();
            organsStrings.addAll(OrganEnum.toArrayList());
            organsCombobox.getItems().setAll(OrganEnum.values());

            typeStrings.clear();
            typeCombobox.getItems().clear();
            typeStrings.add("any");
            typeStrings.add("donor");
            typeStrings.add("receiver");
            typeCombobox.getItems().addAll(typeStrings);
            typeCombobox.getSelectionModel().selectFirst();

            typeCombobox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    performSearchFromFilters();
                }
            });

            genderCombobox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    performSearchFromFilters();
                }
            });

            ProfileDAO database = DAOFactory.getProfileDao();

            try {
                makeSearchTable();
                searchTable.getItems().clear();
                searchTable.setPlaceholder(new Label(
                        "There are " + database.size()
                                + " profiles"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        setPauseTransitions();
    }

    /**
     * Sets a pause transition for searchField, ageField, ageRangeField and regionField.
     */
    private void setPauseTransitions() {
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished(ae -> {
                performSearchFromFilters();
            });
            pauseTransition.playFromStart();
        });

        ageField.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished(ae -> {
                performSearchFromFilters();
            });
            pauseTransition.playFromStart();
        });

        ageRangeField.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished(ae -> {
                performSearchFromFilters();
            });
            pauseTransition.playFromStart();
        });

        regionField.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished(ae -> {
                performSearchFromFilters();
            });
            pauseTransition.playFromStart();
        });
    }
}