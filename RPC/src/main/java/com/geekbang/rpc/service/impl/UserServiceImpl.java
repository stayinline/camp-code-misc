package com.geekbang.rpc.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.geekbang.rpc.service.UserService;

public class UserServiceImpl implements UserService {
	
	private Map<String, String> users = new HashMap<String, String>();
	
	public UserServiceImpl() {
		users.put("user1", "tianyalan1");
		users.put("user2", "tianyalan2");
	}

	@Override
	public String getUserNameByCode(String userCode) {
		System.out.println(userCode + ":userCode");	
		
		return users.get(userCode);
	}
	
}
