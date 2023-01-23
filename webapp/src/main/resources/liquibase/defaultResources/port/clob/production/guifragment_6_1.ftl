<#assign wp=JspTaglibs["/aps-core"]>

<#include "entando_ootb_carbon_include" >

<#assign sessionUser = "" />
<#assign userDisplayName = "" />
<#if (Session.currentUser.username != "guest") >
   <#assign sessionUser = Session.currentUser.username />
   <#if (Session.currentUser.profile??) && (Session.currentUser.profile.displayName??)>
      <#assign userDisplayName = Session.currentUser.profile.displayName />
   <#else>
      <#assign userDisplayName = Session.currentUser />
   </#if>
</#if>

<login-button-widget
   admin-url="<@wp.info key="systemParam" paramName="appBuilderBaseURL" />"
   user-display-name="${userDisplayName}"
   redirect-url="<@wp.url baseUrlMode="requestIfRelative" />"
></login-button-widget>