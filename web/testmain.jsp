<%--
  Created by IntelliJ IDEA.
  User: Bourne
  Date: 16-6-7
  Time: 上午10:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>大亚湾核电文档知识管理系统</title>
    <link rel="stylesheet" href="../css/webforms.css" type="text/css">
    <script type="text/javascript" src="../js/jquery.min.js"></script>
</head>
<body id="modalSmall">
<form id="loginForm" action="login.action" method="post">

<div id="mainPaneset" style="padding: 20px;">
    <div><span class="dialogTitle"><img src="../images/logo.png"></span></div>
    <div id="scrollingcontent" style="overflow: auto;">
        <div class="innerContent">
            <h2 style="display: inline;">
                <div id="logo" ><span class="defaultLabelStyle">大亚湾核电文档知识管理系统</span> &nbsp;<span
                        class="dialogTitleVersion"></span></div>
            </h2>
        <span class="saveCredentialPanel">
        </span><span class="optionsPanel" style="display: none;">
        </span>
            <table align="center" border="0" cellspacing="0" cellpadding="0">
                <tbody>
                <tr>
                    <td height="30" class="fieldlabel rightAlignment" scope="row"><span class="defaultLabelStyle">用户名</span>
                    </td>
                    <td class="defaultcolumnspacer">:&nbsp;</td>
                    <td><input name="username" title="Login Name" id="username"
                               type="text" size="40" value="Administrator" autocomplete="off">
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td height="30" class="fieldlabel rightAlignment" scope="row"><span class="defaultLabelStyle">密&nbsp;&nbsp;&nbsp;码</span>
                    </td>
                    <td class="defaultcolumnspacer"><s:fielderror key="username"/> </td>
                    <td>
                        <input name="password" title="Password" id="password"
                               type="password" size="40" autocomplete="off">

                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td height="30" class="fieldlabel rightAlignment" nowrap="" scope="row"><span
                            class="defaultLabelStyle">Repository</span></td>
                    <td class="defaultcolumnspacer">:&nbsp;</td>
                    <td>
                        <select name="Login_docbase_0" title="Repository" class="defaultDropdownListStyle"
                                id="DocbaseName" onchange="" size="0">
                            <option value="GihowR" selected="">GihowR</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td height="30" class="spacer">&nbsp;</td>
                    <td></td>
                    <td></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div id="buttonareaPane">
        <div class="modalSmallButtonBar">
            <div class="helpButtonSection">
                <button name="Login_Login_Button_0_0" title="Help" class="button image helpImage"
                        onclick="" type="button"><img width="8" height="11" alt=""   src="../images/help.gif"
                                                                                       border="0"></button>

            </div>
            <div class="rightButtonSection" nowrap="">
                <button name="Login_loginButton" title="Login" class="button default" onclick="$('#loginForm').submit();;"    >登&nbsp;录
                </button>

            </div>
        </div>
    </div>
   </div>
    </form>
</body>
</html>