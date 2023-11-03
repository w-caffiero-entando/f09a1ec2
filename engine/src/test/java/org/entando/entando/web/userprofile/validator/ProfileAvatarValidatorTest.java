package org.entando.entando.web.userprofile.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void shouldNotValidateFileNamesThatContainsSlash() throws IOException {
        ProfileAvatarRequest request = new ProfileAvatarRequest("not/valid/filename.png",
                IOUtils.toByteArray(new ClassPathResource("userprofile/image.png").getInputStream()));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "profileAvatarRequest");
        new ProfileAvatarValidator().validate(request, errors);
        assertEquals(1, errors.getErrorCount());
        assertEquals("fileBrowser.filename.invalidFilename", errors.getAllErrors().get(0).getDefaultMessage());
        assertEquals("not/valid/filename.png", ((FieldError) errors.getAllErrors().get(0)).getRejectedValue());
    }

    @Test
    void shouldNotValidateFilesOtherThanImages() {
        String notValidBase64Image = "bm90IGFuIGltYWdl";
        ProfileAvatarRequest request = new ProfileAvatarRequest("valid_filename.txt", notValidBase64Image.getBytes());
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "profileAvatarRequest");
        new ProfileAvatarValidator().validate(request, errors);
        assertEquals(1, errors.getErrorCount());
        assertEquals("fileBrowser.file.invalidType", errors.getAllErrors().get(0).getDefaultMessage());
        assertEquals("base64", ((FieldError) errors.getAllErrors().get(0)).getField());

    }


    @Test
    void shouldThrowUncheckedIOExceptionIfImageReadingFails() throws IOException {
        ProfileAvatarRequest request = new ProfileAvatarRequest("valid_filename.png",
                IOUtils.toByteArray(new ClassPathResource("userprofile/image.png").getInputStream()));
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
                IOUtils.toByteArray(new ClassPathResource("userprofile/image.png").getInputStream()));
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "profileAvatarRequest");
        new ProfileAvatarValidator().validate(request, errors);
        assertTrue(errors.getAllErrors().isEmpty());

    }
}
