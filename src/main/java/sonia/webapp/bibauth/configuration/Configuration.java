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
package sonia.webapp.bibauth.configuration;

import com.unboundid.ldap.sdk.SearchScope;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.helpers.DefaultValidationEventHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.webapp.bibauth.crypto.PasswordGenerator;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@ToString(callSuper = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class Configuration
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    Configuration.class.getName());

  private final static String CONFIGURATION_FILE = "configuration.xml";

  private static Configuration activeConfiguration = new Configuration();

  public Configuration()
  {
    timestamp = System.currentTimeMillis();
    initialized = false;
  }

  public static Configuration load(File configurationFile) throws Exception
  {
    LOGGER.info("Loading configuration file: {}", configurationFile.
      getAbsolutePath());
    // Configuration c = JAXB.unmarshal(configurationFile, Configuration.class);

    JAXBContext context = JAXBContext.newInstance(Configuration.class);
    Unmarshaller unmarshaller = context.createUnmarshaller();
    unmarshaller.setEventHandler(new DefaultValidationEventHandler());
    Configuration c = (Configuration) unmarshaller.unmarshal(configurationFile);

    activeConfiguration = c;
    activeConfiguration.initialized = true;

    LOGGER.debug("activeConfiguration.initialized = true");

    return c;
  }

  private synchronized void check() throws Exception
  {
    // double checkStart = System.currentTimeMillis();
    File configurationFile = new File(CONFIGURATION_FILE);

    if (activeConfiguration.initialized == false)
    {
      LOGGER.info("Loading configuration");
      load(configurationFile);
    }
    else
    {
      LOGGER.debug("Check for update configuration");

      if (activeConfiguration.timestamp < configurationFile.lastModified())
      {
        LOGGER.debug("Updating configuration");
        load(configurationFile);
      }
    }
  }

  public static Configuration getActiveConfiguration()
  {
    if (activeConfiguration == null
      || activeConfiguration.initialized == false
      || activeConfiguration.liveConfiguration == true )
    {
      try
      {
        activeConfiguration.check();
      }
      catch (Exception e)
      {
        LOGGER.error("ERROR: Get active configuration {}", e);
      }
    }
    
    return activeConfiguration;
  }

  public static void writeSampleConfiguration()
  {
    LOGGER.info("writing sample configuration file: {}", CONFIGURATION_FILE);
    Configuration c = new Configuration();
    c.version = "1.0";
    c.description = "bibauth configuration file";
    c.liveConfiguration = false;
    c.ldapBarcodeAttributeName = "barcode";
    c.webServerConfig = new WebServerConfig("",8080,5);
    Credentials cred = new Credentials("cn=SuperDuperAdmin", "hotsecret");
    LdapConfig ldapConfig = new LdapConfig("localhost", 3636, true, cred);
    c.ldapConfig = ldapConfig;

    String plainSecurityToken = PasswordGenerator.generate(35);
    System.out.
      println("\nplain security token:  '" + plainSecurityToken + "'\n");
    c.clientAuthorizationToken = plainSecurityToken;

    List<Organization> ol = new ArrayList<>();
    ol.add(new Organization("orgA", "ou=people,o=org-a.de,dc=text,de=de",
      "(&(objectClass=person)(uid={0}))",
      SearchScope.ONE));
    ol.add(new Organization("orgB", "o=org-b.de,dc=text,de=de",
      "(&(objectClass=eduperson)(cn={0}))",
      SearchScope.SUB));

    c.organizations = ol;

    try ( FileWriter writer = new FileWriter(CONFIGURATION_FILE))
    {
      JAXB.marshal(c, writer);
    }
    catch (IOException ex)
    {
      LOGGER.error("ERROR: Can not write configuration file: {}",
        CONFIGURATION_FILE);
    }
  }

  @XmlTransient
  private final long timestamp;

  @XmlTransient
  private boolean initialized;

  @XmlAttribute
  private String version;

  private String description;

  private boolean liveConfiguration;

  private WebServerConfig webServerConfig;
  
  private LdapConfig ldapConfig;
  
  private String ldapBarcodeAttributeName;

  @XmlJavaTypeAdapter(sonia.webapp.bibauth.configuration.XmlPasswordAdapter.class)
  private String clientAuthorizationToken;

  @XmlElementWrapper(name = "organizations")
  @XmlElement(name = "organization")
  private List<Organization> organizations;
}
