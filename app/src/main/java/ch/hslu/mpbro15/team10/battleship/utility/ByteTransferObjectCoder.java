package ch.hslu.mpbro15.team10.battleship.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Created by dave on 10.05.2015.
 */
public class ByteTransferObjectCoder {

    public static byte[] encodeTransferObject(TransferObject transferObject)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes = null;
        try {
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(transferObject);
                yourBytes = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return yourBytes;
        }  finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public static TransferObject decodeTransferObject(byte[] bytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        Object o = null;
        try {
            try {
                in = new ObjectInputStream(bis);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                o = in.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return (TransferObject) o;
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }

    }
}
