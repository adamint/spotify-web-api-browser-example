## spotify-web-api-kotlin browser example
This example application integrates with the Spotify REST API, as well as the Spotify Web Playback SDK, to interactively 
play content via the browser. To play content, you need a Spotify premium subscription!

What this shows:
- How to develop a frontend Kotlin web application
- How to authenticate using the implicit grant API (re-requesting after token expiry)
- Integration with the Web Playback SDK wrapper
- Integration with the spotify-web-api-kotlin REST APIs

You can also use this application as a template to develop your own applications!

### Running
`./gradlew run` or `./gradlew.bat run` on windows
Please then go to http://localhost:3000 - you will be prompted to log into Spotify

### Examining the code
Please see src/main/kotlin/com/adamratzman/layouts/HomePageComponent.kt for the actual player instantiation.
src/main/kotlin/com/adamratzman/security/AuthUtils.kt shows you how to guard against unauthenticated access


### Screenshots
<img src="https://i.imgur.com/RnOsem8.png" />
