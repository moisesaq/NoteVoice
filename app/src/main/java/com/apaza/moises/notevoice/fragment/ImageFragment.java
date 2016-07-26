package com.apaza.moises.notevoice.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.apaza.moises.notevoice.R;
import com.apaza.moises.notevoice.fragment.dialog.OptionDialog;
import com.apaza.moises.notevoice.global.Global;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageFragment extends Fragment implements View.OnClickListener, OptionDialog.OnOptionDialogListener{

    public static final String TAG = "IMAGE_FRAGMENT";

    private static final int SELECT_IMAGE = 100;
    private static final int CAPTURE_IMAGE = 200;

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    private View view;
    private ImageView image;
    private EditText description;

    private OnImageFragmentListener onImageFragmentListener;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(String param1, OnImageFragmentListener listener) {
        ImageFragment fragment = new ImageFragment();
        fragment.setOnImageFragmentListener(listener);
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
        view = inflater.inflate(R.layout.fragment_image, container, false);
        setupView();
        return view;
    }

    private void setupView(){
        image = (ImageView)view.findViewById(R.id.image);
        ImageButton edit = (ImageButton)view.findViewById(R.id.editImage);
        edit.setOnClickListener(this);

        description = (EditText)view.findViewById(R.id.descriptionImage);
        Button ok = (Button)view.findViewById(R.id.ok);
        ok.setOnClickListener(this);
    }

    public void setOnImageFragmentListener(OnImageFragmentListener listener){
        this.onImageFragmentListener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editImage:
                OptionDialog optionDialog = OptionDialog.newInstance("", this);
                optionDialog.show(getFragmentManager(), OptionDialog.TAG);
                break;
            case R.id.ok:
                if(onImageFragmentListener != null)
                    onImageFragmentListener.onOkClick();
                getActivity().onBackPressed();
                break;
        }
    }

    /*OPTION DIALOG LISTENER*/
    @Override
    public void onImageCapture() {

    }

    @Override
    public void onSelectImage() {
        selectImage();
    }

    @Override
    public void onRemovingImage() {

    }

    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        /*intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);*/
        try {
            intent.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(intent, "Completar accion con"), SELECT_IMAGE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Global.showMessage("Unsupport");
        } catch(Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECT_IMAGE:
                if(resultCode == Activity.RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        image.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case CAPTURE_IMAGE:
                break;
        }
    }

    public interface OnImageFragmentListener {
        void onOkClick();
    }

    /*@Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnImageFragmentListener) {
            mListener = (OnImageFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnImageFragmentListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        onImageFragmentListener = null;
    }
}
