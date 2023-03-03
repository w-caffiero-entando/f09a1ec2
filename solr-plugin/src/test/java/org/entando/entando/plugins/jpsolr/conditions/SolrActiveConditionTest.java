package org.entando.entando.plugins.jpsolr.conditions;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.LinkedMultiValueMap;

@ExtendWith(MockitoExtension.class)
class SolrActiveConditionTest {

    @Mock
    ConditionContext context;

    @Mock
    AnnotatedTypeMetadata metadata;

    @Test
    void testConditionalTrueEnvTrue() {
        mockConditionalAnnotation(true);
        Assertions.assertTrue(new SolrActiveCondition(true).matches(context, metadata));
    }

    @Test
    void testConditionalFalseEnvTrue() {
        mockConditionalAnnotation(false);
        Assertions.assertTrue(new SolrActiveCondition(true).matches(context, metadata));
    }

    @Test
    void testConditionalTrueEnvFalse() {
        mockConditionalAnnotation(true);
        Assertions.assertFalse(new SolrActiveCondition(false).matches(context, metadata));
    }

    @Test
    void testConditionalFalseEnvFalse() {
        mockConditionalAnnotation(false);
        Assertions.assertFalse(new SolrActiveCondition(false).matches(context, metadata));
    }

    @Test
    void testEmptyAnnotatedTypeMetadataEnvTrue() {
        Assertions.assertFalse(new SolrActiveCondition(true).matches(context, metadata));
    }

    @Test
    void testEmptyAnnotatedTypeMetadataEnvFalse() {
        Assertions.assertTrue(new SolrActiveCondition(false).matches(context, metadata));
    }

    void mockConditionalAnnotation(boolean value) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.put("value", List.of(true));
        Mockito.when(metadata.getAllAnnotationAttributes(SolrActive.class.getName())).thenReturn(map);
    }
}
