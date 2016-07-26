package com.apaza.moises.notevoice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.database.NoteContentProvider;
import com.apaza.moises.notevoice.fragment.DetailNote;
import com.apaza.moises.notevoice.fragment.DetailNoteFragment;
import com.apaza.moises.notevoice.fragment.ListNoteFragment;
import com.apaza.moises.notevoice.fragment.ListNoteVoiceFragment;
import com.apaza.moises.notevoice.global.Global;

public class MainActivity extends AppCompatActivity implements ListNoteFragment.OnListNoteFragmentListener, ListNoteVoiceFragment.OnNoteVoiceListFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Global.setContext(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar(toolbar);
        //setSupportActionBar(toolbar);`
        //showFragment(ListNoteFragment.newInstance(""));
        showFragment(ListNoteVoiceFragment.newInstance());
    }

    public void setToolbar(Toolbar toolbar){
        setSupportActionBar(toolbar);
    }

    public void showFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(fragment.getClass().getName());
        ft.replace(R.id.contentMain,fragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_test_db:
                Global.showDataBaseCollections(NoteContentProvider.CONTENT_URI);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditNoteClick(Note note) {
        showFragment(DetailNote.newInstance(note.getId()));
    }

    @Override
    public void onBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStack();
        else
            finish();
    }

    private void setupFloatingButton(){
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });*/
    }

    @Override
    public void onEditNoteVoiceClick(Note note) {
        //showFragment(DetailNote.newInstance(note.getId()));
        showFragment(DetailNoteFragment.newInstance(note.getId()));
    }
}
