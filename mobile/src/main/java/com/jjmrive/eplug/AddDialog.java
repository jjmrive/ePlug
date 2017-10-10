package com.jjmrive.eplug;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class AddDialog extends DialogFragment {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    public static AddDialog newInstance(Double latitude, Double longitude){
        AddDialog dlg = new AddDialog();

        Bundle bundle = new Bundle();
        bundle.putDouble(LATITUDE, latitude);
        bundle.putDouble(LONGITUDE, longitude);

        dlg.setArguments(bundle);

        return dlg;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Double latitude = getArguments().getDouble(LATITUDE);
        Double longitude = getArguments().getDouble(LONGITUDE);

        return createAddDialog(latitude, longitude);
    }

    public AlertDialog createAddDialog(Double latitude, Double longitude) {

        final Double lat = latitude;
        final Double lon = longitude;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View v = inflater.inflate(R.layout.dialog_plug_add, null);

        final TextInputLayout title_layout = (TextInputLayout) v.findViewById(R.id.text_name);
        final TextInputLayout msg_layout = (TextInputLayout) v.findViewById(R.id.text_msg);
        final TextInputLayout url_layout = (TextInputLayout) v.findViewById(R.id.text_url);

        final EditText title = (EditText) v.findViewById(R.id.edit_name);
        final EditText msg = (EditText) v.findViewById(R.id.edit_msg);
        final EditText url = (EditText) v.findViewById(R.id.edit_url);

        final Switch swch = (Switch) v.findViewById(R.id.switchFree);

        builder.setView(v);

        title.addTextChangedListener(new TextValidator(title) {
            @Override
            public void validate(TextView textView, String text) {
                title_layout.setError(null);
                if (title.getText().toString().length() == 0 || title.getText().toString().length() > 20){
                    title.getBackground().clearColorFilter();
                    if (title.getText().toString().length() > 30) {
                        title_layout.setError(getResources().getString(R.string.dialog_add_errorSize));
                    }
                }
            }
        });

        msg.addTextChangedListener(new TextValidator(msg) {
            @Override
            public void validate(TextView textView, String text) {
                msg_layout.setError(null);
            }
        });

        url.addTextChangedListener(new TextValidator(url) {
            @Override
            public void validate(TextView textView, String text) {
                url_layout.setError(null);
            }
        });

        Button add = (Button) v.findViewById(R.id.button_add);
        Button cancel = (Button) v.findViewById(R.id.button_cancel);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ttl = title.getText().toString();
                String ms = msg.getText().toString();
                String pht = url.getText().toString();
                Boolean isFree = swch.isChecked();

                if ((ttl.length() == 0) || (ttl.length() > 30) || (ms.length() == 0)) {
                    if (ttl.length() == 0) {
                        title_layout.setError(getResources().getString(R.string.dialog_add_noName));
                    }
                    if (ttl.length() > 30) {
                        title_layout.setError(getResources().getString(R.string.dialog_add_errorSize));
                    }
                    if (ms.length() == 0) {
                        msg_layout.setError(getResources().getString(R.string.dialog_add_noMsg));
                    }
                } else {
                    AddFragment fragment = (AddFragment) getTargetFragment();
                    fragment.onAddedPlug(lat, lon, ttl, ms, pht, isFree);
                    dismiss();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return builder.create();
    }
}
