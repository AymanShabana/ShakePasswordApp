package com.example.shakepassword;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.time.LocalDate;
import java.util.TimeZone;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.next;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private TriggerEventListener triggerEventListener;
    private boolean[] moved = new boolean[3];
    private int dir = 0;
    int[] password = new int[3];
    int[] newPassword = new int[3];
    int index = 0;
    boolean saved;
    boolean waitingForEntry;
    Button testBtn;
    Button tapBtn;
    int threshold;
    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Log.i("MYSENSOR", String.valueOf(sensor.getMaximumRange()));
        progressBar = findViewById(R.id.progressBar);
        threshold = (int)(sensor.getMaximumRange()/10);
        tapBtn = findViewById(R.id.tap_btn);
        testBtn = findViewById(R.id.test_btn);

        tapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener(MainActivity.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                progressBar.setVisibility(View.VISIBLE);
                testBtn.setVisibility(View.VISIBLE);
                tapBtn.setVisibility(View.GONE);
                testBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        waitingForEntry=true;
                        index=0;
                        progressBar.setProgress(0);
                    }
                });
                Snackbar.make(findViewById(R.id.master), "Move in X, Y or Z-axis to register a 3 element pattern.", Snackbar.LENGTH_SHORT)
                        .show();

            }
        });

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            if(Math.abs(event.values[0])>threshold && isMax(event.values[0],event.values[1],event.values[2]) && !moved[0]){
                Log.i("MYSENSOR","X : "+event.values[0]);
                moved[0]=true;
                if(!saved) {
                    password[index] = 0;
                    index++;
                    progressBar.incrementProgressBy(1);
                    Log.i("MYSENSOR","REGISTERED X "+password[index-1]);
                    Snackbar.make(findViewById(R.id.master), "Moved in X-axis.", Snackbar.LENGTH_SHORT)
                            .show();
                }
                else if(waitingForEntry){
                    newPassword[index] = 0;
                    index++;
                    progressBar.incrementProgressBy(1);
                    Log.i("MYSENSOR","REGISTERED X "+newPassword[index-1]);
                    Snackbar.make(findViewById(R.id.master), "Moved in X-axis.", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
            else{
                moved[0]=false;
            }
            if(Math.abs(event.values[1])>threshold && isMax(event.values[1],event.values[0],event.values[2])  && !moved[1]){
                Log.i("MYSENSOR","Y : "+event.values[1]);
                moved[1]=true;
                if(!saved) {
                    password[index] = 1;;
                    index++;
                    progressBar.incrementProgressBy(1);
                    Log.i("MYSENSOR","REGISTERED Y "+password[index-1] );
                    Snackbar.make(findViewById(R.id.master), "Moved in Y-axis.", Snackbar.LENGTH_SHORT)
                            .show();
                }
                else if(waitingForEntry){
                    newPassword[index] = 1;
                    index++;
                    progressBar.incrementProgressBy(1);
                    Log.i("MYSENSOR","REGISTERED Y "+newPassword[index-1]);
                    Snackbar.make(findViewById(R.id.master), "Moved in Y-axis.", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
            else{
                moved[1]=false;
            }
            if(Math.abs(event.values[2])>threshold && isMax(event.values[2],event.values[0],event.values[1])  && !moved[2]) {
                Log.i("MYSENSOR", "Z : " + event.values[2]);
                moved[2] = true;
                if (!saved) {
                    password[index] = 2;;
                    index++;
                    progressBar.incrementProgressBy(1);
                    Log.i("MYSENSOR","REGISTERED Z "+password[index-1]);
                    Snackbar.make(findViewById(R.id.master), "Moved in Z-axis.", Snackbar.LENGTH_SHORT)
                            .show();
                }
                else if(waitingForEntry){
                    newPassword[index] = 2;
                    index++;
                    progressBar.incrementProgressBy(1);
                    Log.i("MYSENSOR","REGISTERED Z "+newPassword[index-1]);
                    Snackbar.make(findViewById(R.id.master), "Moved in Z-axis.", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
            else{
                moved[2]=false;
            }
            if(index==3) {
                saved=true;
                Log.i("MYSENSOR","PASSWORD = "+password[0]+password[1]+password[2]);
                if(waitingForEntry){
                    boolean res = true;
                    Log.i("MYSENSOR","NEWPASSWORD = "+newPassword[0]+newPassword[1]+newPassword[2]);
                    for(int i=0;i<3;i++){
                        if(password[i]!=newPassword[i]){
                            res=false;
                            break;
                        }
                    }
                    if(res){
                        //Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
                        Snackbar.make(findViewById(R.id.master), "Moved in "+((password[2]==0)?"X":(password[2]==1)?"Y":"Z")+"-axis. Correct password.", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    else{
                        //Toast.makeText(this, "incorrect", Toast.LENGTH_SHORT).show();
                        Snackbar.make(findViewById(R.id.master), "Moved in "+((password[2]==0)?"X":(password[2]==1)?"Y":"Z")+"-axis. Incorrect password.", Snackbar.LENGTH_SHORT)
                                .show();

                    }
                }
                else{
                    //Toast.makeText(this, "Password Saved", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.master), "Moved in "+((password[2]==0)?"X":(password[2]==1)?"Y":"Z")+"-axis. Password saved.", Snackbar.LENGTH_SHORT)
                            .show();
                }
                waitingForEntry=false;
                index++;
            }
        }

    }
    boolean isMax(float x, float y, float z){
        return (Math.max(Math.abs(x),Math.abs(y))==Math.abs(x) && Math.max(Math.abs(x),Math.abs(z))==Math.abs(x));
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}