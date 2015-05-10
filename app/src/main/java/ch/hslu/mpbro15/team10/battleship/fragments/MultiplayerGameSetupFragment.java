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
import ch.hslu.mpbro15.team10.battleship.utility.ByteTransferObjectCoder;
import ch.hslu.mpbro15.team10.battleship.utility.TransferObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MultiplayerGameSetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiplayerGameSetupFragment extends Fragment implements MessageListener {
    private OnFragmentInteractionListener mListener;
    private MultiplayerActivity mActivity = (MultiplayerActivity) getActivity();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameSetupFragment.
     */
    public static MultiplayerGameSetupFragment newInstance() {
        MultiplayerGameSetupFragment fragment = new MultiplayerGameSetupFragment();
        return fragment;
    }

    public MultiplayerGameSetupFragment() {
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
        View view = inflater.inflate(R.layout.fragment_game_setup, container, false);
        Button ready = (Button) view.findViewById(R.id.btnReady);
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Games.RealTimeMultiplayer.sendReliableMessage(mActivity.playConManager.client,null, ByteTransferObjectCoder.encodeTransferObject(new TransferObject("Ready","TRUE")),mActivity.getCurrentRoomId(),mActivity.getEnemy().getParticipantId());
            }
        });
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

        if(transferObject.getType().equals("Ready"))
        {
            TextView oponentStatus = (TextView)getView().findViewById(R.id.txvOpponentGridStatus);
            oponentStatus.setText(getString(R.string.opponentStatusReady));
        }
    }
}
