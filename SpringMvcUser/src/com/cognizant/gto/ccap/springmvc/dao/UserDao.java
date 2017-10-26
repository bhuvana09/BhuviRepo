package com.cognizant.gto.ccap.springmvc.dao;

import java.sql.SQLException;

import com.cognizant.gto.ccap.springmvc.model.Login;
import com.cognizant.gto.ccap.springmvc.model.User;

public interface UserDao {
	void register(User user);

	User validateUser(Login login) throws SQLException ;
}