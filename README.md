<p align="center">
  <img src=".github/banner.png" alt="FPS Horizon Banner" width="720"/>
</p>

<h1 align="center">FPS Horizon</h1>

<p align="center">
  <img alt="Minecraft" src="https://img.shields.io/badge/Minecraft-1.21.1-brightgreen">
  <img alt="Loader" src="https://img.shields.io/badge/Loader-NeoForge-orange">
  <img alt="Sodium" src="https://img.shields.io/badge/Requires-Sodium-blue">
  <img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-yellow">
</p>

---

## 🇬🇧 English

**FPS Horizon** is a client-side NeoForge mod for Minecraft 1.21.1 that dynamically adjusts render distance and simulation distance based on your average FPS and server tick time, keeping the game smooth without any manual tweaking.

### ✨ Features

- **Dynamic render distance** — automatically increases or decreases render distance based on your average FPS
- **Dynamic simulation distance** — adjusts simulation distance based on FPS, server tick time (MS), or both
- **Animated fog transitions** — smooth distance fog hides chunk loading during render distance changes
- **Distance culling** — reduces GPU load by culling chunks and entities outside an ellipsoid shape
- **Culling profiles** *(experimental)* — create custom culling configurations per render distance
- **Real-time configuration** — all settings changeable in-game from the Mods menu, no restart needed
- **Sodium compatible** — fully integrated with Sodium's rendering pipeline via Mixins
- **Client-side only** — works on any server

### 📋 Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | See releases |
| Sodium | 0.6.0+ |

### ⚙️ Configuration

All options available in-game via **Mods → FPS Horizon → Config**.

#### Render Distance
| Option | Default | Description |
|---|---|---|
| Min FPS | 30 | If average FPS drops below this, render distance decreases |
| Max FPS | 50 | If average FPS exceeds this, render distance increases |
| FPS Samples | 15 | Number of FPS samples to average before deciding a change |
| Min Render Distance | 4 | The mod will never go below this value (chunks) |
| Max Render Distance | 12 | The mod will never exceed this value (chunks) |
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
| Vertical Scale | 100% | How aggressively chunks above/below are culled (50%–1000%) |
| Horizontal Extension | 0% | Extends the render ellipsoid horizontally (0–100%) |
| Cull Entities | true | Applies culling to entities (mobs, items, etc) |
| Profiles *(experimental)* | false | Use custom culling profiles per render distance |

**Culling Profiles** — create named profiles with specific vertical/horizontal culling values for a given render distance (exact value or range). Saved to `fps_horizon_profiles.json` in your config folder.

#### Simulation Distance
| Option | Default | Description |
|---|---|---|
| Control Mode | OFF | OFF / FPS / MS / FPS+MS |
| Min Simulation Distance | 4 | The mod will never go below this value (chunks) |
| Max Simulation Distance | 10 | The mod will never exceed this value (chunks) |
| Cooldown after decreasing | 30 ticks | Wait time after decreasing SD |
| Cooldown after increasing | 100 ticks | Wait time after increasing SD |
| Min FPS threshold | 30 | [FPS/BOTH] If FPS drops below this, SD decreases |
| Max FPS threshold | 50 | [FPS/BOTH] If FPS exceeds this, SD increases |
| Max MS (slow server) | 100ms | [MS/BOTH] If tick time exceeds this, SD decreases |
| Min MS (fast server) | 50ms | [MS/BOTH] If tick time stays below this, SD increases |

> ⚠️ **MS mode** only works reliably in singleplayer or when you are the LAN host. In external multiplayer servers the tick time measurement includes network ping and may be unreliable. The mod will automatically disable MS mode in external multiplayer.

#### Debug
| Option | Default | Description |
|---|---|---|
| Show RD changes | false | Shows render distance changes in the Action Bar |
| Verbose debug | false | Shows FPS, SD, state and culling info every tick |

### 🚀 Installation

