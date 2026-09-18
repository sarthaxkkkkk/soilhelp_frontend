# SoilHelp Android

Kotlin + Jetpack Compose client for the SoilHelp backend. Login/register (JWT), latest
sensor reading, and reading history — pulling from your Spring Boot backend which in
turn polls ThingSpeak.

## Before you run it

1. Open this folder in **Android Studio** (not IntelliJ — needs the Android SDK/emulator tooling). It'll auto-generate the Gradle wrapper on first sync.
2. Make sure your backend is running (see `soilhelp-backend`).
3. Check the base URL in `network/RetrofitClient.kt`:
   - **Emulator**: `http://10.0.2.2:8080/` (default, already set — `10.0.2.2` is the emulator's alias for your host machine's `localhost`)
   - **Physical device**: use your machine's LAN IP instead, e.g. `http://192.168.1.42:8080/` (phone and computer must be on the same Wi-Fi)
   - **Once deployed to Render**: swap to `https://soilhelp-backend.onrender.com/` and remove `android:usesCleartextTraffic="true"` from `AndroidManifest.xml` (only needed for plain http:// local testing)
4. Run on an emulator or device (▶ in Android Studio).

## Flow

- Launches to Login if no saved token, Dashboard if already logged in (JWT persisted in `EncryptedSharedPreferences`)
- Register → auto-logs in → Dashboard
- Dashboard shows latest reading + history list, pull the refresh icon to re-poll
- Logout clears the token and returns to Login

## Structure

```
network/     Retrofit interface + client (JWT auto-attached via OkHttp interceptor)
data/        DTOs + TokenManager (encrypted token storage)
viewmodel/   AuthViewModel, ReadingsViewModel (StateFlow-based UI state)
ui/screens/  LoginScreen, RegisterScreen, DashboardScreen (Compose)
MainActivity.kt   Nav host wiring the three screens together
```

## Known simplifications (fine for a demo, flag if you want these hardened later)

- No Room/offline cache yet — dashboard just re-fetches on open/refresh
- No FCM push for alerts — would need backend changes (threshold checks + push) plus Firebase setup
- No pagination on history — pulls up to 50 readings in one call
