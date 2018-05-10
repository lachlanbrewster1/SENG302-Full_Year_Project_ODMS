package odms.profile;

import java.time.DateTimeException;
import java.time.LocalDate;
import jdk.nashorn.internal.runtime.arrays.ArrayIndex;

/**
 * A specific condition for use in medical history
 */
public class Condition {
    private String name;
    private LocalDate dateOfDiagnosis;
    private LocalDate dateCured = null;
    private String dateOfDiagnosisString;
    private String dateCuredString;
    private boolean isCured = false;
    private boolean isChronic = false;
    private String chronicText = "";

    /**
     * Constructor for cured conditions
     * @param name
     * @param dateOfDiagnosis
     * @param dateCured
     * @param isChronic
     */
    public Condition(String name, String dateOfDiagnosis, String dateCured, boolean isChronic) throws IllegalArgumentException {

        this.name = name;
        dateOfDiagnosisString = dateOfDiagnosis;
        dateCuredString = dateCured;
        String[] dates = dateOfDiagnosis.split("-");
        try {
            this.dateOfDiagnosis = LocalDate
                    .of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]),
                            Integer.valueOf(dates[0]));
            if (dateCured != null && isChronic == false) {
                this.isCured = true;
                dates = dateCured.split("-");
                this.dateCured = LocalDate.of(Integer.valueOf(dates[2]), Integer.valueOf(dates[1]),
                        Integer.valueOf(dates[0]));
            } else {
                this.isCured = false;
                this.dateCured = null;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException(e);
        }
        this.isChronic = isChronic;
        if (isChronic) {this.chronicText = "CHRONIC";}
    }

    /**
     * Constructor for uncured conditions
     * @param name
     * @param dateOfDiagnosis
     * @param isChronic
     */
    public Condition(String name, String dateOfDiagnosis, boolean isChronic) throws IllegalArgumentException {
        this(name, dateOfDiagnosis, null, isChronic);
    }

    // getters
    public String getName() { return this.name; }
    public LocalDate getDateOfDiagnosis() { return dateOfDiagnosis; }
    public LocalDate getDateCured() { return dateCured; }
    public String getDateOfDiagnosisString() { return dateOfDiagnosisString; }
    public String getDateCuredString() { return dateCuredString; }
    public boolean getCured() { return this.isCured; }
    public boolean getChronic() { return isChronic; }
    public String getChronicText() { return chronicText; }



    // setters
    public void setIsCured(boolean isCured) { this.isCured = isCured; }
    public void setIsChronic(boolean isChronic) { this.isChronic = isChronic; }
    public void setChronicText(String chronicText) { this.chronicText = chronicText; }
    public void setDateOfDiagnosis(LocalDate dateOfDiagnosis) { this.dateOfDiagnosis = dateOfDiagnosis; }
    public void setDateCured(LocalDate dateCured) { this.dateCured = dateCured; }
    public void setName(String name) { this.name = name; }


}
