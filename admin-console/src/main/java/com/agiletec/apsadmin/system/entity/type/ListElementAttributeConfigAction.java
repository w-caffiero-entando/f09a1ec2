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
package com.agiletec.apsadmin.system.entity.type;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;

import com.agiletec.aps.system.common.entity.model.attribute.AbstractListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author E.Santoboni
 */
public class ListElementAttributeConfigAction extends AbstractBaseEntityAttributeConfigAction implements IListElementAttributeConfigAction {

    private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(ListElementAttributeConfigAction.class);

    @Override
    public String configureListElement() {
		this.valueFormFields(this.getAttributeElement());
        return SUCCESS;
    }

    @Override
    public String saveListElement() {
        try {
            AttributeInterface attribute = this.getAttributeElement();
            this.fillAttributeFields(attribute);
            AbstractListAttribute listAttribute = this.getListAttribute();
            listAttribute.setNestedAttributeType(attribute);
            IApsEntity entityType = this.getEntityType();
            entityType.getAttributeMap().put(listAttribute.getName(), listAttribute);
            List<AttributeInterface> attributes = entityType.getAttributeList();
            IntStream.range(0, attributes.size())
                    .filter(i -> attributes.get(i).getName().equals(listAttribute.getName()))
                    .findFirst().ifPresent(i -> attributes.set(i, listAttribute));
            this.updateEntityType(entityType);
            this.getRequest().getSession().removeAttribute(IListElementAttributeConfigAction.LIST_ATTRIBUTE_ON_EDIT_SESSION_PARAM);
            this.getRequest().getSession().removeAttribute(IListElementAttributeConfigAction.LIST_ELEMENT_ON_EDIT_SESSION_PARAM);
        } catch (Throwable t) {
            _logger.error("error in saveListElement", t);
            return FAILURE;
        }
        return SUCCESS;
    }

    public AbstractListAttribute getListAttribute() {
        return (AbstractListAttribute) this.getRequest().getSession().getAttribute(LIST_ATTRIBUTE_ON_EDIT_SESSION_PARAM);
    }

    public AttributeInterface getAttributeElement() {
        return (AttributeInterface) this.getRequest().getSession().getAttribute(LIST_ELEMENT_ON_EDIT_SESSION_PARAM);
    }

}
