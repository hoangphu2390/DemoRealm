package com.demorealm.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.demorealm.R;
import com.demorealm.listener.EditNameListener;

/**
 * Created by Kiet Nguyen on 01-Jan-17.
 */

public class DialogFactory {

    public static Dialog createInputNameDialog(final Context context, final EditNameListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_input_name);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        Button btnRight = (Button) dialog.findViewById(R.id.btnAccept);
        final EditText edtEmail = (EditText) dialog.findViewById(R.id.edt_email);

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtEmail.getText().toString().trim();
                listener.onInputName(name);
                closeInput(((Activity)context).getCurrentFocus());
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static void closeInput(final View caller) {
        caller.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) caller.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(caller.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 100);
    }
}
