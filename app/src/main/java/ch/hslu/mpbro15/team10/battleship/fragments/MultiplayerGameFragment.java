package ch.hslu.mpbro15.team10.battleship.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;

import ch.hslu.mpbro15.team10.battleship.R;
import ch.hslu.mpbro15.team10.battleship.activities.MessageListener;
import ch.hslu.mpbro15.team10.battleship.activities.MultiplayerActivity;
import ch.hslu.mpbro15.team10.battleship.model.BattleshipGameObject;
import ch.hslu.mpbro15.team10.battleship.utility.ByteTransferObjectCoder;
import ch.hslu.mpbro15.team10.battleship.utility.TransferObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MultiplayerGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiplayerGameFragment extends Fragment implements MessageListener {
    private OnFragmentInteractionListener mListener;
    private MultiplayerActivity mActivity = (MultiplayerActivity) getActivity();
    private boolean myTurn;
    private View previouslyClickedView=null;

private View mView;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameFragment.
     */
    public static MultiplayerGameFragment newInstance() {
        MultiplayerGameFragment fragment = new MultiplayerGameFragment();
        return fragment;
    }

    public MultiplayerGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MultiplayerActivity) getActivity();
        mActivity.playMsgManager.addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        Button shoot = (Button) view.findViewById(R.id.btnShoot);
        final TextView gameStatus = (TextView)view.findViewById(R.id.gameStatus);
        if(mActivity.myTurn)
        {
            gameStatus.setText(getString(R.string.turnYou));
        }
        else
        {
            gameStatus.setText(getString(R.string.turnEnemy));
        }
        shoot.setEnabled(mActivity.myTurn);
        shoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTurn = false;
                Button shoot = (Button) mView.findViewById(R.id.btnShoot);
                shoot.setEnabled(myTurn);
                gameStatus.setText(getString(R.string.turnEnemy));
                TransferObject transferObject = new TransferObject("Shoot",((BattleshipGameObject)previouslyClickedView.getTag()).getCoordinates());
                Games.RealTimeMultiplayer.sendReliableMessage(mActivity.playConManager.client,null,ByteTransferObjectCoder.encodeTransferObject(transferObject),mActivity.getCurrentRoomId(),mActivity.getEnemy().getParticipantId());
            }
        });
        mView = view;
        setupGame(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMessageRecieved(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();

        TransferObject transferObject = ByteTransferObjectCoder.decodeTransferObject(buf);

        if(transferObject.getType().equals("Miss"))
        {
            View oponentGrid = mView.findViewById(R.id.gridOpponent);
            int idResource = oponentGrid.getResources().getIdentifier("grid" + transferObject.getMessage(), "id", "ch.hslu.mpbro15.team10.battleship");
            TextView textView = (TextView)oponentGrid.findViewById(idResource);
            BattleshipGameObject bsGo = (BattleshipGameObject)textView.getTag();
            bsGo.shot();
            textView.setBackground(bsGo.getBackground(oponentGrid));
        }

        if(transferObject.getType().equals("Hit"))
        {
            View oponentGrid = mView.findViewById(R.id.gridOpponent);
            int idResource = oponentGrid.getResources().getIdentifier("grid" + transferObject.getMessage(), "id", "ch.hslu.mpbro15.team10.battleship");
            TextView textView = (TextView)oponentGrid.findViewById(idResource);
            BattleshipGameObject bsGo = (BattleshipGameObject)textView.getTag();
            bsGo.hit();
            textView.setBackground(bsGo.getBackground(oponentGrid));
        }
        if(transferObject.getType().equals("Shoot"))
        {
            View myGrid = mView.findViewById(R.id.gridYou);
            int idResource = myGrid.getResources().getIdentifier("grid" + transferObject.getMessage(), "id", "ch.hslu.mpbro15.team10.battleship");
            TextView textView = (TextView)myGrid.findViewById(idResource);
            BattleshipGameObject bsGo = (BattleshipGameObject)textView.getTag();
            bsGo.shoot();
            textView.setBackground(bsGo.getBackground(myGrid));
                if(bsGo.isHit())
                {
                    TransferObject answerTransferObject = new TransferObject("Hit",transferObject.getMessage());
                    Games.RealTimeMultiplayer.sendReliableMessage(mActivity.playConManager.client,null,ByteTransferObjectCoder.encodeTransferObject(answerTransferObject),mActivity.getCurrentRoomId(),mActivity.getEnemy().getParticipantId());
                    if(mActivity.mMyGrid.isAllSunk())
                    {
                        TransferObject finalTransferObject = new TransferObject("YouWin","Congrats!");
                        Games.RealTimeMultiplayer.sendReliableMessage(mActivity.playConManager.client,null,ByteTransferObjectCoder.encodeTransferObject(answerTransferObject),mActivity.getCurrentRoomId(),mActivity.getEnemy().getParticipantId());
                        TextView gameStatus = (TextView)mView.findViewById(R.id.gameStatus);

                        gameStatus.setText(getString(R.string.YouLose));
                    }
                }
            else
                {
                    TransferObject answerTransferObject = new TransferObject("Miss",transferObject.getMessage());
                    Games.RealTimeMultiplayer.sendReliableMessage(mActivity.playConManager.client,null,ByteTransferObjectCoder.encodeTransferObject(answerTransferObject),mActivity.getCurrentRoomId(),mActivity.getEnemy().getParticipantId());
                }
            myTurn = true;
            TextView gameStatus = (TextView)mView.findViewById(R.id.gameStatus);

            gameStatus.setText(getString(R.string.turnYou));
            Button shoot = (Button) mView.findViewById(R.id.btnShoot);
            shoot.setEnabled(myTurn);
        }
        if(transferObject.getType().equals("YouWin"))
        {
             TextView gameStatus = (TextView)mView.findViewById(R.id.gameStatus);

                gameStatus.setText(getString(R.string.YouWon));

        }
    }

    private void setupGame(View view)
    {
        View myGrid = view.findViewById(R.id.gridYou);
        myGrid.findViewById(R.id.grid00).setTag(mActivity.mMyGrid.getGrid()[0][0]);
        myGrid.findViewById(R.id.grid01).setTag(mActivity.mMyGrid.getGrid()[0][1]);
        myGrid.findViewById(R.id.grid02).setTag(mActivity.mMyGrid.getGrid()[0][2]);
        myGrid.findViewById(R.id.grid03).setTag(mActivity.mMyGrid.getGrid()[0][3]);
        myGrid.findViewById(R.id.grid04).setTag(mActivity.mMyGrid.getGrid()[0][4]);
        myGrid.findViewById(R.id.grid05).setTag(mActivity.mMyGrid.getGrid()[0][5]);
        myGrid.findViewById(R.id.grid06).setTag(mActivity.mMyGrid.getGrid()[0][6]);
        myGrid.findViewById(R.id.grid07).setTag(mActivity.mMyGrid.getGrid()[0][7]);
        myGrid.findViewById(R.id.grid08).setTag(mActivity.mMyGrid.getGrid()[0][8]);
        myGrid.findViewById(R.id.grid09).setTag(mActivity.mMyGrid.getGrid()[0][9]);
        myGrid.findViewById(R.id.grid10).setTag(mActivity.mMyGrid.getGrid()[1][0]);
        myGrid.findViewById(R.id.grid11).setTag(mActivity.mMyGrid.getGrid()[1][1]);
        myGrid.findViewById(R.id.grid12).setTag(mActivity.mMyGrid.getGrid()[1][2]);
        myGrid.findViewById(R.id.grid13).setTag(mActivity.mMyGrid.getGrid()[1][3]);
        myGrid.findViewById(R.id.grid14).setTag(mActivity.mMyGrid.getGrid()[1][4]);
        myGrid.findViewById(R.id.grid15).setTag(mActivity.mMyGrid.getGrid()[1][5]);
        myGrid.findViewById(R.id.grid16).setTag(mActivity.mMyGrid.getGrid()[1][6]);
        myGrid.findViewById(R.id.grid17).setTag(mActivity.mMyGrid.getGrid()[1][7]);
        myGrid.findViewById(R.id.grid18).setTag(mActivity.mMyGrid.getGrid()[1][8]);
        myGrid.findViewById(R.id.grid19).setTag(mActivity.mMyGrid.getGrid()[1][9]);
        myGrid.findViewById(R.id.grid20).setTag(mActivity.mMyGrid.getGrid()[2][0]);
        myGrid.findViewById(R.id.grid21).setTag(mActivity.mMyGrid.getGrid()[2][1]);
        myGrid.findViewById(R.id.grid22).setTag(mActivity.mMyGrid.getGrid()[2][2]);
        myGrid.findViewById(R.id.grid23).setTag(mActivity.mMyGrid.getGrid()[2][3]);
        myGrid.findViewById(R.id.grid24).setTag(mActivity.mMyGrid.getGrid()[2][4]);
        myGrid.findViewById(R.id.grid25).setTag(mActivity.mMyGrid.getGrid()[2][5]);
        myGrid.findViewById(R.id.grid26).setTag(mActivity.mMyGrid.getGrid()[2][6]);
        myGrid.findViewById(R.id.grid27).setTag(mActivity.mMyGrid.getGrid()[2][7]);
        myGrid.findViewById(R.id.grid28).setTag(mActivity.mMyGrid.getGrid()[2][8]);
        myGrid.findViewById(R.id.grid29).setTag(mActivity.mMyGrid.getGrid()[2][9]);
        myGrid.findViewById(R.id.grid30).setTag(mActivity.mMyGrid.getGrid()[3][0]);
        myGrid.findViewById(R.id.grid31).setTag(mActivity.mMyGrid.getGrid()[3][1]);
        myGrid.findViewById(R.id.grid32).setTag(mActivity.mMyGrid.getGrid()[3][2]);
        myGrid.findViewById(R.id.grid33).setTag(mActivity.mMyGrid.getGrid()[3][3]);
        myGrid.findViewById(R.id.grid34).setTag(mActivity.mMyGrid.getGrid()[3][4]);
        myGrid.findViewById(R.id.grid35).setTag(mActivity.mMyGrid.getGrid()[3][5]);
        myGrid.findViewById(R.id.grid36).setTag(mActivity.mMyGrid.getGrid()[3][6]);
        myGrid.findViewById(R.id.grid37).setTag(mActivity.mMyGrid.getGrid()[3][7]);
        myGrid.findViewById(R.id.grid38).setTag(mActivity.mMyGrid.getGrid()[3][8]);
        myGrid.findViewById(R.id.grid39).setTag(mActivity.mMyGrid.getGrid()[3][9]);
        myGrid.findViewById(R.id.grid40).setTag(mActivity.mMyGrid.getGrid()[4][0]);
        myGrid.findViewById(R.id.grid41).setTag(mActivity.mMyGrid.getGrid()[4][1]);
        myGrid.findViewById(R.id.grid42).setTag(mActivity.mMyGrid.getGrid()[4][2]);
        myGrid.findViewById(R.id.grid43).setTag(mActivity.mMyGrid.getGrid()[4][3]);
        myGrid.findViewById(R.id.grid44).setTag(mActivity.mMyGrid.getGrid()[4][4]);
        myGrid.findViewById(R.id.grid45).setTag(mActivity.mMyGrid.getGrid()[4][5]);
        myGrid.findViewById(R.id.grid46).setTag(mActivity.mMyGrid.getGrid()[4][6]);
        myGrid.findViewById(R.id.grid47).setTag(mActivity.mMyGrid.getGrid()[4][7]);
        myGrid.findViewById(R.id.grid48).setTag(mActivity.mMyGrid.getGrid()[4][8]);
        myGrid.findViewById(R.id.grid49).setTag(mActivity.mMyGrid.getGrid()[4][9]);
        myGrid.findViewById(R.id.grid50).setTag(mActivity.mMyGrid.getGrid()[5][0]);
        myGrid.findViewById(R.id.grid51).setTag(mActivity.mMyGrid.getGrid()[5][1]);
        myGrid.findViewById(R.id.grid52).setTag(mActivity.mMyGrid.getGrid()[5][2]);
        myGrid.findViewById(R.id.grid53).setTag(mActivity.mMyGrid.getGrid()[5][3]);
        myGrid.findViewById(R.id.grid54).setTag(mActivity.mMyGrid.getGrid()[5][4]);
        myGrid.findViewById(R.id.grid55).setTag(mActivity.mMyGrid.getGrid()[5][5]);
        myGrid.findViewById(R.id.grid56).setTag(mActivity.mMyGrid.getGrid()[5][6]);
        myGrid.findViewById(R.id.grid57).setTag(mActivity.mMyGrid.getGrid()[5][7]);
        myGrid.findViewById(R.id.grid58).setTag(mActivity.mMyGrid.getGrid()[5][8]);
        myGrid.findViewById(R.id.grid59).setTag(mActivity.mMyGrid.getGrid()[5][9]);
        myGrid.findViewById(R.id.grid60).setTag(mActivity.mMyGrid.getGrid()[6][0]);
        myGrid.findViewById(R.id.grid61).setTag(mActivity.mMyGrid.getGrid()[6][1]);
        myGrid.findViewById(R.id.grid62).setTag(mActivity.mMyGrid.getGrid()[6][2]);
        myGrid.findViewById(R.id.grid63).setTag(mActivity.mMyGrid.getGrid()[6][3]);
        myGrid.findViewById(R.id.grid64).setTag(mActivity.mMyGrid.getGrid()[6][4]);
        myGrid.findViewById(R.id.grid65).setTag(mActivity.mMyGrid.getGrid()[6][5]);
        myGrid.findViewById(R.id.grid66).setTag(mActivity.mMyGrid.getGrid()[6][6]);
        myGrid.findViewById(R.id.grid67).setTag(mActivity.mMyGrid.getGrid()[6][7]);
        myGrid.findViewById(R.id.grid68).setTag(mActivity.mMyGrid.getGrid()[6][8]);
        myGrid.findViewById(R.id.grid69).setTag(mActivity.mMyGrid.getGrid()[6][9]);
        myGrid.findViewById(R.id.grid70).setTag(mActivity.mMyGrid.getGrid()[7][0]);
        myGrid.findViewById(R.id.grid71).setTag(mActivity.mMyGrid.getGrid()[7][1]);
        myGrid.findViewById(R.id.grid72).setTag(mActivity.mMyGrid.getGrid()[7][2]);
        myGrid.findViewById(R.id.grid73).setTag(mActivity.mMyGrid.getGrid()[7][3]);
        myGrid.findViewById(R.id.grid74).setTag(mActivity.mMyGrid.getGrid()[7][4]);
        myGrid.findViewById(R.id.grid75).setTag(mActivity.mMyGrid.getGrid()[7][5]);
        myGrid.findViewById(R.id.grid76).setTag(mActivity.mMyGrid.getGrid()[7][6]);
        myGrid.findViewById(R.id.grid77).setTag(mActivity.mMyGrid.getGrid()[7][7]);
        myGrid.findViewById(R.id.grid78).setTag(mActivity.mMyGrid.getGrid()[7][8]);
        myGrid.findViewById(R.id.grid79).setTag(mActivity.mMyGrid.getGrid()[7][9]);
        myGrid.findViewById(R.id.grid80).setTag(mActivity.mMyGrid.getGrid()[8][0]);
        myGrid.findViewById(R.id.grid81).setTag(mActivity.mMyGrid.getGrid()[8][1]);
        myGrid.findViewById(R.id.grid82).setTag(mActivity.mMyGrid.getGrid()[8][2]);
        myGrid.findViewById(R.id.grid83).setTag(mActivity.mMyGrid.getGrid()[8][3]);
        myGrid.findViewById(R.id.grid84).setTag(mActivity.mMyGrid.getGrid()[8][4]);
        myGrid.findViewById(R.id.grid85).setTag(mActivity.mMyGrid.getGrid()[8][5]);
        myGrid.findViewById(R.id.grid86).setTag(mActivity.mMyGrid.getGrid()[8][6]);
        myGrid.findViewById(R.id.grid87).setTag(mActivity.mMyGrid.getGrid()[8][7]);
        myGrid.findViewById(R.id.grid88).setTag(mActivity.mMyGrid.getGrid()[8][8]);
        myGrid.findViewById(R.id.grid89).setTag(mActivity.mMyGrid.getGrid()[8][9]);
        myGrid.findViewById(R.id.grid90).setTag(mActivity.mMyGrid.getGrid()[9][0]);
        myGrid.findViewById(R.id.grid91).setTag(mActivity.mMyGrid.getGrid()[9][1]);
        myGrid.findViewById(R.id.grid92).setTag(mActivity.mMyGrid.getGrid()[9][2]);
        myGrid.findViewById(R.id.grid93).setTag(mActivity.mMyGrid.getGrid()[9][3]);
        myGrid.findViewById(R.id.grid94).setTag(mActivity.mMyGrid.getGrid()[9][4]);
        myGrid.findViewById(R.id.grid95).setTag(mActivity.mMyGrid.getGrid()[9][5]);
        myGrid.findViewById(R.id.grid96).setTag(mActivity.mMyGrid.getGrid()[9][6]);
        myGrid.findViewById(R.id.grid97).setTag(mActivity.mMyGrid.getGrid()[9][7]);
        myGrid.findViewById(R.id.grid98).setTag(mActivity.mMyGrid.getGrid()[9][8]);
        myGrid.findViewById(R.id.grid99).setTag(mActivity.mMyGrid.getGrid()[9][9]);



        myGrid.findViewById(R.id.grid00).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid00).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid01).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid01).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid02).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid02).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid03).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid03).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid04).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid04).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid05).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid05).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid06).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid06).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid07).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid07).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid08).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid08).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid09).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid09).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid10).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid10).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid11).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid11).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid12).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid12).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid13).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid13).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid14).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid14).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid15).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid15).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid16).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid16).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid17).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid17).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid18).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid18).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid19).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid19).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid20).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid20).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid21).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid21).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid22).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid22).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid23).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid23).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid24).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid24).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid25).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid25).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid26).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid26).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid27).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid27).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid28).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid28).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid29).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid29).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid30).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid30).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid31).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid31).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid32).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid32).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid33).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid33).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid34).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid34).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid35).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid35).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid36).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid36).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid37).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid37).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid38).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid38).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid39).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid39).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid40).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid40).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid41).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid41).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid42).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid42).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid43).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid43).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid44).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid44).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid45).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid45).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid46).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid46).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid47).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid47).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid48).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid48).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid49).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid49).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid50).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid50).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid51).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid51).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid52).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid52).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid53).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid53).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid54).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid54).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid55).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid55).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid56).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid56).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid57).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid57).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid58).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid58).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid59).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid59).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid60).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid60).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid61).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid61).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid62).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid62).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid63).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid63).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid64).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid64).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid65).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid65).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid66).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid66).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid67).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid67).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid68).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid68).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid69).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid69).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid70).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid70).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid71).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid71).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid72).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid72).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid73).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid73).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid74).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid74).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid75).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid75).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid76).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid76).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid77).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid77).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid78).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid78).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid79).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid79).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid80).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid80).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid81).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid81).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid82).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid82).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid83).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid83).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid84).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid84).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid85).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid85).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid86).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid86).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid87).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid87).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid88).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid88).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid89).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid89).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid90).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid90).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid91).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid91).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid92).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid92).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid93).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid93).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid94).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid94).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid95).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid95).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid96).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid96).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid97).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid97).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid98).setBackground(((BattleshipGameObject)myGrid.findViewById(R.id.grid98).getTag()).getBackground(myGrid));
        myGrid.findViewById(R.id.grid99).setBackground(((BattleshipGameObject) myGrid.findViewById(R.id.grid99).getTag()).getBackground(myGrid));





        View enemyGrid = view.findViewById(R.id.gridOpponent);
        enemyGrid.findViewById(R.id.grid00).setTag(mActivity.mEnemyGrid.getGrid()[0][0]);
        enemyGrid.findViewById(R.id.grid01).setTag(mActivity.mEnemyGrid.getGrid()[0][1]);
        enemyGrid.findViewById(R.id.grid02).setTag(mActivity.mEnemyGrid.getGrid()[0][2]);
        enemyGrid.findViewById(R.id.grid03).setTag(mActivity.mEnemyGrid.getGrid()[0][3]);
        enemyGrid.findViewById(R.id.grid04).setTag(mActivity.mEnemyGrid.getGrid()[0][4]);
        enemyGrid.findViewById(R.id.grid05).setTag(mActivity.mEnemyGrid.getGrid()[0][5]);
        enemyGrid.findViewById(R.id.grid06).setTag(mActivity.mEnemyGrid.getGrid()[0][6]);
        enemyGrid.findViewById(R.id.grid07).setTag(mActivity.mEnemyGrid.getGrid()[0][7]);
        enemyGrid.findViewById(R.id.grid08).setTag(mActivity.mEnemyGrid.getGrid()[0][8]);
        enemyGrid.findViewById(R.id.grid09).setTag(mActivity.mEnemyGrid.getGrid()[0][9]);
        enemyGrid.findViewById(R.id.grid10).setTag(mActivity.mEnemyGrid.getGrid()[1][0]);
        enemyGrid.findViewById(R.id.grid11).setTag(mActivity.mEnemyGrid.getGrid()[1][1]);
        enemyGrid.findViewById(R.id.grid12).setTag(mActivity.mEnemyGrid.getGrid()[1][2]);
        enemyGrid.findViewById(R.id.grid13).setTag(mActivity.mEnemyGrid.getGrid()[1][3]);
        enemyGrid.findViewById(R.id.grid14).setTag(mActivity.mEnemyGrid.getGrid()[1][4]);
        enemyGrid.findViewById(R.id.grid15).setTag(mActivity.mEnemyGrid.getGrid()[1][5]);
        enemyGrid.findViewById(R.id.grid16).setTag(mActivity.mEnemyGrid.getGrid()[1][6]);
        enemyGrid.findViewById(R.id.grid17).setTag(mActivity.mEnemyGrid.getGrid()[1][7]);
        enemyGrid.findViewById(R.id.grid18).setTag(mActivity.mEnemyGrid.getGrid()[1][8]);
        enemyGrid.findViewById(R.id.grid19).setTag(mActivity.mEnemyGrid.getGrid()[1][9]);
        enemyGrid.findViewById(R.id.grid20).setTag(mActivity.mEnemyGrid.getGrid()[2][0]);
        enemyGrid.findViewById(R.id.grid21).setTag(mActivity.mEnemyGrid.getGrid()[2][1]);
        enemyGrid.findViewById(R.id.grid22).setTag(mActivity.mEnemyGrid.getGrid()[2][2]);
        enemyGrid.findViewById(R.id.grid23).setTag(mActivity.mEnemyGrid.getGrid()[2][3]);
        enemyGrid.findViewById(R.id.grid24).setTag(mActivity.mEnemyGrid.getGrid()[2][4]);
        enemyGrid.findViewById(R.id.grid25).setTag(mActivity.mEnemyGrid.getGrid()[2][5]);
        enemyGrid.findViewById(R.id.grid26).setTag(mActivity.mEnemyGrid.getGrid()[2][6]);
        enemyGrid.findViewById(R.id.grid27).setTag(mActivity.mEnemyGrid.getGrid()[2][7]);
        enemyGrid.findViewById(R.id.grid28).setTag(mActivity.mEnemyGrid.getGrid()[2][8]);
        enemyGrid.findViewById(R.id.grid29).setTag(mActivity.mEnemyGrid.getGrid()[2][9]);
        enemyGrid.findViewById(R.id.grid30).setTag(mActivity.mEnemyGrid.getGrid()[3][0]);
        enemyGrid.findViewById(R.id.grid31).setTag(mActivity.mEnemyGrid.getGrid()[3][1]);
        enemyGrid.findViewById(R.id.grid32).setTag(mActivity.mEnemyGrid.getGrid()[3][2]);
        enemyGrid.findViewById(R.id.grid33).setTag(mActivity.mEnemyGrid.getGrid()[3][3]);
        enemyGrid.findViewById(R.id.grid34).setTag(mActivity.mEnemyGrid.getGrid()[3][4]);
        enemyGrid.findViewById(R.id.grid35).setTag(mActivity.mEnemyGrid.getGrid()[3][5]);
        enemyGrid.findViewById(R.id.grid36).setTag(mActivity.mEnemyGrid.getGrid()[3][6]);
        enemyGrid.findViewById(R.id.grid37).setTag(mActivity.mEnemyGrid.getGrid()[3][7]);
        enemyGrid.findViewById(R.id.grid38).setTag(mActivity.mEnemyGrid.getGrid()[3][8]);
        enemyGrid.findViewById(R.id.grid39).setTag(mActivity.mEnemyGrid.getGrid()[3][9]);
        enemyGrid.findViewById(R.id.grid40).setTag(mActivity.mEnemyGrid.getGrid()[4][0]);
        enemyGrid.findViewById(R.id.grid41).setTag(mActivity.mEnemyGrid.getGrid()[4][1]);
        enemyGrid.findViewById(R.id.grid42).setTag(mActivity.mEnemyGrid.getGrid()[4][2]);
        enemyGrid.findViewById(R.id.grid43).setTag(mActivity.mEnemyGrid.getGrid()[4][3]);
        enemyGrid.findViewById(R.id.grid44).setTag(mActivity.mEnemyGrid.getGrid()[4][4]);
        enemyGrid.findViewById(R.id.grid45).setTag(mActivity.mEnemyGrid.getGrid()[4][5]);
        enemyGrid.findViewById(R.id.grid46).setTag(mActivity.mEnemyGrid.getGrid()[4][6]);
        enemyGrid.findViewById(R.id.grid47).setTag(mActivity.mEnemyGrid.getGrid()[4][7]);
        enemyGrid.findViewById(R.id.grid48).setTag(mActivity.mEnemyGrid.getGrid()[4][8]);
        enemyGrid.findViewById(R.id.grid49).setTag(mActivity.mEnemyGrid.getGrid()[4][9]);
        enemyGrid.findViewById(R.id.grid50).setTag(mActivity.mEnemyGrid.getGrid()[5][0]);
        enemyGrid.findViewById(R.id.grid51).setTag(mActivity.mEnemyGrid.getGrid()[5][1]);
        enemyGrid.findViewById(R.id.grid52).setTag(mActivity.mEnemyGrid.getGrid()[5][2]);
        enemyGrid.findViewById(R.id.grid53).setTag(mActivity.mEnemyGrid.getGrid()[5][3]);
        enemyGrid.findViewById(R.id.grid54).setTag(mActivity.mEnemyGrid.getGrid()[5][4]);
        enemyGrid.findViewById(R.id.grid55).setTag(mActivity.mEnemyGrid.getGrid()[5][5]);
        enemyGrid.findViewById(R.id.grid56).setTag(mActivity.mEnemyGrid.getGrid()[5][6]);
        enemyGrid.findViewById(R.id.grid57).setTag(mActivity.mEnemyGrid.getGrid()[5][7]);
        enemyGrid.findViewById(R.id.grid58).setTag(mActivity.mEnemyGrid.getGrid()[5][8]);
        enemyGrid.findViewById(R.id.grid59).setTag(mActivity.mEnemyGrid.getGrid()[5][9]);
        enemyGrid.findViewById(R.id.grid60).setTag(mActivity.mEnemyGrid.getGrid()[6][0]);
        enemyGrid.findViewById(R.id.grid61).setTag(mActivity.mEnemyGrid.getGrid()[6][1]);
        enemyGrid.findViewById(R.id.grid62).setTag(mActivity.mEnemyGrid.getGrid()[6][2]);
        enemyGrid.findViewById(R.id.grid63).setTag(mActivity.mEnemyGrid.getGrid()[6][3]);
        enemyGrid.findViewById(R.id.grid64).setTag(mActivity.mEnemyGrid.getGrid()[6][4]);
        enemyGrid.findViewById(R.id.grid65).setTag(mActivity.mEnemyGrid.getGrid()[6][5]);
        enemyGrid.findViewById(R.id.grid66).setTag(mActivity.mEnemyGrid.getGrid()[6][6]);
        enemyGrid.findViewById(R.id.grid67).setTag(mActivity.mEnemyGrid.getGrid()[6][7]);
        enemyGrid.findViewById(R.id.grid68).setTag(mActivity.mEnemyGrid.getGrid()[6][8]);
        enemyGrid.findViewById(R.id.grid69).setTag(mActivity.mEnemyGrid.getGrid()[6][9]);
        enemyGrid.findViewById(R.id.grid70).setTag(mActivity.mEnemyGrid.getGrid()[7][0]);
        enemyGrid.findViewById(R.id.grid71).setTag(mActivity.mEnemyGrid.getGrid()[7][1]);
        enemyGrid.findViewById(R.id.grid72).setTag(mActivity.mEnemyGrid.getGrid()[7][2]);
        enemyGrid.findViewById(R.id.grid73).setTag(mActivity.mEnemyGrid.getGrid()[7][3]);
        enemyGrid.findViewById(R.id.grid74).setTag(mActivity.mEnemyGrid.getGrid()[7][4]);
        enemyGrid.findViewById(R.id.grid75).setTag(mActivity.mEnemyGrid.getGrid()[7][5]);
        enemyGrid.findViewById(R.id.grid76).setTag(mActivity.mEnemyGrid.getGrid()[7][6]);
        enemyGrid.findViewById(R.id.grid77).setTag(mActivity.mEnemyGrid.getGrid()[7][7]);
        enemyGrid.findViewById(R.id.grid78).setTag(mActivity.mEnemyGrid.getGrid()[7][8]);
        enemyGrid.findViewById(R.id.grid79).setTag(mActivity.mEnemyGrid.getGrid()[7][9]);
        enemyGrid.findViewById(R.id.grid80).setTag(mActivity.mEnemyGrid.getGrid()[8][0]);
        enemyGrid.findViewById(R.id.grid81).setTag(mActivity.mEnemyGrid.getGrid()[8][1]);
        enemyGrid.findViewById(R.id.grid82).setTag(mActivity.mEnemyGrid.getGrid()[8][2]);
        enemyGrid.findViewById(R.id.grid83).setTag(mActivity.mEnemyGrid.getGrid()[8][3]);
        enemyGrid.findViewById(R.id.grid84).setTag(mActivity.mEnemyGrid.getGrid()[8][4]);
        enemyGrid.findViewById(R.id.grid85).setTag(mActivity.mEnemyGrid.getGrid()[8][5]);
        enemyGrid.findViewById(R.id.grid86).setTag(mActivity.mEnemyGrid.getGrid()[8][6]);
        enemyGrid.findViewById(R.id.grid87).setTag(mActivity.mEnemyGrid.getGrid()[8][7]);
        enemyGrid.findViewById(R.id.grid88).setTag(mActivity.mEnemyGrid.getGrid()[8][8]);
        enemyGrid.findViewById(R.id.grid89).setTag(mActivity.mEnemyGrid.getGrid()[8][9]);
        enemyGrid.findViewById(R.id.grid90).setTag(mActivity.mEnemyGrid.getGrid()[9][0]);
        enemyGrid.findViewById(R.id.grid91).setTag(mActivity.mEnemyGrid.getGrid()[9][1]);
        enemyGrid.findViewById(R.id.grid92).setTag(mActivity.mEnemyGrid.getGrid()[9][2]);
        enemyGrid.findViewById(R.id.grid93).setTag(mActivity.mEnemyGrid.getGrid()[9][3]);
        enemyGrid.findViewById(R.id.grid94).setTag(mActivity.mEnemyGrid.getGrid()[9][4]);
        enemyGrid.findViewById(R.id.grid95).setTag(mActivity.mEnemyGrid.getGrid()[9][5]);
        enemyGrid.findViewById(R.id.grid96).setTag(mActivity.mEnemyGrid.getGrid()[9][6]);
        enemyGrid.findViewById(R.id.grid97).setTag(mActivity.mEnemyGrid.getGrid()[9][7]);
        enemyGrid.findViewById(R.id.grid98).setTag(mActivity.mEnemyGrid.getGrid()[9][8]);
        enemyGrid.findViewById(R.id.grid99).setTag(mActivity.mEnemyGrid.getGrid()[9][9]);



        enemyGrid.findViewById(R.id.grid00).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid00).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid01).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid01).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid02).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid02).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid03).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid03).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid04).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid04).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid05).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid05).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid06).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid06).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid07).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid07).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid08).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid08).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid09).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid09).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid10).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid10).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid11).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid11).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid12).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid12).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid13).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid13).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid14).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid14).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid15).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid15).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid16).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid16).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid17).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid17).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid18).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid18).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid19).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid19).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid20).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid20).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid21).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid21).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid22).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid22).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid23).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid23).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid24).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid24).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid25).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid25).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid26).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid26).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid27).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid27).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid28).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid28).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid29).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid29).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid30).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid30).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid31).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid31).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid32).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid32).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid33).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid33).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid34).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid34).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid35).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid35).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid36).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid36).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid37).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid37).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid38).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid38).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid39).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid39).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid40).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid40).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid41).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid41).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid42).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid42).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid43).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid43).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid44).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid44).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid45).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid45).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid46).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid46).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid47).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid47).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid48).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid48).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid49).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid49).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid50).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid50).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid51).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid51).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid52).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid52).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid53).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid53).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid54).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid54).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid55).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid55).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid56).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid56).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid57).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid57).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid58).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid58).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid59).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid59).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid60).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid60).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid61).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid61).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid62).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid62).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid63).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid63).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid64).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid64).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid65).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid65).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid66).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid66).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid67).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid67).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid68).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid68).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid69).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid69).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid70).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid70).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid71).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid71).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid72).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid72).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid73).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid73).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid74).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid74).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid75).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid75).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid76).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid76).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid77).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid77).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid78).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid78).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid79).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid79).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid80).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid80).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid81).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid81).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid82).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid82).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid83).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid83).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid84).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid84).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid85).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid85).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid86).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid86).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid87).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid87).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid88).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid88).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid89).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid89).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid90).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid90).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid91).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid91).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid92).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid92).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid93).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid93).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid94).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid94).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid95).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid95).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid96).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid96).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid97).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid97).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid98).setBackground(((BattleshipGameObject)enemyGrid.findViewById(R.id.grid98).getTag()).getBackground(enemyGrid));
        enemyGrid.findViewById(R.id.grid99).setBackground(((BattleshipGameObject) enemyGrid.findViewById(R.id.grid99).getTag()).getBackground(enemyGrid));



                enemyGrid.findViewById(R.id.grid00).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid01).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid02).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid03).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid04).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid05).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid06).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid07).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid08).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid09).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid10).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid11).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid12).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid13).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid14).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid15).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid16).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid17).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid18).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid19).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid20).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid21).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid22).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid23).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid24).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid25).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid26).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid27).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid28).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid29).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid30).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid31).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid32).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid33).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid34).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid35).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid36).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid37).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid38).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid39).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid40).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid41).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid42).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid43).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid44).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid45).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid46).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid47).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid48).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid49).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid50).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid51).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid52).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid53).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid54).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid55).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid56).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid57).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid58).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid59).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid60).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid61).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid62).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid63).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid64).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid65).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid66).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid67).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid68).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid69).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid70).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid71).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid72).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid73).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid74).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid75).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid76).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid77).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid78).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid79).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid80).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid81).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid82).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid83).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid84).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid85).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid86).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid87).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid88).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid89).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid90).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid91).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid92).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid93).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid94).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid95).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid96).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid97).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid98).setOnClickListener(new OnClickListener());
                enemyGrid.findViewById(R.id.grid99).setOnClickListener(new OnClickListener());

    }

    private class OnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            if(previouslyClickedView!=null)
                previouslyClickedView.setBackground(((BattleshipGameObject)previouslyClickedView.getTag()).getBackground(v));
            if(!((BattleshipGameObject)v.getTag()).isHit() || !((BattleshipGameObject)v.getTag()).isShot())
                previouslyClickedView=v;
            v.setBackground(v.getResources().getDrawable(R.drawable.selected));
        }
    }
}
