package ch.hslu.mpbro15.team10.battleship.Model;

import java.util.ArrayList;

/**
 * Created by dave on 03.05.2015.
 */
public class Grid {

    private int gridSize;
    private ArrayList<Ship> ships;

    public Grid() {
        this(10); //Default GridModel Size
    }

    public Grid(int gridSize) {
        this.gridSize = gridSize;
        this.ships = new ArrayList<>();

        ships.add(new Ship(2, EOrientation.HORIZONTAL));
        ships.add(new Ship(3, EOrientation.HORIZONTAL));
        ships.add(new Ship(3, EOrientation.HORIZONTAL));
        ships.add(new Ship(4, EOrientation.HORIZONTAL));
        ships.add(new Ship(5, EOrientation.HORIZONTAL));
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public boolean hit(int xPos, int yPos) {
        for (Ship ship : ships) {
            if (ship.hit(xPos, yPos)) {
                return true;
            }
        }
        return false;
    }

    public Ship getShipByPosition(int xPos, int yPos) {
        Ship currentShip = null;
        for (Ship ship : this.ships) {
            if (ship.getOrientation() == EOrientation.HORIZONTAL) {
                if (ship.getYPos() == yPos) {
                    for (int i = 0; i < ship.getSize(); i++) {
                        if (xPos == (ship.getXPos() + i)) {
                            currentShip = ship;
                        }
                    }
                }

            } else {
                if (ship.getXPos() == xPos) {
                    for (int i = 0; i < ship.getSize(); i++) {
                        if (yPos == (ship.getYPos() + i)) {
                            currentShip = ship;
                        }
                    }
                }
            }
        }
        return currentShip;
    }

    public boolean placeShip(Ship ship, int xPos, int yPos) {
        boolean isValid = true;

        //Grid Border Collision Check
        switch (ship.getOrientation()) {
            case HORIZONTAL:
                if (ship.getSize() + xPos > gridSize) {
                    isValid = false;
                }
                break;
            case VERTICAL:
                if (ship.getSize() + yPos > gridSize) {
                    isValid = false;
                }
                break;
            default:
                break;
        }

        //If the ship is in the grid border continue to check for a valid ship position among other ships
        if (isValid) {

            System.out.println("Border Check Passed");

            //Ship Collision Check
            //Go through all placed Ships. Check both Orientations
            //Check all coordinates for the ship in all placedShips
            int forX = xPos + 1;
            int forY = yPos + 1;
            if (ship.getOrientation() == EOrientation.HORIZONTAL) {
                forX = xPos + ship.getSize();
            } else {
                forY = yPos + ship.getSize();
            }

            for (int x = xPos; x < forX; x++) {
                for (int y = yPos; y < forY; y++) {
                    System.out.println("Check Ship " + x + " | " + y);
                    for (Ship placedShip : ships) {

                        int forXPlacedShip = placedShip.getXPos() + 1;
                        int forYPlacedShip = placedShip.getYPos() + 1;

                        if (placedShip.getOrientation() == EOrientation.HORIZONTAL) {
                            forXPlacedShip = placedShip.getXPos() + placedShip.getSize();
                        } else {
                            forYPlacedShip = placedShip.getYPos() + placedShip.getSize();
                        }

                        for (int xPlacedShip = placedShip.getXPos(); xPlacedShip < forXPlacedShip; xPlacedShip++) {
                            for (int yPlacedShip = placedShip.getYPos(); yPlacedShip < forYPlacedShip; yPlacedShip++) {
                                if (x == xPlacedShip && y == yPlacedShip) {
                                    isValid = false;
                                    break;
                                }
                            }
                            if (isValid == false) {
                                break;
                            }
                        }
                        if (isValid == false) {
                            break;
                        }
                    }
                    if (isValid == false) {
                        break;
                    }
                }
                if (isValid == false) {
                    break;
                }
            }

            System.out.println("IsValid " + isValid);
            //Border and Ship Collision check successful. Set the ship
            if (isValid) {
                ship.setPosX(xPos);
                ship.setPosY(yPos);
            }
        } else {
            System.out.println("Border Check Failed");
        }

        return isValid;
    }

    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }

    public int getGridSize() {
        return this.gridSize;

    }

}
