package ch.hslu.mpbro15.team10.battleship.model;

/**
 * Created by dave on 10.05.2015.
*/
public class GOMinecruiser extends BattleshipGameObject {

    private int lenght = 1;

    @Override
    public boolean isSunk() {
        return BattleshipGameObject.hitCountMinecruiser >= this.getLength();
    }

    @Override
    public int getLength() {
        return lenght;
    }

    @Override
    public void shoot() {
        super.hit();
        super.shot();
        BattleshipGameObject.hitCountMinecruiser++;
    }
}
