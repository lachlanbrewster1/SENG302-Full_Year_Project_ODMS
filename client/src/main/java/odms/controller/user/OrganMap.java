package odms.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import odms.commons.model.enums.NewZealandRegionsEnum;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;
import odms.controller.data.AddressIO;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;
import org.controlsfx.control.PopOver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Organ map controller.
 */
@Slf4j
public class OrganMap extends CommonController {

    private static final double VBOX_PADDING = 10.0;

    private odms.view.user.OrganMap view;

    public void setView(odms.view.user.OrganMap v) {
        view = v;
    }

    /**
     * Gets all of the dead donors.
     *
     * @return Observable list of dead donors.
     */
    public ObservableList<Profile> getDeadDonors() {
        ArrayList<Profile> deadDonors = new ArrayList<>();
        ProfileDAO database = DAOFactory.getProfileDao();
        List<Profile> allDonors = null;
        try {
            allDonors = database.getDead();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        if (allDonors != null) {
            for (Profile profile : allDonors) {
                // If the profile has organs to donate
                if (!profile.getOrgansDonatingNotExpired().isEmpty()) {
                    deadDonors.add(profile);
                }
            }
        }
        return FXCollections.observableArrayList(deadDonors);
    }

    /**
     * Gets all of the dead donors according to a given search string.
     *
     * @param searchString The string to search by
     * @return Observable list of dead donors.
     */
    public ObservableList<Profile> getDeadDonorsFiltered(String searchString) {
        ArrayList<Profile> deadDonors = new ArrayList<>();
        ProfileDAO database = DAOFactory.getProfileDao();
        List<Profile> allDonors = null;
        try {
            allDonors = database.getDeadFiltered(searchString);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        if (allDonors != null) {
            for (Profile profile : allDonors) {
                // If the profile has organs to donate
                if (!profile.getOrgansDonatingNotExpired().isEmpty()) {
                    deadDonors.add(profile);
                }
            }
        }
        return FXCollections.observableArrayList(deadDonors);
    }

    /**
     * Gets all of the receivers for a particular donor.
     *
     * @param donatingProfile The donor that was selected.
     * @return Observable list of receivers.
     */
    public ObservableList<Profile> getReceivers(Profile donatingProfile) {
        List<Profile> receivingProfiles = new ArrayList<>();

        Set<OrganEnum> donatingOrgans = donatingProfile.getOrgansDonatingNotExpired();

        for (OrganEnum organ : donatingOrgans) {
            receivingProfiles.addAll(AvailableOrgans
                    .getSuitableRecipients(organ, donatingProfile));
        }

        return FXCollections.observableArrayList(receivingProfiles);
    }

    public Callback<ListView<ExpandableListElement>, ListCell<ExpandableListElement>> getListViewCallback() {
        return new Callback<ListView<ExpandableListElement>, ListCell<ExpandableListElement>>() {
            @Override
            public ListCell<ExpandableListElement> call(final ListView<ExpandableListElement> lv) {
                return new ListCell<ExpandableListElement>() {
                    @Override
                    protected void updateItem(final ExpandableListElement item, boolean empty) {
                        super.updateItem(item, empty);
                        final VBox vbox = new VBox();
                        setGraphic(vbox);
                        int height = 10;

                        if (item != null && getIndex() > -1) {
                            final Label labelHeader = new Label(item.getTitle());
                            labelHeader.setGraphic(createArrowPath(height, false));
                            labelHeader.setGraphicTextGap(10);
                            labelHeader.setId("tableview-columnheader-default-bg");
                            labelHeader.setPrefWidth(view.getMatchesListViewWidth() - 10);
                            labelHeader.setPrefHeight(height);

                            labelHeader.setOnMouseClicked(me -> {
                                item.setHidden(!item.isHidden());
                                if (item.isHidden()) {
                                    labelHeader.setGraphic(createArrowPath(height, false));
                                    vbox.getChildren().remove(vbox.getChildren().size() - 1);
                                } else {
                                    labelHeader.setGraphic(createArrowPath(height, true));
                                    vbox.getChildren().add(item.getContents());
                                }
                            });

                            vbox.getChildren().add(labelHeader);
                        }
                    }

                };
            }
        };
    }

    /**
     * Creates an arrow path.
     *
     * @param height height.
     * @param up     true if up.
     * @return an SVG path.
     */
    private SVGPath createArrowPath(int height, boolean up) {
        SVGPath svg = new SVGPath();
        int width = height / 4;

        if (up) {
            svg.setContent(
                    "M" + width + " 0 L" + (width * 2) + " " + width + " L0 " + width + " Z");
        } else {
            svg.setContent("M0 0 L" + (width * 2) + " 0 L" + width + " " + width + " Z");
        }

        return svg;
    }

    /**
     * Creates a new pop over containing profile info and a match and open profile button.
     *
     * @param profile Profile marker clicked on.
     * @return a pop over.
     */
    public PopOver createNewPopOver(Profile profile) {

        VBox vbox = createVbox(profile);

        PopOver popOver = new PopOver(vbox);
        popOver.animatedProperty().setValue(true);

        return popOver;
    }

    /**
     * Creates a vbox containing the PopOver content.
     *
     * @param profile Profile marker clicked on.
     * @return VBox.
     */
    private VBox createVbox(Profile profile) {
        Label lblName = new Label(profile.getFullPreferredName());
        VBox vBox = new VBox(lblName, view.getOpenProfileBtn(), view.getMatchBtn());
        vBox.setSpacing(VBOX_PADDING);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(VBOX_PADDING));

        return vBox;
    }

    /**
     * Gets a profiles lat long for the region and country.
     *
     * @param profile Profile being displayed.
     * @return A list containing the lat and long.
     */
    public List<Double> getProfileLatLong(Profile profile) {
        return AddressIO.getLongLatRegion(profile.getRegion(), profile.getCountry());
    }
}
