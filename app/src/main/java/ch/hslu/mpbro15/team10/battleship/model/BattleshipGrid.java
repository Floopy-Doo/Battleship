package ch.hslu.mpbro15.team10.battleship.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dave on 10.05.2015.
 */
public final class BattleshipGrid {
    // Singeltons

    private static BattleshipGrid opponentGrid;
    private static BattleshipGrid ownGrid;
    // Static vairables
    public static final int SHIP_MINECRUISER = 0;
    public static final int SHIP_SUBMARINE = 1;
    public static final int SHIP_DESTROYER = 2;
    public static final int SHIP_BATTLESHIP = 3;
    public static final int SHIP_CARRIER = 4;
    public static final int DIRECTION_HORIZONTAL = 0;
    public static final int DIRECTION_VERTICAL = 1;
    public static final int GRID_WIDHT = 10;
    public static final int GRID_HEIGHT = 10;
    // instance variables
    private BattleshipGameObject[][] playground;
    private BattleshipGameObject lastPlacedShip;
    private int placedShips = 0;

    private BattleshipGrid() {
        //Grid wird mit Wasserobjekten gefüllt
        playground = new BattleshipGameObject[GRID_WIDHT][GRID_HEIGHT];

        // fill grid with water
        for (int x = 0; x < GRID_WIDHT; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                playground[x][y] = new GOWater();
                playground[x][y].setCoordinates(String.valueOf(x),String.valueOf(y));
            }
        }
    }

    private BattleshipGrid(BattleshipGameObject[][] grid) {
        playground = grid;
        placedShips = 5;
    }

    public static BattleshipGrid prepareOwnGrid() {
        if (ownGrid == null) {
            ownGrid = new BattleshipGrid();
        }
        return ownGrid;
    }

    public static BattleshipGrid prepareOpponentGrid() {
        if (opponentGrid == null) {
            opponentGrid = new BattleshipGrid();
        }
        return opponentGrid;
    }

    public static BattleshipGrid prepareTemAIGrid() {
        return new BattleshipGrid();
    }

    public void placeShip(int x, int y, int type, int direction) throws BattleshipInvalidPlacementException {
        BattleshipGameObject placedShip;
        ArrayList<RollbackObject> rollbackStore = new ArrayList<RollbackObject>();

        try {
            if (playground[x][y] instanceof GOWater) {
                placedShip = getGO(type); //create new GO for each field
                playground[x][y] = placedShip;
                playground[x][y].setCoordinates(String.valueOf(x), String.valueOf(y));
                rollbackStore.add(new RollbackObject(x, y));
                checkShipPlacing(placedShip, x, y, direction, true);

                try {
                    for (int l = 1; l < placedShip.getLength(); l++) {
                        placedShip = getGO(type); //create new GO for each field
                        switch (direction) {
                            case DIRECTION_HORIZONTAL:
                                playground[x + l][y] = placedShip;
                                playground[x + l][y].setCoordinates(String.valueOf(x), String.valueOf(y));
                                rollbackStore.add(new RollbackObject(x + l, y));
                                checkShipPlacing(placedShip, x + l, y, direction, false);
                                break;
                            case DIRECTION_VERTICAL:
                                playground[x][y + l] = placedShip;
                                playground[x][y + l].setCoordinates(String.valueOf(x), String.valueOf(y));
                                rollbackStore.add(new RollbackObject(x, y + l));
                                checkShipPlacing(placedShip, x, y + l, direction, false);
                                break;
                            default:
                                throw new IllegalArgumentException("Direction not recognised!");
                        }
                    }

                } catch (IndexOutOfBoundsException iobEx) {
                    throw new BattleshipInvalidPlacementException("Ship is out of grid.");
                }

                placedShips++;
            } else {
                throw new BattleshipInvalidPlacementException("Field is taken");
            }
        } catch (Exception e) { // do rollback if any error has occured
            doRollback(rollbackStore);
            throw e;
        }
    }

    private void checkShipPlacing(BattleshipGameObject placedShip, int x, int y, int direction, boolean isStartOfShip)
            throws BattleshipInvalidPlacementException {
        boolean allowed;
        if (isStartOfShip) {
            allowed = checkFieldIsWater(x - 1, y);      //feld links
            allowed &= checkFieldIsWater(x + 1, y);     //feld rechts
            allowed &= checkFieldIsWater(x, y - 1);     //feld open
            allowed &= checkFieldIsWater(x, y + 1);     //feld unten

        } else if (direction == DIRECTION_HORIZONTAL) {
            allowed = checkFieldIsShip(x - 1, y, placedShip.getClass());        //feld links
            allowed &= checkFieldIsWater(x + 1, y);                             //feld rechts
            allowed &= checkFieldIsWater(x, y - 1);                             //feld open
            allowed &= checkFieldIsWater(x, y + 1);                             //feld unten
        } else {
            allowed = checkFieldIsWater(x - 1, y);                              //feld links
            allowed &= checkFieldIsWater(x + 1, y);                             //feld rechts
            allowed &= checkFieldIsShip(x, y - 1, placedShip.getClass());       //feld open
            allowed &= checkFieldIsWater(x, y + 1);                             //feld unten
        }
        if (!allowed) {
            throw new BattleshipInvalidPlacementException("");
        }
    }

    private boolean checkFieldIsWater(int x, int y) {
        return checkFieldIsShip(x, y, GOWater.class);
    }

    private boolean checkFieldIsShip(int x, int y, Class shipClass) {
        if (x >= 0 && x < GRID_WIDHT && y >= 0 && y < GRID_HEIGHT) {
            return (playground[x][y].getClass() == shipClass);
        }
        // when checked field is out of bound, then it is treated as wather.
        return true;
    }

    private void doRollback(ArrayList<RollbackObject> rollbackList) {
        for (RollbackObject ro : rollbackList) {
            playground[ro.x][ro.y] = new GOWater();
        }
        lastPlacedShip = null;
    }

    private BattleshipGameObject getGO(int shipType) {
        BattleshipGameObject currentShip;
        switch (shipType) {
            case SHIP_MINECRUISER:
                currentShip = new GOMinecruiser();
                break;
            case SHIP_SUBMARINE:
                currentShip = new GOSubmarine((lastPlacedShip instanceof GOSubmarine) ? (GOSubmarine) lastPlacedShip : null);
                break;
            case SHIP_DESTROYER:
                currentShip = new GODestroyer((lastPlacedShip instanceof GODestroyer) ? (GODestroyer) lastPlacedShip : null);
                break;
            case SHIP_BATTLESHIP:
                currentShip = new GOBattleship((lastPlacedShip instanceof GOBattleship) ? (GOBattleship) lastPlacedShip : null);
                break;
            case SHIP_CARRIER:
                currentShip = new GOCarrier((lastPlacedShip instanceof GOCarrier) ? (GOCarrier) lastPlacedShip : null);
                break;
            default:
                throw new IllegalArgumentException("Ship type not recognised!");
        }

        lastPlacedShip = currentShip;
        return currentShip;
    }

    public boolean shoot(int cordX, int cordY) {
        BattleshipGameObject go = playground[cordX][cordY];
        go.shoot();
        if (go.isSunk()) {
            placedShips--;
        }
        return go.isHit();
    }

    public boolean isSunk(int cordX, int cordY) {
        BattleshipGameObject go = playground[cordX][cordY];
        return go.isSunk();
    }

    public boolean isAllSunk() {
        return (placedShips == 0);
    }

    public BattleshipGameObject[][] getGrid() {
        return playground;
    }

    private class RollbackObject {

        public int x;
        public int y;

        public RollbackObject(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void printGrid() {
        for (BattleshipGameObject[] go1 : playground) {
            for (BattleshipGameObject go2 : go1) {
                System.out.print("| " + go2.getClass() + " : "+ go2.isShot() + "\t");
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {
        try {
            BattleshipGrid grid = BattleshipGrid.prepareOwnGrid();
            grid.placeShip(0, 0, 4, 0);
            grid.printGrid();
            GOCarrier go = (GOCarrier) grid.getGrid()[0][0];
            System.out.println(go.isSunk());
            grid.shoot(0,0);
            System.out.println(go.isSunk());
            grid.shoot(1,0);
            System.out.println(go.isSunk());
            grid.shoot(2,0);
            System.out.println(go.isSunk());
            grid.shoot(3,0);
            System.out.println(go.isSunk());
            grid.shoot(4,0);
            System.out.println(go.isSunk());

            grid.printGrid();
        } catch (Exception ex) {
        }
    }
}