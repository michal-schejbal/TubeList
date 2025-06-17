# TubeList
YouTube subscription viewer with Jetpack Compose and OAuth2

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
