package com.apaza.moises.notevoice.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.database.Note;
import com.apaza.moises.notevoice.global.Global;
import com.apaza.moises.notevoice.model.Item;
import com.apaza.moises.notevoice.model.ViewHolder;
import com.apaza.moises.notevoice.view.PinnedSectionListView;

public class SimpleAdapter extends ArrayAdapter<Item> implements PinnedSectionListView.PinnedSectionListAdapter{

    LayoutInflater inflater;
    private final int[] COLORS = new int[] {R.color.green_light, R.color.orange_light, R.color.blue_light, R.color.red_light };

    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private SeekBar seekBar;

    public static int oneTimeOnly = 0;
    private int startTime = 0;
    private int finalTime = 0;
    private Handler handler = new Handler();

    public SimpleAdapter(Context context) {
        super(context, R.layout.item_note_audio);
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    protected void prepareSections(int sectionsNumber) { }

    protected void onSectionAdded(Item section, int sectionPosition) { }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if(view == null){
            view = inflater.inflate(R.layout.item_note_audio, null);
            holder = new ViewHolder();
            holder.viewItem = (LinearLayout)view.findViewById(R.id.viewItem);
            holder.play = (ImageButton)view.findViewById(R.id.play);
            holder.stop = (ImageButton)view.findViewById(R.id.stop);
            holder.seekBarAudio = (SeekBar)view.findViewById(R.id.seekBarAudio);
            holder.textNote = (TextView)view.findViewById(R.id.textNote);
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
            holder.textNote.setText(note.getText());
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
            mediaPlayer = MediaPlayer.create(Global.getContext(), recourseId);

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
}
