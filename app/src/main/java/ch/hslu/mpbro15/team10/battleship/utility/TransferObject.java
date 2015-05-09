package ch.hslu.mpbro15.team10.battleship.utility;

import java.io.Serializable;

/**
 * Created by dave on 10.05.2015.
 */
public class TransferObject implements Serializable {
    private String mType;
    private String mMessage;

    public TransferObject(String type, String message)
    {
        this.mType = type;
        this.mMessage = message;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
