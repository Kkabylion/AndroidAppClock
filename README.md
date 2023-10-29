NTP Clock App

Description:

This Android application displays the current time sourced from an NTP (Network Time Protocol) server, ensuring accurate timekeeping. 
The app dynamically updates every second and showcases the difference between the system's time and the NTP time. 
Designed with a user-friendly interface, the application also responds to network changes, fetching the latest NTP time when the device reconnects to the internet.

The app provides a visual indication of the source of time:

Blue/Green Background: Indicates the time is sourced from the NTP server and there's active network connectivity.

Red Background: Indicates the time is sourced from the system (likely due to no network connectivity).


How to Use:

Launch the app to view the current NTP time.

The time will update every second, and the offset between the system time and NTP time is also displayed.

If you manually disable the device's Wi-Fi or lose network connectivity, the background will turn red, indicating the displayed time is the system time.

Restoring network connectivity will change the background back to blue/green, and the app will fetch and display the NTP time.
