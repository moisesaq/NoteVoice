package com.apaza.moises.notevoice.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.database.Audio;
import com.apaza.moises.notevoice.database.DaoSession;
import com.apaza.moises.notevoice.database.Message;
import com.apaza.moises.notevoice.global.CustomAnimationListener;
import com.apaza.moises.notevoice.global.Utils;
import com.apaza.moises.notevoice.model.Item;
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.view.PinnedSectionListView;
import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.database.NoteDao;
import com.apaza.moises.notevoice.global.Global;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListNoteFragment extends Fragment implements View.OnClickListener, SimpleAdapter.OnItemOptionClickListener,
        View.OnTouchListener{

    private EditText textNote;
    private ImageButton recorder;
    private TextView recordingTime;

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
        recordingTime = (TextView)view.findViewById(R.id.recordingTime);

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
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startAnimationRecord(recorder);
                //startRecording();
                break;

            case MotionEvent.ACTION_UP:
                //endAnimationRecord(recorder);
                collapseText(recordingTime);
                /*Handler recordMissingEndingHandler = new Handler();
                recordMissingEndingHandler.postDelayed(new Runnable() {
                    public void run() {
                        stopRecording();
                    }
                }, Media.RECORD_MISSING_ENDING_FIX_INTERVAL);*/
                break;

            case MotionEvent.ACTION_CANCEL:
                //endAnimationRecord(recorder);
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

    public void startAnimationRecord(View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.5f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.5f);
        //ObjectAnimator animColor = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), Color.parseColor("#FF0000"), Color.parseColor("#8B0000"));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim1, anim2);
        animatorSet.addListener(new CustomAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                expanseText(recordingTime);
            }
        });
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    public void expanseText(View view1){
        ObjectAnimator anim = ObjectAnimator.ofFloat(view1, "X", view1.getLeft());
        anim.setDuration(200);
        anim.setInterpolator(new BounceInterpolator());
        anim.addListener(new CustomAnimationListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                startTime();
            }
        });
        anim.start();
    }

    public void collapseText(View view1){
        ObjectAnimator anim = ObjectAnimator.ofFloat(view1, "X", view1.getRight());
        anim.setDuration(200);
        anim.addListener(new CustomAnimationListener(){

            @Override
            public void onAnimationStart(Animator animation) {
                stopTime();
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                endAnimationRecord(recorder);
            }
        });
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    public void endAnimationRecord(View view) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1.0f);
        //ObjectAnimator animColor = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(),Color.parseColor("#8B0000"), Color.parseColor("#FF0000"));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim2, anim1);
        animatorSet.setDuration(300);
        animatorSet.start();
    }


    public void expanse(View view){
        int left = recorder.getLeft();
        int right = recorder.getRight();
        int top = recorder.getTop();
        int bottom = recorder.getBottom();
        TranslateAnimation translateAnimation = new TranslateAnimation(left, right, top, bottom);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        translateAnimation.setDuration(300);
        translateAnimation.setFillAfter(true);
        view.startAnimation(translateAnimation);
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

    int count = 0;
    protected void startTime(){
        stopTime();
        recordHandler.postDelayed(recordRunnable = new Runnable() {
            @Override
            public void run() {
                count++;
                //recordingTime.setText(String.valueOf(count));
                /*recordingTime.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(count*1000),
                        TimeUnit.MILLISECONDS.toSeconds(count*1000) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(count*1000))
                ));*/
                recordingTime.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(count*1000),
                        TimeUnit.MILLISECONDS.toSeconds(count*1000) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(count*1000))
                ));
                recordHandler.postDelayed(recordRunnable, 1000);
            }
        }, 1000);
    }

    private void stopTime(){
        count = 0;
        recordHandler.removeCallbacks(recordRunnable);
        recordingTime.setText("0");
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
