package io.mapwize.mapwizeui.details;

import java.util.List;

public class PlaceDetailsConfig {
    private List<ButtonSmall> buttonsSmall;
    private List<ButtonBig> buttonsBig;
    private List<Row> rows;
    private boolean preventExpandDetails = false;

    public PlaceDetailsConfig(List<ButtonSmall> buttonsSmall, List<ButtonBig> buttonsBig, List<Row> rows) {
        this.buttonsSmall = buttonsSmall;
        this.buttonsBig = buttonsBig;
        this.rows = rows;
    }

    public List<ButtonSmall> getButtonsSmall() {
        return buttonsSmall;
    }

    public void setButtonsSmall(List<ButtonSmall> buttonsSmall) {
        this.buttonsSmall = buttonsSmall;
    }

    public List<ButtonBig> getButtonsBig() {
        return buttonsBig;
    }

    public void setButtonsBig(List<ButtonBig> buttonsBig) {
        this.buttonsBig = buttonsBig;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public boolean isPreventExpandDetails() {
        return preventExpandDetails;
    }

    public void setPreventExpandDetails(boolean preventExpandDetails) {
        this.preventExpandDetails = preventExpandDetails;
    }
}
