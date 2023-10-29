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


public class MainActivity extends AppCompatActivity {

    // Be able to connect to NTP Server
    private final NTPUDPClient reneclient = new NTPUDPClient();

    // The address for the NTP Server, googles public server
    private final String timeServer = "time.google.com";

    // Holds the ntp time
    private long returnTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fetch current Time from NTP server
        if (isNetworkConnected()) {
            getNTPTime();
        }
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
            TimeInfo timeInfo = reneclient.getTime(inetAddress);
            returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

        }   catch (Exception e){
            e.printStackTrace();
            // This will make the time go back to system time
            returnTime = 0;
    }
        // Start thread
    }).start();
    }

    // Updates the textview for the ntptime..
    private void updateDisplay() {
        // Fetch the Textviews from the layout
        TextView timeTextView = findViewById(R.id.timeTextView);

        // Formatter to display the time in HH:mm:ss format
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // Gets current system time
        Date systemTime = new Date();
        // Display the NTP time or system time, if the time hasn't been retrieved
        Date ntpTime = (returnTime != 0) ? new Date(returnTime) : systemTime;
        if (returnTime != 0) {
            returnTime += 1000;
        } // Sets the time string to the TextView
        String ntpTimeString = format.format(ntpTime);
        timeTextView.setText(ntpTimeString);
        // Updates the round_background.xml based on network connectivity.
        if (isNetworkConnected()){
            timeTextView.setBackgroundResource(R.drawable.round_background);
        } else {
            timeTextView.setBackgroundResource(R.drawable.round_background2);

        }
    }
        // BroadcastReceiver to listen for network state changes
        private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
            public void onReceive(Context context, Intent intent) {
            // If connected to a network, fetch NTP time
            if (isNetworkConnected()) {
                // Update NTP time, when network connection changes
                getNTPTime();
            } else {
                // Reset the time if there is no network connection.
                returnTime = 0;
            }
        }
        };

        // Calls method during activity on the foreground.
        @Override
        protected void onResume(){
            super.onResume();
            // Listens to network changes
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkReceiver, filter);
        }
        // Calls method when activity goes to the background.
        @Override
        protected void onPause() {
            super.onPause();
            // Stops listening to network changes
            unregisterReceiver(networkReceiver);
        }
        // Method to check if the Cellphone is connected to a network.
        private boolean isNetworkConnected() {
            ConnectivityManager cma = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return cma != null && cma.getActiveNetworkInfo() != null && cma.getActiveNetworkInfo().isConnected();
        }
    }

