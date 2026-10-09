# watch-app — Android + Wear OS Question Game

A question/answer game prototype (Magic-8-Ball style) with an Android phone app and a Wear OS companion app, synced over the Wearable Data Layer.

## Features

- **Phone app (Kotlin):** increments and pushes a counter to the watch via the Wearable `DataClient` API
- **Wear OS app:** categorized JSON answer packs (brain-idea, career-success, dangers, desires, diamond, confetti, …)
- Bluetooth connectivity, voice input support (RECORD_AUDIO), wake-lock handling
- Gradle multi-module build (`mobile` + `wear`)

## Tech Stack

- Kotlin, Android SDK 30 (min SDK 26)
- Wearable Data Layer API (`com.google.android.gms.wearable`)
- Gradle (Kotlin DSL wrapper), Material Components

## Project Structure

```
watch-app/
├── mobile/          # Phone app (MainActivity, DataClient sync)
└── wear/            # Wear OS app (JSON answer assets, voice input)
```

## Getting Started

1. Open the folder in **Android Studio**
2. Sync Gradle
3. Run `mobile` on a phone/emulator and `wear` on a Wear OS device/emulator

> Prototype status: package `org.incentives.questiongame_v2`, app label not yet customized.
