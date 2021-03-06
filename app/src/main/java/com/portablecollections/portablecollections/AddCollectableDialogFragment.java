package com.portablecollections.portablecollections;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class AddCollectableDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        void onDialogTakePhotoClick(DialogFragment dialog);
        void onDialogPickImageClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String source = args.getString("source");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final CharSequence[] options = {"Take photo", "Choose from gallery", "Cancel"};
        String titleText;
        if(source == "main") {
            titleText = "Select a source for the new image:";
        } else if (source == "details") {
            titleText = "Replace the image with a new one from:";
        } else {
            titleText = "Select a source:";
        }

        builder.setTitle(titleText)
                .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item] == "Take photo") {
                                    dialog.dismiss();
                                    mListener.onDialogTakePhotoClick(AddCollectableDialogFragment.this);
                                } else if (options[item] == "Choose from gallery") {
                                    dialog.dismiss();
                                    mListener.onDialogPickImageClick(AddCollectableDialogFragment.this);
                                } else if (options[item] == "Cancel") {
                                    dialog.dismiss();
                                }
                            }
                        }
                );

        return builder.create();
    }


}

