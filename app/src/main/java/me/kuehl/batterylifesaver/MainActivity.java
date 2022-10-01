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

public class MainActivity extends AppCompatActivity {

    private final static int INTERVAL = 1000 * 5;

    MainActivity bob;
    boolean state = false;
    Handler handler = new Handler();
    Runnable handlerTask = () -> {

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bob = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickBtn(View view) {
        int percentage = getBatteriePercentage();
        TextView statusPercentage = findViewById(R.id.statusPercentage);
        statusPercentage.setText("Akku ist bei " + percentage + "%");

        setPowerState("http://192.168.178.72", state);
    }

    private void setPowerState(String url, boolean powerState) {
        TextView statusSwitch = findViewById(R.id.statusSwitch);

        String endpoint = url + "/cm?cmnd=Power%20" + (powerState ? "On" : "off");
        URL serverUrl = null;
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, endpoint, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.
                    statusSwitch.setText(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    statusSwitch.setText(error.getMessage());
                }
            });

// Add the request to the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Fehler getreten: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private int getBatteriePercentage(){
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }
}