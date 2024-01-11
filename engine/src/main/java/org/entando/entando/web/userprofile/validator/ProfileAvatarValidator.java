/*
 * Copyright 2023-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.web.userprofile.validator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.web.userprofile.model.ProfileAvatarRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProfileAvatarValidator implements Validator {

    public static final String ERRCODE_INVALID_FILE_NAME = "1";
    public static final String ERRCODE_INVALID_FILE_TYPE = "2";

    @Override
    public boolean supports(@NonNull Class<?> paramClass) {
        return (ProfileAvatarRequest.class.equals(paramClass));
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        ProfileAvatarRequest request = (ProfileAvatarRequest) target;

        String filename = request.getFilename();
        if (StringUtils.isEmpty(FilenameUtils.getExtension(filename))) {
            errors.rejectValue("filename", ERRCODE_INVALID_FILE_NAME, new String[]{filename},
                    "fileBrowser.filename.invalidFilename");
            return;
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getBase64())) {
            if (ImageIO.read(byteArrayInputStream) == null) {
                errors.rejectValue("base64", ERRCODE_INVALID_FILE_TYPE, "fileBrowser.file.invalidType");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
