# NextAIPtvPlayer

App de IPTV para Android TV (Kotlin + Jetpack Compose). Carga una lista M3U,
permite activar/desactivar categorías y canales individuales para acelerar la
carga, y reproduce los canales con Media3/ExoPlayer.

## Cómo obtener el APK compilado (sin instalar nada)

Este repo incluye un workflow de GitHub Actions (`.github/workflows/build-apk.yml`)
que compila un APK de depuración automáticamente. Pasos:

1. Crea un repositorio nuevo en GitHub (puede ser privado).
2. Desde una terminal, dentro de esta carpeta:
   ```bash
   git init
   git add .
   git commit -m "Proyecto inicial NextAIPtvPlayer"
   git branch -M main
   git remote add origin https://github.com/TU_USUARIO/TU_REPO.git
   git push -u origin main
   ```
3. Entra en la pestaña **Actions** de tu repositorio en GitHub. El workflow
   "Build debug APK" se ejecutará solo: primero corre los tests unitarios del
   parser M3U y, si pasan, compila el APK.
4. Cuando termine (ícono verde), abre esa ejecución y baja hasta
   **Artifacts**: ahí está `NextAIPtvPlayer-debug-apk` para descargar.
5. Instala el APK en tu Android TV (por ejemplo con `adb install` o un
   gestor de archivos/instalador de APKs en el propio dispositivo).

Si el workflow falla, la pestaña Actions muestra el log completo del error;
pégamelo y lo corregimos.

## Estructura del proyecto

- `data/` — modelos, parser de M3U y persistencia (DataStore) de categorías/canales desactivados.
- `ui/AppViewModel.kt` — carga la lista y expone el estado filtrado.
- `ui/screens/` — Inicio, Configuración, Gestionar categorías, Gestionar canales, Reproductor.
- `ui/components/` — piezas reutilizables (tarjeta enfocable para mando, logo de canal).
- `app/src/test/` — tests unitarios del parser M3U (filtrado por categoría, estabilidad de los ids).

## Decisiones y límites conocidos

- Se usa **Jetpack Compose + Material3 estándar** (no `androidx.tv:tv-material`)
  para maximizar la probabilidad de que compile a la primera: la librería de
  Compose para TV es más nueva y su API ha cambiado más entre versiones. El
  foco por mando a distancia funciona igual gracias a `Modifier.clickable`.
- Las tarjetas de "reproduciendo ahora" con guía de programación (EPG) de los
  mockups no están implementadas: un M3U normal no trae esa información;
  haría falta una fuente XMLTV aparte.
- De la pantalla de Configuración solo están conectadas las opciones de
  cambiar lista y gestionar categorías; el resto de casillas del mockup
  (idioma, control parental, subtítulos, etc.) son ampliables más adelante.
- Las versiones de librerías (AGP, Kotlin, Media3, Compose BOM) se eligieron
  con la información más reciente disponible, pero no se han compilado en
  este entorno (sin SDK de Android ni red). El primer build en GitHub Actions
  es la primera compilación real; si hay algún choque de versiones, lo vemos
  con el log y lo ajustamos.
