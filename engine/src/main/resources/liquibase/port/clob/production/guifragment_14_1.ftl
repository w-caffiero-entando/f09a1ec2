<#assign s=JspTaglibs["/struts-tags"]>
<#assign wp=JspTaglibs["/aps-core"]>
<#assign wpsa=JspTaglibs["/apsadmin-core"]>
<#assign wpsf=JspTaglibs["/apsadmin-form"]>
<@s.if test="#attribute.attributes.size() != 0">
	<ul class="unstyled">
</@s.if>

<@s.set var="masterListAttributeTracer" value="#attributeTracer" />
<@s.set var="masterListAttribute" value="#attribute" />

<@s.iterator value="#attribute.attributes" var="attribute" status="elementStatus">
	<@s.set var="attributeTracer" value="#masterListAttributeTracer.getMonoListElementTracer(#elementStatus.index)"></@s.set>
	<@s.set var="elementIndex" value="#elementStatus.index" />
	<@s.set var="i18n_attribute_name">userprofile_ATTR<@s.property value="#attribute.name" /></@s.set>
	<@s.set var="attribute_id">userprofile_<@s.property value="#attribute.name" />_<@s.property value="#elementStatus.count" /></@s.set>

	<li class="control-group  <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@s.if test="#attribute.type == 'Composite'">
				<@s.property value="#elementStatus.count" /><span class="noscreen">&#32;<@s.text name="label.compositeAttribute.element" /></span>
				&#32;
				<@s.if test="#lang.default">
					<#include "userprofile_is_front_AllList_operationModule" >
				</@s.if>
			</@s.if>
			<@s.else>
				<@s.property value="#elementStatus.count" />
				&#32;
				<#include "userprofile_is_front_AllList_operationModule" >
			</@s.else>
		</label>
		<div class="controls">
			<@s.if test="#attribute.type == 'Boolean'">
				<#include "userprofile_is_front-BooleanAttribute" >
			</@s.if>
			<@s.elseif test="#attribute.type == 'CheckBox'">
				<#include "userprofile_is_front-CheckboxAttribute" >
			</@s.elseif>
			<@s.elseif test="#attribute.type == 'Composite'">
				<#include "userprofile_is_front-CompositeAttribute" >
			</@s.elseif>
			<@s.elseif test="#attribute.type == 'Date'">
				<#include "userprofile_is_front-DateAttribute" >
			</@s.elseif>
			<@s.elseif test="#attribute.type == 'Enumerator'">
				<#include "userprofile_is_front-EnumeratorAttribute" >
			</@s.elseif>
			<@s.elseif test="#attribute.type == 'EnumeratorMap'">
				<#include "userprofile_is_front-EnumeratorMapAttribute" >
			</@s.elseif>
			<@s.elseif test="#attribute.type == 'Hypertext'">
				<#include "userprofile_is_front-HypertextAttribute" >
			</@s.elseif>
			<@s.elseif test="#attribute.type == 'Longtext'">
				<#include "userprofile_is_front-LongtextAttribute" >
			</@s.elseif>
			<@s.elseif test="#attribute.type == 'Monotext'">
				<#include "userprofile_is_front-MonotextAttribute" >
			</@s.elseif>
			<@s.elseif test="#attribute.type == 'Number'">
				<#include "userprofile_is_front-NumberAttribute" >
			</@s.elseif>
			<@s.elseif test="#attribute.type == 'ThreeState'">
				<#include "userprofile_is_front-ThreeStateAttribute" >
			</@s.elseif>
			<@s.elseif test="#attribute.type == 'Text'">
				<#include "userprofile_is_front-MonotextAttribute" >
			</@s.elseif>
			<@s.else>
				<#include "userprofile_is_front-MonotextAttribute" >
			</@s.else>
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</li>
</@s.iterator>

<@s.set var="attributeTracer" value="#masterListAttributeTracer" />
<@s.set var="attribute" value="#masterListAttribute" />
<@s.set var="elementIndex" value="" />
<@s.if test="#attribute.attributes.size() != 0">
</ul>
</@s.if>
<@s.if test="#lang.default">
	<div class="control-group">
		<div class="controls">
			<#include "userprofile_is_front-AllList-addElementButton" >
		</div>
	</div>
</@s.if>