# Showtime

Showtime je Compose Multiplatform aplikacija za RMA Projekat 2. Projekat cilja Android i Desktop/JVM, sa zajednickim UI i business slojem u `composeApp`.

## Struktura

Glavni paket je `rs.edu.raf.rma.showtime`.

```text
showtime
  core
    auth
    database
    datastore
    model
    network
    mvi
    navigation
    ui
  data
    remote
    local
    mapper
    repository
  feature
    auth
    catalog
    details
    favorite
    watchlist
    quiz
    profile
  di
  ShowtimeApp.kt
```

`feature` paketi drze ekrane i ViewModel-e, `data` drzi API/Room/repository sloj, a `core` drzi zajednicke infrastrukturne delove kao sto su navigacija, auth lifecycle, DataStore, Room database i MVI helpers.

## Pokretanje

Android debug build:

```shell
./gradlew :androidApp:assembleDebug
```

Desktop app:

```shell
./gradlew :desktopApp:run
```

Hot reload za Compose desktop iteraciju:

```shell
./gradlew hot
```
