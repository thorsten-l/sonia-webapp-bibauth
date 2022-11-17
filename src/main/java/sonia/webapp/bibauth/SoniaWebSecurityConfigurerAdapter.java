/*
 * Copyright 2022 Thorsten Ludewig (t.ludewig@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sonia.webapp.bibauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import sonia.webapp.bibauth.auth.SoniaBasicAuthenticationEntryPoint;
import sonia.webapp.bibauth.filter.StoreRequestUriFilter;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Configuration
@EnableWebSecurity
public class SoniaWebSecurityConfigurerAdapter
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    SoniaWebSecurityConfigurerAdapter.class.getName());

  @Autowired
  private SoniaBasicAuthenticationEntryPoint authenticationEntryPoint;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
  {
    LOGGER.debug("filterChain");
    http.csrf().disable().authorizeRequests()
      .antMatchers("/", "/build")
      .permitAll()
      .anyRequest()
      .authenticated()
      .and()
      .httpBasic()
      .authenticationEntryPoint(authenticationEntryPoint);

    return http.build();
  }

  @Bean
  public FilterRegistrationBean<StoreRequestUriFilter> registerStoreRequestUriFilter()
  {
    LOGGER.debug("registerStoreRequestUriFilter");
    FilterRegistrationBean<StoreRequestUriFilter> registrationBean
      = new FilterRegistrationBean<>();

    registrationBean.setFilter(new StoreRequestUriFilter());
    registrationBean.addUrlPatterns("/auth/*");
    registrationBean.setOrder(Integer.MIN_VALUE); // must be the very first in chain

    return registrationBean;
  }

}
