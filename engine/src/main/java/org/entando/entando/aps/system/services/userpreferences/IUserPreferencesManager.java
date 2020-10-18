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
package org.entando.entando.aps.system.services.userpreferences;

import org.entando.entando.ent.exception.EntException;

public interface IUserPreferencesManager {
	
	UserPreferences getUserPreferences(String username)	throws EntException;
	void addUserPreferences(UserPreferences userPreferences) throws EntException;
	void updateUserPreferences(UserPreferences userPreferences) throws EntException;
	void deleteUserPreferences(String username) throws EntException;
}