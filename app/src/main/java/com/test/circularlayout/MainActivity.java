package com.test.circularlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.lib.circularlayoutlib.CircularLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CircularLayout cl = (CircularLayout) findViewById(R.id.circular_layout);
        if (cl != null) {
            cl.setOnCircularItemClickListener(new CircularLayout.OnCircularItemClickListener() {
                @Override
                public void onCircularItemClick(int index) {
                    Toast.makeText(getApplicationContext(), "Item " + index + " clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
