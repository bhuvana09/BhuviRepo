package com.cognizant.gto.ccap.springmvc.dao;

import java.io.File;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import com.cognizant.gto.ccap.springmvc.model.Login;
import com.cognizant.gto.ccap.springmvc.model.User;
import javax.net.ssl.SSLSocketFactory;

public class UserDaoImpl implements UserDao {
	@Autowired
	DataSource datasource;
	@Autowired
	JdbcTemplate jdbcTemplate;
	Login login;
	User user;
	String xx = "gdf";
	String yy = "dfgag";
	String input = "ff";
	String fname="r";

	private static final PersistenceManagerFactory pmfInstance = JDOHelper 
			   .getPersistenceManagerFactory("transactions-optional"); 
	
	public static PersistenceManagerFactory getInstance() { 
		  return pmfInstance; 
		 } 
	//String sql = "select * from users where username='"+login.getUsername()+"' and password='"+login.getPassword()+"";
	
	public void register(User user) {
		String sql = "insert into users values(?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { user.getUsername(), user.getPassword(), user.getFirstname(),
				user.getLastname(), user.getEmail(), user.getAddress(), user.getPhone() });
	}

	@GET
	@Path("/images/{image}")
	@Produces("images/*")
	public boolean getImage(@javax.ws.rs.PathParam("image") String image) {
		try {
	    File file = new File("resources/images/", image); //Weak point
	    //File file1 = new File("resources/images/", FilenameUtils.getName(image));
	    if (!file.exists()) {
	        return false;
	    }
	    return true;
		}catch (Error e){
			
		}
		return false;
	}

	public User validateUser(Login login) {
			int val = 512;
			PersistenceManagerFactory pmf = UserDaoImpl.getInstance();
			  String slit = "AES/ECB/NoPadding";
			  PersistenceManager pm = pmf.getPersistenceManager(); 
			  Query q = pm.newQuery("select * from Users where name = " + input);
			  q.declareParameters(input);
			  q.execute();
			  try {
				Socket soc = SSLSocketFactory.getDefault().createSocket("www.google.com", 80);
				//Socket sock = SSLSocketFactory.getDefault().createSocket("www.google.com", 443);
				  
				  KeyPairGenerator keyGenpair = KeyPairGenerator.getInstance("RSA");
				  keyGenpair.initialize(val);
				  
				  String slit1 = "AES/ECB/NoPadding";
				  String str1 = "235263623";
				  byte[] testBytes1 = str1.getBytes();
				  Key k = null;
				  SecureRandom iv = null;
				
				  Cipher c = Cipher.getInstance(slit);
				  
				  Cipher c2 = Cipher.getInstance(slit1);
				  c.init(Cipher.ENCRYPT_MODE, k, iv);
				  byte[] cipherText = c.doFinal(testBytes1);
				  
					KeyGenerator keyGen = KeyGenerator.getInstance("Blowfish");
					keyGen.init(64);
					
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					String str = "235263623";
					byte[] testBytes = str.getBytes();
					byte[] resultBytes = md.digest(testBytes);
					Long valL = 12345678910L;
					Integer intt = 123564;
					StringBuilder stringBuilder = new StringBuilder();
					// stringBuilder.append( Integer.toHexString( resultBytes[0] & 0xFF ) );
					String sr = Integer.toHexString(intt);
					 stringBuilder.append( Integer.toHexString(intt));
					
				/*} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e) {
					e.printStackTrace();
				}*/
					 
			  } catch (Exception e) {
					e.printStackTrace();
				} 
			  
			  
		String sql = "select * from users where username='"+login.getUsername()+"' and password='"+login.getPassword()+"";  
		
		List<User> users1 = jdbcTemplate.query("select * from users where username=? and password=?",
				new UserMapper(), login.getUsername(), login.getPassword()); 
	  
		String queryone = "select * from users where username='"+login.getUsername()+"' and password='"+login.getPassword()+"";
		List<User> users = jdbcTemplate.query(sql, new UserMapper());	  
		//List<User> users1 = jdbcTemplate.query("select * from users where username='"+login.getUsername()+"' and password='"+login.getPassword()+"", new UserMapper());
		
		String paramName = "paramName";
		
		JdbcTemplate jdbc = new JdbcTemplate();
		int count = jdbc.queryForObject("select count(*) from Users where name ="+ paramName + "", Integer.class);
		
		ResultSetExtractor<String> rse = new ResultSetExtractor<String>() {

			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		RowMapper<String> rm = new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet arg0, int arg1) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		PreparedStatementSetter pms = new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement arg0) throws SQLException {
				
				// TODO Auto-generated method stub
				arg0.setString(1, login.getUsername());
				arg0.setString(2, login.getPassword());
			}
		};
		
		List<String> y  = jdbcTemplate.query(queryone, pms, rm);
		
		String x = jdbcTemplate.query(sql, rse);
		
		return users1.size() > 0 ? users1.get(0) : null;
	}
}

class UserMapper implements RowMapper<User> {
	public User mapRow(ResultSet rs, int arg1) throws SQLException {
		User user = new User();
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		user.setFirstname(rs.getString("firstname"));
		user.setLastname(rs.getString("lastname"));
		user.setEmail(rs.getString("email"));
		user.setAddress(rs.getString("address"));
		user.setPhone(rs.getInt("phone"));
		return user;
	}
}
