# FPS Horizon — Port a Fabric (1.21.1)

Port del mod `fps-horizon` (rama `1.21.1-Neoforge` de
[Kratos-PalletTown/fps-horizon](https://github.com/Kratos-PalletTown/fps-horizon))
de NeoForge a Fabric Loom.

## ⚠️ Importante antes de compilar

Este proyecto fue escrito y revisado a mano, **pero no fue compilado** en el
entorno donde se generó (sin acceso a los repositorios Maven de Fabric/Mojang/
Modrinth/Cloth Config). Antes de hacer un build de producción:

1. Corré `./gradlew build` localmente y revisá los errores de compilación,
   si los hay (mappings/versión de alguna API pueden haber cambiado).
2. Verificá que la versión de Sodium en `build.gradle` (`sodium_version` /
   la línea `modImplementation "maven.modrinth:sodium:..."`) sea la última
   disponible para 1.21.1 en https://modrinth.com/mod/sodium/versions
3. Verificá versiones de Fabric API, Fabric Loader, Mod Menu y Cloth Config
   en `gradle.properties` contra sus páginas de Modrinth/Maven — quedaron
   fijadas a las últimas que conozco, pero pueden haber salido versiones
   nuevas.

## Qué cambió respecto a la versión NeoForge

| NeoForge | Fabric | Archivo |
|---|---|---|
| `@Mod("fpshorizon")` + `ModContainer`/`IEventBus` | `ClientModInitializer` | `FpsHorizonClient.java` |
| `NeoForge.EVENT_BUS.register(...)` por cada sistema | Un solo `ClientTickEvents.END_CLIENT_TICK`, `KratosOptimizer.tick()` orquesta todo | `FpsHorizonClient.java`, `KratosOptimizer.java` |
| `ModConfigSpec` (.toml) | Config propio en JSON (`config/fpshorizon.json`) vía Gson, con un wrapper `ConfigValue<T>` que imita el `.get()`/`.set()` de NeoForge | `KratosConfig.java` |
| `IConfigScreenFactory` (botón "Config" de NeoForge) | Mod Menu (`ModMenuApi`) + pantalla generada con **Cloth Config** | `KratosModMenuIntegration.java` |
| Pantalla de config hecha a mano (`KratosConfigScreen`) | Reemplazada por Cloth Config (pedido explícito) | *(eliminada, ver abajo)* |
| `ViewportEvent.RenderFog` | Mixin a `FogRenderer.setupFog` (TAIL) | `mixin/KratosFogMixin.java`, `KratosFog.applyFog()` |
| `AccessTransformer` (`lastViewDistance` → public) | Mixin `@Accessor` | `mixin/KratosLevelRendererAccessor.java` |
| `FMLPaths.CONFIGDIR` | `FabricLoader.getInstance().getConfigDir()` | `KratosProfiles.java`, `KratosConfig.java` |
| Los demás mixins (Sodium, culling, packets, F3+A, etc.) | **Sin cambios** — Sodium usa el mismo paquete `net.caffeinemc.mods.sodium` en Fabric y en su build para NeoForge, y Fabric Loom remapea los mixins a vanilla automáticamente | `mixin/*.java` |

### La vieja pantalla de configuración (`KratosConfigScreen.java`)

No se portó tal cual: reemplazada enteramente por **Cloth Config**
(`KratosModMenuIntegration.java`), como pediste. Las dos clases de sliders
custom que usaba internamente (`PercentSlider`, `CullingVerticalSlider`) sí
se conservan — las sigue usando `KratosProfilesScreen`, que se mantiene
como una pantalla vanilla normal — y quedaron extraídas a
`KratosSliderWidgets.java`.

### Acceso a la pantalla de Perfiles

Cloth Config no está pensado para alojar una sub-pantalla completa con
listas/CRUD dentro de su propia lista de opciones, así que en vez de un
botón embebido, `KratosProfilesScreen` se abre con un keybind dedicado
(sin bindear por defecto, configurable en *Controles → FPS Horizon*),
igual que la pantalla principal de config. Ambas también quedan
accesibles llamando a `KratosModMenuIntegration.buildConfigScreen(...)`
o `new KratosProfilesScreen(...)` desde código si preferís otra UX
(por ejemplo un botón dentro de un HUD custom).

## Dependencias en tiempo de ejecución

- **Fabric API** (obligatoria)
- **Sodium** (obligatoria — varios mixins apuntan a sus clases internas)
- **Mod Menu** (opcional, recomendada — sin ella no hay botón de config
  visible, pero podés seguir abriendo las pantallas con los keybinds)
- **Cloth Config** (obligatoria si tenés Mod Menu instalado, ya que la
  pantalla de settings depende de ella)

## Build

```bash
./gradlew build
```

El `.jar` queda en `build/libs/`.

## Estructura

```
src/main/java/pueblopaleta/
  FpsHorizonClient.java          -- entrypoint (ClientModInitializer)
  KratosModMenuIntegration.java  -- pantalla Cloth Config + ModMenuApi
  KratosConfig.java              -- config JSON (reemplaza ModConfigSpec)
  KratosOptimizer.java           -- máquina de estados / orquestador
  KratosFog.java                 -- niebla animada
  KratosCulling.java             -- culling elipsoidal
  KratosSimulation.java          -- simulation distance dinámico
  KratosChunkRetainer.java       -- retiene chunks al subir RD
  KratosDebug.java                -- mensajes de debug
  KratosProfiles.java            -- perfiles de culling (persistencia)
  KratosProfilesScreen.java      -- UI de gestión de perfiles
  KratosSliderWidgets.java       -- sliders custom reutilizados por Profiles
  mixin/                         -- todos los mixins (casi todos sin cambios)
```
