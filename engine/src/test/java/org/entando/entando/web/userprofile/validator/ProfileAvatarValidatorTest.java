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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.entando.entando.web.userprofile.model.ProfileAvatarRequest;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

class ProfileAvatarValidatorTest {


    @Test
    void shouldSupportOnlyProfileAvatarRequest() {
        assertTrue(new ProfileAvatarValidator().supports(ProfileAvatarRequest.class));
        assertFalse(new ProfileAvatarValidator().supports(Object.class));
    }

    @Test
    void shouldNotValidateFileNamesMissingExtensions() throws IOException {
        ProfileAvatarRequest request = new ProfileAvatarRequest("missing_extension_filename",
                IOUtils.toByteArray(new ClassPathResource("userprofile/image.png").getInputStream()), false);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "profileAvatarRequest");
        new ProfileAvatarValidator().validate(request, errors);
        assertEquals(1, errors.getErrorCount());
        assertEquals("fileBrowser.filename.invalidFilename", errors.getAllErrors().get(0).getDefaultMessage());
        assertEquals("missing_extension_filename", ((FieldError) errors.getAllErrors().get(0)).getRejectedValue());
    }

    @Test
    void shouldNotValidateFilesOtherThanImages() {
        String notValidBase64Image = "bm90IGFuIGltYWdl";
        ProfileAvatarRequest request = new ProfileAvatarRequest("valid_filename.txt", notValidBase64Image.getBytes(), false);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "profileAvatarRequest");
        new ProfileAvatarValidator().validate(request, errors);
        assertEquals(1, errors.getErrorCount());
        assertEquals("fileBrowser.file.invalidType", errors.getAllErrors().get(0).getDefaultMessage());
        assertEquals("base64", ((FieldError) errors.getAllErrors().get(0)).getField());

    }


    @Test
    void shouldThrowUncheckedIOExceptionIfImageReadingFails() throws IOException {
        ProfileAvatarRequest request = new ProfileAvatarRequest("image.png",
                IOUtils.toByteArray(new ClassPathResource("userprofile/image.png").getInputStream()), false);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "profileAvatarRequest");
        try (MockedStatic<ImageIO> mockStatic = Mockito.mockStatic(ImageIO.class)) {
            mockStatic.when(() -> ImageIO.read(any(InputStream.class))).thenThrow(IOException.class);
            ProfileAvatarValidator profileAvatarValidator = new ProfileAvatarValidator();
            assertThrows(UncheckedIOException.class, () -> profileAvatarValidator.validate(request, errors));
        }
    }

    @Test
    void shouldValidateAcceptValidImageWithValidFileName() throws IOException {
        ProfileAvatarRequest request = new ProfileAvatarRequest("image.png",
                IOUtils.toByteArray(new ClassPathResource("userprofile/image.png").getInputStream()), false);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "profileAvatarRequest");
        new ProfileAvatarValidator().validate(request, errors);
        assertTrue(errors.getAllErrors().isEmpty());

    }
}
