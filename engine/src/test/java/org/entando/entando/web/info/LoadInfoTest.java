package org.entando.entando.web.info;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.entando.entando.web.info.InfoConfigurations.Configurations;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class LoadInfoTest {

    @Test
    void testLoadInfo() throws Exception{
        Configurations bProp = new Configurations(new ClassPathResource("info/build-info.properties"), StandardCharsets.UTF_8);
        Configurations gProp = new Configurations(new ClassPathResource("info/git.properties"), StandardCharsets.UTF_8);
        InfoLoader ld = new InfoLoader(new InfoConfigurations(bProp,gProp));
        BuildInfoDto build = ld.buildInfo();
        GitInfoDto git =ld.gitInfo();

        assertThat(build.getName()).isEqualTo("Entando Core: Engine");
        assertThat(build.getArtifact()).isEqualTo("entando-engine");
        assertThat(build.getGroup()).isEqualTo("org.entando.entando");
        assertThat(build.getVersion()).isEqualTo("7.2.0-SNAPSHOT");
        assertThat(build.getTime()).isEqualTo("2023-01-04T11:36:14.459Z");

        assertThat(git.getBranch()).isEqualTo("ENG-4456_Every-Entando-BE-microservices-MUST-return-their-version-number");
        assertThat(git.getCommit().getId()).isEqualTo("4955710");
    }

}
