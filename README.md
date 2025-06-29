# TubeList
**TubeList** is an Android application built with **Kotlin** and **Jetpack Compose** that allows users to browse their YouTube subscriptions after authenticating via **OAuth2 (Google Sign-In)**. It uses the **YouTube Data API** to fetch channels and metadata, and applies modern Android architecture practices such as MVVM, Koin DI, and Flow-based state handling.

## Features

- 🔐 Authentication: Google OAuth 2.0 Sign-In
- 📺 Subscription Feed: View and search your YouTube subscriptions
- 📃 Channel Details: View detailed channel information
- 👤 User Profile: user’s avatar, name, and email and logout
- 🌙 Material 3 support with dynamic theming


## 🧰 Tech Stack

- **Architecture**: MVVM · StateFlow · Repository Pattern · Adapter Pattern · Feature-first modularity · Clean separation of UI, domain, and data concerns
- **UI**: Jetpack Compose (Material 3) · Scaffold layout · Bottom sheets · LazyColumn · Coil for image loading · Responsive previews
- **Auth**: CredentialsManager (Android Identity API) · Google Sign-In with OAuth 2.0 · ID token and access token handling · TokenAuthenticator with retry logic
- **Network**: Retrofit · OkHttp with custom AuthInterceptor · YouTube Data API v3 · DTOs mapping for subscriptions and channels
- **Dependency Injection**: Koin modules with ViewModel injection and scoped dependencies
- **Logging**: Custom IAppLogger abstraction over Timber · File/line trace support in logs for easier debugging
- **Testing**: JUnit · Coroutine Test Dispatchers · KoinTest · Mock repositories · StateFlow-based ViewModel testing

## 🔐 OAuth2 Setup
To authenticate with the YouTube API, create a `credentials.properties` file in the root of the project. This file should contain OAuth 2.0 client ID obtained from the Google Cloud Console.

### Create `credentials.properties`

Create a file named `credentials.properties` in the project root.

Add the following line:

```
client_id=YOUR_OAUTH2_CLIENT_ID_HERE
```

> **Note:** Without this file the project will fail to build.

### Add a Test User in Google Cloud Console

For the OAuth flow to work in development, you must add at least one test user:

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Open **APIs & Services > OAuth consent screen > Audience**.
3. Under **Test users**, click **Add Users** and enter the email address you will use to sign in during development.
4. Click **Save** to apply the changes.

> **Note:** If you don’t add a test user, you may encounter a "403 access denied" error or be blocked from proceeding with the login flow.

## 📄 License
MIT License © 2025 Michal Schejbal

## 📝 Notes
This app is designed for learning purposes and to demonstrate modern Android practices.

YouTube playback support may be limited based on player integration and API terms.