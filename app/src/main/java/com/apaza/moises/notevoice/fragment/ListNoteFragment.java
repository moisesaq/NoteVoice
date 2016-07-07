package com.apaza.moises.notevoice.fragment;
import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.database.Audio;
import com.apaza.moises.notevoice.database.DaoSession;
import com.apaza.moises.notevoice.database.Message;
import com.apaza.moises.notevoice.global.Utils;
import com.apaza.moises.notevoice.model.Item;
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.view.PinnedSectionListView;
import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.view.RecordButton;

import java.util.List;

public class ListNoteFragment extends Fragment implements View.OnClickListener, SimpleAdapter.OnItemOptionClickListener, RecordButton.OnRecordButtonListener{

    private EditText textNote;
    private RecordButton recordAudio;

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
        recordAudio = (RecordButton) view.findViewById(R.id.recordAudio);
        recordAudio.setOnRecordButtonListener(this);

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
            case R.id.recordAudio:
                saveNote();
                break;
        }
    }

    private void saveNote(){
        Note note = getNoteOfView();
        if(note != null){
            DaoSession daoSession = Global.getHandlerDB().getDaoSession();
            long idNote = daoSession.getNoteDao().insert(note);
            if(idNote > 0){
                Message message = getMessageOfView();
                message.setIdNote(idNote);
                daoSession.getMessageDao().insert(message);

                Audio audio = getAudioOfView();
                audio.setIdNote(idNote);
                daoSession.getAudioDao().insert(audio);

                noteAdapter.add(new Item(Item.NOTE, note));
                Global.showMessage("Note recorded");
            }
        }

    }

    private Note getNoteOfView(){
        Note note = new Note();
        note.setCode(Utils.generateCodeUnique("note"));

        note.setColor(String.valueOf(Utils.colorGenerator.getRandomColor()));
        note.setCreateAt(Utils.getCurrentDate());
        note.setUpdateAt(Utils.getCurrentDate());

        return note;
    }

    private Message getMessageOfView(){
        String text = textNote.getText().toString();
        Message message = new Message();
        message.setCode(Utils.generateCodeUnique("message"));
        message.setTextMessage(text.isEmpty() ? "This is a note" : text);
        message.setCreateAt(Utils.getCurrentDate());
        return message;
    }

    private Audio getAudioOfView(){
        Audio audio = new Audio();
        audio.setCode(Utils.generateCodeUnique("audio"));
        audio.setRoute(Media.AUDIO_DESTINATION_DIRECTORY + outputFilename);//String.valueOf(R.raw.detective));
        audio.setDuration(0);//Media.formatDuration(Media.getDurationAudioFile(audio.getRoute()));
        audio.setCreateAt(Utils.getCurrentDate());
        return audio;
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
                Global.showMessage("Delete " + item.note.getNoteMessage().get(0).getTextMessage());
                Global.getHandlerDB().getDaoSession().getNoteDao().delete(item.note);
                noteAdapter.remove(item);
                noteAdapter.notifyDataSetChanged();
                Global.getMedia().eraseAudioFromDisk(item.note.getNoteAudio().get(0).getRoute());
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
    public void onResume(){
        super.onResume();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(false);
    }

    protected void startRecording() {
        Global.getMedia().startAudioPlaying(R.raw.prip, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                outputFilename = Media.generateOutputFilename();
                Log.d("FILE NAME ", " FIRST >>> " + Media.AUDIO_DESTINATION_DIRECTORY + outputFilename);
                Global.getMedia().startAudioRecording(Media.AUDIO_DESTINATION_DIRECTORY + outputFilename);
                //startMaxLengthTimer();
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
        if (Global.getMedia().isRecording()) {
            //stopMaxLengthTimer();
            Global.getMedia().stopAudioRecording();

            // Recorded more than a half second?
            if (Global.getMedia().getAudioLength(Media.AUDIO_DESTINATION_DIRECTORY + outputFilename) > 500) {
                if (!outputFilename.equals("")){
                    //Log.d("FILE NAME ", " >>>>>>>>>>> EMPTY");
                    saveNote();
                }else{
                    Log.d("ERROR FILE NAME ", Media.AUDIO_DESTINATION_DIRECTORY + " >>> " + outputFilename);
                }
            } else {
                //Here
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

    @Override
    public void onStartRecord() {
        Global.showMessage("Start record");
    }

    @Override
    public void onEndRecord(long second) {
        Global.showMessage("End record");
    }

    @Override
    public void onCancelRecord() {
        Global.showMessage("Cancel record");
    }

    public interface OnListNoteFragmentListener {
        void onEditNoteClick(Note note);
    }
}
