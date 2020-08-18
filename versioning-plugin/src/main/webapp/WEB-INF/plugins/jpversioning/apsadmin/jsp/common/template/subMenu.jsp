<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/apsadmin-core" prefix="wpsa" %>
<%@ taglib prefix="wp" uri="/aps-core" %>

<wp:ifauthorized permission="superuser" var="isSuperUser" />
<wp:ifauthorized permission="editContents" var="isEditContents" />
<wp:ifauthorized permission="manageResources" var="isManageResources" />

<c:if test="${isSuperUser || isEditContents || isManageResources}">
    <li class="list-group-item">
        <a href="<s:url action="list" namespace="/do/jpversioning/Content/Versioning" />">
            <span class="list-group-item-value"><s:text name="jpversioning.admin.menu" /></span>
        </a>
    </li>
</c:if>