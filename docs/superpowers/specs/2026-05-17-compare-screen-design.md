# Compare Screen — Design Spec
**Date:** 2026-05-17

## Overview
New middle tab ("Comparar") that lets users compare 2 AI tools side-by-side. Stateless (in-memory only). Picker via `ModalBottomSheet`.

---

## Module Structure

```
compare/
├── presentation/
│   ├── CompareScreen.kt
│   ├── CompareViewModel.kt
│   └── AiPickerBottomSheet.kt
├── domain/usecase/
│   └── GetAllAisForPickerUseCase.kt   ← delegates to existing HomeRepository
└── di/
    └── CompareModule.kt
```

No `data/` layer — no new storage. `CompareModule` only provides the ViewModel; `HomeRepository` + `RatingsRepository` already in `CoreModule` / `RatingsModule`.

---

## UI State

```kotlin
enum class PickerTarget { A, B }

data class CompareUiState(
    val aiA: Ai? = null,
    val aiB: Ai? = null,
    val summaryA: RatingSummary? = null,
    val summaryB: RatingSummary? = null,
    val pickerTarget: PickerTarget? = null,
    val pickerAis: List<Ai> = emptyList(),
    val pickerQuery: String = ""
)
```

---

## Data Flow

1. `CompareViewModel.init` → loads full AI list once from `HomeRepository.getAllAis()` (Room cache).
2. User taps slot → `openPicker(PickerTarget)` → sets `pickerTarget`.
3. User types in picker → `onQueryChange(q)` → filters `pickerAis` in-memory.
4. User selects AI → `selectAi(ai)` → sets `aiA`/`aiB`, clears picker, launches `GetRatingSummaryUseCase(ai.id)` for that slot.
5. User taps ✕ → `clearSlot(PickerTarget)` → nulls AI + summary for that slot.

Rating fetch: `viewModelScope.launch { try { summaryA = getRatingSummaryUseCase(id) } catch { /* silent */ } }` — same pattern as `DetailViewModel`.

---

## UI Components

### CompareScreen
- Top: 2 slots + "VS" label between them.
  - Filled slot: logo + name + ✕ button.
  - Empty slot: dashed border, "+" icon, "Elegir IA" label.
- Bottom: comparison table (only rendered when both slots filled).
- Table rows (in order): Descripción · Precio · Valoración · Plataformas · Categorías · Características.
- Each row: label header (uppercase, muted) + two equal-width columns.
- Features row: union of `aiA.features + aiB.features` deduplicated by id; each column shows ✔ (green) if that AI's feature list contains the feature, ✘ (red/muted) if not.
- All row composables are private funs inside `CompareScreen.kt` — not reused elsewhere.

### AiPickerBottomSheet
- `ModalBottomSheet` with drag handle.
- `TextField` search bar at top (autofocused).
- `LazyColumn` of simplified AI rows (logo + name + priceModel chip).
- On row tap → `viewModel.selectAi(ai)`.
- Empty state: `AIpediaEmptyState` if filtered list is empty.

---

## Navigation Changes

`NavigationRoute`:
```kotlin
object Compare : NavigationRoute("compare")
```

`navItems` in `NavigationHost`: replace `"explore"` entry with `NavigationRoute.Compare.route`, icon `Icons.Filled.Compare` / `Icons.Outlined.Compare` (or `CompareArrows`), label "Comparar".

`NavHost`: add `composable(NavigationRoute.Compare.route) { CompareScreen() }`.

Bottom nav `onClick`: was `null` for explore → now functional.

---

## Edge Cases

| Case | Handling |
|---|---|
| Same AI in both slots | Allowed — no restriction |
| Rating unavailable | `summaryA/B = null` → row shows "–" |
| Picker list empty (no Room data) | `AIpediaEmptyState` in bottom sheet |
| Rotation / process death | State lost (in-memory) — consistent with rest of app |

---

## Out of Scope
- Persisting last comparison (future: SharedPreferences upgrade)
- "Comparar" button in DetailScreen (future enhancement)
- More than 2 AIs
- Sharing/exporting the comparison
