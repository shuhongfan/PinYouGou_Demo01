package com.shf.pyg.shop.service;

import com.shf.pyg.pojo.TbSeller;
import com.shf.pyg.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

public class UserDetailsServiceImpl implements UserDetailsService {
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("经过UserDetailsServiceImpl");
//        根据username查询数据库得到用户对象
        ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

//        根据登录名称查询商家对象
        TbSeller seller = sellerService.findOne(username);
        if (seller==null){
            return null;
        } else {
//            判断用户是否被禁用
            if ("1".equals(seller.getStatus())){
                return new User(username,seller.getPassword(), authorities);
            } else {
                return null;
            }
        }
    }
}
