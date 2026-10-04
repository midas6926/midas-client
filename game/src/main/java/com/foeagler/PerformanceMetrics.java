package com.foeagler;

public final class PerformanceMetrics {
    private long frameStartMs;
    private int frameCount = 0;
    private long totalFrameTimeMs = 0;
    private long maxFrameTimeMs = 0;
    private long minFrameTimeMs = Long.MAX_VALUE;

    private int entityRenderCount = 0;
    private int blockEntityRenderCount = 0;
    private int entityCullCount = 0;
    private int blockEntityCullCount = 0;

    public void startFrame() {
        frameStartMs = System.currentTimeMillis();
    }

    public void endFrame() {
        long frameTime = System.currentTimeMillis() - frameStartMs;
        totalFrameTimeMs += frameTime;
        maxFrameTimeMs = Math.max(maxFrameTimeMs, frameTime);
        minFrameTimeMs = Math.min(minFrameTimeMs, frameTime);
        frameCount++;
    }

    public void recordEntityRender(boolean culled) {
        if (culled) {
            entityCullCount++;
        } else {
            entityRenderCount++;
        }
    }

    public void recordBlockEntityRender(boolean culled) {
        if (culled) {
            blockEntityCullCount++;
        } else {
            blockEntityRenderCount++;
        }
    }

    public double getAverageFrameTimeMs() {
        return frameCount == 0 ? 0 : (double) totalFrameTimeMs / frameCount;
    }

    public long getMaxFrameTimeMs() {
        return maxFrameTimeMs;
    }

    public long getMinFrameTimeMs() {
        return minFrameTimeMs == Long.MAX_VALUE ? 0 : minFrameTimeMs;
    }

    public String getReport() {
        return "=== Performance Report ===\n" +
                "Avg frame time: " + String.format("%.2f", getAverageFrameTimeMs()) + " ms\n" +
                "Max frame time: " + getMaxFrameTimeMs() + " ms\n" +
                "Min frame time: " + getMinFrameTimeMs() + " ms\n" +
                "Entities rendered: " + entityRenderCount + ", culled: " + entityCullCount + "\n" +
                "Block entities rendered: " + blockEntityRenderCount + ", culled: " + blockEntityCullCount + "\n";
    }
}
