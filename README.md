# Kimi AI for Android

A free, open-source AI chat application for Android powered by Kimi (Moonshot AI). Built with Kotlin and Jetpack Compose.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2025.02.00-brightgreen)](https://developer.android.com/jetpack/compose)

## Features

- **Free & Open Source** - No ads, no subscriptions, fully transparent code
- **Powered by Kimi AI** - Uses Moonshot AI's powerful language models
- **Multiple Models** - Choose between 8K, 32K, and 128K context models
- **Beautiful UI** - Modern Material Design 3 with dynamic theming
- **Chat History** - Save and manage multiple conversations
- **Dark Mode** - Automatic dark/light theme support
- **Privacy Focused** - Your API key stays on your device only

## Screenshots

| Chat Screen | Settings | Drawer |
|------------|----------|--------|
| *Coming soon* | *Coming soon* | *Coming soon* |

## Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) or newer
- JDK 17 or higher
- Android SDK with API level 26+ (Android 8.0)
- A Moonshot AI API key (free tier available)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/gkzome/kimi-ai-android.git
   cd kimi-ai-android
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Choose the `kimi-ai-android` folder

3. **Build the project**
   - Let Gradle sync complete
   - Click `Build > Make Project` or press `Ctrl+F9`

4. **Run on device/emulator**
   - Connect an Android device or start an emulator
   - Click `Run > Run 'app'` or press `Shift+F10`

### Setting Up Your API Key

1. Get your free API key from [platform.moonshot.cn](https://platform.moonshot.cn/)
2. Open the app
3. Tap the menu (⋮) > **Settings**
4. Enter your API key and tap **Save**

> **Note:** Your API key is stored locally on your device and is never shared with anyone.

## Architecture

```
com.kimi.ai.android/
├── data/
│   ├── api/
│   │   └── KimiApiService.kt       # Retrofit API interface
│   ├── model/
│   │   └── ChatModels.kt           # Data classes
│   └── repository/
│       └── ChatRepository.kt       # Data management
├── ui/
│   ├── screens/
│   │   ├── ChatScreen.kt           # Main chat UI
│   │   └── SettingsScreen.kt       # Settings UI
│   ├── theme/
│   │   ├── Color.kt               # Theme colors
│   │   ├── Theme.kt               # App theme
│   │   └── Type.kt                # Typography
│   └── viewmodel/
│       └── ChatViewModel.kt        # Business logic
├── MainActivity.kt                 # Entry point
└── KimiApp.kt                     # Application class
```

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin 2.0 |
| UI Framework | Jetpack Compose |
| Architecture | MVVM |
| Networking | Retrofit + OkHttp |
| DI | Manual (no framework needed) |
| Async | Kotlin Coroutines + Flow |
| Storage | DataStore Preferences |
| Icons | Material Icons Extended |

## Available Models

| Model | Context | Best For |
|-------|---------|----------|
| Moonshot v1 (8K) | 8,192 tokens | Fast, everyday tasks |
| Moonshot v1 (32K) | 32,768 tokens | Long documents |
| Moonshot v1 (128K) | 131,072 tokens | Very long content |

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Privacy

This app respects your privacy:
- No data collection or tracking
- API key stored locally using encrypted preferences
- All chat data stays on your device
- No third-party analytics
- No internet permissions except for API calls

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Moonshot AI](https://www.moonshot.cn/) for the Kimi API
- [Jetpack Compose](https://developer.android.com/jetpack/compose) team
- [Material Design 3](https://m3.material.io/) for the design system

## Support

If you find this app useful, please consider:
- Giving this repo a star
- Sharing it with friends
- Contributing to the project

---

**Disclaimer:** This is an unofficial app and is not affiliated with Moonshot AI. You need your own API key to use this app.
