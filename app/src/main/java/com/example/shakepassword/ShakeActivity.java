package com.example.shakepassword;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean moved;
    int[] password = new int[3];
    int[] newPassword = new int[3];
    int index = 0;
    boolean saved;
    boolean waitingForEntry;
    Button tapBtn;
    Button testBtn;
    ProgressBar progressBar;
    TextView textView;
    int threshold;
    long lastTimestamp;
    Timer timer = new Timer();
    class myTask extends TimerTask {
        public void run() {
            index++;
            String txt = "";
            if(index==3){
                txt="***";
            }
            else{
                for(int i=0;i<index+1;i++){
                    if(i<index){
                        txt+='*';
                    }
                    else{
                        txt+=(waitingForEntry)?newPassword[i]:password[i];
                    }
                }
            }
            textView.setText(txt);
            Log.i("MYSENSOR","index = "+index);
        }
    }
    Timer progressTimer = new Timer();
    class porgressTask extends TimerTask {
        public void run() {
            progressBar.incrementProgressBy(-10);
            if(progressBar.getProgress()<=0){
                progressBar.setProgress(300);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Log.i("MYSENSOR", String.valueOf(sensor.getMaximumRange()));
        threshold = (int)(sensor.getMaximumRange()/10);
        tapBtn = findViewById(R.id.tap_btn2);
        testBtn = findViewById(R.id.button3);
        textView = findViewById(R.id.textView2);
        progressBar = findViewById(R.id.progressBar2);
        tapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener(ShakeActivity.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                index=0;
                lastTimestamp = System.currentTimeMillis();
                progressBar.setVisibility(View.VISIBLE);
                tapBtn.setVisibility(View.GONE);
                timer.scheduleAtFixedRate(new myTask(), 3000, 3000);
                progressTimer.scheduleAtFixedRate(new porgressTask(),100,100);
                Snackbar.make(findViewById(R.id.master), "Shake your phone in any direction a specific number of times during each interval.", Snackbar.LENGTH_SHORT)
                        .show();

            }
        });
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitingForEntry=true;
                index=0;
                for(int i=0;i<3;i++)
                    newPassword[i]=0;
                lastTimestamp = System.currentTimeMillis();
                progressBar.setVisibility(View.VISIBLE);
                testBtn.setVisibility(View.GONE);
                timer.scheduleAtFixedRate(new myTask(), 3000, 3000);
                progressTimer.scheduleAtFixedRate(new porgressTask(),100,100);
                Snackbar.make(findViewById(R.id.master), "Shake your phone in any direction a specific number of times during each interval.", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            if(Math.abs(event.values[0])>threshold && isMax(event.values[0],event.values[1],event.values[2]) && !moved && System.currentTimeMillis()-lastTimestamp>200 ){
                Log.i("MYSENSOR","X : "+event.values[0]);
                moved=true;
                lastTimestamp = System.currentTimeMillis();
                if(!saved) {
                    password[index]++;
                    String txt = "";
                    for(int i=0;i<index+1;i++){
                        if(i<index){
                            txt+='*';
                        }
                        else{
                            txt+=password[i];
                        }
                    }
                    textView.setText(txt);
                    //progressBar.incrementProgressBy(1);
                    Log.i("MYSENSOR","REGISTERED index "+index+" = "+password[index]);
                }
                else if(waitingForEntry){
                    newPassword[index]++;
                    String txt = "";
                    for(int i=0;i<index+1;i++){
                        if(i<index){
                            txt+='*';
                        }
                        else{
                            txt+=newPassword[i];
                        }
                    }
                    textView.setText(txt);
                }
            }
            else{
                moved=false;
            }
            if(Math.abs(event.values[1])>threshold && isMax(event.values[1],event.values[0],event.values[2])  && !moved && System.currentTimeMillis()-lastTimestamp>200){
                Log.i("MYSENSOR","Y : "+event.values[1]);
                moved=true;
                lastTimestamp = System.currentTimeMillis();
                if(!saved) {
                    password[index]++;;
                    String txt = "";
                    for(int i=0;i<index+1;i++){
                        if(i<index){
                            txt+='*';
                        }
                        else{
                            txt+=password[i];
                        }
                    }
                    textView.setText(txt);
                    //progressBar.incrementProgressBy(1);
                    Log.i("MYSENSOR","REGISTERED index "+index+" = "+password[index]);
                }
                else if(waitingForEntry){
                    newPassword[index]++;
                    String txt = "";
                    for(int i=0;i<index+1;i++){
                        if(i<index){
                            txt+='*';
                        }
                        else{
                            txt+=newPassword[i];
                        }
                    }
                    textView.setText(txt);
                }
            }
            else{
                moved=false;
            }
            if(Math.abs(event.values[2])>threshold && isMax(event.values[2],event.values[0],event.values[1])  && !moved  && System.currentTimeMillis()-lastTimestamp>200) {
                Log.i("MYSENSOR", "Z : " + event.values[2]);
                moved = true;
                lastTimestamp = System.currentTimeMillis();
                if (!saved) {
                    password[index]++;;
                    String txt = "";
                    for(int i=0;i<index+1;i++){
                        if(i<index){
                            txt+='*';
                        }
                        else{
                            txt+=password[i];
                        }
                    }
                    textView.setText(txt);
                    //progressBar.incrementProgressBy(1);
                    Log.i("MYSENSOR","REGISTERED index "+index+" = "+password[index]);
                }
                else if(waitingForEntry){
                    newPassword[index]++;
                    String txt = "";
                    for(int i=0;i<index+1;i++){
                        if(i<index){
                            txt+='*';
                        }
                        else{
                            txt+=newPassword[i];
                        }
                    }
                    textView.setText(txt);
                }
            }
            else{
                moved=false;
            }
            if(index==3) {
                saved=true;
                timer.cancel();
                timer = new Timer();
                textView.setText("");
                progressTimer.cancel();
                progressTimer = new Timer();
                progressBar.setVisibility(View.INVISIBLE);
                testBtn.setVisibility(View.VISIBLE);
                Log.i("MYSENSOR","PASSWORD = "+password[0]+password[1]+password[2]);
                if(waitingForEntry){
                    textView.setText("");
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
                        Snackbar.make(findViewById(R.id.master), "Correct password.", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    else{
                        //Toast.makeText(this, "incorrect", Toast.LENGTH_SHORT).show();
                        Snackbar.make(findViewById(R.id.master), "Incorrect password. Try again!", Snackbar.LENGTH_SHORT)
                                .show();

                    }
                }
                else{
                    //Toast.makeText(this, "Password Saved", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.master), "Password saved. "+password[0]+password[1]+password[2], Snackbar.LENGTH_SHORT)
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