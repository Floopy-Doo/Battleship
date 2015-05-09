package ch.hslu.mpbro15.team10.battleship.Model;

/**
 * Created by dave on 03.05.2015.
 */
public abstract class Player {

    private String name;
    //private ClientThread clientThread;
    private int playerIndex;
    private boolean shipsPlaced;
    private Grid battleShipGrid;

    public Player() {
        shipsPlaced = false;
        battleShipGrid = new Grid();
    }

    public void setName(String name) {
        this.name = name;
    }

    /*    public void setClientThread(ClientThread clientThread) {
    this.clientThread = clientThread;
    }*/

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public int getPlayerIndex() {
        return this.playerIndex;
    }


    /*    public ClientThread getClientThread() {
    return this.clientThread;
    }*/

    /**
     * @return the shipsPlaced
     */
    public boolean isShipsPlaced() {
        return shipsPlaced;
    }

    /**
     * @param shipsPlaced the shipsPlaced to set
     */
    public void setShipsPlaced(boolean shipsPlaced) {
        this.shipsPlaced = shipsPlaced;
    }

    /**
     * @return the battleShipGrid
     */
    public Grid getBattleShipGrid() {
        return battleShipGrid;
    }

}
