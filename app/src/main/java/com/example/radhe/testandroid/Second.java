package com.example.radhe.testandroid;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

public class Second extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        VideoView vv = (VideoView)findViewById(R.id.videoView);
        MediaController mc = new MediaController(this);
        mc.setAnchorView(vv);
        Uri uri = Uri.parse("android.resource://"+ getPackageName()+"/"+  R.raw.ani);
        vv.setMediaController(null);
        vv.setVideoURI(uri);
        vv.requestFocus();
        vv.start();

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(i);
            }
        });

    }
}
