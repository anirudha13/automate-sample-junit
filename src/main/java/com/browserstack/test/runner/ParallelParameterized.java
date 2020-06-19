package com.browserstack.test.runner;

import org.junit.runners.Parameterized;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Anirudha Khanna
 */
public class ParallelParameterized extends Parameterized {

    /**
     * Only called reflectively. Do not use programmatically.
     *
     * @param klass
     */
    public ParallelParameterized(Class<?> klass) throws Throwable {
        super(klass);
        setScheduler(new ParallelScheduler());
    }
}
