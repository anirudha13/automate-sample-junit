package com.browserstack.test.runner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.model.RunnerScheduler;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public class ParallelScheduler implements RunnerScheduler {

    private final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private final ExecutorService executorService;

    public ParallelScheduler() {
        String numThreadsStr = System.getProperty("junit.parallelism");
        int parallelism = DEFAULT_THREADS;
        if (numThreadsStr != null && !numThreadsStr.isEmpty()) {
            parallelism = Integer.valueOf(numThreadsStr);
        }
        this.executorService = Executors.newWorkStealingPool(parallelism);
    }

    /**
     * Schedule a child statement to run
     *
     * @param childStatement
     */
    @Override
    public void schedule(Runnable childStatement) {
        this.executorService.submit(childStatement);
    }

    /**
     * Override to implement any behavior that must occur
     * after all children have been scheduled (for example,
     * waiting for them all to finish)
     */
    @Override
    public void finished() {
        try {
            this.executorService.awaitTermination(3, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        this.executorService.shutdownNow();
    }
}
