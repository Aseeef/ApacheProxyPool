package com.github.Aseeef.proxy;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter(onMethod_ = {@Synchronized}) @Setter(onMethod_ = {@Synchronized})
public class ProxyMeta {

    public ProxyMeta(ProxyCredentials credentials) {
        this.credentials = credentials;
    }

    /**
     * The credentials for this proxy (may be null).
     */
    private final ProxyCredentials credentials;

    /**
     * User defined metadata information for this proxy
     */
    private Map<String, Object> metadata = new ConcurrentHashMap<>();

    /**
     * The time at which this proxy was taken from the pool in epoch millis. A value of -1 indicates that this proxy is currently with in the pool.
     */
    private volatile long timeTaken = -1L;

    /**
     * The reference from the stack of who took the connection
     */
    private volatile StackTraceElement[] stackBorrower = null;

    /**
     * The last time that this proxy was validated to be working in epoch millis. A value of null indicates that this proxy has never been tested.
     */
    private volatile ProxyHealthReport latestHealthReport = null;

    /**
     * Whether this proxy is a leaked proxy
     */
    private volatile boolean leaked = false;

    /**
     * Whether this proxy is currently being inspected. Inspected proxies cannot be leaked.
     */
    private volatile boolean inspecting = false;

    /**
     * @return true if in the pool and false otherwise
     */
    public synchronized boolean isInPool() {
        return this.timeTaken == -1 && this.isAlive();
    }

    /**
     * @return whether this proxy is alive based on the latest tests. If no health report tests are available, defaults to true.
     */
    public synchronized boolean isAlive() {
        return this.latestHealthReport == null || this.latestHealthReport.isAlive();
    }

    public Optional<ProxyCredentials> getCredentials() {
        return Optional.ofNullable(credentials);
    }
}
