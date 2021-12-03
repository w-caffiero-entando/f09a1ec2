<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="/aps-core" prefix="wp" %>
<%@ taglib uri="/apsadmin-core" prefix="wpsa" %>
<%@ taglib uri="/apsadmin-form" prefix="wpsf" %>

<legend><s:text name="jpseo.label.config" /></legend>

<div class="form-checkbox form-group">
    <div class="col-sm-3 control-label">
        <label class="display-block" for="useExtraDescriptions">
            <s:text name="jpseo.label.useBetterDescriptions" />
        </label>
    </div>
    <div class="col-sm-4">
        <wpsf:checkbox name="useExtraDescriptions" id="useExtraDescriptions" value="#attr.useExtraDescriptions" cssClass="bootstrap-switch" />
    </div>
</div>
