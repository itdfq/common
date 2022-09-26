package com.itdfq.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author duanfangqin 2022/9/26 15:22
 * @implNote
 */
public class NamedThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber;
    private final String namePrefix;
    private final boolean daemon;

    public NamedThreadFactory(String namePrefix, boolean daemon) {
        this.threadNumber = new AtomicInteger(1);
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
    }

    public NamedThreadFactory(String namePrefix) {
        this(namePrefix, false);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, this.namePrefix + "-thread-" + this.threadNumber.getAndIncrement(), 0L);
        t.setDaemon(this.daemon);
        return t;
    }
}
