package com.arriwe.wayndr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sancsvision.arriwe.BuildConfig;
import com.sancsvision.arriwe.R;

/**
 * Created by Anand Jain on 2/23/2017.
 */

public class AboutUs extends AppCompatActivity implements View.OnClickListener {

    TextView version_app;
    LinearLayout bottom_ll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);



        version_app = (TextView)findViewById(R.id.version_app);
        bottom_ll = (LinearLayout)findViewById(R.id.bottom_ll);

        version_app.setText("Version "+ BuildConfig.VERSION_NAME);
        bottom_ll.setOnClickListener(AboutUs.this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bottom_ll:
                finish();
                break;
        }
    }
}
