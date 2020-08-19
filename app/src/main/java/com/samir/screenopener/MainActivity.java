package com.samir.screenopener;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {

    TextView text, player_id_text ;
    Button permission_button ;
    ProgressBar progressbar;


    OSSubscriptionObserver osSubscriptionObserver = new OSSubscriptionObserver() {
        @Override
        public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
            if (!stateChanges.getFrom().getSubscribed() && stateChanges.getTo().getSubscribed()) {
                player_id_text.setText("Player ID\n"+OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);
        player_id_text = findViewById(R.id.player_id_text);
        permission_button = findViewById(R.id.permission_button);
        progressbar = findViewById(R.id.progressbar);

        permission_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 181);
            }
        });

        fetchPlayerIdThenConfiguration(player_id_text.getText().toString());
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkOverLayPermission();
    }

    private void checkOverLayPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(this)){
                text.setText("It seems you overlay permission is missing for this , please give this permission to open screen when app is even close");
                permission_button.setVisibility(View.VISIBLE);
            }else{
                text.setText("Over permission is on , you can send notiication from one signal panel to open screen from anywhere.");
                permission_button.setVisibility(View.GONE);
            }
        }
    }

    private void fetchPlayerIdThenConfiguration(String loadingText) {
        if (OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getSubscribed()) {
            player_id_text.setText("Player ID\n"+OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
            progressbar.setVisibility(View.GONE);
        } else {
            player_id_text.setText(""+loadingText);
            progressbar.setVisibility(View.VISIBLE);
            OneSignal.addSubscriptionObserver(osSubscriptionObserver);
            checkPlayerIdAfterSomeTime();
        }
    }


    private void checkPlayerIdAfterSomeTime() {
        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                try {
                    if (OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getSubscribed()) {
                        player_id_text.setText("Player ID\n"+OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                        progressbar.setVisibility(View.GONE);
                    } else {
                        OneSignal.removeSubscriptionObserver(osSubscriptionObserver);
                        fetchPlayerIdThenConfiguration("" + player_id_text.getText().toString() + " .");
                    }
                } catch (Exception e) {
                }
            }

        }.start();

    }

}
