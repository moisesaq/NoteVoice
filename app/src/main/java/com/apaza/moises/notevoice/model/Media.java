package com.apaza.moises.notevoice.model;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Base64;

import com.apaza.moises.notevoice.global.Global;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Media {
    public static final String AUDIO_DESTINATION_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS) + "/";
    //RECORDING AUDIO
    public static final int RECORD_MISSING_ENDING_FIX_INTERVAL = 500;
    public static final int RECORD_MAX_LENGTH_INTERVAL = 10000;

    private boolean recording = false;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MediaRecorder mediaRecorder = new MediaRecorder();

    public void startAudioPlaying(int resourceId, MediaPlayer.OnCompletionListener onCompletionListener) {
        if(mediaPlayer != null)
            if (mediaPlayer.isPlaying()) stopAudio();

        try {
            mediaPlayer = MediaPlayer.create(Global.getContext(), resourceId);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            stopAudio();
        }
    }

    public void setupAudio(String pathAudio){
        try{
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(pathAudio);
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startAudio(MediaPlayer.OnCompletionListener onCompletionListener){
        try{
            if(mediaPlayer != null){
                if(mediaPlayer.isPlaying())
                    stopAudio();
                else{
                    mediaPlayer.setOnCompletionListener(onCompletionListener);
                    mediaPlayer.start();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopAudio(){
        try{
            if(mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isAudioPlaying(){
        if(mediaPlayer != null)
            return mediaPlayer.isPlaying();
        return false;
    }

    public void startAudioPlaying(String filename, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mediaPlayer.isPlaying()) stopAudio();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filename);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            stopAudio();
        }
    }

    public int getAudioCurrentPosition() {
        if(mediaPlayer == null)
            return 0;
        return mediaPlayer.getCurrentPosition();
    }

    public int getAudioMaxDuration(){
        if(mediaPlayer == null)
            return 0;
        return mediaPlayer.getDuration();
    }

    public void startAudioRecording(String outputFilename) {
        if (mediaPlayer.isPlaying()) stopAudio();
        try {
            recording = true;
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(outputFilename);

            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            stopAudioRecording();
        }
    }

    public void stopAudioRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeAudioToDisk(String folder, String filename, String bytes) {
        try {
            File file = new File(folder, filename);
            if (!file.exists()) {
                FileOutputStream fileOutputStream = new FileOutputStream(folder + filename);
                fileOutputStream.write(Base64.decode(bytes, 0));
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean eraseAudioFromDisk(String pathFileName) {
        File file = new File(pathFileName);
        return file.exists() && file.delete();
    }

    public long getAudioLength(String filename) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(filename);
        String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.valueOf(duration);
    }



    public static String formatDuration(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60 ;
        int minutes = ((milliseconds / (1000 * 60)) % 60);
        int hours   = ((milliseconds / (1000 * 60 * 60)) % 24);
        return ((hours < 10) ? "0"  +hours : hours) + ":" +
                ((minutes < 10) ? "0" + minutes : minutes) + ":" +
                ((seconds < 10) ? "0" +seconds : seconds);
    }

    public String getFormattedDuration() {
        return formatDuration(mediaPlayer.getDuration());
    }

    public String getFormattedCurrentPosition() {
        return formatDuration(mediaPlayer.getCurrentPosition());
    }

    public boolean isRecording() {
        return recording;
    }

    public static String generateOutputFilename() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault());
        return simpleDateFormat.format(new Date()) + ".mp3";
    }

    public static String getAudioFileBytes(String pathFile){
        File audioFile = new File(pathFile);
        byte[] fileData = new byte[(int) audioFile.length()];
        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(audioFile);
            fileInputStream.read(fileData);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String audioFileBytes = Base64.encodeToString(fileData, 0);
        return audioFileBytes;
    }

    public static int getDurationAudioFile(String fileAudio){
        File file = new File(fileAudio);
        MediaPlayer mp = new MediaPlayer();
        FileInputStream fs;
        FileDescriptor fd;
        try {
            fs = new FileInputStream(file);
            fd = fs.getFD();
            mp.setDataSource(fd);
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mp.getDuration();
    }
}
