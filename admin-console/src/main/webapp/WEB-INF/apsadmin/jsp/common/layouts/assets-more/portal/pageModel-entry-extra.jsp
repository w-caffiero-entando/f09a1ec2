<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="/aps-core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<link rel="stylesheet" type="text/css" href="<wp:resourceURL ignoreTenant="true" />administration/css/entando.grid-generator.css"/>
<link rel="stylesheet" type="text/css" href="<wp:resourceURL ignoreTenant="true" />administration/css/pages/pageModel-entry.css"/>

<script src="<wp:resourceURL ignoreTenant="true" />administration/js/jquery.xml2json.js"></script>
<script src="<wp:resourceURL ignoreTenant="true" />administration/js/lodash.js"></script>
<script src="<wp:resourceURL ignoreTenant="true" />administration/js/entando.grid-generator.js"></script>
<script src="<wp:resourceURL ignoreTenant="true" />administration/ace-editor/ace.js" type="text/javascript" charset="utf-8"></script>

<script>
    var PROPERTY = {};
    PROPERTY['code'] = '<s:property value="code" />';

    var TEXT = {};
    TEXT['error.grid.overlappingFrames'] = '<s:text name="error.grid.overlappingFrames"/>';
    TEXT['error.grid.malformedFrames'] = '<s:text name="error.grid.malformedFrames"/>';
    TEXT['error.grid.gridError'] = '<s:text name="error.grid.gridError"/>';
</script>
<script src="<wp:resourceURL ignoreTenant="true" />administration/js/pages/pageModel-entry.js"></script>
