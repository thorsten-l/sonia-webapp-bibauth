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
package sonia.webapp.bibauth.util;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLSocketFactory;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.webapp.bibauth.configuration.Configuration;
import sonia.webapp.bibauth.configuration.LdapConfig;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Getter
public class LDAPConnectionUtil
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    LDAPConnectionUtil.class.getName());

  private final static LDAPConnectionUtil SINGLETON = new LDAPConnectionUtil();
 
  public static LDAPConnectionUtil getInstance()
  {
    return SINGLETON;
  }
  
  public LDAPConnection getConnection() throws LDAPException, GeneralSecurityException
  {
    LDAPConnection ldapConnection;
    LdapConfig ldapConfig = Configuration.getActiveConfiguration().getLdapConfig();
    
    if (ldapConfig.isSslEnabled())
    {
      ldapConnection = new LDAPConnection(createSSLSocketFactory(),
        ldapConfig.getHostname(), ldapConfig.getPort(), 
        ldapConfig.getCredentials().getBindDN(),
        ldapConfig.getCredentials().getPassword());
    }
    else
    {
      ldapConnection = new LDAPConnection(
        ldapConfig.getHostname(), ldapConfig.getPort(), 
        ldapConfig.getCredentials().getBindDN(), 
        ldapConfig.getCredentials().getPassword());
    }

    return ldapConnection;
  }

  private static SSLSocketFactory createSSLSocketFactory() throws
    GeneralSecurityException
  {
    SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    return sslUtil.createSSLSocketFactory();
  }
  
}
