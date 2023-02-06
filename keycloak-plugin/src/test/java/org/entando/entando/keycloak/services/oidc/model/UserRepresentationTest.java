package org.entando.entando.keycloak.services.oidc.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

class UserRepresentationTest {

    @Test
    void userRepresentationShouldBeSerializable(){
        UserRepresentation user = new UserRepresentation();
        user.setId("06b73bd57b3b938786daed820cb9fa4561bf0e8e");
        user.setUsername("testUser");
        assertThat(SerializationUtils.serialize(user)).isNotNull();
    }
}
