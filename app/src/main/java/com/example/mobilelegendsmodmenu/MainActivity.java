package com.example.mobilelegendsmodmenu;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startModButton = findViewById(R.id.start_mod_button);
        startModButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
                } else {
                    startFloatingWidgetService();
                }
            }
        });

        // Example of a button that could trigger a game exploit
        Button triggerExploitButton = findViewById(R.id.trigger_exploit_button);
        triggerExploitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Triggering game exploit (placeholder)...", Toast.LENGTH_SHORT).show();
                // In a real scenario, this would call native methods (JNI)
                // or interact with the game's process directly.
                // For example:
                // new GamePatcher().activateAimbot();
            }
        });
    }

    private void startFloatingWidgetService() {
        startService(new Intent(MainActivity.this, FloatingWidgetService.class));
        finish(); // Close main activity after starting the service
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SYSTEM_ALERT_WINDOW_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                startFloatingWidgetService();
            } else {
                Toast.makeText(this, "Overlay permission denied. Mod menu cannot be displayed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
