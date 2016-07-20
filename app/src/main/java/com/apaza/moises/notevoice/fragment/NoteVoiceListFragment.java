package com.apaza.moises.notevoice.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.adapter.NoteVoiceListAdapter;
import com.apaza.moises.notevoice.base.BaseFragment;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.view.RecordButton;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class NoteVoiceListFragment extends BaseFragment implements RecordButton.OnRecordButtonListener, NoteVoiceListAdapter.OnNoteVoiceListAdapterListener, View.OnClickListener{

    public static final String TAG = "NOTE_VOICE_LIST_FRAGMENT";

    private View view;
    private ViewPager viewPagerAction;

    private RecyclerView listNoteVoice;
    private NoteVoiceListAdapter adapter;

    private TextView message;
    private RecordButton recordAudio;
    private EditText textNote;
    private ImageButton saveTextNote, selectImage;

    private LinearLayout viewMessage;
    private ProgressBar loading;
    private ActionBar actionBar;

    private String outputFilename;

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
        viewPagerAction = (ViewPager)view.findViewById(R.id.viewPagerAction);
        viewPagerAction.setAdapter(new ActionPageAdapter());

        CircleIndicator indicator = (CircleIndicator)view.findViewById(R.id.indicator);
        indicator.setViewPager(viewPagerAction);

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
            adapter.sortDesc();
            adapter.setOnNoteVoiceListAdapterListener(this);
            listNoteVoice.setAdapter(adapter);
        }else{
            listNoteVoice.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.GONE);
            message.setText("No found data");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveTextNote:
                String text = textNote.getText().toString();
                if(text.length() > 0){
                    Note note = Global.saveNewNote();
                    if (Global.saveTextNote(note, text) != null){
                        Global.showMessage("Text note saved");
                        adapter.addItem(note);
                    }
                }else{
                    Global.showMessage(getContext().getString(R.string.write_here));
                }
                break;
            case R.id.selectImage:
                Global.showMessage("Select a image");
                break;
        }
    }



    @Override
    public void onResume(){
        super.onResume();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(false);
    }

    /*RECORD BUTTON LISTENER*/
    @Override
    public void onStartRecord() {
        outputFilename = Media.AUDIO_DESTINATION_DIRECTORY + Media.generateOutputFilename();
        Global.getMedia().startAudioRecording(outputFilename);
    }

    @Override
    public void onEndRecord(long second) {
        if (Global.getMedia().isRecording()) {
            Global.getMedia().stopAudioRecording();

            if (!outputFilename.isEmpty()){
                Note note = Global.saveNewNote();
                if(Global.saveAudioNote(note, outputFilename) != null){
                    Global.showMessage("Audio recorded");
                    adapter.addItem(note);
                }
            }
        }
    }

    @Override
    public void onCancelRecord() {
        Global.showMessage("Cancel record");
    }

    /*NOTE VOICE LIST ADAPTER LISTENER*/
    @Override
    public void onDeleteClick(final Note note) {
        try{
            Global.showDialogConfirmation(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Global.getHandlerDB().getDaoSession().getNoteDao().delete(note);
                    adapter.removeItem(note);
                    if(note.getNoteAudio() != null && note.getNoteAudio().size() > 0)
                        Global.getMedia().eraseAudioFromDisk(note.getNoteAudio().get(0).getRoute());
                    dialog.dismiss();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

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

    public class ActionPageAdapter extends PagerAdapter{

        LinearLayout page, pageAudio, pageText, pageImage;

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            switch (position){
                case 0:
                    if(pageAudio == null)
                        pageAudio = (LinearLayout)LayoutInflater.from(container.getContext()).inflate(R.layout.page_audio_note, container, false);
                    page = pageAudio;
                    setupRecordButton();
                    break;

                case 1:
                    if(pageText == null)
                        pageText = (LinearLayout)LayoutInflater.from(container.getContext()).inflate(R.layout.page_text_note, container, false);
                    page = pageText;
                    setupTextNote();
                    break;

                case 2:
                    if(pageImage == null)
                        pageImage = (LinearLayout)LayoutInflater.from(container.getContext()).inflate(R.layout.page_image_note, container, false);
                    page = pageImage;
                    setupSelectImage();
                    break;
            }
            container.addView(page);
            return page;//super.instantiateItem(container, position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout)object);
        }

        public void setupRecordButton(){
            recordAudio = (RecordButton) pageAudio.findViewById(R.id.recordAudio);
            recordAudio.setOnRecordButtonListener(NoteVoiceListFragment.this);
        }

        public void setupTextNote(){
            textNote = (EditText)pageText.findViewById(R.id.textNote);
            saveTextNote = (ImageButton)pageText.findViewById(R.id.saveTextNote);
            saveTextNote.setOnClickListener(NoteVoiceListFragment.this);
        }

        public void setupSelectImage(){
            selectImage = (ImageButton)pageImage.findViewById(R.id.selectImage);
            selectImage.setOnClickListener(NoteVoiceListFragment.this);
        }
    }

}
