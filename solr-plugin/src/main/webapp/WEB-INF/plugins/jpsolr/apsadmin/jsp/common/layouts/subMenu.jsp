<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="/aps-core" %>
<%@ taglib prefix="jpsolr" uri="/jpsolr-core" %>

<wp:ifauthorized permission="superuser">
    <jpsolr:if-active>
        <li class="list-group-item">
            <a href="<s:url action="config" namespace="/do/jpsolr" />">
                <span class="list-group-item-value"><s:text name="jpsolr.admin.config" /></span>
            </a>
        </li>
    </jpsolr:if-active>
</wp:ifauthorized>
