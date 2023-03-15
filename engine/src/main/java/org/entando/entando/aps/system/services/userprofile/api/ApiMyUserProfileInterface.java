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
package org.entando.entando.aps.system.services.userprofile.api;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.Properties;
import org.entando.entando.aps.system.services.api.IApiErrorCodes;
import org.entando.entando.aps.system.services.api.model.ApiException;
import org.entando.entando.aps.system.services.userprofile.api.model.JAXBUserDetails;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.springframework.http.HttpStatus;

/**
 * @author E.Santoboni
 */
public class ApiMyUserProfileInterface {

	private static final EntLogger _logger =  EntLogFactory.getSanitizedLogger(ApiMyUserProfileInterface.class);
	
    public JAXBUserDetails getMyUserProfile(Properties properties) throws ApiException, Throwable {
        try {
            UserDetails userDetail = (UserDetails) properties.get(SystemConstants.API_USER_PARAMETER);
            if (null == userDetail) {
                throw new ApiException(IApiErrorCodes.API_VALIDATION_ERROR, "Null user", HttpStatus.UNAUTHORIZED);
            }
            return new JAXBUserDetails(userDetail);
        } catch (ApiException ae) {
            throw ae;
        } catch (Throwable t) {
        	_logger.error("Error extracting userprofile", t);
            //ApsSystemUtils.logThrowable(t, this, "getMyUserProfile");
            throw new EntException("Error extracting userprofile", t);
        }
    }
    
}
