package com.apaza.moises.notevoice.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.adapter.ListNoteVoiceAdapter;
import com.apaza.moises.notevoice.base.BaseFragment;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.view.RecordButton;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class ListNoteVoiceFragment extends BaseFragment implements RecordButton.OnRecordButtonListener, ListNoteVoiceAdapter.OnNoteVoiceListAdapterListener, View.OnClickListener{

    public static final String TAG = "NOTE_VOICE_LIST_FRAG";

    private View view;
    private ViewPager viewPagerAction;

    private RecyclerView listNoteVoice;
    private ListNoteVoiceAdapter adapter;

    private TextView empty;
    private EditText textNote;
    private ImageButton saveTextNote, selectImage;

    private ActionBar actionBar;

    private String outputFilename;

    private OnNoteVoiceListFragmentListener listener;

    public ListNoteVoiceFragment(){

    }

    public static ListNoteVoiceFragment newInstance(){
        return new ListNoteVoiceFragment();
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

        empty = (TextView)view.findViewById(R.id.empty);

        listNoteVoice = (RecyclerView)view.findViewById(R.id.listNoteVoice);
        loadNotes();
    }

    @Override
    public void onResume(){
        super.onResume();
        //refreshList();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(false);
    }

    private void loadNotes(){
        List<Note> list = Global.getHandlerDB().getDaoSession().getNoteDao().queryBuilder().list();

        if(list.size() > 0){
            hideEmpty();
        }else{
            list = new ArrayList<>();
            showEmpty();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        listNoteVoice.setLayoutManager(linearLayoutManager);
        Global.showListNote();
        adapter = new ListNoteVoiceAdapter(list);
        adapter.sortDesc();
        adapter.setOnNoteVoiceListAdapterListener(this);
        listNoteVoice.setAdapter(adapter);
    }

    private void refreshList(){
        List<Note> list = Global.getHandlerDB().getDaoSession().getNoteDao().queryBuilder().list();
        adapter.swap(list);
    }

    public void hideEmpty(){
        empty.setVisibility(View.GONE);
        listNoteVoice.setVisibility(View.VISIBLE);
    }

    public void showEmpty(){
        listNoteVoice.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveTextNote:
                String text = textNote.getText().toString();
                if(text.length() > 0){
                    saveTextNote(text);
                }else{
                    Global.showMessage(getContext().getString(R.string.write_here));
                }
                break;
            case R.id.selectImage:
                Global.showMessage("Select a image");
                break;
        }
    }

    public void saveTextNote(String text){
        Note note = Global.saveNewNote();
        if (Global.saveTextNote(note, text) != null){
            Global.showMessage("Text note saved");
            adapter.addItem(note);
            if(adapter.getItemCount() > 0)
                hideEmpty();
        }
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
                    adapter.addItem(note);
                    if(adapter.getItemCount() > 0)
                        hideEmpty();
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

                    if(Global.deleteNote(note)){
                        adapter.removeItem(note);
                        if(adapter.getItemCount() == 0)
                            showEmpty();
                        dialog.dismiss();
                        Global.showListNote();
                    }
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
            throw new RuntimeException(context.toString() + " must implement OnNoteVoiceListFragmentListener");
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
            RecordButton recordAudio = (RecordButton) pageAudio.findViewById(R.id.recordAudio);
            recordAudio.setOnRecordButtonListener(ListNoteVoiceFragment.this);
        }

        public void setupTextNote(){
            textNote = (EditText)pageText.findViewById(R.id.textNote);
            saveTextNote = (ImageButton)pageText.findViewById(R.id.saveTextNote);
            saveTextNote.setOnClickListener(ListNoteVoiceFragment.this);
        }

        public void setupSelectImage(){
            selectImage = (ImageButton)pageImage.findViewById(R.id.selectImage);
            selectImage.setOnClickListener(ListNoteVoiceFragment.this);
        }
    }

}
