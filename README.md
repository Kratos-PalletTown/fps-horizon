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

**FPS Horizon** is a client-side Forge mod that dynamically adjusts render distance based on your average FPS, keeping the game smooth without any manual tweaking.

### ✨ Features

- **Dynamic render distance** — automatically increases or decreases render distance based on your average FPS
- **Animated fog transitions** — smooth distance fog hides chunk loading during render distance changes
- **Distance culling** — reduces GPU load by culling chunks and entities outside an ellipsoid shape
- **Culling profiles** — create custom culling configurations per render distance (exact value or range)
- **Dynamic culling** *(experimental)* — automatically adjusts culling aggressiveness based on current render distance
- **Real-time configuration** — all settings can be changed in-game from the Mods menu without restarting
- **Embeddium compatible** — fully integrated with Embeddium's rendering pipeline via Mixins
- **Lightweight** — runs entirely on client side, no server needed

### 📋 Requirements

| Dependency | Version |
|---|---|
| Forge | See releases |
| Embeddium | 0.3.31+ |

### ⚙️ Configuration

All options are available in-game via **Mods → FPS Horizon → Config**.

#### FPS Control
| Option | Default | Description |
|---|---|---|
| Min FPS | 30 | If average FPS drops below this, render distance decreases |
| Max FPS | 50 | If average FPS exceeds this, render distance increases |
| FPS Samples | 15 | Number of FPS samples to average before deciding a change |

#### Render Distance
| Option | Default | Description |
|---|---|---|
| Min Render Distance | 4 chunks | The mod will never go below this value |
| Max Render Distance | 12 chunks | The mod will never exceed this value |

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
| Fog Speed | 0.05 | Animation speed of fog transitions |

#### Culling
| Option | Default | Description |
|---|---|---|
| Enable Culling | true | Enables distance-based chunk and entity culling |
| Vertical Scale | 2.0x | How aggressively chunks above/below are culled |
| Horizontal Extension | 10% | Extends the render ellipsoid horizontally |
| Cull Entities | true | Applies culling to entities as well |
| Dynamic Culling *(experimental)* | false | Auto-adjusts culling based on current render distance |
| Profiles *(experimental)* | false | Use custom culling profiles per render distance |

#### Culling Profiles
Create profiles that define vertical and horizontal culling values for a specific render distance (exact value or range). When active, the main culling sliders are disabled and the matching profile is used automatically. Profiles are saved in `fps_horizon_profiles.json`.

#### Debug
| Option | Default | Description |
|---|---|---|
| Show RD changes | false | Shows render distance changes in the Action Bar |
| Verbose debug | false | Shows FPS, state, cooldown and culling info every tick |

### 🚀 Installation

