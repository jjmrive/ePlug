package com.jjmrive.eplug;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class InfoDialog extends DialogFragment {

    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String DISTANCE = "distance";
    private static final String URLPHOTO = "urlPhoto";
    private static final String ISFREE = "isFree";

    public static InfoDialog newInstance(String name, String description, Double distance, String urlPhoto, Boolean isFree){

        InfoDialog dlg = new InfoDialog();

        Bundle bundle = new Bundle();
        bundle.putString(NAME, name);
        bundle.putString(DESCRIPTION, description);
        bundle.putDouble(DISTANCE, distance);
        bundle.putString(URLPHOTO, urlPhoto);
        bundle.putBoolean(ISFREE, isFree);

        dlg.setArguments(bundle);

        return dlg;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String name = getArguments().getString(NAME);
        String description = getArguments().getString(DESCRIPTION);
        Double distance = getArguments().getDouble(DISTANCE);
        String urlPhoto = getArguments().getString(URLPHOTO);
        Boolean isFree = getArguments().getBoolean(ISFREE);

        return createInfoDialog(name, description, distance, urlPhoto, isFree);
    }

    public AlertDialog createInfoDialog(String name, String description, Double distance, String urlPhoto, Boolean isFree) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View v = inflater.inflate(R.layout.dialog_plug_info, null);

        ImageView img = (ImageView) v.findViewById(R.id.img_plug);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView descrp = (TextView) v.findViewById(R.id.description);
        TextView dst = (TextView) v.findViewById(R.id.distance);
        TextView free = (TextView) v.findViewById(R.id.isFree);

        builder.setView(v);

        if (!Objects.equals(urlPhoto, "")) {
            Picasso.with(getActivity()).load(urlPhoto).transform(new CircleTransform()).into(img);
        }
        title.setText(name);
        descrp.setText(description);
        dst.setText(String.format("%.3f", distance) + " " + getResources().getString(R.string.distance_end));
        if (isFree){
            free.setText(getResources().getString(R.string.dialog_add_editIsFree));
            free.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGreen500));
        } else {
            free.setText(getResources().getString(R.string.non_free_plug));
            free.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed700));
        }

        return builder.create();
    }
}
