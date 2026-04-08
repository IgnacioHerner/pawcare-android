# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Test
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests

# Quality
./gradlew lint
./gradlew clean
```

**Config:** Min SDK 26, Target SDK 35, Kotlin 2.0.21, Java 11. Dependencies managed via `gradle/libs.versions.toml`.

## Architecture

Clean Architecture + MVVM, single `:app` module.

```
data/
  local/
    dao/           # Room DAOs — all return Flow<T>
    entity/        # Room entities (suffix: Entity)
    mapper/        # Entity ↔ Domain mappers (toDomain() / toEntity())
    PawCareDatabase.kt          # Room DB, version 15, fallbackToDestructiveMigration
    WorkManagerHelper.kt        # Schedules/cancels WorkManager jobs
    WorkManagerSyncManager.kt   # Reconciles jobs on app startup
    *Worker.kt                  # VaccineWorker, MedicationWorker, MedicationFinishWorker
  repository/      # One per domain entity; maps entities to domain models
domain/
  model/           # Pure Kotlin data classes — no Android dependencies
presentation/
  <feature>/       # Screen.kt + ViewModel.kt per feature
  components/      # Shared Compose composables
  PawCareNavGraph.kt            # All routes and nav args
di/
  AppModule.kt     # Hilt @Singleton providers for DB, DAOs, DataStore
```

**Data flow:** Room Entity → DAO (Flow) → Repository (maps to domain model) → ViewModel (StateFlow\<UiState\>) → Compose UI (collectAsStateWithLifecycle).

## Key Patterns

**UiState sealed classes** — every ViewModel exposes a `StateFlow<XxxUiState>` with `Loading`, `Empty`, `Success(data)`, `Error(message)` variants.

**Hilt injection** — `@HiltViewModel` on every ViewModel; `@Singleton` repositories and DB; WorkManager uses `HiltWorker`. WorkManager auto-init is disabled in the manifest; `PawCareApp` implements `Configuration.Provider`.

**Navigation** — type-safe string routes defined in `PawCareDestinations` object inside `PawCareNavGraph.kt`. Helper functions build routes with arguments (e.g., `petDetail(petId: Long)`).

**Background notifications** — `WorkManagerHelper` schedules/cancels periodic `VaccineWorker` and `MedicationWorker` jobs (vaccine/medication reminders) and one-time `MedicationFinishWorker` jobs. `WorkManagerSyncManager` runs on startup to reconcile DB state with enqueued jobs.

**Database** — Room version 15, `fallbackToDestructiveMigration()` (no migration files). 6 entities: Pet, Vaccine, Medication, Appointment, Weight, Owner.

## Language & Naming

- Codebase is in **Spanish** — domain models, UI labels, comments, and variable names are all in Spanish.
- Entity classes: `PetEntity`, `VaccineEntity`, etc.
- Screen composables: `PetDetailScreen`, `VaccineListScreen`, etc.
- ViewModels: `PetViewModel`, `MedicationViewModel`, etc.

## Key Libraries

| Library | Purpose |
|---------|---------|
| Hilt 2.50 | Dependency injection |
| Room 2.6.1 | Local database |
| DataStore 1.0 | Typed preferences (settings) |
| WorkManager 2.9.0 | Background notification jobs |
| Compose + Material 3 | UI |
| Navigation Compose 2.7.7 | In-app navigation |
| Coil 2.5.0 | Image loading (pet photos) |
| Vico 2.0.0-alpha | Weight history charts |
