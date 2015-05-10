package ch.hslu.mpbro15.team10.battleship.model;

/**
 * Created by dave on 10.05.2015.
 */
public class BattleshipInvalidPlacementException extends Exception {

    public BattleshipInvalidPlacementException() {
        super("Invalid placement in Grid: ");
    }

    public BattleshipInvalidPlacementException(String msg) {
        super("Invalid placement in Grid: " + msg);
    }

}
