package edu.univalle.battleship.model;

/**
 * Represents the data model for the start screen of the Battleship game.
 * <p>
 * This class holds information about the availability of continuing a previously saved game.
 */
public class StartModel {

    /** Indicates whether the "Continue Game" option is available. */
    private boolean continueGameAvailable;

    /**
     * Constructs a new {@code StartModel} instance.
     * <p>
     * By default, the "Continue Game" option is set to unavailable.
     */
    public StartModel() {
        this.continueGameAvailable = false;
    }
}
