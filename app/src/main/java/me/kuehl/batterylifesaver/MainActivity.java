package me.kuehl.batterylifesaver;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final static int INTERVAL = 1000 * 5;

    private final static int MIN_CHARGE = 45;
    private final static int MAX_CHARGE = 55;

    private String url = "http://192.168.178.72";

    MainActivity bob;
    boolean state = false;
    boolean isCharging = true;

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {

        boolean shouldCharge = false;

        @Override
        public void run() {
            int charge = getBatteryPercentage();
            if (charge > MAX_CHARGE)
                shouldCharge=false;
            if (charge < MIN_CHARGE)
                shouldCharge=true;

            if(shouldCharge!=isCharging){
                setPowerState(shouldCharge);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bob = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateButtonText();
    }

    public void onClickBtn(View view) {
        //int percentage = getBatteryPercentage();
        //TextView statusPercentage = findViewById(R.id.statusPercentage);
        //statusPercentage.setText("Akku ist bei " + percentage + "%");

        setPowerState(true);
        state = !state;
        updateButtonText();

        if (state){
            timer.scheduleAtFixedRate(timerTask, 0, INTERVAL);
        }else{
            timer.cancel();
        }
    }

    private void setPowerState(boolean powerState) {
        isCharging = powerState;
        TextView statusSwitch = findViewById(R.id.statusSwitch);

        String endpoint = url + "/cm?cmnd=Power%20" + (powerState ? "On" : "off");
        URL serverUrl = null;
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, endpoint, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    statusSwitch.setText(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    statusSwitch.setText(error.getMessage());
                }
            });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Fehler getreten: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private int getBatteryPercentage(){
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    private void updateButtonText(){
        Button button = findViewById(R.id.button);
        button.setText(state ? "Läuft. Tippen zum stoppen" : "Tippen zum starten");
    }
}