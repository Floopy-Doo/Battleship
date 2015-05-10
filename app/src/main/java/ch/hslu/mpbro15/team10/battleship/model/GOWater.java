package ch.hslu.mpbro15.team10.battleship.model;

/**
 * Created by dave on 10.05.2015.
 */
public class GOWater extends BattleshipGameObject {
    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public boolean isSunk() {
        return false;
    }

    @Override
    public void shoot() {
        super.shot();
    }
}
