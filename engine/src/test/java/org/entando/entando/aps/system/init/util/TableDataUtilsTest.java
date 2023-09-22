/*
 * Copyright 2017-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.init.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.agiletec.aps.util.FileTextReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import javax.sql.DataSource;
import org.entando.entando.aps.system.init.model.TableDumpReport;
import org.entando.entando.ent.exception.EntException;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

/**
 * @author E.Santoboni
 */
class TableDataUtilsTest {

    @Test
	public void dumpTableShouldWorkWithLongForBigIntType() throws EntException, SQLException, IOException {
		// --GIVEN
		DataSource mockedDataSource = Mockito.mock(DataSource.class, Answers.RETURNS_DEEP_STUBS);
		when(mockedDataSource.getConnection().createStatement().executeQuery(anyString()).getMetaData().getColumnCount()).thenReturn(
				1);
		when(mockedDataSource.getConnection().createStatement().executeQuery(anyString()).getMetaData().getColumnType(anyInt())).thenReturn(
				Types.BIGINT);
		when(mockedDataSource.getConnection().createStatement().executeQuery(anyString()).getMetaData().getColumnName(anyInt())).thenReturn(
				"columnName");
		when(mockedDataSource.getConnection().createStatement().executeQuery(anyString()).next()).thenReturn(
				true).thenReturn(false);
		when(mockedDataSource.getConnection().createStatement().executeQuery(anyString()).getObject(anyInt())).thenReturn(
				42L);
		// input variables needed by the subject under test to write out the table dump
		StringWriter out = new StringWriter();
		BufferedWriter br = new BufferedWriter(out);

		// --WHEN
		TableDumpReport dumpReport = TableDataUtils.dumpTable(br, mockedDataSource, "fakeTable");
		// we need to flush buffer to retrieve the written output from StringWriter
		br.flush();

		// --THEN
		assertEquals(1,dumpReport.getRows());
		assertEquals("INSERT INTO fakeTable (columnname) VALUES (42);\n", out.toString());
	}
    @Test
	public void dumpTableShouldWorkWithNullForBigIntType() throws EntException, SQLException, IOException {
		// --GIVEN
		DataSource mockedDataSource = Mockito.mock(DataSource.class, Answers.RETURNS_DEEP_STUBS);
		when(mockedDataSource.getConnection().createStatement().executeQuery(anyString()).getMetaData().getColumnCount()).thenReturn(
				1);
		when(mockedDataSource.getConnection().createStatement().executeQuery(anyString()).getMetaData().getColumnType(anyInt())).thenReturn(
				Types.BIGINT);
		when(mockedDataSource.getConnection().createStatement().executeQuery(anyString()).getMetaData().getColumnName(anyInt())).thenReturn(
				"columnName");
		when(mockedDataSource.getConnection().createStatement().executeQuery(anyString()).next()).thenReturn(
				true).thenReturn(false);

		// input variables needed by the subject under test to write out the table dump
		StringWriter out = new StringWriter();
		BufferedWriter br = new BufferedWriter(out);

		// --WHEN
		TableDumpReport dumpReport = TableDataUtils.dumpTable(br, mockedDataSource, "fakeTable");
		// we need to flush buffer to retrieve the written output from StringWriter
		br.flush();

		// --THEN
		assertEquals(1,dumpReport.getRows());
		assertEquals("INSERT INTO fakeTable (columnname) VALUES (NULL);\n", out.toString());
	}
}
