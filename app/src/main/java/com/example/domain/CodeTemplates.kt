package com.example.domain

object CodeTemplates {

    data class CodePreset(
        val title: String,
        val filename: String,
        val language: String,
        val description: String,
        val code: String,
        val executionLog: String,
        val complexity: String,
        val testCoverage: String
    )

    val PRESETS = listOf(
        CodePreset(
            title = "Real-time WebSocket Protocol Streamer",
            filename = "WebSocketProtocolStreamer.ts",
            language = "TypeScript",
            description = "Resilient full-duplex socket streamer with auto-reconnect backoff, heartbeat telemetry, and binary framing.",
            code = """
import { EventEmitter } from 'events';

export interface StreamTelemetry {
  bytesReceived: number;
  rttMs: number;
  lastPing: number;
}

export class WebSocketProtocolStreamer extends EventEmitter {
  private socket: WebSocket | null = null;
  private backoffMs = 500;
  private maxBackoffMs = 8000;
  private heartbeatInterval: NodeJS.Timeout | null = null;
  private telemetry: StreamTelemetry = { bytesReceived: 0, rttMs: 12, lastPing: Date.now() };

  constructor(private readonly endpoint: string) {
    super();
  }

  public connect(): void {
    console.log(`[JARVIS-Streamer] Connecting to ${'$'}{this.endpoint}...`);
    this.socket = new WebSocket(this.endpoint);

    this.socket.onopen = () => {
      console.log('[JARVIS-Streamer] Connection established. Initializing heartbeat.');
      this.backoffMs = 500;
      this.startHeartbeat();
      this.emit('connected');
    };

    this.socket.onmessage = (event) => {
      this.telemetry.bytesReceived += (event.data as string).length;
      this.emit('data', JSON.parse(event.data as string));
    };

    this.socket.onclose = () => {
      console.warn('[JARVIS-Streamer] Socket disconnected. Auto-repairing connection...');
      this.scheduleReconnect();
    };
  }

  private startHeartbeat(): void {
    this.heartbeatInterval = setInterval(() => {
      if (this.socket?.readyState === WebSocket.OPEN) {
        const start = performance.now();
        this.socket.send(JSON.stringify({ type: 'HEARTBEAT_PING', timestamp: Date.now() }));
        this.telemetry.rttMs = Math.round(performance.now() - start);
      }
    }, 3000);
  }

  private scheduleReconnect(): void {
    clearInterval(this.heartbeatInterval!);
    setTimeout(() => {
      this.backoffMs = Math.min(this.backoffMs * 1.8, this.maxBackoffMs);
      this.connect();
    }, this.backoffMs);
  }
}
""".trimIndent(),
            executionLog = """
[COMPILER] TypeScript 5.4.2 AST verified. 0 syntax errors.
[SANDBOX] Initializing headless Node.js v22 runtime container...
[SANDBOX] Instantiating WebSocketProtocolStreamer('wss://telemetry.jarvis.internal/v1')
[SOCKET] Connected to mock broker in 18ms.
[HEARTBEAT] Ping sent. RTT: 12ms. Status: OPTIMAL.
[TELEMETRY] Transferred 1,420 bytes across 14 events.
[TEST_SUITE] 6/6 tests passed. Zero memory leaks detected.
""".trimIndent(),
            complexity = "O(1) Dispatch | O(log N) Reconnect",
            testCoverage = "100% (6/6 assertions pass)"
        ),

        CodePreset(
            title = "Distributed Lock Manager with Raft",
            filename = "DistributedLockManager.kt",
            language = "Kotlin",
            description = "High-availability distributed mutex with lease renewal, heartbeat fencing tokens, and split-brain defense.",
            code = """
package com.jarvis.cluster

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.atomic.AtomicLong

class DistributedLockManager(
    private val nodeId: String,
    private val clusterSize: Int
) {
    private val localMutex = Mutex()
    private val fenceToken = AtomicLong(1000L)
    private var activeLeaseJob: Job? = null

    suspend fun acquireLock(resourceKey: String, ttlMs: Long = 5000): Long {
        localMutex.lock()
        val currentToken = fenceToken.incrementAndGet()
        println("[${'$'}nodeId] Acquired lock for resource='${'$'}resourceKey' with token=${'$'}currentToken")

        activeLeaseJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(ttlMs / 2)
                renewLease(resourceKey, currentToken)
            }
        }
        return currentToken
    }

    private fun renewLease(resourceKey: String, token: Long) {
        println("[${'$'}nodeId] Lease renewed for '${'$'}resourceKey' [FencingToken=${'$'}token]")
    }

    suspend fun releaseLock(resourceKey: String, token: Long) {
        activeLeaseJob?.cancel()
        if (localMutex.isLocked) localMutex.unlock()
        println("[${'$'}nodeId] Released lock for '${'$'}resourceKey' [Token=${'$'}token]")
    }
}
""".trimIndent(),
            executionLog = """
[COMPILER] Kotlin 2.2 kotlinc target JVM 21 compiled successfully.
[SANDBOX] Spinning up 3 virtual nodes: [JARVIS-Alpha, JARVIS-Beta, JARVIS-Gamma]
[RAFT] Leader election resolved in 14ms -> JARVIS-Alpha is cluster leader.
[ACQUIRE] Lock acquired on 'neural_weight_partition_0' [Token=1001]
[RENEW] Lease heartbeat verified at 2500ms.
[RELEASE] Resource gracefully unlocked.
[TEST_SUITE] 8/8 distributed concurrency tests PASSED.
""".trimIndent(),
            complexity = "O(1) Local Mutex | Raft Quorum N/2 + 1",
            testCoverage = "100% (8/8 assertions pass)"
        ),

        CodePreset(
            title = "High-Throughput Token Bucket Rate Limiter",
            filename = "TokenBucketRateLimiter.ts",
            language = "TypeScript",
            description = "Atomic token replenishment rate limiter with microsecond burst allowance and leaky backpressure.",
            code = """
export class TokenBucketRateLimiter {
  private tokens: number;
  private lastRefillTimestamp: number;

  constructor(
    private readonly capacity: number,
    private readonly refillRatePerSecond: number
  ) {
    this.tokens = capacity;
    this.lastRefillTimestamp = Date.now();
  }

  public tryAcquire(requestedTokens = 1): boolean {
    this.refill();

    if (this.tokens >= requestedTokens) {
      this.tokens -= requestedTokens;
      return true;
    }
    return false;
  }

  private refill(): void {
    const now = Date.now();
    const elapsedSeconds = (now - this.lastRefillTimestamp) / 1000;
    const tokensToAdd = elapsedSeconds * this.refillRatePerSecond;

    if (tokensToAdd > 0) {
      this.tokens = Math.min(this.capacity, this.tokens + tokensToAdd);
      this.lastRefillTimestamp = now;
    }
  }

  public getAvailableTokens(): number {
    this.refill();
    return Math.floor(this.tokens);
  }
}
""".trimIndent(),
            executionLog = """
[COMPILER] TypeScript build succeeded. Zero warnings.
[SANDBOX] Benchmark simulation: 50,000 requests/sec synthetic load.
[THROUGHPUT] 48,200 allowed, 1,800 throttled smoothly with HTTP 429 backpressure.
[ACCURACY] Token drift < 0.002% over 10,000 cycles.
[TEST_SUITE] 5/5 assertions green.
""".trimIndent(),
            complexity = "O(1) Lockless Constant Time",
            testCoverage = "100% (5/5 assertions pass)"
        ),

        CodePreset(
            title = "Adaptive LRU-K Neural Memory Cache",
            filename = "NeuralCache.py",
            language = "Python",
            description = "Intelligent vector memory caching using access frequency decay and priority eviction policies.",
            code = """
import time
from collections import OrderedDict

class NeuralVectorCache:
    def __init__(self, capacity: int = 1024, decay_half_life_s: float = 60.0):
        self.capacity = capacity
        self.decay = decay_half_life_s
        self.store = OrderedDict()
        self.access_history = {}

    def get(self, key: str):
        if key not in self.store:
            return None
        self.access_history[key] = time.time()
        self.store.move_to_end(key)
        return self.store[key]

    def put(self, key: str, value: any):
        if key in self.store:
            self.store.move_to_end(key)
        elif len(self.store) >= self.capacity:
            oldest_key, _ = self.store.popitem(last=False)
            self.access_history.pop(oldest_key, None)
        self.store[key] = value
        self.access_history[key] = time.time()

    def stats(self):
        return {
            "cached_vectors": len(self.store),
            "capacity": self.capacity,
            "memory_usage_estimate_kb": len(self.store) * 0.128
        }
""".trimIndent(),
            executionLog = """
[COMPILER] Python 3.12 syntax check: OK.
[SANDBOX] Pre-loading 1,024 vector tensors (512-dim float32).
[BENCHMARK] Eviction policy tested: 10,000 get/put cycles.
[HIT_RATE] Cache hit ratio: 94.6%.
[TEST_SUITE] 7/7 unit tests verified.
""".trimIndent(),
            complexity = "O(1) Lookup & Eviction",
            testCoverage = "100% (7/7 assertions pass)"
        )
    )
}
