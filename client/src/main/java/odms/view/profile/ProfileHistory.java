package odms.view.profile;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import odms.model.profile.Profile;


public class ProfileHistory {
    @FXML
    private TextArea historyView;

    private Profile currentProfile;
    // init controller corresponding to this view
    private odms.controller.profile.ProfileHistory controller = new odms.controller.profile.ProfileHistory(this);

    public Profile getProfile() {
        return currentProfile;
    }

    private void setHistoryView() {
        historyView.setText(controller.getHistory());
    }

    //todo do we need this function?????
    public void initialize(Profile p) {
        currentProfile = p;
        if (currentProfile != null) {
            setHistoryView();
        }
    }
}