package com.arriwe.wayndr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.sancsvision.arriwe.R;

public class TipsFirst extends Activity
{
    //TextView tagging, next_two;
    CheckBox checkdone;
    Button btngot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one);
        checkdone=(CheckBox)findViewById(R.id.chk_nexttipdontshow);
        btngot=(Button)findViewById(R.id.button_nexttipdontshow);
        checkdone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    //checked

                }
                else
                {
                    //not checked
                }

            }

    });

    }
}