package com.browserstack.test.runner;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.RunnerScheduler;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public class ParallelScheduler implements RunnerScheduler {

    private static final Logger LOGGER = LogManager.getLogger(ParallelScheduler.class);

    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final int WAIT_INTERVAL_MS = 500;
    private static final int MAX_WAIT_TRIES = 10;

    private final ExecutorService executorService;
    private final List<Future<?>> submittedTasks = new LinkedList<>();

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
        this.submittedTasks.add(this.executorService.submit(childStatement));
    }

    /**
     * Override to implement any behavior that must occur
     * after all children have been scheduled (for example,
     * waiting for them all to finish)
     */
    @Override
    public void finished() {

        int numTries = 0;
        try {
            while (!this.submittedTasks.isEmpty() || numTries < MAX_WAIT_TRIES) {
                Thread.sleep(WAIT_INTERVAL_MS);
                this.submittedTasks.removeIf(Future::isDone);
                numTries++;
            }
        } catch (InterruptedException iex) {
            LOGGER.warn("Caught exception while waiting for task completion :: ", iex);
        }

        try {
            this.executorService.shutdown();
            if (!this.executorService.isTerminated()) {
                this.executorService.awaitTermination(3, TimeUnit.MINUTES);
            }
        } catch (InterruptedException e) {
            throw new Error(e);
        }
        this.executorService.shutdownNow();
    }
}
