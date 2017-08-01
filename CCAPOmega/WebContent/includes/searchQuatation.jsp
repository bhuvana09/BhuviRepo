<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form action="SearchQuotation" method="get">
Enter the following details to search
<table>
	<tbody>
		<tr>
			<td>SSN:</td>
			<td><input name="SSN" /></td>
		</tr>
		<tr>
			<td>License Number:</td>
			<td><input name="licenseNumber" /><br />
			</td>
		</tr>
		<tr>
			<td>Click to Submit:</td>
			<td>
			<button type="submit" value="SearchQuotation" name="Search Quotation">Search Quotation</button>
			</td>
		</tr>
	</tbody>
</table>
</form>
</body>
</html>