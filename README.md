<p align="center">
  <img src=".github/banner.png" alt="FPS Horizon Banner" width="720"/>
</p>

<h1 align="center">FPS Horizon</h1>

<p align="center">
  <img alt="Loader" src="https://img.shields.io/badge/Loader-Forge-orange">
  <img alt="Embeddium" src="https://img.shields.io/badge/Requires-Embeddium-blue">
  <img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-yellow">
</p>

---

## 🇬🇧 English

**FPS Horizon** is a client-side Forge mod that dynamically adjusts render distance and simulation distance based on your average FPS, keeping the game smooth without any manual tweaking.

### ✨ Features

- **Dynamic render distance** — automatically increases or decreases render distance based on your average FPS
- **Dynamic simulation distance** *(new)* — auto-adjusts simulation distance to improve CPU performance alongside GPU optimization
- **Animated fog transitions** — smooth distance fog hides chunk loading during render distance changes
- **Distance culling** — reduces GPU load by culling chunks and entities outside an ellipsoid shape
- **Entity whitelist** — Bosses, item frames, and paintings are never culled for better visual continuity
- **Memory Guard** — prevents render distance increases if RAM usage exceeds 85%, avoiding garbage collection stutters
- **1% Low filtering** — ignores micro-stutters when calculating FPS average, ensuring stable render distance decisions
- **Micro-HUD display** *(new)* — optional compact visual indicator in debug view showing mod state and active adjustments
- **Culling profiles** — create custom culling configurations per render distance (exact value or range), saved to `fps_horizon_profiles.json`
- **Dynamic culling** *(experimental)* — automatically adjusts culling based on current render distance
- **Real-time configuration** — all settings changeable in-game from the Mods menu, no restart needed
- **Embeddium compatible** — fully integrated with Embeddium's rendering pipeline via Mixins
- **Client-side only** — works on any server

### 📋 Requirements

| Dependency | Version |
|---|---|
| Forge | See releases |
| Embeddium | 0.3.31+ |

### ⚙️ Configuration

All options available in-game via **Mods → FPS Horizon → Config**.

#### Render Distance
| Option | Default | Description |
|---|---|---|
| Min Render Distance | 4 | The mod will never go below this value (chunks) |
| Max Render Distance | 12 | The mod will never exceed this value (chunks) |
| Min FPS | 30 | If average FPS drops below this, render distance decreases |
| Max FPS | 50 | If average FPS exceeds this, render distance increases |
| FPS Samples | 15 | Number of FPS samples to average before deciding a change (1% Low filtering applied) |
| Memory Guard | true | Prevents RD increase if RAM usage exceeds 85% |

#### Simulation Distance *(new)*
| Option | Default | Description |
|---|---|---|
| Dynamic Simulation | false | Enable/disable dynamic simulation distance adjustment |
| Min Simulation Distance | 4 | Minimum simulation range (chunks) |
| Max Simulation Distance | 12 | Maximum simulation range (chunks) |

#### Cooldown
| Option | Default | Description |
|---|---|---|
| Cooldown after decreasing | 30 ticks | Wait time after decreasing RD (20 ticks = 1 second) |
| Cooldown after increasing | 100 ticks | Wait time after increasing RD |

#### Fog
| Option | Default | Description |
|---|---|---|
| Enable Fog | true | Enables the distance fog that hides chunk loading |
| Fog Start | 0 blocks | Distance where fog begins |
| Fog End | 0.80 | Fraction of render distance where fog becomes fully opaque |
| Fog Close Factor | 0.80 | How aggressively the fog closes during a render distance change |
| Fog Speed | 0.05 | Animation speed of fog transitions (0.01 = slow, 0.5 = fast) |

#### Culling
| Option | Default | Description |
|---|---|---|
| Enable Culling | true | Enables distance-based chunk and entity culling |
| Vertical Scale | 100% | How aggressively chunks above/below are culled (0–1000%) |
| Horizontal Extension | 0% | Extends the render ellipsoid horizontally (0–100%) |
| Cull Entities | true | Applies culling to entities (mobs, items, etc) as well |
| Dynamic Culling *(experimental)* | false | Auto-adjusts culling based on current render distance |
| Profiles *(experimental)* | false | Use custom culling profiles per render distance |

**Dynamic Culling** adjusts automatically:
- RD 1–2: Vertical 50%, Horizontal 30%
- RD 3: Vertical 50%, Horizontal 40%
- RD 4+: Uses your configured slider values

**Culling Profiles** let you create named profiles with specific vertical/horizontal culling values for a given render distance (exact value or range). When active, the Vertical and Horizontal sliders are ignored.

