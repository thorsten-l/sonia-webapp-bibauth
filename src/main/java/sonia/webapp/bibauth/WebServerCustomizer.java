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

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import sonia.webapp.bibauth.configuration.Configuration;
import sonia.webapp.bibauth.configuration.WebServerConfig;

/**
 *
 * @author Dr. Thorsten Ludewig, t.ludewig@gmail.com
 */
@Component
public class WebServerCustomizer
  implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    WebServerCustomizer.class.getName());

  @Override
  public void customize(TomcatServletWebServerFactory factory)
  {
    LOGGER.debug("customize");

    WebServerConfig config = Configuration.getActiveConfiguration()
      .getWebServerConfig();

    String contextPath = config.getContextPath();
    if (contextPath != null && contextPath.trim().length() > 0)
    {
      factory.setContextPath(contextPath);
    }
    LOGGER.info("Web server context path: '{}'", contextPath);

    factory.setPort(config.getPort());
    LOGGER.info("Web server port: {}", config.getPort());
    
    factory.getSession().setTimeout(Duration.ofMinutes(
      config.getSessionTimeoutMinutes()));
    LOGGER.info("Web server session timeout in minutes: {}", 
      config.getSessionTimeoutMinutes());
  }
}
