<#assign s=JspTaglibs["/struts-tags"]>
<#assign wp=JspTaglibs["/aps-core"]>
<#assign wpsa=JspTaglibs["/apsadmin-core"]>
<#assign wpsf=JspTaglibs["/apsadmin-form"]>
<#assign i18n_attribute_name ><@s.property value="#i18n_attribute_name" /></#assign>
<@s.if test="#attribute.type == 'Boolean'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-BooleanAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.if>
<@s.elseif test="#attribute.type == 'CheckBox'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-CheckboxAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'Composite'">
	<div class="well well-small">
		<fieldset class=" <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
			<legend class="margin-medium-top">
				<@wp.i18n key="${i18n_attribute_name}" />
				<#include "userprofile_is_front_AttributeInfo" >
			</legend>
			<#include "userprofile_is_front_attributeInfo-help-block" >
			<#include "userprofile_is_front-CompositeAttribute" >
		</fieldset>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'Date'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-DateAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'Enumerator'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-EnumeratorAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'EnumeratorMap'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-EnumeratorMapAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'Hypertext'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-HypertextAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'List'">
	<div class="well well-small">
		<fieldset class=" <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
			<legend class="margin-medium-top">
				<@wp.i18n key="${i18n_attribute_name}" />
					<#include "userprofile_is_front_AttributeInfo" >
			</legend>
			<#include "userprofile_is_front_attributeInfo-help-block" >
			<#include "userprofile_is_front-MonolistAttribute" >
		</fieldset>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'Longtext'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-LongtextAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'Monolist'">
	<div class="well well-small">
		<fieldset class=" <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
			<legend class="margin-medium-top"><@wp.i18n key="${i18n_attribute_name}" />
				<#include "userprofile_is_front_AttributeInfo" >
			</legend>
			<#include "userprofile_is_front_attributeInfo-help-block" >
			<#include "userprofile_is_front-MonolistAttribute" >
		</fieldset>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'Monotext'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-MonotextAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'Number'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-NumberAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'Text'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-MonotextAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.elseif>
<@s.elseif test="#attribute.type == 'ThreeState'">
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="#attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-ThreeStateAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.elseif>
<@s.else> <#-- for all other types, insert a simple label and a input[type="text"] -->
	<div class="control-group <@s.property value="%{' attribute-type-'+#attribute.type+' '}" />">
		<label class="control-label" for="<@s.property value="attribute_id" />">
			<@wp.i18n key="${i18n_attribute_name}" />
			<#include "userprofile_is_front_AttributeInfo" >
		</label>
		<div class="controls">
			<#include "userprofile_is_front-MonotextAttribute" >
			<#include "userprofile_is_front_attributeInfo-help-block" >
		</div>
	</div>
</@s.else>