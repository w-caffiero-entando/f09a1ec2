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
package com.agiletec.plugins.jacms.aps.system.services.contentpagemapper;

import com.agiletec.aps.system.common.AbstractCacheWrapper;
import com.agiletec.aps.system.common.AbstractService;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.events.PageChangedEvent;
import com.agiletec.aps.system.services.page.events.PageChangedObserver;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.util.ApsTenantApplicationUtils;
import com.agiletec.plugins.jacms.aps.system.services.contentpagemapper.cache.IContentMapperCacheWrapper;
import org.entando.entando.aps.system.services.tenants.RefreshableBeanTenantAware;
import org.entando.entando.ent.exception.EntException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servizio gestore della mappa dei contenuti pubblicati nelle pagine. Il
 * servizio carica e gestisce nella mappa esclusivamente i contenuti pubblicati
 * esplicitamente nel frame principale delle pagine.
 *
 * @author W.Ambu
 */
public class ContentPageMapperManager extends AbstractService implements IContentPageMapperManager, PageChangedObserver,
		RefreshableBeanTenantAware {

	private static final Logger logger = LoggerFactory.getLogger(ContentPageMapperManager.class);

	private transient IPageManager pageManager;
	private transient IPageModelManager pageModelManager;
	private transient IContentMapperCacheWrapper cacheWrapper;

	@Override
	public void init() throws Exception {
		initTenantAware();
		logger.debug("{} ready.", this.getClass().getName());
	}

	@Override
	protected void release() {
		releaseTenantAware();
		super.release();
	}

	@Override
	public void initTenantAware() throws Exception {
		this.getCacheWrapper().initCache(this.getPageManager(), this.getPageModelManager());
	}

	@Override
	public void releaseTenantAware() {
		((AbstractCacheWrapper) this.getCacheWrapper()).release();
	}

	/**
	 * Effettua il caricamento della mappa contenuti pubblicati / pagine
	 *
	 * @throws EntException
	 */
	@Override
	public void reloadContentPageMapper() throws EntException {
		this.getCacheWrapper().initCache(this.getPageManager(), this.getPageModelManager());
	}

	@Override
	public String getPageCode(String contentId) {
		return this.getCacheWrapper().getPageCode(contentId);
	}

	@Override
	public void updateFromPageChanged(PageChangedEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("END - EVENT -> {} - tenant {}", event.getClass(),
					ApsTenantApplicationUtils.getTenant().orElse("primary"));
		}
		try {
			this.reloadContentPageMapper();
			String pagecode = (null != event.getPage()) ? event.getPage().getCode() : "*undefined*";
			logger.debug("Notified page change event for page '{}'", pagecode);
		} catch (Throwable t) {
			logger.error("Error notifying event", t);
		}
	}

	protected IPageManager getPageManager() {
		return pageManager;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

    protected IPageModelManager getPageModelManager() {
        return pageModelManager;
    }
    public void setPageModelManager(IPageModelManager pageModelManager) {
        this.pageModelManager = pageModelManager;
    }

	protected IContentMapperCacheWrapper getCacheWrapper() {
		return cacheWrapper;
	}

	public void setCacheWrapper(IContentMapperCacheWrapper cacheWrapper) {
		this.cacheWrapper = cacheWrapper;
	}

}
