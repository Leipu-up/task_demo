package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author 作者:zhanglei
 * @version 创建时间:2026/4/14 11:05
 * @Description 描述:
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())          // 禁用 CSRF 以便于测试
				.authorizeHttpRequests(auth -> auth
						.anyRequest().authenticated()      // 所有请求都需要认证
				)
				.httpBasic(Customizer.withDefaults()); // 启用 HTTP Basic 认证

		return http.build();
	}

	@Bean
	public UserDetailsService users() {
		// 创建内存用户，密码使用 {noop} 前缀表示明文存储（仅用于演示）
		UserDetails user1 = User.builder()
				.username("user1")
				.password("{noop}pass1")
				.roles("USER")
				.build();

		UserDetails user2 = User.builder()
				.username("user2")
				.password("{noop}pass2")
				.roles("USER")
				.build();

		UserDetails user3 = User.builder()
				.username("user3")
				.password("{noop}pass3")
				.roles("USER")
				.build();

		UserDetails user4 = User.builder()
				.username("user4")
				.password("{noop}pass4")
				.roles("USER")
				.build();

		UserDetails admin = User.builder()
				.username("admin")
				.password("{noop}admin")
				.roles("ADMIN")
				.build();

		return new InMemoryUserDetailsManager(user1, user2, user3, user4, admin);
	}
}
