<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<title>Auto Insurance Quotation Application</title>

<link rel="stylesheet" href="styles/custom.css">
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=ISO-8859-1">

<script language="JavaScript" type="text/JavaScript"
	src="scripts/formValdiation.js"></script>

</head>

<body bgcolor="#ffffff" marginwidth="0" marginheight="0" topmargin="0"
	leftmargin="0" rightmargin="0" bottommargin="0">



<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr class="border" valign="baseline">
		<td width="8"></td>
		<td width="5%"><br />
		<a class="my_page" href="home.action"> <img border="0"
			src="images/bidhammer.gif" width="85" height="85"> </a></td>
		<td width="16"></td>
		<td width="16"></td>
		<td align="left" class="header"><br />
		Auto Insurance Quotation Application
		<div class="pagetitle">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Integrated
		insurance portal</div>
		</td>
		<td width="8"></td>
	</tr>

	<tr>
		<td class="border" width="8"></td>
		<td class="border"></td>
		<td class="border"></td>
		<td align="left" valign="top"><img src="images/Corner1.gif"></td>
		<td align="right" colspan="2">

		<div class="small">&nbsp;<br />
		<br />

		</div>

		</td>
		<td width="8"></td>
	</tr>
	<tr>
		<td class="border" width="8" height="8"></td>
		<td class="border" align="left" valign="top" height="8">
		<DIV ALIGN="left" class="bordermenu">
		<a class="my_page" href="dataCollection.jsp">Home</a> <br/>
		</DIV>
		</td>
		<td class="border"></td>
		<td></td>
		<td align="left" valign="top" class="content" colspan="2" rowspan="10">
		<%
		    String includePage = request.getParameter("includePage");
		%> <jsp:include page="<%=includePage%>" flush="true" /> <br />
		</td>
		<td></td>
	</tr>
	<tr>
		<td class="border" width="8"></td>
		<td class="border" align="left" valign="top">
		<DIV ALIGN="left" class="bordermenu"><a class="my_page"
			href="searchQuatation.jsp">Search</a><br />
		</DIV>
		</td>
		<td class="border" height="8"></td>
		<td></td>
		<td align="left" valign="top" class="content" colspan="2">
		
		</td>
		<td></td>
	</tr>
	<tr>
		<td></td><td>
		</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
	</tr>
</table>


</BODY>
</HTML>