#### Debug
| Option | Default | Description |
|---|---|---|
| Show RD changes | false | Shows render distance changes in the Action Bar |
| Verbose debug | false | Shows FPS, state, cooldown and culling info every tick |
| Micro-HUD | false | Display compact indicator of mod state in debug overlay |

### 🚀 Installation

1. Install [Minecraft Forge](https://files.minecraftforge.net/) for your version
2. Install [Embeddium 0.3.31+](https://modrinth.com/mod/embeddium)
3. Drop `fps-horizon-X.X.X.jar` into your `mods/` folder
4. Launch and configure via **Mods → FPS Horizon → Config**

### 📋 Changelog

**v1.3.0** *(Current)*
- **UI Redesign**: Unified Render Distance tab with 2-column layout (Min/Max RD sliders on left, FPS control on right)
- **Dynamic Simulation Distance**: New category for auto-adjusting CPU simulation alongside GPU render distance
- **Auto-detect Max RD**: Automatically detects maximum supported render distance (compatible with mods like Farsight that extend limits)
- **Memory Guard**: Prevents render distance increases if RAM usage exceeds 85%, reducing garbage collection stutters
- **1% Low Filtering**: FPS samples now exclude micro-stutters for more stable decisions
- **Entity Whitelist**: Bosses (Ender Dragon, Wither), item frames, and paintings are permanently whitelisted from culling
- **Micro-HUD Display**: Optional compact indicator in debug overlay showing mod state and active optimizations
- **Silent Reload System**: Smooth chunk updates without F3+A reload visual glitches, especially in caves
- **World-space Culling**: Fixed cave holes when looking down—culling now works in world coordinates instead of camera-relative
- **Profile Configuration Fixes**: Resolved value bleeding between profiles and UI state persistence issues

**v1.1.0**
- Added distance culling system (chunks and entities via ellipsoid)
- Added culling profiles — custom vertical/horizontal values per render distance (exact or range)
- Added dynamic culling *(experimental)* — auto-adjusts culling for low render distances
- Fog transitions now also trigger on significant culling vertical scale changes
- Fixed fog initialization artifacts on world load

**v1.0.0**
- Initial release
- Dynamic render distance based on average FPS
- Animated fog transitions
- In-game config screen with categories
- Embeddium integration via Mixins

---

## 🇦🇷 Español

**FPS Horizon** es un mod cliente de Forge que ajusta automáticamente la distancia de renderizado y la distancia de simulación según el promedio de FPS, manteniendo el juego fluido sin configuración manual.

### ✨ Características

- **Distancia dinámica** — aumenta o reduce el render distance según tus FPS promedio
- **Distancia de simulación dinámica** *(nuevo)* — ajusta automáticamente la simulación para mejorar rendimiento de CPU junto con GPU
- **Transiciones de niebla animadas** — niebla suave oculta la carga de chunks durante los cambios
- **Culling de distancia** — reduce carga de GPU descartando chunks y entidades fuera de un elipsoide
- **Lista blanca de entidades** — Jefes, marcos de ítems y cuadros nunca se descartan
- **Memory Guard** — impide aumentar render distance si el RAM supera 85%, evitando tirones de garbage collection
- **Filtrado de 1% Low** — ignora micro-tirones al calcular el promedio de FPS
- **Indicador Micro-HUD** *(nuevo)* — pequeño indicador visual opcional en debug mostrando estado del mod
- **Perfiles de culling** — configuraciones personalizadas de culling por distancia, guardadas en `fps_horizon_profiles.json`
- **Culling dinámico** *(experimental)* — ajusta automáticamente según la distancia actual
- **Configuración en tiempo real** — todo cambiable en-juego desde menú de Mods, sin reiniciar
- **Compatible con Embeddium** — integrado con pipeline de renderizado vía Mixins
- **Solo cliente** — funciona en cualquier servidor

### 📋 Requisitos

| Dependencia | Versión |
|---|---|
| Forge | Ver releases |
| Embeddium | 0.3.31+ |

### ⚙️ Configuración

Todas las opciones disponibles en **Mods → FPS Horizon → Config**.

#### Distancia de Renderizado
| Opción | Por defecto | Descripción |
|---|---|---|
| Distancia Mínima | 4 | El mod nunca bajará de este valor (chunks) |
| Distancia Máxima | 12 | El mod nunca superará este valor (chunks) |
| FPS Mínimos | 30 | Si el promedio baja, se reduce la distancia |
| FPS Máximos | 50 | Si el promedio sube, se aumenta la distancia |
| Muestras de FPS | 15 | Cantidad de muestras a promediar (con filtrado de 1% Low) |
| Memory Guard | true | Impide aumentar RD si RAM supera 85% |

#### Distancia de Simulación *(nuevo)*
| Opción | Por defecto | Descripción |
|---|---|---|
| Simulación Dinámica | false | Activar/desactivar ajuste dinámico de simulación |
| Distancia Mínima | 4 | Rango mínimo de simulación (chunks) |
| Distancia Máxima | 12 | Rango máximo de simulación (chunks) |

#### Cooldown
| Opción | Por defecto | Descripción |
|---|---|---|
| Cooldown al bajar | 30 ticks | Espera tras reducir la distancia (20 ticks = 1 segundo) |
| Cooldown al subir | 100 ticks | Espera tras aumentar la distancia |

#### Niebla
| Opción | Por defecto | Descripción |
|---|---|---|
| Activar Niebla | true | Activa la niebla que oculta la carga de chunks |
| Inicio de Niebla | 0 bloques | Distancia donde empieza la niebla |
| Fin de Niebla | 0.80 | Fracción de distancia donde la niebla se vuelve opaca |
| Factor de Cierre | 0.80 | Qué tan agresivo es el cierre durante un cambio |
| Velocidad de Niebla | 0.05 | Velocidad de animación (0.01 = lento, 0.5 = rápido) |

#### Culling
| Opción | Por defecto | Descripción |
|---|---|---|
| Activar Culling | true | Activa el culling de chunks y entidades por distancia |
| Escala Vertical | 100% | Qué tan agresivamente se descartan chunks arriba/abajo (0–1000%) |
| Extensión Horizontal | 0% | Extiende el elipsoide horizontalmente (0–100%) |
| Culling de Entidades | true | Aplica culling también a entidades (mobs, items, etc) |
| Culling Dinámico *(experimental)* | false | Ajusta automáticamente según la distancia actual |
| Perfiles *(experimental)* | false | Usa perfiles de culling personalizados por distancia |

**Culling Dinámico** ajusta automáticamente:
- RD 1–2: Vertical 50%, Horizontal 30%
- RD 3: Vertical 50%, Horizontal 40%
- RD 4+: Usa tus valores configurados

**Perfiles de Culling** — creá perfiles con valores específicos de culling vertical y horizontal para una distancia dada (exacta o rango). Cuando está activo, los sliders se ignoran.

#### Debug
| Opción | Por defecto | Descripción |
|---|---|---|
| Mostrar cambios de RD | false | Muestra cambios de distancia en el Action Bar |
| Debug detallado | false | Muestra FPS, estado, cooldown e info de culling en cada tick |
| Indicador Micro-HUD | false | Muestra compacto indicador de estado del mod en debug |

### 🚀 Instalación

1. Instalá [Minecraft Forge](https://files.minecraftforge.net/) para tu versión
2. Instalá [Embeddium 0.3.31+](https://modrinth.com/mod/embeddium)
3. Copiá `fps-horizon-X.X.X.jar` en tu carpeta `mods/`
4. Iniciá el juego y configurá desde **Mods → FPS Horizon → Config**

### 📋 Changelog

**v1.3.0** *(Actual)*
- **Rediseño de UI**: Pestaña Render Distance unificada con layout de 2 columnas (Min/Max RD a izquierda, control FPS a derecha)
- **Distancia de Simulación Dinámica**: Nueva categoría para ajustar CPU junto con GPU
- **Auto-detección de Máximo RD**: Detecta automáticamente límite máximo soportado (compatible con mods como Farsight)
- **Memory Guard**: Impide aumentar RD si RAM supera 85%, reduciendo tirones de garbage collection
- **Filtrado 1% Low**: Muestras de FPS ahora excluyen micro-tirones para decisiones más estables
- **Lista Blanca de Entidades**: Jefes, marcos de ítems y cuadros nunca se descartan
- **Indicador Micro-HUD**: Indicador compacto opcional en overlay de debug
- **Sistema de Recarga Silenciosa**: Actualización suave de chunks sin glitches de F3+A, especialmente en cuevas
- **Culling en Coordenadas del Mundo**: Corregidos huecos en cuevas al mirar hacia abajo
- **Correcciones de Perfiles**: Resueltos problemas de compatibilidad entre perfiles y persistencia de UI

**v1.1.0**
- Sistema de culling por distancia (chunks y entidades vía elipsoide)
- Perfiles de culling — valores personalizados por distancia (exacto o rango)
- Culling dinámico *(experimental)* — ajusta automáticamente para distancias bajas
- Transiciones de niebla también se disparan en cambios significativos de culling
- Corregido bug de inicialización de niebla al cargar el mundo

**v1.0.0**
- Lanzamiento inicial
- Distancia de renderizado dinámica basada en FPS promedio
- Transiciones de niebla animadas
- Pantalla de configuración ingame con categorías
- Integración con Embeddium vía Mixins

---

<p align="center">Made with ❤️ by <a href="https://github.com/Kratos-PalletTown">Kratos</a></p>
