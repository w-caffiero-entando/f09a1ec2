<#assign wp=JspTaglibs["/aps-core"]>
<#if (userFilterOptionsVar??) && userFilterOptionsVar?has_content && (userFilterOptionsVar?size > 0)>
<div class="row-fluid"><div class="span12 padding-medium-top">
<#assign hasUserFilterError = false >
<#list userFilterOptionsVar as userFilterOptionVar>
<#if (userFilterOptionVar.formFieldErrors??) && userFilterOptionVar.formFieldErrors?has_content && (userFilterOptionVar.formFieldErrors?size > 0)>
<#assign hasUserFilterError = true >
</#if>
</#list>
<#if (hasUserFilterError)>
<div class="alert alert-error">
	<a class="close" data-dismiss="alert" href="#"><i class="icon-remove"></i></a>
	<h2 class="alert-heading"><@wp.i18n key="ERRORS" /></h2>
	<ul>
		<#list userFilterOptionsVar as userFilterOptionVar>
			<#if (userFilterOptionVar.formFieldErrors??) && (userFilterOptionVar.formFieldErrors?size > 0)>
			<#assign formFieldErrorsVar = userFilterOptionVar.formFieldErrors >
			<#list formFieldErrorsVar?keys as formFieldErrorKey>
			<li>
			<@wp.i18n key="jacms_LIST_VIEWER_FIELD" />&#32;<em>${formFieldErrorsVar[formFieldErrorKey].attributeName}</em><#if (formFieldErrorsVar[formFieldErrorKey].rangeFieldType??)>:&#32;<em><@wp.i18n key="${formFieldErrorsVar[formFieldErrorKey].rangeFieldType}" /></em></#if>&#32;<@wp.i18n key="${formFieldErrorsVar[formFieldErrorKey].errorKey}" />
			</li>
			</#list>
			</#if>
		</#list>
	</ul>
</div>
</#if>
<#assign hasUserFilterError = false >
<p><button type="button" class="btn btn-info" data-toggle="collapse" data-target="#content-viewer-list-filters"><@wp.i18n key="SEARCH_FILTERS_BUTTON" /> <i class="icon-zoom-in icon-white"></i></button></p>
<form action="<@wp.url />" method="post" class="form-horizontal collapse" id="content-viewer-list-filters">
	<#list userFilterOptionsVar as userFilterOptionVar>
		<#if !userFilterOptionVar.attributeFilter && (userFilterOptionVar.key == "fulltext" || userFilterOptionVar.key == "category")>
			<#include "jacms_content_viewer_list_userfilter_met_${userFilterOptionVar.key}" >
		</#if>
		<#if userFilterOptionVar.attributeFilter >
			<#if userFilterOptionVar.attribute.type == "Monotext" || userFilterOptionVar.attribute.type == "Text" || userFilterOptionVar.attribute.type == "Longtext" || userFilterOptionVar.attribute.type == "Hypertext">
				<#include "jacms_content_viewer_list_userfilter_ent_Text" >
			</#if>
			<#if userFilterOptionVar.attribute.type == "Enumerator" >
				<#include "jacms_content_viewer_list_userfilter_ent_Enumer" >
			</#if>
			<#if userFilterOptionVar.attribute.type == "EnumeratorMap" >
				<#include "jacms_content_viewer_list_userfilter_ent_EnumerMap" >
			</#if>
			<#if userFilterOptionVar.attribute.type == "Number">
				<#include "jacms_content_viewer_list_userfilter_ent_Number" >
			</#if>
			<#if userFilterOptionVar.attribute.type == "Date">
				<#include "jacms_content_viewer_list_userfilter_ent_Date" >
			</#if>
			<#if userFilterOptionVar.attribute.type == "Boolean">
				<#include "jacms_content_viewer_list_userfilter_ent_Boolean" >
			</#if>
			<#if userFilterOptionVar.attribute.type == "CheckBox">
				<#include "jacms_content_viewer_list_userfilter_ent_CheckBox" >
			</#if>
			<#if userFilterOptionVar.attribute.type == "ThreeState">
				<#include "jacms_content_viewer_list_userfilter_ent_ThreeSt" >
			</#if>
		</#if>
	</#list>
	<p class="form-actions">
		<input type="submit" value="<@wp.i18n key="SEARCH" />" class="btn btn-primary" />
	</p>
</form>
</div></div>
</#if>