package org.entando.entando.plugins.jpcds.aps.system.storage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

public class CdsConfigurationTest {

    @Test
    void shouldDtoWork(){
        CdsConfiguration config = new CdsConfiguration();
        config.setBaseURL("");
        config.setBaseDiskRoot("");
        config.setProtectedBaseURL("");
        config.setProtectedBaseDiskRoot("");
        config.setEnabled(true);
        config.setCdsPublicUrl("https://cds.entando.org");
        config.setCdsPrivateUrl("https://cds.entando.org");
        config.setCdsPath("/api/v1");
        config.setKcAuthUrl("http://localhost:8081/auth");
        config.setKcRealm("entando");
        config.setKcClientId("entando-app");
        config.setKcClientSecret("secret");

        Assertions.assertThat(config.getCdsPath()).isEqualTo("/api/v1");
        Assertions.assertThat(config.isEnabled()).isTrue();

    }


    @Test
    public void shouldWorkFineWithHashEqualsAndToString() {

        CdsConfiguration config1 = new CdsConfiguration();
        config1.setBaseURL("");
        config1.setBaseDiskRoot("");
        config1.setProtectedBaseURL("");
        config1.setProtectedBaseDiskRoot("");
        config1.setEnabled(true);
        config1.setCdsPublicUrl("https://cds.entando.org");
        config1.setCdsPrivateUrl("https://cds.entando.org");
        config1.setCdsPath("/api/v1");
        config1.setKcAuthUrl("http://localhost:8081/auth");
        config1.setKcRealm("entando");
        config1.setKcClientId("entando-app");
        config1.setKcClientSecret("secret");

        CdsConfiguration config2 = new CdsConfiguration();
        config2.setBaseURL("");
        config2.setBaseDiskRoot("");
        config2.setProtectedBaseURL("");
        config2.setProtectedBaseDiskRoot("");
        config2.setEnabled(true);
        config2.setCdsPublicUrl("https://cds.entando.org");
        config2.setCdsPrivateUrl("https://cds.entando.org");
        config2.setCdsPath("/api/v1");
        config2.setKcAuthUrl("http://localhost:8081/auth");
        config2.setKcRealm("entando");
        config2.setKcClientId("entando-app");
        config2.setKcClientSecret("secret");

        Assertions.assertThat(config2.toString()).contains("https://cds.entando.org","https://cds.entando.org","entando");
        Assertions.assertThat(config2.hashCode()).isEqualTo(config1.hashCode());
        Assertions.assertThat(config2.equals(config1)).isTrue();
    }

}
