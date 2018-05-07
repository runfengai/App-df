package com.jarry.app.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.eftimoff.androipathview.PathView;
import com.jarry.app.App;
import com.jarry.app.R;
import com.jarry.app.databinding.ActivityWelcomeBinding;
import com.jarry.app.model.Welcome;


public class WelcomeActivity extends BaseActivity {
    private ActivityWelcomeBinding binding;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        Welcome welcome = new Welcome("APP");
        binding.setWel(welcome);
//        final PathView pathView = (PathView) findViewById(R.id.pathView);
//        pathView.getPathAnimator().
//                delay(100).
//                duration(500).listenerEnd(new PathView.AnimatorBuilder.ListenerEnd() {
//            @Override
//            public void onAnimationEnd() {
//                handler.postDelayed(runnable, 1000);
//            }
//        }).
//        interpolator(new AccelerateDecelerateInterpolator()).
//                start();
        handler.postDelayed(runnable, 1500);


    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(self, LoginActivity.class));
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        App.exit();
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
        super.onDestroy();
    }
}
