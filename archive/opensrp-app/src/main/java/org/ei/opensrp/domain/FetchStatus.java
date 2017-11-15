package org.ei.opensrp.domain;

public enum FetchStatus implements Displayable {
    fetched("Update successful."), nothingFetched("Already up to date."), fetchedFailed("Update failed. Please try again.");
    private String displayValue;

    private FetchStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }
}
