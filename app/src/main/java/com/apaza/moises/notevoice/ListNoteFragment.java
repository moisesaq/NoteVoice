package com.apaza.moises.notevoice;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListNoteFragment extends Fragment implements View.OnClickListener{

    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;

    private ImageButton recorder;
    private SeekBar seekBar;
    private int startTime = 0;
    private int finalTime = 0;
    private Handler handler = new Handler();

    public static int oneTimeOnly = 0;

    private View view;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView list;
    private ListNoteAdapter adapter;

    public ListNoteFragment() {
        // Required empty public constructor
    }

    public static ListNoteFragment newInstance(String param1, String param2) {
        ListNoteFragment fragment = new ListNoteFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_note, container, false);
        setupView();
        return view;
    }

    private void setupView(){
        recorder = (ImageButton)view.findViewById(R.id.recorder);
        recorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        Button play = (Button) view.findViewById(R.id.play);
        play.setOnClickListener(this);
        Button stop = (Button)view.findViewById(R.id.stop);
        stop.setOnClickListener(this);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);

        list = (ListView)view.findViewById(R.id.listNoteVoice);
        adapter = new ListNoteAdapter(getActivity(), Note.getListTest());
        list.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                startAudio(R.raw.detective, seekBar, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        showMessage("Finished audio");
                    }
                });
                break;
            case R.id.stop:
                stopAudio();
                break;
        }
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
            mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), recourseId);

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
        void onFragmentInteraction(Uri uri);
    }

    public void showMessage(String message){
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    public class ListNoteAdapter extends ArrayAdapter<Note> {

        public ListNoteAdapter(Context context, List<Note> list) {
            super(context, R.layout.item_note_audio, list);
        }

        public class ViewHolder{
            ImageButton play, stop;
            SeekBar seekBarAudio;
            TextView message;
        }

        @Override
        public Note getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            if(view == null){
                view = getActivity().getLayoutInflater().inflate(R.layout.item_note_audio, null);
                holder = new ViewHolder();
                holder.play = (ImageButton)view.findViewById(R.id.play);
                holder.stop = (ImageButton)view.findViewById(R.id.stop);
                holder.seekBarAudio = (SeekBar)view.findViewById(R.id.seekBarAudio);
                holder.message = (TextView)view.findViewById(R.id.message);
                view.setTag(holder);
            }else{
                holder = (ViewHolder)view.getTag();
            }
            final Note note = getItem(position);
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAudio(note.getAudio(), holder.seekBarAudio, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopAudio();
                        }
                    });
                }
            });
            holder.stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.message.setText(note.getIdNote() + " - " + note.getMessage());
            return view;
        }

    }

}
