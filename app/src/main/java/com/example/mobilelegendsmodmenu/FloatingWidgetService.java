package com.example.mobilelegendsmodmenu;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class FloatingWidgetService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingWidgetView;
    private FrameLayout collapsedView;
    private View expandedView;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFloatingWidgetView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingWidgetView, params);

        collapsedView = mFloatingWidgetView.findViewById(R.id.collapsed_view);
        expandedView = mFloatingWidgetView.findViewById(R.id.expanded_view);

        Button closeButton = mFloatingWidgetView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });

        Button expandButton = mFloatingWidgetView.findViewById(R.id.expand_button);
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsedView.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);
            }
        });

        Button collapseButton = mFloatingWidgetView.findViewById(R.id.collapse_button);
        collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandedView.setVisibility(View.GONE);
                collapsedView.setVisibility(View.VISIBLE);
            }
        });

        // Example exploit buttons in expanded view
        Button noSkillCooldownButton = mFloatingWidgetView.findViewById(R.id.no_skill_cooldown_button);
        noSkillCooldownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWidgetService.this, "No Skill Cooldown activated (placeholder)", Toast.LENGTH_SHORT).show();
                // Implement actual exploit logic here (JNI calls, memory patching, etc.)
            }
        });

        Button wallHackButton = mFloatingWidgetView.findViewById(R.id.wall_hack_button);
        wallHackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWidgetService.this, "Wall Hack activated (placeholder)", Toast.LENGTH_SHORT).show();
                // Implement actual exploit logic here
            }
        });

        // Make the widget draggable
        mFloatingWidgetView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private long lastClickTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastClickTime = System.currentTimeMillis(); // Store the click time
                        return true;

                    case MotionEvent.ACTION_UP:
                        long clickDuration = System.currentTimeMillis() - lastClickTime;
                        if (clickDuration < 200) { // Consider it a click if released quickly
                            if (collapsedView.getVisibility() == View.VISIBLE) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            } else {
                                expandedView.setVisibility(View.GONE);
                                collapsedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingWidgetView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingWidgetView != null) {
            mWindowManager.removeView(mFloatingWidgetView);
        }
    }
}
