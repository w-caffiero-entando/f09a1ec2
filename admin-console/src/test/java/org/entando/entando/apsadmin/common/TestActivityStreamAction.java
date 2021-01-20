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
package org.entando.entando.apsadmin.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.entando.entando.aps.system.services.actionlog.ActionLoggerTestHelper;
import org.entando.entando.aps.system.services.actionlog.IActionLogManager;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.apsadmin.ApsAdminBaseTestCase;
import com.opensymphony.xwork2.Action;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author E.Santoboni
 */
public class TestActivityStreamAction extends ApsAdminBaseTestCase {

    /*
	@Test
    public void testActivityStreamSearchBean() throws Throwable {
		Content content = this._contentManager.loadContent("EVN41", false);//"coach" group
		String contentOnSessionMarker = AbstractContentAction.buildContentOnSessionMarker(content, ApsAdminSystemConstants.ADD);
		content.setId(null);
		String contentId = null;
		Date dateBeforeSave = new Date();
		Thread.sleep(1000);
		try {
			this.getRequest().getSession().setAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT_PREXIX + contentOnSessionMarker, content);
			this.initContentAction("/do/jacms/Content", "save", contentOnSessionMarker);
			this.setUserOnSession("admin");
			String result = this.executeAction();
			assertEquals(Action.SUCCESS, result);
			contentId = content.getId();
			assertNotNull(this._contentManager.loadContent(contentId, false));
			super.waitThreads(IActionLogManager.LOG_APPENDER_THREAD_NAME_PREFIX);
			Date firstDate = new Date();
			ActionLogRecordSearchBean searchBean = this._helper.createSearchBean("admin", null, null, null, null, null);
			List<Integer> ids = this._actionLoggerManager.getActionRecords(searchBean);
			assertEquals(1, ids.size());
			ActionLogRecord firstRecord = this._actionLoggerManager.getActionRecord(ids.get(0));

			ActivityStreamSeachBean activityStreamSeachBean = new ActivityStreamSeachBean();
			activityStreamSeachBean.setEndUpdate(firstDate);
			List<Integer> activityStreamEndDate = this._actionLoggerManager.getActivityStream(activityStreamSeachBean);
			assertEquals(1, activityStreamEndDate.size());

			Thread.sleep(1000);
			this.getRequest().getSession().setAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT_PREXIX + contentOnSessionMarker, content);
			this.initContentAction("/do/jacms/Content", "save", contentOnSessionMarker);
			this.setUserOnSession("admin");
			result = this.executeAction();
			assertEquals(Action.SUCCESS, result);
			contentId = content.getId();
			assertNotNull(this._contentManager.loadContent(contentId, false));
			super.waitThreads(IActionLogManager.LOG_APPENDER_THREAD_NAME_PREFIX);


			activityStreamSeachBean = new ActivityStreamSeachBean();
			activityStreamSeachBean.setEndUpdate(new Date());
			List<Integer> activityStreamBetweenSave2 = this._actionLoggerManager.getActivityStream(activityStreamSeachBean);
			assertEquals(2, activityStreamBetweenSave2.size());

			String firstDateString = DateConverter.getFormattedDate(firstDate, ApsAdminSystemConstants.CALENDAR_TIMESTAMP_PATTERN);
			this.initActivityStreamAction("/do/ActivityStream", "update", firstDateString);
			this.setUserOnSession("admin");
			result = this.executeAction();
			assertEquals(Action.SUCCESS, result);
			ActivityStreamAction activityStreamAction = (ActivityStreamAction) this.getAction();
			List<Integer> update = activityStreamAction.getActionRecordIds();
			assertEquals(1, update.size());
			ActionLogRecord updateRecord = this._actionLoggerManager.getActionRecord(update.get(0));

			String actionRecordDate = DateConverter.getFormattedDate(updateRecord.getActionDate(), ApsAdminSystemConstants.CALENDAR_TIMESTAMP_PATTERN);
			this.initActivityStreamAction("/do/ActivityStream", "viewMore", actionRecordDate);
			this.setUserOnSession("admin");
			result = this.executeAction();
			assertEquals(Action.SUCCESS, result);
			activityStreamAction = (ActivityStreamAction) this.getAction();
			List<Integer> viewMore = activityStreamAction.getActionRecordIds();
			assertEquals(1, viewMore.size());
			assertEquals(firstRecord.getId(), viewMore.get(0).intValue());

		} catch (Throwable t) {
			throw t;
		} finally {
			this._contentManager.deleteContent(content);
			assertNull(this._contentManager.loadContent(contentId, false));
		}
	}
     */
    @Test
    public void testCallAction() throws Throwable {
        this.initActivityStreamAction("/do/ActivityStream", "update", "2012-12-12 12:12:12|121");
        this.setUserOnSession("admin");
        String result = this.executeAction();
        assertEquals(Action.SUCCESS, result);
    }

    protected void initContentAction(String namespace, String name, String contentOnSessionMarker) throws Exception {
        this.initAction(namespace, name);
        this.addParameter("contentOnSessionMarker", contentOnSessionMarker);
    }

    protected void initActivityStreamAction(String namespace, String name, String timestamp) throws Exception {
        this.initAction(namespace, name);
        this.addParameter("timestamp", timestamp);
    }

    @BeforeEach
    private void init() {
        this._actionLoggerManager = (IActionLogManager) this.getService(SystemConstants.ACTION_LOGGER_MANAGER);
        this._pageManager = (IPageManager) this.getService(SystemConstants.PAGE_MANAGER);
        this._langManager = (ILangManager) this.getService(SystemConstants.LANGUAGE_MANAGER);
        //this._contentManager = (IContentManager) this.getService(JacmsSystemConstants.CONTENT_MANAGER);
        this._helper = new ActionLoggerTestHelper(this.getApplicationContext());
        this._helper.cleanRecords();
    }

    @AfterEach
    protected void destroy() throws Exception {
        this._helper.cleanRecords();
    }

    private IActionLogManager _actionLoggerManager;
    private IPageManager _pageManager = null;
    private ILangManager _langManager = null;
    //private IContentManager _contentManager = null;
    private ActionLoggerTestHelper _helper;

}
