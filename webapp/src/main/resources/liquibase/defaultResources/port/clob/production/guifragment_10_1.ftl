<#assign jacms=JspTaglibs["/jacms-aps-core"]>
<#assign wp=JspTaglibs["/aps-core"]>
<@jacms.contentList listName="contentList" contentType="NWS" />
<@wp.currentPage param="code" var="currentPageCode" />
<link rel="stylesheet" type="text/css" href="<@wp.resourceURL />ootb-widgets/static/css/sitemap.css">

<div class="well well-small sitemap">
<h2>Sitemap</h2>

<ul class="nav nav-list">
<@wp.nav spec="code(homepage).subtree(50)" var="page">
   <#if (previousPage?? && previousPage.code??)>
	<#assign previousLevel=previousPage.level>
	<#assign level=page.level>
	<#include "sitemap_menu_include" >
   </#if>
   <#assign previousPage=page>
</@wp.nav>
<#if (previousPage??)>
   <#assign previousLevel = previousPage.level>
   <#assign level=0>
   <#include "sitemap_menu_include" >
   <#if (previousLevel != 0)>
	<#list 0..(previousLevel - 1) as ignoreMe>
		</ul></li>
	</#list>
   </#if>
</#if>
<ul class="nav nav-list">
     <li class="nav-header">
     <strong>News</strong>
<ul class="nav-list">
<#list contentList as contentId>
	<@jacms.content contentId="${contentId}" modelId="10020" />
</#list>
</ul>
</li>
</ul>
</div>
<#assign previousPage="">
