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
package sonia.webapp.bibauth.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import sonia.webapp.bibauth.api.ApiExceptionHandler;
import sonia.webapp.bibauth.api.ErrorResponseObject;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Component
public class SoniaBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    SoniaBasicAuthenticationEntryPoint.class.getName());

  private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  public void commence(
    HttpServletRequest request, HttpServletResponse response,
    AuthenticationException authenticationException)
    throws IOException
  {
    LOGGER.debug("commence");

    response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName());
    response.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE
      + ";charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    LOGGER.debug("AuthenticationException: {}", authenticationException.
      getMessage());

    ErrorResponseObject error = new ErrorResponseObject(
      ApiExceptionHandler.RESPONSE_INVALID_CERDENTIALS, "Password incorrect");

    if (authenticationException.getMessage().equals(
      ApiExceptionHandler.RESPONSE_NOT_FOUND))
    {
      error = new ErrorResponseObject(
        ApiExceptionHandler.RESPONSE_NOT_FOUND, "User does not exist");
    }

    OBJECT_MAPPER.writeValue(response.getWriter(), error);
  }

  @Override
  public void afterPropertiesSet()
  {
    LOGGER.debug("afterPropertiesSet");
    setRealmName("BibAuth");
    super.afterPropertiesSet();
  }
}
