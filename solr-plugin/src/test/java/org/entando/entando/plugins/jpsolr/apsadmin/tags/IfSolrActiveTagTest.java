package org.entando.entando.plugins.jpsolr.apsadmin.tags;

import org.entando.entando.plugins.jpsolr.SolrEnvironmentVariables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class IfSolrActiveTagTest {

    @Test
    void shouldIncludeContent() throws Exception {
        try (MockedStatic<SolrEnvironmentVariables> solrEnvStaticMock = Mockito.mockStatic(
                SolrEnvironmentVariables.class)) {
            solrEnvStaticMock.when(() -> SolrEnvironmentVariables.active()).thenReturn(true);
            IfSolrActiveTag tag = new IfSolrActiveTag();
            Assertions.assertEquals(1, tag.doStartTag());
        }
    }

    @Test
    void shouldSkipContent() throws Exception {
        try (MockedStatic<SolrEnvironmentVariables> solrEnvStaticMock = Mockito.mockStatic(
                SolrEnvironmentVariables.class)) {
            solrEnvStaticMock.when(() -> SolrEnvironmentVariables.active()).thenReturn(false);
            IfSolrActiveTag tag = new IfSolrActiveTag();
            Assertions.assertEquals(0, tag.doStartTag());
        }
    }
}
