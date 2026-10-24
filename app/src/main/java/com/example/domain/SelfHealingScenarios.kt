package com.example.domain

object SelfHealingScenarios {

    data class FaultScenario(
        val incidentCode: String,
        val faultType: String,
        val affectedModule: String,
        val description: String,
        val rootCause: String,
        val patchDiff: String,
        val recoveryTimeMs: Long,
        val verificationSuite: String
    )

    val SCENARIOS = listOf(
        FaultScenario(
            incidentCode = "ERR-9104: HEAP_LEAK_UNBOUNDED_GROWTH",
            faultType = "Memory Leak",
            affectedModule = "TelemetryBuffer.kt",
            description = "Unbounded ByteBuffer accumulation in telemetry queue causing rapid JVM heap exhaustion.",
            rootCause = "Telemetry queue retained ByteBuffers without eviction bounds, preventing garbage collection.",
            patchDiff = """
--- a/TelemetryBuffer.kt
+++ b/TelemetryBuffer.kt
@@ -14,6 +14,9 @@
-    private val buffer = mutableListOf<ByteBuffer>()
+    private val buffer = EvictingQueue<ByteBuffer>(maxCapacity = 256)
     fun append(bytes: ByteBuffer) {
-        buffer.add(bytes)
+        val evicted = buffer.enqueue(bytes)
+        evicted?.clear() // Release native off-heap buffer immediately
     }
""".trimIndent(),
            recoveryTimeMs = 1420L,
            verificationSuite = "12/12 LeakSanitizer checks passed. Heap dropped from 94.2MB to 16.8MB."
        ),

        FaultScenario(
            incidentCode = "ERR-4412: THREAD_DEADLOCK_MUTEX_INVERSION",
            faultType = "Concurrency Deadlock",
            affectedModule = "NeuralStateSync.kt",
            description = "Deadlock between state synchronization thread and background logger on Dispatchers.Default.",
            rootCause = "Cyclic lock inversion between primaryLock and secondaryLock during state commit.",
            patchDiff = """
--- a/NeuralStateSync.kt
+++ b/NeuralStateSync.kt
@@ -21,7 +21,5 @@
-    fun syncState() = runBlocking {
-        primaryLock.withLock {
-            secondaryLock.withLock { commit() }
-        }
-    }
+    suspend fun syncState() {
+        atomicState.compareAndSet(currentState, nextState)
+    }
""".trimIndent(),
            recoveryTimeMs = 860L,
            verificationSuite = "Zero deadlocks across 2,000 coroutine iterations. Lockless atomic CAS verified."
        ),

        FaultScenario(
            incidentCode = "ERR-1092: BUFFER_OVERFLOW_NPE",
            faultType = "Buffer Overflow & NPE",
            affectedModule = "NetworkFrameDecoder.kt",
            description = "Network frame exceeding 64KB causes memory boundary violation returning null buffer handle.",
            rootCause = "Missing input size bounds validation before allocating direct native frame.",
            patchDiff = """
--- a/NetworkFrameDecoder.kt
+++ b/NetworkFrameDecoder.kt
@@ -31,4 +31,7 @@
-    val frame = allocateDirect(header.length)
-    return frame.readBytes()
+    if (header.length > MAX_FRAME_SIZE || header.length <= 0) {
+        throw MalformedFrameException("Payload length out of bounds: ${'$'}{header.length}")
+    }
+    return bufferPool.acquireSafely(header.length)
""".trimIndent(),
            recoveryTimeMs = 1100L,
            verificationSuite = "15/15 boundary fuzz checks passed. Malformed frames quarantined safely."
        ),

        FaultScenario(
            incidentCode = "ERR-7720: HYDRATION_DIFF_MISMATCH",
            faultType = "React State Sync",
            affectedModule = "JarvisDashboardWidget.tsx",
            description = "SSR hydration mismatch error in UI widget causing full client-side DOM re-render tree thrashing.",
            rootCause = "Non-deterministic Date.now() executed during server render differs from client snapshot.",
            patchDiff = """
--- a/JarvisDashboardWidget.tsx
+++ b/JarvisDashboardWidget.tsx
@@ -8,3 +8,5 @@
-  const timeString = new Date().toLocaleTimeString();
+  const [timeString, setTimeString] = useState<string | null>(null);
+  useEffect(() => {
+    setTimeString(new Date().toLocaleTimeString());
+  }, []);
""".trimIndent(),
            recoveryTimeMs = 620L,
            verificationSuite = "Zero SSR reconciliation warnings. Smooth 60fps client hydration confirmed."
        ),

        FaultScenario(
            incidentCode = "ERR-5531: RATE_BURST_STARVATION",
            faultType = "Token Starvation",
            affectedModule = "RateLimiter.ts",
            description = "Integer division truncation causing token replenishment to freeze under micro-bursts.",
            rootCause = "Rounding Math.floor(elapsedSeconds) caused fractional token refills to truncate to 0.",
            patchDiff = """
--- a/RateLimiter.ts
+++ b/RateLimiter.ts
@@ -18,3 +18,3 @@
-  const tokens = Math.floor(elapsedSeconds) * this.rate;
+  const tokens = elapsedSeconds * this.rate;
""".trimIndent(),
            recoveryTimeMs = 740L,
            verificationSuite = "Refill continuous flow verified. Zero throughput starvation under 10k bursts."
        )
    )
}
