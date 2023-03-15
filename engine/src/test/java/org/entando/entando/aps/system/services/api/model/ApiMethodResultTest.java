package org.entando.entando.aps.system.services.api.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApiMethodResultTest {

    @Test
    void shouldAddError() {
        ApiMethodResult apiMethodResult = new ApiMethodResult();
        apiMethodResult.setResult("result");
        apiMethodResult.addError("errorCode", "errorDescription");
        Assertions.assertEquals("result", apiMethodResult.getResult());
        Assertions.assertEquals(1, apiMethodResult.getErrors().size());
        LegacyApiError apiError = apiMethodResult.getErrors().get(0);
        Assertions.assertEquals("errorCode", apiError.getCode());
        Assertions.assertEquals("errorDescription", apiError.getMessage());
    }
}
