<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="/aps-core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>

<html lang="en">
    <head>
        <title>Entando - Log in</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta charset="utf-8"/>
        <link rel="shortcut icon" href="<wp:resourceURL ignoreTenant="true" />administration/img/favicon-entando.png">

        <!--CSS inclusions-->
        <link rel="stylesheet" href="<wp:resourceURL ignoreTenant="true" />administration/css/login.css" />
        <!--JS inclusions-->
    </head>

    <body>
        <div class="LoginPage">
            <form class="LoginPage__form" action="doLogin" id="form-login" method="post">
                <div class="LoginPage__brand">
                    <div class="LoginPage__logo"></div>
                    <div class="LoginPage__description"></div>
                </div>
                <div class="LoginPage__formGroup">
                    <s:if test="#session.currentUser != null && #session.currentUser.username != 'guest'">
                        <h2>
                            <s:text name="note.userbar.welcome"/>&#32;<s:property value="#session.currentUser"/>&nbsp;!
                        </h2>
                        <wp:ifauthorized permission="enterBackend" var="checkEnterBackend"/>
                        <c:choose>
                            <c:when test="${checkEnterBackend}">
                                <div class="LoginPage__actionGroup">
                                    <p>
                                        <strong><s:text name="note.login.yetLogged"/></strong>
                                    </p>
                                </div>
                                <div class="LoginPage__actionGroup">
                                    <a href="<s:url action="main" />">
                                        <s:text name="note.goToMain"/>
                                    </a>
                                    <a href="<s:url action="logout" namespace="/do" />">
                                        <s:text name="menu.exit"/>
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div>
                                    <strong><s:text name="note.login.notAllowed"/></strong><br/>
                                </div>
                                <div>
                                    <a href="<s:url action="logout" namespace="/do" />">
                                        <s:text name="menu.exit"/>
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </s:if>
                    <s:else>
                        <div class="LoginPage__inputGroup">
                            <label class="LoginPage__label"><s:text name="label.username"/></label>
                            <input type="text" name="username" tabindex="1" class="LoginPage__input" id="username" placeholder="Username" />
                        </div>
                        <div class="LoginPage__inputGroup extra-margin">
                            <label class="LoginPage__label"><s:text name="label.password"/></label>
                            <input type="password" name="password" tabindex="2" class="LoginPage__input" id="password" placeholder="Password" />
                        </div>
                        <s:if test="hasActionErrors()">
                            <div class="LoginPage__error">
                                <ul>
                                    <s:iterator value="actionErrors">
                                        <li>
                                            <s:property/>
                                        </li>
                                    </s:iterator>
                                </ul>
                            </div>
                            <div class="LoginPage__actionGroup" style="margin-top: 0">
                                <div></div>
                                <button class="LoginPage__button" id="button-login" type="submit"><s:text name="label.signin"/></button>
                                <div class="LoginPage__loading">
                                    <div class="LoginPage__spinner" />
                                </div>
                            </div>
                        </s:if>
                        <s:else>
                            <div class="LoginPage__error">
                                <ul>
                                    <s:iterator value="actionErrors">
                                        <li>
                                            <s:property/>
                                        </li>
                                    </s:iterator>
                                </ul>
                            </div>
                            <div class="LoginPage__actionGroup">
                                <div></div>
                                <button class="LoginPage__button" id="button-login" type="submit"><s:text name="label.signin"/></button>
                                <div class="LoginPage__loading">
                                    <div class="LoginPage__spinner" />
                                </div>
                            </div>
                        </s:else>
                    </s:else>
                </div>
                <jsp:useBean id="date" class="java.util.Date" />
                <div class="LoginPage__copyright">Copyright <fmt:formatDate value="${date}" pattern="yyyy" /> <a href="https://www.entando.com/" class="LoginPage__url"> Entando</a></div>
            </form>
        </div>
    </body>

</html>
