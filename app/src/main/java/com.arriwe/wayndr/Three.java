package com.arriwe.wayndr;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sancsvision.arriwe.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Three extends AppCompatActivity implements Animation.AnimationListener {

    CircleImageView circleImageView1,circleImageView2;
    Animation fade_in,fade_out;
    RelativeLayout r1,r2;
    LinearLayout tapOnThe,tagSomeOne;
    TextView tagging,next_two;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_set_up_profile_new);
        circleImageView1 = (CircleImageView) findViewById(R.id.pro_two);
        circleImageView2 = (CircleImageView) findViewById(R.id.pro_three);

       // this.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        animation();
        context = this;
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        tapOnThe = (LinearLayout) findViewById(R.id.tap_ll);

        tagging = (TextView) findViewById(R.id.tagging); tagging.setVisibility(View.GONE);
        tagSomeOne = (LinearLayout) findViewById(R.id.tagSomeOne); tagSomeOne.setVisibility(View.GONE);
        next_two = (TextView) findViewById(R.id.next_two); next_two.setVisibility(View.GONE);

        r1 = (RelativeLayout) findViewById(R.id.rr1); r1.setLayoutAnimationListener(this);
        r2 = (RelativeLayout) findViewById(R.id.rr2);
        r1.setVisibility(View.VISIBLE);
        r2.setVisibility(View.GONE);

    }

    void animation(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // only for gingerbread and newer versions
            circleImageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    circleImageView1.setColorFilter(0xFFffc000, PorterDuff.Mode.MULTIPLY);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            circleImageView1.clearColorFilter();
                            r1.startAnimation(fade_out);
                            r2.setVisibility(View.VISIBLE);
                            r2.startAnimation(fade_in);

                            tapOnThe.startAnimation(fade_out);
                            tagging.setVisibility(View.VISIBLE);
                            tagging.startAnimation(fade_in);

                            tagSomeOne.setVisibility(View.VISIBLE);
                            tagSomeOne.startAnimation(fade_in);

                            next_two.setVisibility(View.VISIBLE);
                            next_two.startAnimation(fade_in);
                        }
                    },100);
                }
            });
            circleImageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    circleImageView1.setColorFilter(0xFFffc000, PorterDuff.Mode.MULTIPLY);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            circleImageView1.clearColorFilter();
                        }
                    },100);
                }
            });
        }
        else {
            circleImageView1.setOnTouchListener(new View.OnTouchListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            r1.startAnimation(fade_out);
                            r2.setVisibility(View.VISIBLE);
                            r2.startAnimation(fade_in);

                            tapOnThe.startAnimation(fade_out);
                            tagging.setVisibility(View.VISIBLE);
                            tagging.startAnimation(fade_in);

                            tagSomeOne.setVisibility(View.VISIBLE);
                            tagSomeOne.startAnimation(fade_in);

                            next_two.setVisibility(View.VISIBLE);
                            next_two.startAnimation(fade_in);
                        }
                    },300);

                    // Pass all events through to the host view.
                    return false;
                }
            });
        }
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }


    @Override
    public void onAnimationEnd(Animation animation) {
        r1.setVisibility(View.GONE); circleImageView1.setVisibility(View.GONE);
    }


    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void go(View view){
        startActivity(new Intent(context,LoginActivity.class));
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }
}
