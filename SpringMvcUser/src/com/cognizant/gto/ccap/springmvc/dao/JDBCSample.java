package com.cognizant.gto.ccap.springmvc.dao;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.cognizant.gto.ccap.springmvc.model.Login;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class JDBCSample {
	private static int nbSales = 100;
	private static String coffeeName = "coffeeday";
	private static String name = "coffeeday";
	private static EntityManagerFactory emf;

	public static void main(String a[]) throws Exception{

		Connection con = null;
		Login login = null;
		try {
			
			Integer intt = 123564;
			StringBuilder stringBuilder = new StringBuilder();
		//	stringBuilder.append( Integer.toHexString(intt));
			 stringBuilder.append( String.format( "%08X", intt ) );
			System.out.println(stringBuilder);

			MessageDigest md;
			int b =0x6709;
			StringBuilder stringBuilder1 = new StringBuilder();
			//for(byte b :resultBytes) {
			    //stringBuilder1.append( Integer.toHexString( b & 0xFF ) );
			    stringBuilder1.append( String.format( "%02X", b ) );
			//}
			System.out.println(stringBuilder1);
			

			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@<hostname>:<port num>:<DB name>", "sonar", "sonar");

			Statement stmt = con.createStatement();

			String query = "update COFFEES set SALES =" + nbSales + " where COF_NAME  =" + coffeeName + "";

			boolean x = stmt.execute("select * from COFFEES where SALES =" + nbSales + " and COF_NAME  =" + coffeeName + "");
			int y = stmt.executeUpdate(	"select * from COFFEES where SALES =" + nbSales + " and COF_NAME  =" + coffeeName + "");
			Long z = stmt.executeLargeUpdate("select * from COFFEES where SALES =" + nbSales + " and COF_NAME  =" + coffeeName + "");
			ResultSet rs2 = stmt.executeQuery(query);

			EntityManager em = emf.createEntityManager();			
			String jpaquery = "select a from Account a where a.name =" + coffeeName + " and password=" + name + "";
			Query allAccountQuery2 = em.createQuery(jpaquery);


		} catch (Error e) {
			e.printStackTrace();
		} finally {
			try {

				if (con != null)
					con.close();
			} catch (Error ex) {
			}
			} 
		
	}
}