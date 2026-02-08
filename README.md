# AIpedia 🤖

**AIpedia** es una enciclopedia móvil nativa de Android diseñada para explorar, entender y comparar el creciente ecosistema de la Inteligencia Artificial. La aplicación permite a los usuarios descubrir herramientas de IA, filtrar por categorías, consultar precios y realizar comparativas detalladas entre diferentes modelos.

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-blue?logo=kotlin)
![Compose](https://img.shields.io/badge/Jetpack-Compose-orange?logo=jetpackcompose)
![Material3](https://img.shields.io/badge/Material-3-purple?logo=materialdesign)

---

## ✨ Características Principales

- 🔍 **Catálogo Completo:** Explora una amplia lista de IAs con descripciones detalladas sobre su funcionamiento.
- 🏷️ **Búsqueda por Categorías:** Encuentra herramientas específicas para generación de texto, imágenes, video, código, productividad y más.
- 💰 **Información de Costes:** Consulta rápidamente si una IA es gratuita, freemium o de pago antes de usarla.
- ⚖️ **Comparador de IAs:** Compara dos o más herramientas cara a cara para ver cuál se adapta mejor a tus necesidades según sus funciones y precios.
- ⚡ **Interfaz Fluida:** Experiencia de usuario optimizada con animaciones modernas y navegación intuitiva basada en Material 3.

---

## 🛠 Stack Tecnológico

Este proyecto utiliza las tecnologías más punteras del desarrollo Android actual:

- **Lenguaje:** [Kotlin 2.1.21](https://kotlinlang.org/) (Última versión con soporte para el compilador K2).
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (BOM 2024.09.00) con **Material 3**.
- **Arquitectura:** **MVVM** (Model-View-ViewModel) para una separación clara de responsabilidades y un código escalable.
- **Plugins:** 
  - `Kotlin Compose Plugin`: Integración nativa para un rendimiento de UI superior.
  - `Android Gradle Plugin (AGP) 8.13.2`: Configuración de compilación optimizada.
- **Librerías Core:**
  - `androidx-core-ktx`: Extensiones esenciales para Kotlin.
  - `lifecycle-runtime-ktx`: Gestión eficiente del ciclo de vida de la app.
  - `activity-compose`: Integración de Compose con las actividades de Android.

---

## 🏗️ Estructura del Proyecto

El código está organizado siguiendo principios de arquitectura limpia:

- `ui/`: Pantallas principales (`HomeScreen`, `DetailsScreen`, `CompareScreen`) y componentes reutilizables de Compose.
- `viewmodel/`: Lógica de negocio y gestión del estado de la interfaz.
- `data/`: Modelos de datos de las IAs, repositorios y fuentes de información.
- `navigation/`: Configuración de rutas y navegación entre pantallas.

---

## 👤 Autor

**BuriDeveloper**
- **LinkedIn:** [Mi Perfil de LinkedIn](https://www.linkedin.com/in/david-sevillano-domínguez-a7a432244/)

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.
