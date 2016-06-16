package com.apaza.moises.notevoice.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.view.PinnedSectionListView;

public class DetailNoteFragment extends Fragment {
    private View view;
    private ImageButton recorder;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private SeekBar seekBar;
    private int startTime = 0;
    private int finalTime = 0;
    private Handler handler = new Handler();
    public static int oneTimeOnly = 0;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DetailNoteFragment() {
        // Required empty public constructor
    }
    public static DetailNoteFragment newInstance(String param1, String param2) {
        DetailNoteFragment fragment = new DetailNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_note, container, false);
    }

    private void setupView(){
        recorder = (ImageButton)view.findViewById(R.id.recorder);
        //recorder.setOnClickListener(this);

        Button play = (Button) view.findViewById(R.id.play);
        //play.setOnClickListener(this);
        Button stop = (Button)view.findViewById(R.id.stop);
        //stop.setOnClickListener(this);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private Runnable UpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(startTime);
            handler.postDelayed(this, 100);
        }
    };

    public void startAudio(int recourseId, SeekBar seekBar, MediaPlayer.OnCompletionListener listener){
        try{
            if(mediaPlayer != null){
                if(mediaPlayer.isPlaying())
                    stopAudio();
            }
            this.seekBar = seekBar;
            mediaPlayer = MediaPlayer.create(Global.getContext(), recourseId);

            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            this.seekBar.setMax(finalTime);
            mediaPlayer.setOnCompletionListener(listener);
            mediaPlayer.start();

            seekBar.setProgress(startTime);
            handler.postDelayed(UpdateSeekBar, 100);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopAudio(){
        try{
            if(mediaPlayer != null){
                finalTime = 0;
                startTime = 0;
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                //new MediaPlayer.OnCompletionListener().onCompletion(mediaPlayer);
            }
            if(seekBar != null)
                seekBar.setProgress(0);
            handler.removeCallbacks(UpdateSeekBar);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
