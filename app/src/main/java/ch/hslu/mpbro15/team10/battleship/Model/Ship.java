package ch.hslu.mpbro15.team10.battleship.Model;

import java.util.ArrayList;

/**
 * Created by dave on 03.05.2015.
 */
public class Ship {
    public static int idCount = 0;
    private final int size;
    private final int id;
    private int xPos;
    private int yPos;
    private EOrientation orientation;
    private ArrayList<ShipPart> shipParts;


    public Ship(int size, EOrientation orientation) {
        id = idCount++;
        this.size = size;
        this.orientation = orientation;
        this.xPos = 0 - this.size; //Set Position in the negative area. else there are blocked fields to set the ships
        this.yPos = 0 - this.size;

        shipParts = new ArrayList<>(this.size);

        for (int i = 0; i < this.size; i++) {
            shipParts.add(new ShipPart());
        }
    }

    public boolean hit(int xPos, int yPos) {
        boolean isHit = false;
        int deltaHitPoint = -1;

        // calculate if hit is within the ship
        if (this.orientation == EOrientation.HORIZONTAL && this.yPos == yPos) {
            deltaHitPoint = xPos - this.xPos;
        }
        else if (this.orientation == EOrientation.VERTICAL && this.xPos == xPos) {
            deltaHitPoint = yPos - this.yPos;
        }

        //First check is if it fires before the ship. The second if it fires after the ship
        if (deltaHitPoint >= 0 && deltaHitPoint < this.size) {
            shipParts.get(deltaHitPoint).setDestroyed();
            isHit = true;
        }

        return isHit;
    }

    public boolean isDestroyed() {
        // Check if one part is not destoryed
        for (ShipPart part : shipParts) {
            if (!part.getDestroyed()) {
                return false;
            }
        }

        // all parts are destoyed
        return true;
    }

    public int getXPos() {
        return this.xPos;
    }

    public int getYPos() {
        return this.yPos;
    }

    public int getSize() {
        return this.size;
    }

    public int getId() {
        return this.id;
    }

    public EOrientation getOrientation() {
        return this.orientation;
    }

    public void setOrientation(EOrientation orientation) {
        this.orientation = orientation;
    }

    public void setPosX(int x) {
        xPos = x;
    }

    public void setPosY(int y) {
        yPos = y;
    }

    @Override
    public String toString() {
        return (getSize() + "er Schiff");
    }

}
