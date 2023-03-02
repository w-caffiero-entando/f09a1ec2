/*
 * Copyright 2022-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.util;

import static org.entando.entando.aps.util.UrlUtils.ENTANDO_APP_USE_TLS;
import static org.entando.entando.aps.util.UrlUtils.HTTPS_SCHEME;
import static org.entando.entando.aps.util.UrlUtils.HTTP_SCHEME;
import static org.entando.entando.aps.util.UrlUtils.fetchServerNameFromUri;
import static org.mockito.Mockito.when;

import com.google.common.net.HttpHeaders;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.entando.entando.aps.util.UrlUtils.EntUrlBuilder;
import org.entando.entando.test_utils.UnitTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

class LogUtilsTest {
    @Test
    void shouldCleanupDataForLogWorksFine() {
        final String data = "test";
        Assertions.assertEquals(data+"__", LogUtils.cleanupDataForLog(data+"\n\r"));
        Assertions.assertNull(LogUtils.cleanupDataForLog(null));
        Assertions.assertEquals("", LogUtils.cleanupDataForLog(""));
    }


}
