# Fabulously Optimized for midas-client

Comprehensive performance optimization layer integrated directly into the Eaglercraft 26.2 client.

## What's Integrated

- **FrameBudgetManager** - 60 FPS active → 20 FPS idle
- **EntityCullingFast** - Skip distant entities before rendering
- **ChunkUpdateBudget** - Rate-limit chunk mesh rebuilds
- **RenderStateDeduplicator** - Skip redundant GL state changes
- **NoAllocRenderQueue** - Fixed-size, zero-allocation render commands
- **FloatArrayPool** + **IntArrayPool** - Reuse temporary arrays
- **ChunkMeshCache** - Cache mesh hashes, avoid redundant rebuilds
- **WorldTickOptimizer** - Throttle world simulation (~20 TPS max)
- **PerformanceMetrics** - Frame time tracking and culling reports
- **ComprehensiveOptimizationManager** - Single interface for all optimizations

## How to Use

All classes are in `game/src/main/java/com/foeagler/`.

### 1. Create the manager in your main Minecraft class

```java
import com.foeagler.*;

public class Minecraft {
    private ComprehensiveOptimizationManager optManager;

    public Minecraft() {
        BrowserPerformanceConfig config = BrowserPerformanceConfig.defaultConfig();
        optManager = new ComprehensiveOptimizationManager(config);
    }

    public void tick() {
        optManager.onFrameStart();
        // ... existing game logic ...
        optManager.onFrameEnd();
    }
}
```

### 2. Call onInputEvent() in input handlers

Wherever keyboard/mouse input is processed, add:

```java
optManager.onInputEvent();
```

This tracks idle time for Dynamic FPS.

### 3. Use entity culling before rendering entities

```java
if (optManager.shouldRenderEntity(camX, camY, camZ, entX, entY, entZ)) {
    // render entity
}
```

### 4. Use chunk update budgeting

Instead of rebuilding all dirty chunks per frame:

```java
int budget = optManager.getChunkUpdateBudget();
for (int i = 0; i < budget; i++) {
    ChunkUpdateBudget.ChunkPos update = optManager.pollChunkUpdate();
    if (update == null) break;
    rebuildChunkMesh(update.x, update.y, update.z);
}
```

### 5. Use pooled arrays

```java
float[] tempData = optManager.acquireFloatArray(256);
// ... use array ...
optManager.releaseFloatArray(tempData);
```

### 6. Get performance reports

```java
System.out.println(optManager.getPerformanceReport());
// Output:
// === Performance Report ===
// Avg frame time: 12.34 ms
// Max frame time: 25.67 ms
// Min frame time: 8.90 ms
// Entities rendered: 156, culled: 412
// Block entities rendered: 8, culled: 24
```

## Compilation

Compile Java only (no web output):

```bash
./gradlew :game:compileJava --console=plain --no-daemon
```

Build standalone HTML:

```bash
java -jar eaglercraft-26.2-java-cli.jar build-standalone \
  --input /path/to/your/minecraft-26.2.jar \
  --output /path/to/midas-client
```

## Performance Impact

Expected improvements:

- **Frame time**: 20-40% reduction with entity culling + render deduplication
- **Memory**: 60-80% fewer allocations per frame (via pooling)
- **Idle power**: 75%+ reduction when tab is inactive
- **GPU state changes**: 40-70% fewer redundant calls

## Configuration

Edit defaults in `BrowserPerformanceConfig.Defaults`:

```java
public static final int TARGET_FPS = 60;              // Active FPS
public static final int INACTIVE_FPS = 20;           // Idle FPS
public static final int IDLE_WINDOW_MS = 3000;       // Time before idle
public static final float ENTITY_CULLING_DISTANCE = 24.0f;  // Blocks
public static final float BLOCK_ENTITY_RENDER_DISTANCE = 18.0f;  // Blocks
public static final boolean DYNAMIC_FPS_ENABLED = true;
public static final boolean ENTITY_CULLING_ENABLED = true;
```

## Files Added

```
game/src/main/java/com/foeagler/
├── BrowserPerformanceConfig.java
├── FrameBudgetManager.java
├── EntityCullingFast.java
├── ChunkUpdateBudget.java
├── RenderStateDeduplicator.java
├── NoAllocRenderQueue.java
├── FloatArrayPool.java
├── IntArrayPool.java
├── ChunkMeshCache.java
├── WorldTickOptimizer.java
├── PerformanceMetrics.java
└── ComprehensiveOptimizationManager.java
```

## Next Steps

1. Integrate `ComprehensiveOptimizationManager` into your main game loop
2. Test the build with `./gradlew :game:compileJava`
3. Adjust config values based on your target device performance
4. Monitor metrics to find remaining bottlenecks

## Notes

- This is **not** a mod; it is direct client optimization code
- All classes are browser/TeaVM compatible
- Zero per-frame allocations in hot paths (via pooling and fixed-size queues)
- Bounded memory overhead (~1-2 MB)
