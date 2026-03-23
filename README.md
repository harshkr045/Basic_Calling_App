# Basic Calling App
​
 A simple Android application built with Jetpack Compose that simulates a calling interface, including a dial pad, incoming/outgoing call screens, and active call management.
​
 ## Approach
​
 ### 1. Architecture: MVVM
 The app follows the **Model-View-ViewModel** architecture pattern to ensure a clean separation of concerns.
 - **Model**: `CallState` enum defines the various states of a call (IDLE, CALLING, RINGING, ACTIVE, ENDED).
 - **ViewModel (`CallViewModel`)**: Manages the application state, including the current phone number, call status, timer, and audio settings (mute/speaker). It uses `StateFlow` to emit state updates to the UI.
 - **View (`CallScreen`)**: A collection of Composable functions that react to state changes in the ViewModel.
​
 ### 2. UI & UX (Jetpack Compose)
 - **Declarative UI**: Built entirely using Jetpack Compose for a modern and responsive design.
 - **State-Driven Navigation**: Instead of traditional fragment navigation, the app uses `AnimatedContent` to swap between different screens (DialPad, Incoming, Active, etc.) based on the `callState` value.
 - **Material 3**: Utilizes Material Design 3 components and color schemes for a polished look.
​
 ### 3. Key Features & Implementation
 - **Dial Pad**: A standard grid-based dialer with backspace functionality and a call button.
 - **Call Simulation**:
     - **Outgoing**: Simulated delay (2s) to transition from 'Calling' to 'Active'.
     - **Incoming**: A dedicated button on the dial pad triggers a simulated incoming call from a known contact.
 - **Active Call Management**: 
     - Real-time call timer using a Coroutine-based ticker.
     - Interactive Mute and Speakerphone toggles with visual feedback.
 - **Bonus Features**:
     - **Contact Mapping**: Automatically displays names for specific numbers (e.g., "Mom", "Emergency").
     - **Smooth Transitions**: Fade-in/Fade-out animations between call states for a premium feel.
​
 ## How to Run
 1. Open the project in Android Studio (Iguana or newer recommended).
 2. Sync Gradle and run the `app` module on an emulator or physical device.
 3. Use the Dial Pad to enter a number and press the Green call button, or click "Simulate Incoming" to test the receiving flow.
