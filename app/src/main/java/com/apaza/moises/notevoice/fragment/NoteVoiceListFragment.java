package com.apaza.moises.notevoice.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.adapter.NoteVoiceListAdapter;
import com.apaza.moises.notevoice.base.BaseFragment;
import com.apaza.moises.notevoice.database.Audio;
import com.apaza.moises.notevoice.database.DaoSession;
import com.apaza.moises.notevoice.database.Message;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.global.Utils;
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.view.RecordButton;

import java.util.List;

public class NoteVoiceListFragment extends BaseFragment implements RecordButton.OnRecordButtonListener, NoteVoiceListAdapter.OnNoteVoiceListAdapterListener{

    public static final String TAG = "NOTE_VOICE_LIST_FRAGMENT";

    private View view;
    private RecyclerView listNoteVoice;
    private NoteVoiceListAdapter adapter;
    private TextView textNote, message;
    private RecordButton recordAudio;
    private LinearLayout viewMessage;
    private ProgressBar loading;

    private ActionBar actionBar;

    private String outputFilename;
    private Handler recordHandler = new Handler();
    private Runnable recordRunnable;

    private OnNoteVoiceListFragmentListener listener;

    public NoteVoiceListFragment(){

    }

    public static NoteVoiceListFragment newInstance(){
        return new NoteVoiceListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_note_voice_list, container, false);
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

        listNoteVoice = (RecyclerView)view.findViewById(R.id.listNoteVoice);
        loadNotes();
    }

    private void loadNotes(){
        viewMessage.setVisibility(View.VISIBLE);
        List<Note> list = Global.getHandlerDB().getDaoSession().getNoteDao().queryBuilder().list();
        if(list != null && list.size() > 0){
            viewMessage.setVisibility(View.INVISIBLE);
            listNoteVoice.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            listNoteVoice.setLayoutManager(linearLayoutManager);
            adapter = new NoteVoiceListAdapter(list);
            adapter.setOnNoteVoiceListAdapterListener(this);
            listNoteVoice.setAdapter(adapter);
        }else{
            listNoteVoice.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.GONE);
            message.setText("No found data");
        }
    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recordAudio:
                //saveNote();
                break;
        }
    }*/

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

    @Override
    public void onDeleteClick(final Note note) {
        Global.showDialogConfirmation(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Global.showMessage("Delete " + note.getNoteMessage().get(0).getTextMessage());
                Global.getHandlerDB().getDaoSession().getNoteDao().delete(note);
                /*noteAdapter.remove(item);
                noteAdapter.notifyDataSetChanged();*/
                Global.getMedia().eraseAudioFromDisk(note.getNoteAudio().get(0).getRoute());
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onEditClick(Note note) {
        listener.onEditNoteVoiceClick(note);
    }

    public interface OnNoteVoiceListFragmentListener {
        void onEditNoteVoiceClick(Note note);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnNoteVoiceListFragmentListener) {
            listener = (OnNoteVoiceListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNoteVoiceListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
