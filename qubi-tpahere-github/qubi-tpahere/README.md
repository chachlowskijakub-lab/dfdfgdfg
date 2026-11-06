# Qubi TPAHere

Client-side Fabric mod (Minecraft 1.21.11). Gdy ktoś napisze na czacie, mod wysyła `/tpahere <nick>`.
Nick jest brany z listy graczy online, więc ranga/prefiks nigdy nie trafia do komendy.

- GUI: **Prawy Shift** (fioletowo-białe, włącz/wyłącz + cooldown)
- Wymaga: Fabric Loader, Fabric API, Java 21

*created by qubiszonnn*

## Build lokalnie
```
gradle build
```
Gotowy plik: `build/libs/qubi-tpahere-1.0.0.jar`

## Build na GitHubie
Wrzuć repo, wejdź w **Actions** -> *Build* -> **Run workflow**. Jar pobierzesz z Artifacts.

## Uwaga
Sprawdź wersje w `gradle.properties` na https://fabricmc.net/develop (1.21.11).
Używaj zgodnie z regulaminem serwera.
