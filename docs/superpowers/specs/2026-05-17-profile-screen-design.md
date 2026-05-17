# Profile Screen — Design Spec
**Date:** 2026-05-17  
**Feature:** Pantalla de perfil como 3er ítem del bottom navigation

---

## Objetivo

Reemplazar el 3er ítem "Guardados" del bottom nav por "Perfil", crear la pantalla de perfil con layout Hero (opción A aprobada), y cablear la navegación entre Home y Perfil.

---

## Diseño visual aprobado

**Layout:** Avatar (iniciales) + nombre + email + badge Google · 3 estadísticas (Reseñas, Rating medio, Favoritos) · Filas de ajustes (Idioma, Notificaciones, Versión) · Botón "Cerrar sesión".

**Colores:** tema oscuro existente — fondo `MdBackground (#0E1014)`, primario `MdPrimary (#B5C5FF)`, superficie `MdSurfaceContainer (#1C2026)`, avatar con gradiente `CatEducacionA→MdPrimary`.

---

## Arquitectura

Sigue el patrón de feature module existente (MVVM + Clean Architecture).

### Archivos nuevos

```
app/src/main/java/com/burixer85/aipedia/
├── profile/
│   ├── di/
│   │   └── ProfileModule.kt          — Hilt bindings
│   └── presentation/
│       ├── ProfileScreen.kt          — Composable principal
│       ├── ProfileViewModel.kt       — StateFlow<ProfileUiState>
│       └── ProfileUiState.kt         — sealed class Loading/Success/Error
├── ratings/
│   └── domain/usecase/
│       └── GetUserReviewsUseCase.kt  — Reviews del usuario (junto al resto de use cases de ratings)
```

### Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `ratings/domain/repository/RatingsRepository.kt` | Añadir `getUserReviews(userId: String): List<Review>` |
| `ratings/data/repository/RatingsRepositoryImp.kt` | Implementar el método con query Supabase filtrado por userId |
| `ratings/di/RatingsModule.kt` | Exponer `GetUserReviewsUseCase` |
| `navigation/NavigationRoute.kt` | Añadir `object Profile : NavigationRoute("profile")` |
| `navigation/NavigationHost.kt` | Añadir destino Profile; gestionar bottom nav compartido con `selectedRoute` |
| `home/presentation/HomeScreen.kt` | Eliminar el `BottomNav` y `NavItem` internos; aceptar callbacks `onProfileClick` |

---

## Estado de UI

```kotlin
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val displayName: String,
        val email: String,
        val reviewCount: Int,
        val averageRating: Float?,   // null si reviewCount == 0
        val favoriteCount: Int       // 0 (placeholder hasta que exista sistema de favoritos)
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
```

---

## ProfileViewModel

- Inyecta `GetCurrentUserUseCase`, `GetUserReviewsUseCase`, `SignOutUseCase`.
- En `init {}`: combina el flow de usuario con la carga de reviews (suspendida al obtener userId).
- `signOut()`: llama a `SignOutUseCase`; el cambio de `sessionStatus` en Supabase propaga automáticamente la sesión caída al NavHost.

---

## Navegación

El bottom nav se saca de `HomeScreen` y pasa a vivir en `NavigationHost`, envuelto en un `Box` con `align = BottomCenter` igual que ahora.

- `selectedRoute` es un `rememberSaveable { mutableStateOf(NavigationRoute.Home.route) }` en `NavigationHost`.
- Al pulsar un ítem del bottom nav se llama `navController.navigate(route) { launchSingleTop = true; restoreState = true }`.
- El bottom nav **no aparece** en la pantalla de detalle (`NavigationRoute.Ai`): se oculta cuando `currentBackStackEntry?.destination?.route` empieza por `"ai"`.
- Items del bottom nav:
  1. Inicio — `Icons.Filled.Home` / `Icons.Outlined.Home`
  2. Explorar — `Icons.Filled.Explore` / `Icons.Outlined.Explore`  
  3. Perfil — `Icons.Filled.Person` / `Icons.Outlined.Person`

---

## GetUserReviews — datos de stats

- `GetUserReviewsUseCase` llama a `ratingsRepository.getUserReviews(userId)`.
- En `RatingsRepositoryImp` se hace un query PostgREST: `select * from reviews where user_id = eq.{userId}`.
- `reviewCount = list.size`
- `averageRating = if (list.isEmpty()) null else list.map { it.score }.average().toFloat()`
- `favoriteCount = 0` (hardcoded; se actualizará cuando se implemente el sistema de favoritos).

---

## Pantalla de perfil — secciones

1. **Header**: gradiente de fondo `#1C2550 → MdSurfaceContainer`; avatar circular con iniciales (primera letra de cada palabra del displayName, máx 2), gradiente `CatEducacionA → MdPrimary`; nombre; email; badge "Cuenta Google".
2. **Stats row**: 3 tarjetas `MdSurfaceContainer` con `MdPrimary` para el valor y `MdOnSurfaceMuted` para la etiqueta.
3. **Settings items**: tarjetas `MdSurfaceContainer` con icono, label, subtítulo. Solo lectura (no navegables en esta versión).
4. **Cerrar sesión**: fondo `rgba(255,100,100,0.10)`, borde `rgba(255,100,100,0.20)`, texto `#FF7B7B`. Al pulsar llama a `viewModel.signOut()`.

---

## Fuera de alcance (esta versión)

- Sistema de favoritos (favoriteCount siempre 0).
- Edición de perfil.
- Cambio de idioma funcional.
- Toggle de notificaciones funcional.
- Pantalla "Mis reseñas" navegable.
