package ca.bcit.net;

import ca.bcit.Settings;

public class Regenerator {
    private boolean[] occupiedStatuses = new boolean[Settings.numberOfCores];

    public boolean[] getOccupiedStatuses() {
        return occupiedStatuses;
    }

    public void setOccupiedStatuses(boolean[] occupiedStatuses) {
        this.occupiedStatuses = occupiedStatuses;
    }
}
