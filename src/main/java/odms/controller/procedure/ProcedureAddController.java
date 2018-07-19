package odms.controller.procedure;

import javafx.collections.ObservableList;
import odms.view.profile.ProfileAddProcedureView;
import odms.view.profile.ProfileDisplayControllerTODO;
import odms.model.enums.OrganEnum;
import odms.model.profile.Procedure;

import java.time.LocalDate;
import java.util.ArrayList;

public class ProcedureAddController {

    private ProfileDisplayControllerTODO controller;

    private ObservableList<OrganEnum> donatedOrgans;

    private ProfileAddProcedureView view;

    public ProcedureAddController(ProfileAddProcedureView v) {
        view = v;
    }

    public void add() throws IllegalArgumentException {
        String summary = view.getSummaryField();
        LocalDate dateOfProcedure = view.getDateOfProcedureDatePicker();
        String longDescription = view.getDescriptionField();

        Procedure procedure;
        LocalDate dob = controller.getCurrentProfile().getDateOfBirth();
        if (dob.isAfter(dateOfProcedure)) {
            throw new IllegalArgumentException();
        }
        if (longDescription.equals("")) {
            procedure = new Procedure(summary, dateOfProcedure);

        } else {
            procedure = new Procedure(summary, dateOfProcedure, longDescription);
        }

        procedure.setOrgansAffected(view.getAffectedOrgansListView());

        addProcedure(procedure);
    }

    /**
     * Add a procedure to the current profile
     *
     * @param procedure
     */
    private void addProcedure(Procedure procedure) {
        ArrayList<Procedure> procedures = view.getSearchedDonor().getAllProcedures();
        procedures.add(procedure);
    }



}