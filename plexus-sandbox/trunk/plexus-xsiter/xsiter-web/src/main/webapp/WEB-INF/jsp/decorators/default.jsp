<%--
  ~ Copyright 2005-2006 The Codehaus.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="pss" uri="/plexusSecuritySystem" %>
<html>
<head>
  <title>Plexus xSiter&nbsp;::&nbsp;<decorator:title default="Management Console"/> </title>
  <style type="text/css" media="all">
    @IMPORT url("/css/xsiter.css");
  </style>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
</head>

<body onload="<decorator:getProperty property="body.onload" />">

<div id="breadcrumbs">
 <div class="xleft">
    <c:import url="/WEB-INF/jsp/pss/include/securityLinks.jsp"/>
  </div>
  
  <div class="xright">
    <a href="http://www.codehaus.org/">Codehaus</a> |
    <a href="http://plexus.codehaus.org/">Plexus</a> 
  </div>
  
  <div class="clear">
  </div>
</div>

<!-- Main Content -->
<div id="content">  
  <div id="navigation">
    <c:import url="/WEB-INF/jsp/includes/navigation.jsp"/>
  </div>
  <decorator:body/>
  <div class="clear">
  </div>
</div>

<!--  This block only to spit out xWork info -->
<div id="xworkinfo">

  <strong>session scope:</strong>
  <ul>
  <c:forEach var="ss" items="${sessionScope}">
    <li>
      <em><c:out value="${ss.key}" /></em> : 
        <c:choose>
          <c:when test="${ss.value != null}">
            (<c:out value="${ss.value.class.name}" /> ) <br />
            &nbsp; &nbsp; &nbsp; <c:out value="${ss.value}" />
          </c:when>
          <c:otherwise>
            &lt;null&gt;
          </c:otherwise>
        </c:choose>
    </li>
  </c:forEach>
  </ul>
  
  <strong>request scope:</strong>
  <ul>
  <c:forEach var="rs" items="${requestScope}">
    <li>
      <em><c:out value="${rs.key}" /></em> : 
        <c:choose>
          <c:when test="${rs.value != null}">
            (<c:out value="${rs.value.class.name}" /> ) <br />
            &nbsp; &nbsp; &nbsp; <c:out value="${rs.value}" />
          </c:when>
          <c:otherwise>
            &lt;null&gt;
          </c:otherwise>
        </c:choose>
    </li>
  </c:forEach>
  </ul>
  
  <strong>page scope:</strong>
  <ul>
  <c:forEach var="ps" items="${requestScope}">
    <li>
      <em><c:out value="${ps.key}" /></em> : 
        <c:choose>
          <c:when test="${ps.value != null}">
            (<c:out value="${ps.value.class.name}" /> ) <br />
            &nbsp; &nbsp; &nbsp; <c:out value="${ps.value}" />
          </c:when>
          <c:otherwise>
            &lt;null&gt;
          </c:otherwise>
        </c:choose>
    </li>
  </c:forEach>
  </ul>     
  
</div>
<!-- End of xwork info -->

<div class="clear">
</div>

<div id="footer">
  <div class="xright">&#169; 2006 Codehaus.org </div>
  <div class="clear">
  </div>
</div>
</body>
</html>