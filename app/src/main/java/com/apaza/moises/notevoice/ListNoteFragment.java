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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.database.NoteDao;

import java.util.Date;
import java.util.List;

public class ListNoteFragment extends Fragment implements View.OnClickListener{

    private NoteDao noteDao;

    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;

    private EditText textNote;
    private ImageButton recorder;

    private SeekBar seekBar;
    private int startTime = 0;
    private int finalTime = 0;
    private Handler handler = new Handler();

    public static int oneTimeOnly = 0;

    private View view;
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private ListView list;
    private ListNoteAdapter adapter;

    private PinnedSectionListView listNote;
    private SimpleAdapter noteAdapter;
    public int sectionPosition;
    public int listPosition;

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
        recorder.setOnClickListener(this);

        Button play = (Button) view.findViewById(R.id.play);
        play.setOnClickListener(this);
        Button stop = (Button)view.findViewById(R.id.stop);
        stop.setOnClickListener(this);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);

        list = (ListView)view.findViewById(R.id.listNoteVoice);
        adapter = new ListNoteAdapter(getActivity(), NoteTest.getListTest());
        list.setAdapter(adapter);

        listNote = (PinnedSectionListView)view.findViewById(R.id.listNote);
        clearAdapter();
        initializeAdapter();
        generateListTest(5,"Android");
        generateListTest(10,"Moises");
        generateListTest(7,"Apaza");
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
            case R.id.recorder:
                saveNote();
                break;
        }
    }

    private void saveNote(){
        noteDao = Global.getHandlerDB().getDaoSession().getNoteDao();
        if(noteDao.insert(getNoteOfView()) > 0)
            showMessage("Note recorded");
    }

    private Note getNoteOfView(){
        Note note = new Note();
        note.setCode(Global.generateCodeUnique("note"));
        note.setText(textNote.getText().toString());
        note.setPathAudio(String.valueOf(R.raw.dejala_hablar));
        note.setColor(String.valueOf(Global.colorGenerator.getRandomColor()));
        note.setDateCreated(new Date().toString());
        return note;
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

    public class ListNoteAdapter extends ArrayAdapter<NoteTest>{

        public ListNoteAdapter(Context context, List<NoteTest> list) {
            super(context, R.layout.item_note_audio, list);
        }

        @Override
        public NoteTest getItem(int position) {
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
            final NoteTest noteTest = getItem(position);
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAudio(noteTest.getAudio(), holder.seekBarAudio, new MediaPlayer.OnCompletionListener() {
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
            holder.message.setText(noteTest.getIdNote() + " - " + noteTest.getMessage());
            return view;
        }
    }

    public class ViewHolder{
        LinearLayout viewItem;
        ImageButton play, stop;
        SeekBar seekBarAudio;
        TextView message;
        TextView titleSection;
    }

    private void initializeAdapter() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            listNote.setFastScrollAlwaysVisible(true);
        }*/
        noteAdapter = new SimpleAdapter(getActivity());
        listNote.setAdapter(noteAdapter);
    }

    public void clearAdapter(){
        if(noteAdapter != null) noteAdapter.clear();
        sectionPosition = 0;
        listPosition = 0;
    }

    public class SimpleAdapter extends ArrayAdapter<Item> implements PinnedSectionListView.PinnedSectionListAdapter {

        private final int[] COLORS = new int[] {R.color.green_light, R.color.orange_light, R.color.blue_light, R.color.red_light };

        public SimpleAdapter(Context context) {
            super(context, R.layout.item_note_audio);
        }

        protected void prepareSections(int sectionsNumber) { }

        protected void onSectionAdded(Item section, int sectionPosition) { }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            if(view == null){
                view = getActivity().getLayoutInflater().inflate(R.layout.item_note_audio, null);
                holder = new ViewHolder();
                holder.viewItem = (LinearLayout)view.findViewById(R.id.viewItem);
                holder.play = (ImageButton)view.findViewById(R.id.play);
                holder.stop = (ImageButton)view.findViewById(R.id.stop);
                holder.seekBarAudio = (SeekBar)view.findViewById(R.id.seekBarAudio);
                holder.message = (TextView)view.findViewById(R.id.message);
                holder.titleSection = (TextView)view.findViewById(R.id.titleSection);
                view.setTag(holder);
            }else{
                holder = (ViewHolder)view.getTag();
            }
            final Item item = getItem(position);
            if(item.type == Item.SECTION){
                holder.titleSection.setVisibility(View.VISIBLE);
                holder.viewItem.setVisibility(View.GONE);
                holder.titleSection.setText(item.title);
                view.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));
            }else{
                final Note note = item.note;
                holder.viewItem.setVisibility(View.VISIBLE);
                holder.titleSection.setVisibility(View.GONE);
                holder.play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startAudio(Integer.valueOf(note.getPathAudio()), holder.seekBarAudio, new MediaPlayer.OnCompletionListener() {
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
                holder.message.setText(note.getText());
            }

            return view;
        }

        @Override public int getViewTypeCount() {
            return 2;
        }

        @Override public int getItemViewType(int position) {
            return getItem(position).type;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == Item.SECTION;
        }

    }

    public class FastScrollAdapter extends SimpleAdapter implements SectionIndexer {

        private Item[] sections;

        public FastScrollAdapter(Context context) {
            super(context);
        }

        @Override protected void prepareSections(int sectionsNumber) {
            sections = new Item[sectionsNumber];
        }

        @Override protected void onSectionAdded(Item section, int sectionPosition) {
            sections[sectionPosition] = section;
        }

        @Override public Item[] getSections() {
            return sections;
        }

        @Override public int getPositionForSection(int section) {
            if (section >= sections.length) {
                section = sections.length - 1;
            }
            return sections[section].listPosition;
        }

        @Override public int getSectionForPosition(int position) {
            if (position >= getCount()) {
                position = getCount() - 1;
            }
            return getItem(position).sectionPosition;
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

    public static class Item{
        public static final int SECTION = 0;
        public static final int NOTE = 1;

        public int sectionPosition;
        public int listPosition;

        public final int type;
        public String title;
        public Note note;

        public Item(int type, String titleSection){
            this.type = type;
            this.title = titleSection;
        }

        public Item(int type, Note note){
            this.type = type;
            this.note = note;
        }
    }

}
