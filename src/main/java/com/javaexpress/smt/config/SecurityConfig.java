package com.javaexpress.smt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javassist.bytecode.analysis.Analyzer;

@EnableWebSecurity()
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DaoAuthenticationProvider daoAuthenticationProvider;

	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.authenticationProvider(daoAuthenticationProvider);
	}

	protected void configure(HttpSecurity http) throws Exception {	
		http.authorizeRequests()
		.antMatchers("/admin/**").hasRole("ADMIN")
		.antMatchers("/user/**").hasRole("USER")
		.antMatchers("/**").permitAll()
		.and()
		.formLogin().loginPage("/signin")
		.and()
		.httpBasic()
		.and()
		.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll();
		
	}
}
