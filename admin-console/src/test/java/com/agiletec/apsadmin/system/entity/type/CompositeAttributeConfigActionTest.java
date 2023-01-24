package com.agiletec.apsadmin.system.entity.type;

import static com.agiletec.apsadmin.system.entity.type.ICompositeAttributeConfigAction.COMPOSITE_ATTRIBUTE_ON_EDIT_SESSION_PARAM;
import static com.agiletec.apsadmin.system.entity.type.IEntityTypeConfigAction.ENTITY_TYPE_ON_EDIT_SESSION_PARAM;

import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.CompositeAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.MonoListAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.TextAttribute;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;

@ExtendWith(MockitoExtension.class)
class CompositeAttributeConfigActionTest {

    private static final String COMPOSITE_ATTRIBUTE_NAME = "my_composite_attribute";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private BeanFactory beanFactory;

    @InjectMocks
    private CompositeAttributeConfigAction action;

    @BeforeEach
    void setUp() {
        Mockito.when(request.getSession()).thenReturn(session);

        CompositeAttribute compositeAttribute = new CompositeAttribute();
        compositeAttribute.setName(COMPOSITE_ATTRIBUTE_NAME);
        addTextAttribute(compositeAttribute, "attribute1");
        addTextAttribute(compositeAttribute, "attribute2");
        Mockito.when(session.getAttribute(COMPOSITE_ATTRIBUTE_ON_EDIT_SESSION_PARAM)).thenReturn(compositeAttribute);
    }

    private void addTextAttribute(CompositeAttribute compositeAttribute, String attributeName) {
        TextAttribute attribute = new TextAttribute();
        attribute.setName(attributeName);
        compositeAttribute.getAttributes().add(attribute);
        compositeAttribute.getAttributeMap().put(attribute.getName(), attribute);
    }

    @Test
    void testMoveAttribute() {
        action.setMovement(ApsAdminSystemConstants.MOVEMENT_UP_CODE);
        action.setAttributeIndex(1);
        action.moveAttributeElement();
        Mockito.verify(session, Mockito.times(1))
                .setAttribute(Mockito.eq(COMPOSITE_ATTRIBUTE_ON_EDIT_SESSION_PARAM), Mockito.any());
    }

    @Test
    void testRemoveAttributeElement() {
        action.setAttributeIndex(0);
        action.removeAttributeElement();
        Mockito.verify(session, Mockito.times(1))
                .setAttribute(Mockito.eq(COMPOSITE_ATTRIBUTE_ON_EDIT_SESSION_PARAM), Mockito.any());
    }


    @Test
    void testSaveAttributeElement() {
        String entityManagerName = "EntityManagerName";
        String attributeTypeCode = "typeCode";
        Mockito.when(session.getAttribute(IEntityTypeConfigAction.ENTITY_TYPE_MANAGER_SESSION_PARAM))
                .thenReturn(entityManagerName);
        Map<String, AttributeInterface> attributeTypes = new HashMap<>();
        attributeTypes.put(attributeTypeCode, new TextAttribute());
        IEntityManager entityManager = Mockito.mock(IEntityManager.class);
        Mockito.when(beanFactory.getBean(entityManagerName)).thenReturn(entityManager);
        Mockito.when(entityManager.getEntityAttributePrototypes()).thenReturn(attributeTypes);
        action.setAttributeTypeCode(attributeTypeCode);
        action.saveAttributeElement();
        Mockito.verify(session, Mockito.times(1))
                .setAttribute(Mockito.eq(COMPOSITE_ATTRIBUTE_ON_EDIT_SESSION_PARAM), Mockito.any());
    }

    @Test
    void testSaveCompositeAttribute() {
        MonoListAttribute attribute = new MonoListAttribute();
        IApsEntity entity = Mockito.mock(IApsEntity.class);
        Mockito.when(entity.getAttribute(COMPOSITE_ATTRIBUTE_NAME)).thenReturn(attribute);
        Mockito.when(session.getAttribute(ENTITY_TYPE_ON_EDIT_SESSION_PARAM)).thenReturn(entity);
        action.saveCompositeAttribute();
        Mockito.verify(session, Mockito.times(1))
                .setAttribute(Mockito.eq(ENTITY_TYPE_ON_EDIT_SESSION_PARAM), Mockito.any());
    }
}
