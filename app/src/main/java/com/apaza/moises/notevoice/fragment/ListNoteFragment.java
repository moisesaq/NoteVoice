package com.apaza.moises.notevoice.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.global.Utils;
import com.apaza.moises.notevoice.model.Item;
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.view.PinnedSectionListView;
import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.database.NoteDao;
import com.apaza.moises.notevoice.global.Global;

import java.util.List;

public class ListNoteFragment extends Fragment implements View.OnClickListener, SimpleAdapter.OnItemOptionClickListener,
        View.OnTouchListener{

    private EditText textNote;
    private ImageButton recorder;

    private View view;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    private OnListNoteFragmentListener mListener;

    private PinnedSectionListView listNote;
    private SimpleAdapter noteAdapter;
    private LinearLayout viewMessage;
    private ProgressBar loading;
    private TextView message;

    public int sectionPosition;
    public int listPosition;

    private String outputFilename;
    private Handler recordHandler = new Handler();
    private Runnable recordRunnable;

    ActionBar actionBar;

    public ListNoteFragment() {
        // Required empty public constructor
    }

    public static ListNoteFragment newInstance(String param1) {
        ListNoteFragment fragment = new ListNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_note, container, false);
        setupView();
        return view;
    }

    private void setupView(){
        textNote = (EditText)view.findViewById(R.id.textNote);
        recorder = (ImageButton)view.findViewById(R.id.recorder);
        recorder.setOnTouchListener(this);

        viewMessage = (LinearLayout)view.findViewById(R.id.viewMessage);
        loading = (ProgressBar)view.findViewById(R.id.loading);
        message = (TextView)view.findViewById(R.id.message);

        listNote = (PinnedSectionListView)view.findViewById(R.id.listNote);
        clearAdapter();
        initializeAdapter();
        loadNotes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recorder:
                saveNote();
                break;
        }
    }

    private void saveNote(){
        Note note = getNoteOfView();
        if(note != null){
            if(Global.getHandlerDB().getDaoSession().getNoteDao().insert(getNoteOfView()) > 0){
                noteAdapter.add(new Item(Item.NOTE, note));
                Global.showMessage("Note recorded");
            }
        }

    }

    private Note getNoteOfView(){
        String text = textNote.getText().toString();
        Note note = new Note();
        note.setCode(Utils.generateCodeUnique("note"));
        note.setText(text.isEmpty() ? "This is a note" : text);
        note.setPathAudio(Media.AUDIO_DESTINATION_DIRECTORY + outputFilename);//String.valueOf(R.raw.detective));
        note.setColor(String.valueOf(Utils.colorGenerator.getRandomColor()));
        note.setDateCreated(Utils.getCurrentDateString());
        return note;
    }


    private void initializeAdapter() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            listNote.setFastScrollAlwaysVisible(true);
        }*/
        noteAdapter = new SimpleAdapter(getActivity(), this);
        listNote.setAdapter(noteAdapter);
    }

    private void loadNotes(){
        viewMessage.setVisibility(View.VISIBLE);
        List<Note> list = Global.getHandlerDB().getDaoSession().getNoteDao().queryBuilder().list();
        if(list != null && list.size() > 0){
            viewMessage.setVisibility(View.INVISIBLE);
            listNote.setVisibility(View.VISIBLE);

            Item section = new Item(Item.SECTION, "Test");
            section.sectionPosition = sectionPosition;
            section.listPosition = listPosition++;
            noteAdapter.onSectionAdded(section, sectionPosition);
            noteAdapter.add(section);
            for (Note note: list){
                Item item = new Item(Item.NOTE, note);
                item.sectionPosition = sectionPosition;
                item.listPosition = listPosition++;
                noteAdapter.add(item);
            }
            sectionPosition++;
        }else{
            listNote.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.GONE);
            message.setText("No found data");
        }
    }

    public void clearAdapter(){
        if(noteAdapter != null) noteAdapter.clear();
        sectionPosition = 0;
        listPosition = 0;
    }

    @Override
    public void onDeleteClick(final Item item) {
        Global.showDialogConfirmation(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Global.showMessage("Delete " + item.note.getText());
                Global.getHandlerDB().getDaoSession().getNoteDao().delete(item.note);
                noteAdapter.remove(item);
                noteAdapter.notifyDataSetChanged();
                Global.getMedia().eraseAudioFromDisk(item.note.getPathAudio());
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onEditClick(Item item) {
        //Global.showMessage("Edit " + item.note.getText());
        mListener.onEditNoteClick(item.note);
    }

    private void addNoteToAdapter(Note note){
        Item section = new Item(Item.SECTION, "Test");
        section.sectionPosition = sectionPosition;
        section.listPosition = listPosition++;
        noteAdapter.onSectionAdded(section, sectionPosition);
        noteAdapter.add(section);

        Item item = new Item(Item.NOTE, note);
        item.sectionPosition = sectionPosition;
        item.listPosition = listPosition++;
        noteAdapter.add(item);

        sectionPosition++;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startAnimationRecord();
                startRecording();
                break;

            case MotionEvent.ACTION_UP:
                endAnimationRecord();
                Handler recordMissingEndingHandler = new Handler();
                recordMissingEndingHandler.postDelayed(new Runnable() {
                    public void run() {
                        stopRecording();
                    }
                }, Media.RECORD_MISSING_ENDING_FIX_INTERVAL);
                break;

            case MotionEvent.ACTION_CANCEL:
                endAnimationRecord();
                break;
        }
        return false;
    }
    @Override
    public void onResume(){
        super.onResume();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(false);
    }

    public void startAnimationRecord() {
        recorder.setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent));
        /*Animation animRecord = AnimationUtils.loadAnimation(getActivity(), R.anim.begin_record);
        animRecord.setFillAfter(true);
        containerButtons.startAnimation(animRecord);*/
    }

    public void endAnimationRecord() {
        recorder.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        /*Animation animEnd = AnimationUtils.loadAnimation(getActivity(), R.anim.end_record);
        animEnd.setFillAfter(true);
        containerButtons.startAnimation(animEnd);*/
    }

    protected void startRecording() {
        Global.getMedia().startAudioPlaying(R.raw.prip, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                outputFilename = Media.generateOutputFilename();
                Log.d("FILE NAME ", " FIRST >>> " + Media.AUDIO_DESTINATION_DIRECTORY + outputFilename);
                Global.getMedia().startAudioRecording(Media.AUDIO_DESTINATION_DIRECTORY + outputFilename);
                startMaxLengthTimer();
            }
        });
    }

    protected void startMaxLengthTimer() {
        stopMaxLengthTimer();
        recordHandler.postDelayed(
                recordRunnable = new Runnable() {
                    public void run() {
                        stopRecording();
                    }
                }, Media.RECORD_MAX_LENGTH_INTERVAL);
    }

    protected void stopMaxLengthTimer() {
        recordHandler.removeCallbacks(recordRunnable);
    }

    protected void stopRecording() {
        // Button released before start recording?
        if (Global.getMedia().isRecording()) {
            //setRecordBackground(R.drawable.radio_sending);
            stopMaxLengthTimer();
            Global.getMedia().stopAudioRecording();

            // Recorded more than a half second?
            if (Global.getMedia().getAudioLength(Media.AUDIO_DESTINATION_DIRECTORY + outputFilename) > 500) {
                if (!outputFilename.equals("")){
                    //Log.d("FILE NAME ", " >>>>>>>>>>> EMPTY");
                    saveNote();
                }else{
                    Log.d("ERROR FILE NAME ", Media.AUDIO_DESTINATION_DIRECTORY + " >>> " + outputFilename);
                }
                //setRecordBackground(R.drawable.radio_success);
            } else {
                //setRecordBackground(R.drawable.radio_error);
            }
        }
    }

    /*protected void messageRecording() {
        Message message = new Message();
        message
                .setTypeMessage(Utils.TYPE_MESSAGE_AUDIO)
                .setFileName(Global.AUDIO_DESTINATION_DIRECTORY + outputFilename)
                .setCreatedAt(Utils.getCurrentTime(Utils.getCurrentDate()))
                .setSender(Utils.getSenderUser());
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scrollDown();
        hideNote();
        Global.getModernWsClient().sendAudio(receiverUser, Global.AUDIO_DESTINATION_DIRECTORY + outputFilename);
    }*/

    public void onButtonPressed(Note note) {
        if (mListener != null) {
            mListener.onEditNoteClick(note);
        }
    }

    public void generateListTest(int countItem, String titleHeader){
        Item section = new Item(Item.SECTION, titleHeader);
        section.sectionPosition = sectionPosition;
        section.listPosition = listPosition++;
        noteAdapter.onSectionAdded(section, sectionPosition);
        noteAdapter.add(section);
        for(int i = 1; i <= countItem; i++){
            Item item = new Item(Item.NOTE, new Note());
            item.sectionPosition = sectionPosition;
            item.listPosition = listPosition++;

            noteAdapter.add(item);
        }
        sectionPosition++;
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnListNoteFragmentListener) {
            mListener = (OnListNoteFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDetailNoteListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListNoteFragmentListener {
        void onEditNoteClick(Note note);
    }
}
