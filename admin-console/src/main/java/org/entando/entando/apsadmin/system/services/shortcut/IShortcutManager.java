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
package org.entando.entando.apsadmin.system.services.shortcut;

import java.util.List;


import com.agiletec.aps.system.services.user.UserDetails;

import org.entando.entando.apsadmin.system.services.shortcut.model.Shortcut;
import org.entando.entando.apsadmin.system.services.shortcut.model.UserConfigBean;
import org.entando.entando.ent.exception.EntException;

/**
 * Interface of the manager of Shortcut catalog and user config.
 * @author E.Santoboni
 */
public interface IShortcutManager {
	
	/**
	 * Save a shortcut config of the given user.
	 * The saved config shold be not equals than the given, the invalid position 
	 * (with invalid shortcut code, or with shortcut not allowed to the user) will be emptied.
	 * @param user The user owner of the config to save.
	 * @param config The config to save.
	 * @return The saved config.
	 * @throws EntException In case of error.
	 */
	public String[] saveUserConfig(UserDetails user, String[] config) throws EntException;
	
	/**
	 * Save a shortcut config of the given user.
	 * The saved config shold be not equals than the given, the invalid position 
	 * (with invalid shortcut code, or with shortcut not allowed to the user) will be emptied.
	 * @param user The user owner of the config to save.
	 * @param userConfig The config to save.
	 * @return The saved config.
	 * @throws EntException In case of error.
	 */
	public UserConfigBean saveUserConfigBean(UserDetails user, UserConfigBean userConfig) throws EntException;
	
	/**
	 * Return the size of the box that contains the user shortcuts.
	 * @return The size.
	 */
	public Integer getUserShortcutsMaxNumber();
	
	/**
	 * Return the shorcut config of the given user.
	 * The config contains only the shortcut allowed.
	 * @param user The user that require the config.
	 * @return The config of the given user.
	 * @throws EntException In case of error.
	 */
	public String[] getUserConfig(UserDetails user) throws EntException;
	
	/**
	 * Return the shorcut config of the given user.
	 * The config contains only the shortcut allowed.
	 * @param user The user that require the config.
	 * @return The user config of the given user.
	 * @throws EntException In case of error.
	 */
	public UserConfigBean getUserConfigBean(UserDetails user) throws EntException;
	
	/**
	 * Delete a config by user
	 * @param username The username of the config to delete.
	 * @throws EntException In case of error.
	 */
	public void deleteUserConfig(String username) throws EntException;
	
	/**
	 * Return the list of the allowed shortcuts to the given user.
	 * @param user The user making the request
	 * @return The list of the allowed shortcut
	 * @throws EntException In case of error.
	 */
	public List<Shortcut> getAllowedShortcuts(UserDetails user) throws EntException;
	
	/**
	 * Return a shortcut by code.
	 * @param code The code of the shortcut to return.
	 * @return The searched shortcut.
	 */
	public Shortcut getShortcut(String code);
	
	public static final String SHORTCUT_INSPECT_DIRS = "/WEB-INF/apsadmin/shortcut.xml,/WEB-INF/**/**/apsadmin/shortcut.xml";
	
}