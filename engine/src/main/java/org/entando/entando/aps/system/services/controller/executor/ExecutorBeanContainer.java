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
package org.entando.entando.aps.system.services.controller.executor;

import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.TemplateModel;

/**
 * Object that contains helpers, template service objects (and so on) used by the executors services.
 *
 * @author E.Santoboni
 */
public class ExecutorBeanContainer {

    private final Configuration configuration;
    private final TemplateModel templateModel;

    public ExecutorBeanContainer(Configuration configuration, TemplateModel templateModel) {
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
        this.configuration = configuration;
        this.templateModel = templateModel;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * WARNING: As stated in FreeMarker documentation, Template instances and data-models should be handled as immutable
     * (read-only) objects, so the model retrieved using this method should be used only by the template.process()
     * method and its properties mustn't be updated after its creation.
     *
     * @return the template model
     * @see https://freemarker.apache.org/docs/pgui_misc_multithreading.html
     */
    public TemplateModel getTemplateModel() {
        return templateModel;
    }
}
