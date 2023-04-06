/*
 * Copyright 2015-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.init;

import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.entando.entando.aps.system.init.IInitializerManager.DatabaseMigrationStrategy;
import org.entando.entando.aps.system.init.model.SystemInstallationReport;

/**
 * @author E.Santoboni
 */
public interface IDatabaseInstallerManager {
	
	public SystemInstallationReport installDatabase(SystemInstallationReport report,
			DatabaseMigrationStrategy defaultMigrationStrategy,
			Optional<Map<String, DataSource>> datasources) throws Exception;
	
}
