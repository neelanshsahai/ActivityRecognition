package info.androidhive.activityrecognition;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class VandA extends AppCompatActivity implements SensorEventListener {

    private Sensor sensor1, sensor2;
    private SensorManager sensorManager;

    private float ti, tf, ux, uy, uz, vx, vy, vz, sx, sy, sz, s;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, tv10, tv11, tv12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vand);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor1 = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this, sensor1, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensor2, SensorManager.SENSOR_DELAY_NORMAL);

        tv1 = (TextView) findViewById(R.id.tev1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);
        tv9 = (TextView) findViewById(R.id.tv9);
        tv10 = (TextView) findViewById(R.id.tv10);
        tv11 = (TextView) findViewById(R.id.tv11);
        tv12 = (TextView) findViewById(R.id.tv12);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
//    private long lastUpdate = 0;
//    private float last_x, last_y, last_z;
    float gx, gy, gz;
    float ax, ay, az;
    @Override
    public void onSensorChanged(SensorEvent event) {
//
//        float x = event.values[0];
//        float y = event.values[1];
//        float z = event.values[2];
//
//        long curTime = System.currentTimeMillis();
//
//        float speed=0;
//        if ((curTime - lastUpdate) > 100) {
//            long diffTime = (curTime - lastUpdate);
//            lastUpdate = curTime;
//
//            speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
//            last_x = x;
//            last_y = y;
//            last_z = z;
//        }
//
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = (int) Math.abs(gx - event.values[0]);
            ay = (int) Math.abs(gy - event.values[1]);
            az = (int) Math.abs(gz - event.values[2]);
//            ax = event.values[0];
//            ay = event.values[1];
//            az = event.values[2];
        }
        tf = System.currentTimeMillis();
        float dt;
        if(ti!=0)
            dt = (float) (tf-ti)/1000;
        else
            dt=0;
        vx = ux + ax*dt;
        vy = uy + ay*dt;
        vz = uz + az*dt;
        float v = (float) Math.sqrt(vx*vx + vy*vy + vz*vz);
        float u = (float) Math.sqrt(ux*ux + uy*uy + uz*uz);
        sx += (float) (ux*dt + 0.5*ax*dt*dt);
        sy += (float) (uy*dt + 0.5*ay*dt*dt);
        sz += (float) (uz*dt + 0.5*az*dt*dt);

        s += (v-u)*dt;

        tv1.setText("Vx = "+String.valueOf(vx)+" m/s");
        tv2.setText("Vy = "+String.valueOf(vy)+" m/s");
        tv3.setText("Vz = "+String.valueOf(vz)+" m/s");
        tv4.setText("Ax = "+String.valueOf(ax)+" m/s2");
        tv5.setText("Ay = "+String.valueOf(ay)+" m/s2");
        tv6.setText("Az = "+String.valueOf(az)+" m/s2");
        tv7.setText("Speed = "+String.valueOf(Math.sqrt(vx*vx + vy*vy + vz*vz))+" m/s");
        tv8.setText("Acceleration = "+String.valueOf(Math.sqrt(ax*ax + ay*ay + az*az))+" m/s2");
        tv9.setText("Sx = "+sx+" m");
        tv10.setText("Sy = "+sy+" m");
        tv11.setText("Sz = "+sz+" m");
        tv12.setText("S = "+s+" m");
        ux = vx;
        uy = vy;
        uz = vz;
        ti = System.currentTimeMillis();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Toast.makeText(this, "Accuracy Changed", Toast.LENGTH_SHORT).show();
    }
}
