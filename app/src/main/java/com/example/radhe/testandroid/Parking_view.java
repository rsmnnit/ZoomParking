
package com.example.radhe.testandroid;

        import android.content.res.ColorStateList;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v4.view.ScrollingView;
        import android.support.v4.widget.TextViewCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.GridLayoutManager;
        import android.support.v7.widget.LinearLayoutCompat;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.GridLayout;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.ScrollView;
        import android.widget.TextView;

        import com.google.android.gms.vision.text.Line;

        import static android.support.v7.widget.LinearLayoutCompat.*;

public class Parking_view extends AppCompatActivity {
    String parkingSequence,id;
    String pName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_view);
         parkingSequence = getIntent().getStringExtra("string");
        Log.d("parkingsequence",parkingSequence);
         id = getIntent().getStringExtra("id");
        pName = getIntent().getStringExtra("name");
        Log.d("name",pName);
        TextView tv = (TextView) findViewById(R.id.parkingname);
        tv.setText(pName);


        View view = getLayoutInflater().inflate(R.layout.activity_parking_view,null);

        GridLayout grid = new GridLayout(this);
        ScrollView sview = (ScrollView)findViewById(R.id.sview);
        LinearLayout linear = (LinearLayout)findViewById(R.id.linear);
        parkingAllocation(grid,sview,linear);



    }

    private void parkingAllocation(GridLayout grid,ScrollView sview,LinearLayout linear) {

        int l=parkingSequence.length();

        grid.setColumnCount(5);
        grid.setRowCount(l/3+1);
        for(int i=0;i<l;i++) {
            ImageView image = new ImageView(this);
            if (parkingSequence.charAt(i) == '1') {
                image.setImageResource(R.mipmap.redcar);
                    image.setPadding(40,40,40,40);
                grid.addView(image, i);
            } else {
                TextView tv = new TextView(this);
                tv.setText(String.valueOf(i+1));
                tv.setPadding(40,40,40,40);
                grid.addView(tv, i);
            }

        };
        linear.addView(grid);
    }



}