1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1
2. Install [Sodium 0.6.0+](https://modrinth.com/mod/sodium)
3. Drop `fps-horizon-X.X.X.jar` into your `mods/` folder
4. Launch and configure via **Mods → FPS Horizon → Config**

### 📋 Changelog
> ℹ️ Version history below carries over from the original 1.20.1 Forge mod, which was ported to NeoForge 1.21.1.

v1.3.0 (Stable)
- Completely rewritten culling system — replaced the complex ellipsoid + hysteresis with a simple, proven formula (fixed margins + auto - vertical scale).
- Full synchronization with Embeddium — the mod now updates the internal RenderSectionManager and calls markGraphDirty() whenever render distance changes.
- Chunk retention during RD increase — chunks are kept loaded while the render distance grows, eliminating visual holes.
- Silent render distance changes — mod‑triggered RD changes no longer cause full world reloads or flickering.
- Removed the need to press F3+A — holes and missing chunks are now a thing of the past.
- Removed obsolete configuration options (Vertical Scale, Horizontal Extension, Culling Profiles) – they are no longer needed.
- Improved debug info – verbose mode now shows culling statistics.

**v1.2.0** *(Beta)*
- Added dynamic simulation distance control (FPS / MS / FPS+MS modes)
- Merged Render Distance + FPS Control + Cooldown into one category
- Two-column layout for Simulation Distance screen
- Fixed slider text positioning (label fixed left, value fixed right)
- Fixed MS mode warning showing incorrectly in main menu
- Added MIN > MAX validation warning on RD and SD sliders
- MS samples now use FPS Samples config value for consistency
- Fixed duplicate accessor interface causing potential runtime conflict

**v1.1.1** *(Beta)*
- Improved overall stability during chunk updates
- Known bug: visual gaps when changing altitude quickly — press F3+A on PC or change RD by 1 to fix

**v1.1.0**
- Added distance culling system (chunks and entities via ellipsoid)
- Added culling profiles — custom values per render distance (exact or range)
- Fog transitions now trigger on significant culling changes
- Fixed fog initialization artifacts on world load

**v1.0.0**
- Initial release
- Dynamic render distance based on average FPS
- Animated fog transitions
- In-game config screen with categories
- Embeddium integration via Mixins

---

## 🇦🇷 Español

**FPS Horizon** es un mod cliente de NeoForge para Minecraft 1.21.1 que ajusta automáticamente la distancia de renderizado y la distancia de simulación según el promedio de FPS y el tiempo de tick del servidor, manteniendo el juego fluido sin configuración manual.

### ✨ Características

- **Distancia de renderizado dinámica** — aumenta o reduce el render distance según tus FPS promedio
- **Distancia de simulación dinámica** — ajusta la simulation distance según FPS, tiempo de tick del servidor (MS) o ambos
- **Transiciones de niebla animadas** — niebla suave oculta la carga de chunks durante los cambios
- **Culling de distancia** — reduce la carga de GPU descartando chunks y entidades fuera de un elipsoide
- **Perfiles de culling** *(experimental)* — configuraciones personalizadas de culling por distancia de renderizado
- **Configuración en tiempo real** — todo cambiable en el juego desde el menú de Mods, sin reiniciar
- **Compatible con Sodium** — integrado con el pipeline de renderizado de Sodium vía Mixins
- **Solo cliente** — funciona en cualquier servidor

### 📋 Requisitos

| Dependencia | Versión |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | Ver releases |
| Sodium | 0.6.0+ |

### ⚙️ Configuración

Todas las opciones disponibles en **Mods → FPS Horizon → Config**.

#### Render Distance
| Opción | Por defecto | Descripción |
|---|---|---|
| FPS Mínimos | 30 | Si el promedio baja de este valor, se reduce la distancia |
| FPS Máximos | 50 | Si el promedio supera este valor, se aumenta la distancia |
| Muestras de FPS | 15 | Cantidad de muestras a promediar antes de decidir un cambio |
| Distancia Mínima | 4 | El mod nunca bajará de este valor (chunks) |
| Distancia Máxima | 12 | El mod nunca superará este valor (chunks) |
| Cooldown al bajar | 30 ticks | Espera tras reducir la distancia (20 ticks = 1 segundo) |
| Cooldown al subir | 100 ticks | Espera tras aumentar la distancia |

#### Niebla
| Opción | Por defecto | Descripción |
|---|---|---|
| Activar Niebla | true | Activa la niebla que oculta la carga de chunks |
| Inicio de Niebla | 0 bloques | Distancia donde empieza la niebla |
| Fin de Niebla | 0.80 | Fracción de la distancia donde la niebla se vuelve opaca |
| Factor de Cierre | 0.80 | Qué tan agresivo es el cierre de niebla durante un cambio |
| Velocidad de Niebla | 0.05 | Velocidad de animación (0.01 = lento, 0.5 = rápido) |

#### Culling
| Opción | Por defecto | Descripción |
|---|---|---|
| Activar Culling | true | Activa el culling de chunks y entidades por distancia |
| Escala Vertical | 100% | Qué tan agresivamente se descartan chunks arriba/abajo (50%–1000%) |
| Extensión Horizontal | 0% | Extiende el elipsoide horizontalmente (0–100%) |
| Culling de Entidades | true | Aplica el culling también a entidades (mobs, items, etc) |
| Perfiles *(experimental)* | false | Usá perfiles de culling personalizados por distancia de renderizado |

**Perfiles de Culling** — creá perfiles con nombre y valores de culling para una distancia de renderizado específica (exacta o rango). Se guardan en `fps_horizon_profiles.json` en tu carpeta de config.

#### Distancia de Simulación
| Opción | Por defecto | Descripción |
|---|---|---|
| Modo de Control | OFF | OFF / FPS / MS / FPS+MS |
| Distancia Mínima | 4 | El mod nunca bajará de este valor (chunks) |
| Distancia Máxima | 10 | El mod nunca superará este valor (chunks) |
| Cooldown al bajar | 30 ticks | Espera tras reducir la SD |
| Cooldown al subir | 100 ticks | Espera tras aumentar la SD |
| FPS Mínimos | 30 | [FPS/BOTH] Si los FPS bajan de este valor, se reduce la SD |
| FPS Máximos | 50 | [FPS/BOTH] Si los FPS superan este valor, se aumenta la SD |
| MS Máximos (servidor lento) | 100ms | [MS/BOTH] Si el tick supera este valor, se reduce la SD |
| MS Mínimos (servidor rápido) | 50ms | [MS/BOTH] Si el tick se mantiene bajo este valor, se aumenta la SD |

> ⚠️ **Modo MS** solo funciona de forma confiable en singleplayer o cuando sos el host de LAN. En servidores externos el tiempo de tick incluye el ping de red y puede ser poco confiable. El mod desactiva automáticamente el modo MS en servidores externos.

#### Debug
| Opción | Por defecto | Descripción |
|---|---|---|
| Mostrar cambios de RD | false | Muestra los cambios de distancia en el Action Bar |
| Debug detallado | false | Muestra FPS, SD, estado e info de culling en cada tick |

### 🚀 Instalación

1. Instalá [NeoForge](https://neoforged.net/) para Minecraft 1.21.1
2. Instalá [Sodium 0.6.0+](https://modrinth.com/mod/sodium)
3. Copiá `fps-horizon-X.X.X.jar` en tu carpeta `mods/`
4. Iniciá el juego y configurá desde **Mods → FPS Horizon → Config**

### 📋 Changelog
> ℹ️ El historial de versiones a continuación proviene del mod original para 1.20.1 Forge, que fue portado a NeoForge 1.21.1.

v1.3.0 (Estable)
- Sistema de culling completamente reescrito — reemplazado el complejo elipsoide + histéresis por una fórmula simple y probada (márgenes fijos + autoescala vertical).
- Sincronización total con Embeddium — el mod ahora actualiza el RenderSectionManager y llama a markGraphDirty() cada vez que cambia la distancia de renderizado.
- Retención de chunks durante aumentos de RD — los chunks se mantienen cargados mientras el RD crece, eliminando huecos visuales.
- Cambios silenciosos de render distance — los cambios automáticos ya no provocan recargas completas del mundo ni parpadeos.
- Eliminada la necesidad de pulsar F3+A — los huecos y chunks desaparecidos son cosa del pasado.
- Eliminadas opciones obsoletas (Escala Vertical, Extensión Horizontal, Perfiles de Culling) – ya no son necesarias.
- Información de depuración mejorada – el modo verbose muestra estadísticas del culling.

**v1.2.0** *(Beta)*
- Sistema de distancia de simulación dinámica (modos FPS / MS / FPS+MS)
- Render Distance + FPS Control + Cooldown fusionados en una sola categoría
- Layout de dos columnas en la pantalla de Simulation Distance
- Texto de sliders fijo (nombre a la izquierda, valor a la derecha)
- Fix del aviso de modo MS que aparecía incorrectamente en el menú principal
- Validación visual cuando MIN > MAX en RD y SD
- Las muestras de MS ahora usan el valor de FPS Samples para consistencia
- Fix de accessor duplicado que podía causar conflictos en runtime

**v1.1.1** *(Beta)*
- Mejora de estabilidad general durante actualizaciones de chunks
- Bug conocido: huecos visuales al cambiar altitud rápido — presionar F3+A en PC o cambiar RD en 1 para corregir

**v1.1.0**
- Sistema de culling por distancia (chunks y entidades vía elipsoide)
- Perfiles de culling — valores personalizados por distancia de renderizado (exacto o rango)
- Transiciones de niebla también se disparan ante cambios significativos de culling
- Fix de artefactos visuales de niebla al cargar el mundo

**v1.0.0**
- Lanzamiento inicial
- Distancia de renderizado dinámica basada en FPS promedio
- Transiciones de niebla animadas
- Pantalla de configuración ingame con categorías
- Integración con Embeddium vía Mixins

---

<p align="center">Made with ❤️ by <a href="https://github.com/Kratos-PalletTown">Kratos</a></p>
