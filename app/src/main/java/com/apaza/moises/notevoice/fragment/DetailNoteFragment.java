package com.apaza.moises.notevoice.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.adapter.ListAudioAdapter;
import com.apaza.moises.notevoice.adapter.ListTextAdapter;
import com.apaza.moises.notevoice.base.BaseFragment;
import com.apaza.moises.notevoice.database.Audio;
import com.apaza.moises.notevoice.database.Message;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.view.RecordButton;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

public class DetailNoteFragment extends BaseFragment implements View.OnClickListener, RecordButton.OnRecordButtonListener, NewTextNoteDialog.OnNewTextNoteListener{

    public static final String TAG = "DETAIL_NOTE_FRAGMENT";
    public static final String ID_NOTE = "idNote";

    private long idNote;
    private Note note;
    private View view;

    private ListView listText;
    private ListTextAdapter listTextAdapter;
    private TextView emptyText;

    private ListView listAudio;
    private ListAudioAdapter listAudioAdapter;
    private TextView emptyAudio;
    private RecordButton recordButton;

    private FloatingActionMenu fam;
    private FloatingActionButton fabActionNewText;
    private FloatingActionButton fabActionNewImage;

    private String outputFilename;

    public DetailNoteFragment(){

    }

    public static DetailNoteFragment newInstance(long idNote){
        DetailNoteFragment detailNoteFragment = new DetailNoteFragment();
        Bundle args = new Bundle();
        args.putLong(ID_NOTE, idNote);
        detailNoteFragment.setArguments(args);
        return detailNoteFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            idNote = getArguments().getLong(ID_NOTE);
            note = Global.getHandlerDB().getDaoSession().getNoteDao().load(idNote);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_note, container, false);
        setupView();
        return view;
    }

    private void setupView(){
        prepareListAudio();
        prepareListText();

        fam = (FloatingActionMenu)view.findViewById(R.id.actionMenu);
        fabActionNewText = (com.github.clans.fab.FloatingActionButton)view.findViewById(R.id.actionNewText);
        fabActionNewText.setOnClickListener(this);
        fabActionNewImage = (com.github.clans.fab.FloatingActionButton)view.findViewById(R.id.actionNewImage);
        fabActionNewImage.setOnClickListener(this);
    }

    private void prepareListAudio(){
        listAudio = (ListView)view.findViewById(R.id.listAudio);
        emptyAudio = (TextView) view.findViewById(R.id.emptyAudio);
        recordButton = (RecordButton) view.findViewById(R.id.recordAudio);
        recordButton.setOnRecordButtonListener(this);

        List<Audio> list = Global.getHandlerDB().getDaoSession().getAudioDao()._queryNote_NoteAudio(note.getId());
        if(list.size() > 0)
            hideEmptyAudio();
        listAudioAdapter = new ListAudioAdapter(getContext(), list);
        listAudio.setAdapter(listAudioAdapter);
    }

    private void showEmptyAudio(){
        emptyAudio.setVisibility(View.VISIBLE);
        listAudio.setVisibility(View.INVISIBLE);
    }

    private void hideEmptyAudio(){
        emptyAudio.setVisibility(View.INVISIBLE);
        listAudio.setVisibility(View.VISIBLE);
    }

    private void prepareListText(){
        listText = (ListView)view.findViewById(R.id.listText);
        emptyText = (TextView)view.findViewById(R.id.emptyText);

        List<Message> list = Global.getHandlerDB().getDaoSession().getMessageDao()._queryNote_NoteMessage(note.getId());
        Log.d(TAG, " Size" + list.size());
        if(list.size() > 0)
            hideEmptyText();

        listTextAdapter = new ListTextAdapter(getContext(), list);
        listText.setAdapter(listTextAdapter);
    }

    private void showEmptyText(){
        listText.setVisibility(View.GONE);
        emptyText.setVisibility(View.VISIBLE);
    }

    private void hideEmptyText(){
        listText.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_save).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_save:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.actionNewText:
                NewTextNoteDialog newTextNoteDialog = NewTextNoteDialog.newInstance(this);
                newTextNoteDialog.show(getFragmentManager(), NewTextNoteDialog.TAG);
                fam.close(true);
                break;

            case R.id.actionNewImage:
                Global.showMessage("Add new image");
                fam.close(true);
                break;
        }
    }

    /*RECORD BUTTON LISTENER*/
    @Override
    public void onStartRecord() {
        Global.showMessage("Start record");
        outputFilename = Media.AUDIO_DESTINATION_DIRECTORY + Media.generateOutputFilename();
        Global.getMedia().startAudioRecording( outputFilename);
    }

    @Override
    public void onEndRecord(long second) {
        if (Global.getMedia().isRecording()) {
            Global.getMedia().stopAudioRecording();

            if (!outputFilename.isEmpty()){
                Audio audio = Global.saveAudioNote(note, outputFilename);
                if(audio != null){
                    hideEmptyAudio();
                    Global.showMessage("Audio note added");
                    listAudioAdapter.add(audio);
                }
            }
        }
    }

    @Override
    public void onCancelRecord() {
        Global.showMessage("Cancel record");
    }

    /*NEW TEXT NOTE DIALOG LISTENER*/
    @Override
    public void onAccept(String text) {
        if(text.length() > 0){
            Message message = Global.saveTextNote(note, text);
            if(message != null){
                hideEmptyText();
                Global.showMessage("Text note added");
                listTextAdapter.add(message);
                listTextAdapter.notifyDataSetChanged();
            }
        }
    }
}
