package com.pinyougou.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailServiceImpl implements UserDetailsService{
	
	private SellerService sellerService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		TbSeller seller=sellerService.findOne(username);
		if(seller!=null){
			if(seller.getStatus().equals("1")){
				List<GrantedAuthority> list=new ArrayList<>();
				list.add(new SimpleGrantedAuthority("ROLE_USER"));
				return new User(username, seller.getPassword(), list);
			}
		}
		return null;
	}

	public SellerService getSellerService() {
		return sellerService;
	}

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}
	
}
