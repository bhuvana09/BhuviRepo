package com.cognizant.gto.ccap.springmvc.dao;

import java.io.PrintWriter;
import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CookieExample extends HttpServlet{
	private static byte[] iv = new byte[] {(byte)0,(byte)1,(byte)2};
	public void doPost(HttpServletRequest request, HttpServletResponse response){  
	    try{  
	    	new SecureRandom().nextBytes(iv);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
	    response.setContentType("text/html");  
	    PrintWriter out = response.getWriter();  
	          
	    String n=request.getParameter("userName");  
	    out.print("Welcome "+n);  
	  
	    Cookie ck=new Cookie("uname",n);//creating cookie object  
	    ck.setSecure(true);
	    ck.setHttpOnly(true);
	    response.addCookie(ck);//adding cookie in the response  
	  
	    //creating submit button  
	    out.print("<form action='servlet2'>");  
	    out.print("<input type='submit' value='go'>");  
	    out.print("</form>");  
	          
	    out.close();  

	        }catch(Exception e){System.out.println(e);}  
	  }  
}
