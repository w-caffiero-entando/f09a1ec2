package org.entando.entando.plugins.jpsolr.aps.system.solr.model;

import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.aps.system.services.lang.Lang;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentTypeSettingsTest {

    @Test
    void shouldDetectMissingLangField() {

        ContentTypeSettings contentTypeSettings = new ContentTypeSettings("NWS", "News");

        TextAttribute titleAttribute = new TextAttribute();
        titleAttribute.setName("title");
        titleAttribute.setType("Text");

        Map<String, Map<String, Serializable>> currentField = Map.of("en_title", Map.of(
                "name", "en_title",
                "type", "text_gen_sort",
                "multiValued", false
        ));

        contentTypeSettings.addAttribute(titleAttribute, currentField, getLanguages("en", "it"));

        Assertions.assertFalse(contentTypeSettings.isValid());
    }

    @Test
    void shouldDetectLangMismatchInField() {

        ContentTypeSettings contentTypeSettings = new ContentTypeSettings("NWS", "News");

        TextAttribute titleAttribute = new TextAttribute();
        titleAttribute.setName("title");
        titleAttribute.setType("Text");

        Map<String, Map<String, Serializable>> currentField = Map.of("en_title", Map.of(
                "name", "en_title",
                "type", "text_gen_sort",
                "multiValued", false
        ));

        contentTypeSettings.addAttribute(titleAttribute, currentField, getLanguages("es"));

        Assertions.assertFalse(contentTypeSettings.isValid());
    }

    @Test
    void shouldDetectValidLangField() {

        ContentTypeSettings contentTypeSettings = new ContentTypeSettings("NWS", "News");

        TextAttribute titleAttribute = new TextAttribute();
        titleAttribute.setName("title");
        titleAttribute.setType("Text");

        Map<String, Map<String, Serializable>> currentField = Map.of("en_title", Map.of(
                "name", "en_title",
                "type", "text_gen_sort",
                "multiValued", false
        ));

        contentTypeSettings.addAttribute(titleAttribute, currentField, getLanguages("en"));

        Assertions.assertTrue(contentTypeSettings.isValid());
    }

    private List<Lang> getLanguages(String... codes) {
        return Arrays.stream(codes).map(c -> {
            Lang lang = new Lang();
            lang.setCode(c);
            return lang;
        }).collect(Collectors.toList());
    }
}
