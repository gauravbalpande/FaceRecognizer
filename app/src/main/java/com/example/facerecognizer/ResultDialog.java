package com.example.facerecognizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ResultDialog extends DialogFragment {
    Button btn;
    TextView txt1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_resultdialog,container,false);
        String text="";
        btn=view.findViewById(R.id.ok_btn);
        txt1=view.findViewById(R.id.dialog);

        // Getting the bundle
        Bundle bundle=getArguments();
        text=bundle.getString("data");
        //text=bundle.getString("RESULT_TEXT");
        txt1.setText(text);

        // Adding click  listeners to buttons
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
