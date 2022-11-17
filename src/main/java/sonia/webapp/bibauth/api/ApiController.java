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
package sonia.webapp.bibauth.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import sonia.webapp.bibauth.BuildProperties;
import sonia.webapp.bibauth.auth.SonicAuthenticationFacade;
import sonia.webapp.bibauth.configuration.Configuration;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@RestController
public class ApiController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    ApiController.class.getName());

  @Autowired
  private SonicAuthenticationFacade authenticationFacade;

  @GetMapping(
    path = "/",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public BuildProperties homeGET()
  {
    LOGGER.debug("homeGET");
    return BuildProperties.getInstance();
  }

  @GetMapping(
    path = "/build",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public BuildProperties buildGET()
  {
    LOGGER.debug("buildGET");
    return BuildProperties.getInstance();
  }

  @GetMapping(
    path = "/auth/{organization}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public String authGET(
    @PathVariable String organization,
    @RequestHeader("Client-Authorization") String clientAuthorizationToken)
  {
    LOGGER.debug("authGET organization={}", organization);
    LOGGER.trace("clientAuthorizationToken={}", clientAuthorizationToken);
    LOGGER.trace("authenticationFacade={}", authenticationFacade);

    Configuration configuration = Configuration.getActiveConfiguration();

    if (!configuration.isInitialized() || !configuration.
      getClientAuthorizationToken().equals(clientAuthorizationToken))
    {
      LOGGER.error("ERROR: wrong client authorization token: {}",
        clientAuthorizationToken);
      throw new HttpUnauthorizedException(
        ApiExceptionHandler.RESPONSE_INVALID_CLIENT);
    }

    LOGGER.trace("barcode={}", authenticationFacade.getBarcode());

    return "{\"patron\":\"" + authenticationFacade.getBarcode() + "\"}";
  }
}
