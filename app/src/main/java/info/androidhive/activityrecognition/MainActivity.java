package info.androidhive.activityrecognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver broadcastReceiver;

    private File file;
    private FileOutputStream outputStream;

    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;
    private Button start, stop;

    private DatabaseReference mReference, mRef;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main3);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        mRef = mReference.child("Users").child(Build.BRAND + Build.MODEL);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);

        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);

        file = new File(getApplicationContext().getFilesDir(), Constants.filename);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTracking();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTracking();
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };

        startTracking();
    }

    private void handleUserActivity(int type, int confidence) {
        String label = getString(R.string.activity_unknown);
        int icon = R.drawable.ic_still;

        DatabaseReference mR = mRef.child(String.valueOf(System.currentTimeMillis()));

        switch (type) {
            case DetectedActivity.IN_VEHICLE:
                tv1.setText("In-Vehicle \t "+confidence);
                mR.child("In-Vehicle").setValue(String.valueOf(confidence));
                break;
            case DetectedActivity.ON_BICYCLE:
                tv2.setText("On-Bicycle \t "+confidence);
                mR.child("On-Bicycle").setValue(String.valueOf(confidence));
                break;
            case DetectedActivity.ON_FOOT:
                tv3.setText("On-Foot \t "+confidence);
                mR.child("On-Foot").setValue(String.valueOf(confidence));
                break;
            case DetectedActivity.STILL:
                tv4.setText("Still \t "+confidence);
                mR.child("Still").setValue(String.valueOf(confidence));
                break;
            case DetectedActivity.UNKNOWN:
                tv5.setText("Unknown \t "+confidence);
                mR.child("Unknown").setValue(String.valueOf(confidence));
                break;
            case DetectedActivity.TILTING:
                tv6.setText("Tilting \t "+confidence);
                mR.child("Tilting").setValue(String.valueOf(confidence));
                break;
            case DetectedActivity.WALKING:
                tv7.setText("Walking \t "+confidence);
                mR.child("Walking").setValue(String.valueOf(confidence));
                break;
            case DetectedActivity.RUNNING:
                tv8.setText("Running \t "+confidence);
                mR.child("Running").setValue(String.valueOf(confidence));
                break;
        }

//        Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void startTracking() {
        Intent intent = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent);
    }

    private void stopTracking() {
        Intent intent = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
    }
}
