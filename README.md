# Compass and Azkar Sensors Project

This is a mobile application developed in Kotlin for Android devices. The project combines utility and spiritual features, offering a Qibla Compass for directional guidance and an Azkar service for scheduled Islamic supplications (morning and evening). The app also utilizes various device sensors to provide an interactive and personalized experience.

## Features

### 1. Qibla Compass
- Provides accurate direction to the Qibla (Kaaba) based on the device's location and orientation.
- Utilizes the device's accelerometer and magnetic field sensors for compass functionality.
- Vibrates when perfectly aligned with the Qibla direction for user convenience.

### 2. Azkar Service
- Plays pre-recorded Azkar (Islamic supplications) at specified times (morning and evening).
- Includes dynamic interaction with device sensors:
  - **Light Sensor:** Adjusts Azkar playback based on ambient brightness.
  - **Accelerometer:** Triggers playback when specific device orientations are detected.
  - **Gyroscope and Proximity Sensor:** Pauses or stops Azkar playback based on user gestures or proximity.

### 3. User Customization
- Allows users to configure Azkar playback times and brightness thresholds.
- Provides an easy-to-use interface for managing the Azkar service.

### 4. Additional Features
- Splash screen with a smooth transition to the main interface.
- Foreground service for continuous Azkar playback with notifications.

## Technologies Used
- **Programming Language:** Kotlin
- **Android Sensors:** Light, Proximity, Gyroscope, Accelerometer, Magnetic Field
- **Location Services:** Google Fused Location Provider for determining user location.
- **UI Design:** Android View Binding for seamless UI interactions.
- **Notifications:** Supports notification channels for managing foreground services.

## How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/Sameer-Murtaja/Compass-and-Azkar-Sensors-Project.git
   ```
2. Open the project in Android Studio.
3. Build and run the application on an Android device or emulator with required sensors.
4. Grant necessary permissions (Location, Notifications) when prompted.
