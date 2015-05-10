package ch.hslu.mpbro15.team10.battleship.data;

import java.util.Date;

/**
 * Created by Floopy-Doo on 10.05.2015.
 */
public class HighscoreObject {

    private long _id;
    private Date date;
    private int points;

    public HighscoreObject() {

    }

    public HighscoreObject(int id, Date date, int points) {
        this._id = id;
        this.date = date;
        this.points = points;
    }

    public HighscoreObject(Date date, int points) {
        this.date = date;
        this.points = points;
    }

    public void setID(long id) {
        this._id = id;
    }

    public long getID() {
        return this._id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return this.points;
    }
}
