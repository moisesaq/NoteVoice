package com.apaza.moises.notevoice.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import java.lang.reflect.Field;

public class SimpleAdapter extends ArrayAdapter<Item> implements PinnedSectionListView.PinnedSectionListAdapter{

    private Context context;
    LayoutInflater inflater;
    private final int[] COLORS = new int[] {R.color.green_light, R.color.orange_light, R.color.blue_light, R.color.red_light };

    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private SeekBar seekBar;

    public static int oneTimeOnly = 0;
    private int startTime = 0;
    private int finalTime = 0;
    private Handler handler = new Handler();

    public OnItemOptionClickListener listener;

    public SimpleAdapter(Context context, OnItemOptionClickListener listener) {
        super(context, R.layout.item_note_audio);
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener = listener;
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
            holder.more = (ImageButton)view.findViewById(R.id.more);
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
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu(holder, item);
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

    private Runnable UpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(startTime);
            handler.postDelayed(this, 100);
        }
    };

    private void showMenu(ViewHolder holder, final Item itemList){
        PopupMenu popupMenu = new PopupMenu(getContext(), holder.more);
        popupMenu.inflate(R.menu.popup_menu);
        // Force icons to show
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[] { boolean.class };
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            Log.w("SIMPLE ADAPTER", "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_delete:
                        listener.onDeleteClick(itemList);

                        return true;
                    case R.id.action_edit:
                        listener.onEditClick(itemList);

                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public interface OnItemOptionClickListener{
        void onDeleteClick(Item item);
        void onEditClick(Item item);
    }
}
