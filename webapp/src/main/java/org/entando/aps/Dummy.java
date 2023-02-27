package org.entando.aps;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is here only to provide coverage stats
 */
@Slf4j
public class Dummy {

    boolean halfCoverage = true;

    public void doSomethingUseless() {
        if (!halfCoverage) {
            log.debug("This line won't execute");
            log.debug("This line won't execute either");
            log.debug("This line won't execute either");
            log.debug("This line won't execute either to ensure 50% code coverage");
        }
        log.debug("This line will execute");
    }
}
