<#assign wp=JspTaglibs["/aps-core"]>

<@wp.headInfo type="JS" info="entando-misc-jquery/jquery-3.4.1.min.js" />
<@wp.headInfo type="JS" info="entando-misc-bootstrap/bootstrap.min.js" />

<@wp.currentPage param="code" var="currentPageCode" />
  <ul class="nav">
<@wp.nav var="page">

  <#if (previousPage?? && previousPage.code??)>
    <#assign previousLevel=previousPage.level>
    <#assign level=page.level>
    <#include "legacy-navigation-menu_include" >
  </#if>
  <#assign previousPage=page>
</@wp.nav>

<#if (previousPage??)>
  <#assign previousLevel=previousPage.level>
  <#assign level=0>
  <#include "legacy-navigation-menu_include" >

  <#if (previousLevel != 0)>
    <#list 0..(previousLevel - 1) as ignoreMe>
      </ul></li>
    </#list>

  </#if>
</#if>

</ul>
<#assign previousPage="">