1. Install [Minecraft Forge](https://files.minecraftforge.net/) for your version
2. Install [Embeddium 0.3.31+](https://modrinth.com/mod/embeddium)
3. Drop `fps-horizon-X.X.X.jar` into your `mods/` folder
4. Launch the game and configure via **Mods → FPS Horizon → Config**

### 📋 Changelog

**v1.1.0**
- Added distance culling system (chunk and entity culling via ellipsoid)
- Added culling profiles — custom vertical/horizontal values per render distance
- Added dynamic culling (experimental) — auto-adjusts culling for low render distances
- Fog transitions now also trigger on significant culling changes
- Fixed fog initialization causing visual artifacts on world load

**v1.0.0**
- Initial release
- Dynamic render distance based on average FPS
- Animated fog transitions
- In-game config screen with categories
- Embeddium integration via Mixins

---

## 🇦🇷 Español

**FPS Horizon** es un mod cliente de Forge que ajusta automáticamente la distancia de renderizado según el promedio de FPS, manteniendo el juego fluido sin configuración manual.

### ✨ Características

- **Distancia dinámica** — aumenta o reduce el render distance automáticamente según tus FPS promedio
- **Transiciones de niebla animadas** — niebla suave oculta la carga de chunks durante los cambios
- **Culling de distancia** — reduce la carga de GPU descartando chunks y entidades fuera de un elipsoide
- **Perfiles de culling** — creá configuraciones de culling personalizadas por distancia de renderizado (valor exacto o rango)
- **Culling dinámico** *(experimental)* — ajusta automáticamente el culling según la distancia actual
- **Configuración en tiempo real** — todos los ajustes se pueden cambiar en el juego desde el menú de Mods sin reiniciar
- **Compatible con Embeddium** — integrado completamente con el pipeline de renderizado de Embeddium vía Mixins
- **Liviano** — funciona solo del lado del cliente, no requiere servidor

### 📋 Requisitos

| Dependencia | Versión |
|---|---|
| Forge | Ver releases |
| Embeddium | 0.3.31+ |

### ⚙️ Configuración

Todas las opciones están disponibles en el juego en **Mods → FPS Horizon → Config**.

#### Control de FPS
| Opción | Por defecto | Descripción |
|---|---|---|
| FPS Mínimos | 30 | Si el promedio de FPS baja de este valor, se reduce la distancia |
| FPS Máximos | 50 | Si el promedio de FPS supera este valor, se aumenta la distancia |
| Muestras de FPS | 15 | Cantidad de muestras a promediar antes de decidir un cambio |

#### Distancia de Renderizado
| Opción | Por defecto | Descripción |
|---|---|---|
| Distancia Mínima | 4 chunks | El mod nunca bajará de este valor |
| Distancia Máxima | 12 chunks | El mod nunca superará este valor |

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
| Fin de Niebla | 0.80 | Fracción de la distancia donde la niebla se vuelve opaca |
| Factor de Cierre | 0.80 | Qué tan agresivo es el cierre de niebla durante un cambio |
| Velocidad de Niebla | 0.05 | Velocidad de animación |

#### Culling
| Opción | Por defecto | Descripción |
|---|---|---|
| Activar Culling | true | Activa el culling de chunks y entidades por distancia |
| Escala Vertical | 2.0x | Qué tan agresivamente se descartan chunks arriba/abajo |
| Extensión Horizontal | 10% | Extiende el elipsoide horizontalmente |
| Culling de Entidades | true | Aplica el culling también a entidades |
| Culling Dinámico *(experimental)* | false | Ajusta automáticamente el culling según la distancia actual |
| Perfiles *(experimental)* | false | Usá perfiles de culling personalizados por distancia de renderizado |

#### Perfiles de Culling
Creá perfiles que definen los valores de culling vertical y horizontal para una distancia de renderizado específica (valor exacto o rango). Cuando está activo, los sliders principales se deshabilitan y se usa el perfil que corresponda automáticamente. Los perfiles se guardan en `fps_horizon_profiles.json`.

#### Debug
| Opción | Por defecto | Descripción |
|---|---|---|
| Mostrar cambios de RD | false | Muestra los cambios de distancia en el Action Bar |
| Debug detallado | false | Muestra FPS, estado, cooldown e info de culling en cada tick |

### 🚀 Instalación

1. Instalá [Minecraft Forge](https://files.minecraftforge.net/) para tu versión
2. Instalá [Embeddium 0.3.31+](https://modrinth.com/mod/embeddium)
3. Copiá `fps-horizon-X.X.X.jar` en tu carpeta `mods/`
4. Iniciá el juego y configurá desde **Mods → FPS Horizon → Config**

### 📋 Changelog

**v1.1.0**
- Sistema de culling por distancia (chunks y entidades via elipsoide)
- Perfiles de culling — valores personalizados de vertical/horizontal por distancia de renderizado
- Culling dinámico (experimental) — ajusta automáticamente el culling para distancias bajas
- Las transiciones de niebla ahora también se disparan ante cambios significativos de culling
- Corregido bug de inicialización de niebla que causaba artefactos visuales al cargar el mundo

**v1.0.0**
- Lanzamiento inicial
- Distancia de renderizado dinámica basada en FPS promedio
- Transiciones de niebla animadas
- Pantalla de configuración ingame con categorías
- Integración con Embeddium vía Mixins

---

<p align="center">Made with ❤️ by <a href="https://github.com/Kratos-PalletTown">Kratos</a></p>
