# PawCare 🐾

App Android para gestionar la salud y el bienestar de tus mascotas.

## Features
- [ ] Perfil de cada mascota con foto
- [ ] Historial médico y vacunas
- [ ] Turnos veterinarios
- [ ] Registro de peso y crecimiento
- [ ] Recordatorios y notificaciones

## Stack técnico
- Kotlin
- Jetpack Compose
- Clean Architecture + MVVM
- Room + Coroutines + Flow
- Hilt
- WorkManager
- Navigation Compose

## Arquitectura
```
data/         → Room, DAOs, Repositories
domain/       → Modelos puros
presentation/ → ViewModels + Screens
di/           → Inyección de dependencias
```

## Estado del proyecto
🚧 En desarrollo activo