package com.example.naotosaito.clocktest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;


/**
 * Created by naotosaito on 2017/08/30.
 */
public class DatePickerDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("曜日");
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cansel", null);

        return builder.create();
    }
}
