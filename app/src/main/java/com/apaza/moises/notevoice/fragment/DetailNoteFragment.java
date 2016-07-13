package com.apaza.moises.notevoice.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.base.BaseFragment;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.Global;

public class DetailNoteFragment extends BaseFragment {

    public static final String TAG = "DETAIL_NOTE_FRAGMENT";
    public static final String ID_NOTE = "idNote";

    private long idNote;
    private Note note;
    private View view;

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
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_detail_note, container, false);
        return view;
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
}
