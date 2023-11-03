package org.entando.entando.web.userprofile.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import org.apache.derby.iapi.types.SQLTinyint;
import org.entando.entando.web.userprofile.model.ProfileAvatarRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

class ProfileAvatarValidatorTest {

    private final static String validBase64Image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAABhGlDQ1BJQ0MgcHJvZml"
            + "sZQAAKJF9kT1Iw0AcxV9bpaItDnYQ6ZChOlkQLeKoVShChVArtOpgcukXNGlIUlwcBdeCgx+LVQcXZ10dXAVB8APE1cVJ0UVK/F9SaBH"
            + "jwXE/3t173L0D/M0qU82eCUDVLCOTSgq5/KoQfMUAAggjioTETH1OFNPwHF/38PH1Ls6zvM/9OcJKwWSATyCeZbphEW8QT29aOud94gg"
            + "rSwrxOfG4QRckfuS67PIb55LDfp4ZMbKZeeIIsVDqYrmLWdlQiRPEMUXVKN+fc1nhvMVZrdZZ+578haGCtrLMdZpRpLCIJYgQIKOOCqq"
            + "wEKdVI8VEhvaTHv4Rxy+SSyZXBYwcC6hBheT4wf/gd7dmcWrSTQolgd4X2/4YBYK7QKth29/Htt06AQLPwJXW8deawMwn6Y2OFjsCBre"
            + "Bi+uOJu8BlzvA8JMuGZIjBWj6i0Xg/Yy+KQ8M3QL9a25v7X2cPgBZ6ip9AxwcAmMlyl73eHdfd2//nmn39wN8yXKr1FLmsAAAAAlwSFl"
            + "zAAAuIwAALiMBeKU/dgAAAAd0SU1FB+cLAw0dHTZCjPMAAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAADElEQVQ"
            + "I12P4//8/AAX+Av7czFnnAAAAAElFTkSuQmCC";

    private final static String notValidBase64Image = "bm90IGFuIGltYWdl";

    @Test
    void shouldNotValidateFileNamesThatContainsSlash() {
        ProfileAvatarRequest request = new ProfileAvatarRequest("not/valid/filename.png", validBase64Image.getBytes());
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "profileAvatarRequest");
        new ProfileAvatarValidator().validate(request, errors);
        assertEquals(1, errors.getErrorCount());
        assertEquals("fileBrowser.filename.invalidFilename", errors.getAllErrors().get(0).getDefaultMessage());
        assertEquals("not/valid/filename.png", ((FieldError) errors.getAllErrors().get(0)).getRejectedValue());
    }

    @Test
    void shouldNotValidateFilesOtherThanImages() {
        ProfileAvatarRequest request = new ProfileAvatarRequest("valid_filename.txt", notValidBase64Image.getBytes());
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "profileAvatarRequest");
        new ProfileAvatarValidator().validate(request, errors);
        assertEquals(1, errors.getErrorCount());
        assertEquals("fileBrowser.file.invalidType", errors.getAllErrors().get(0).getDefaultMessage());
        assertEquals("base64", ((FieldError) errors.getAllErrors().get(0)).getField());

    }


    @Test
    void shouldThrowUncheckedIOExceptionIfImageReadingFails() {
        ProfileAvatarRequest request = new ProfileAvatarRequest("valid_filename.png", validBase64Image.getBytes());
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "profileAvatarRequest");
        try (MockedStatic<ImageIO> mockStatic = Mockito.mockStatic(ImageIO.class)) {
            mockStatic.when(() -> ImageIO.read(any(InputStream.class))).thenThrow(IOException.class);
            assertThrows(UncheckedIOException.class, () -> new ProfileAvatarValidator().validate(request, errors));
        }

    }
}
