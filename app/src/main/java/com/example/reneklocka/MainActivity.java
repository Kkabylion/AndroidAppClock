package com.example.reneklocka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // Be able to connect to NTP Server
    private final NTPUDPClient reneClient = new NTPUDPClient();

    // The address for the NTP Server, googles public server
    private final String timeServer = "time.google.com";

    // Holds the Time
    private long returnTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Fetch current Time from NTP server
        getNTPTime();

        // Timer to update the UI every 1 second
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run () {
                // Update the UI on the main Thread.
                runOnUiThread(() -> updateDisplay());
            }
        }, 0, 1000); // Begins at the start and repeats every second
}
// Fetches the current time from NTP  google´s server
private void getNTPTime(){
        // Start a new Thread for network operations
    new Thread(() -> {
    try {
        // Get IP address from the NTP server
        InetAddress inetAddress = InetAddress.getByName(timeServer);
        // Get the time from the NTP server
        TimeInfo timeInfo = reneClient.getTime(inetAdress);
        returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

    } catch (Exception e){
        e.printStackTrace();
    }

        // Start thread
    }).start();
    }

    // Updates the textfields for the ntptime,systemtime, offset time.
    private void updateDisplay() {
        // Fetch the Textviews from the layout
        TextView timeTextView = findViewById(R.id.timeTextView);
        TextView systemTimeTextView = findViewById(R.id.systemTimeTextView);
        TextView offsetTextView = findViewById(R.id.offsetTextView);

        // Formatter to display the time in HH:mm:ss format
        SimpleDateFormat Format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // Gets current system time
        Date systemTime = new Date();
        String systemTimeString = format.format(systemTime);
        systemTimeTextView.setText("System Time: " + systemTimeString);

        // Display the NTP time or system time, if the time hasn't been retrieved
        Date ntpTime;
        if (returnTime != 0) {
            ntpTime = new Date(returnTime);
            returnTime += 1000;
        } else{

            ntpTime = systemTime;
        }
        String ntpTimeSting = format.format(ntpTime);
        timeTextView.setText("NTP Time: " + ntpTimeString);

        long offsetMillis = ntpTime.getTime() - systemTime.getTime();
        long hours = TimeUnit.MILLISECONDS.toHours(offsetMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(offsetMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis) % 60;
        String offsetString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        offsetTextView.setText("Offset: " + offsetString);
    }
        // BroadcastReceiver to listen for network state changes
        private final BroadcastReceiver networkReceiver = new BroadcastReceived() {


        @Override
            public void onReceive(Context context, Intent intent)   {
            // Update NTP time, when network connection changes
            getNTPTime();
        }
        };

        // Calls during activity
        @Override
        protected void onResume(){
            super.onResume();
            // Listens to network changes
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkReceiver, filter);
        }

        @Override
        protected void onPause() {
            super.onPause;
            // stops listening to network changes
            unregisterReceiver(networkReceiver);
        }
}
