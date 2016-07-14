package com.apaza.moises.notevoice.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.util.List;

public class DetailNoteFragment extends BaseFragment implements View.OnClickListener{

    public static final String TAG = "DETAIL_NOTE_FRAGMENT";
    public static final String ID_NOTE = "idNote";

    private long idNote;
    private Note note;
    private View view;

    private ListView listAudio;
    private ListAudioAdapter listAudioAdapter;
    private LinearLayout emptyAudio;
    private ImageButton addNewAudio;

    private ListView listText;
    private ListTextAdapter listTextAdapter;
    private TextView emptyText;
    private EditText inputTextNote;
    private ImageButton addNewText;

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
    }

    private void prepareListAudio(){
        listAudio = (ListView)view.findViewById(R.id.listAudio);
        emptyAudio = (LinearLayout)view.findViewById(R.id.emptyAudio);
        addNewAudio = (ImageButton)view.findViewById(R.id.addNewAudio);
        addNewAudio.setOnClickListener(this);

        List<Audio> list = Global.getHandlerDB().getDaoSession().getAudioDao()._queryNote_NoteAudio(note.getId());
        if(list != null && list.size() > 0){
            hideEmptyAudio();
            listAudioAdapter = new ListAudioAdapter(getContext(), list);
            listAudio.setAdapter(listAudioAdapter);
        }else {
            showEmptyAudio();
        }
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
        inputTextNote = (EditText)view.findViewById(R.id.inputTextNote);
        addNewText = (ImageButton)view.findViewById(R.id.addNewText);
        addNewText.setOnClickListener(this);

        List<Message> list = Global.getHandlerDB().getDaoSession().getMessageDao()._queryNote_NoteMessage(note.getId());
        if(list != null && list.size() > 0){
            hideEmptyText();
            listTextAdapter = new ListTextAdapter(getContext(), list);
            listText.setAdapter(listTextAdapter);
        }else{
            showEmptyText();
        }
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
            case R.id.addNewAudio:
                Global.showMessage("Add new audio");
                break;

            case R.id.addNewText:
                Global.showMessage("Add new text");
                break;
        }
    }
}
