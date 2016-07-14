package com.apaza.moises.notevoice.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
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
import com.apaza.moises.notevoice.model.Media;
import com.apaza.moises.notevoice.model.ViewHolder;
import com.apaza.moises.notevoice.view.PinnedSectionListView;

import java.lang.reflect.Field;

public class SimpleAdapter extends ArrayAdapter<Item> implements PinnedSectionListView.PinnedSectionListAdapter{

    /*THIS IS DEPRECATED, NO USE. USE LIST AUDIO ADAPTER OR NOTE VOICE LIST ADAPTER*/
    private Context context;
    LayoutInflater inflater;
    private final int[] COLORS = new int[] {R.color.green_light, R.color.orange_light, R.color.blue_light, R.color.red_light };

    private ViewHolder viewHolder;
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

    public void prepareSections(int sectionsNumber) { }

    public void onSectionAdded(Item section, int sectionPosition) { }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if(view == null){
            view = inflater.inflate(R.layout.item_note_audio, null);
            holder = new ViewHolder();
            holder.viewItem = (LinearLayout)view.findViewById(R.id.viewItem);
            holder.play = (ImageButton)view.findViewById(R.id.play);
            holder.stop = (ImageButton)view.findViewById(R.id.stop);
            holder.seekBarAudio = (SeekBar)view.findViewById(R.id.seekBarAudio);
            holder.duration = (TextView)view.findViewById(R.id.duration);
            holder.textNote = (TextView)view.findViewById(R.id.textNote);
            holder.more = (ImageButton)view.findViewById(R.id.more);
            holder.dateCreated = (TextView)view.findViewById(R.id.dateCreated);
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
            //String path = item.note.getNoteAudio().get(0).getRoute();
            if(item.note.getNoteAudio().size() > 0)
                prepareAudio(holder, item.note);
            else
                holder.duration.setText("Error");
            //Log.d("AUDIO",">>>>>>" + path);
            /*holder.play.setOnClickListener(new View.OnClickListener() {
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
            });*/
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu(holder, item);
                }
            });
            holder.textNote.setText(note.getNoteMessage().get(0).getTextMessage());
            holder.dateCreated.setText(note.getCreateAt().toString());
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

    public void prepareAudio(final ViewHolder holder, final Note note){
        showPlayButton(holder);
        holder.duration.setText(Media.formatDuration(Media.getDurationAudioFile(note.getNoteAudio().get(0).getRoute())));

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStopButton(holder);
                startAudio1(note.getNoteAudio().get(0).getRoute(), holder, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopAudio1();
                        //showPlayButton(holder);
                    }
                });

            }
        });

        holder.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.getMedia().stopAudio();
                showPlayButton(holder);
            }
        });
    }

    private Runnable UpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            startTime = Global.getMedia().getAudioCurrentPosition();//mediaPlayer.getCurrentPosition();
            viewHolder.seekBarAudio.setProgress(startTime);
            handler.postDelayed(this, 100);
        }
    };

    public void startAudio1(String pathAudio, ViewHolder holder, MediaPlayer.OnCompletionListener listener){
        try{
            if(Global.getMedia().isAudioPlaying()){
                stopAudio1();
            }
            Global.getMedia().setupAudio(pathAudio);
            this.viewHolder = holder;

            finalTime = Global.getMedia().getAudioMaxDuration();
            startTime = Global.getMedia().getAudioCurrentPosition();
            this.viewHolder.seekBarAudio.setMax(finalTime);
            this.viewHolder.seekBarAudio.setProgress(startTime);

            Global.getMedia().startAudio(listener);
            handler.postDelayed(UpdateSeekBar, 100);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopAudio1(){
        try{
            Global.getMedia().stopAudio();
            //showPlayButton(holder);
            if(viewHolder != null){
                showPlayButton(viewHolder);
                viewHolder.seekBarAudio.setProgress(0);
            }
            handler.removeCallbacks(UpdateSeekBar);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showPlayButton(ViewHolder holder) {
        holder.play.setVisibility(View.VISIBLE);
        holder.stop.setVisibility(View.GONE);
        //holder.error.setVisibility(View.GONE);
    }


    private void showStopButton(ViewHolder holder) {
        holder.stop.setVisibility(View.VISIBLE);
        holder.play.setVisibility(View.GONE);
        //holder.error.setVisibility(View.GONE);
    }

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
