/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.aps.system.services.actionlog;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author E.Santoboni
 */
public class ActivityStreamCleanerThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ActivityStreamCleanerThread.class);

    private final Integer maxActivitySizeByGroup;
    private final IActionLogManager actionLogManager;

    public ActivityStreamCleanerThread(Integer maxActivitySizeByGroup, IActionLogManager actionLogManager) {
        this.maxActivitySizeByGroup = maxActivitySizeByGroup;
        this.actionLogManager = actionLogManager;
    }

    @Override
    public void run() {
        try {
            Set<Integer> ids = this.actionLogManager.extractOldRecords(this.maxActivitySizeByGroup);
            if (null != ids) {
                for (int id : ids) {
                    this.actionLogManager.deleteActionRecord(id);
                }
            }
        } catch (Throwable t) {
            logger.error("Error in run ", t);
        }
    }

}