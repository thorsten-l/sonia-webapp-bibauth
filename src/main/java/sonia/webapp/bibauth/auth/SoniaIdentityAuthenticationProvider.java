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

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sonia.webapp.bibauth.api.ApiExceptionHandler;
import sonia.webapp.bibauth.api.HttpUnauthorizedException;
import sonia.webapp.bibauth.configuration.Configuration;
import sonia.webapp.bibauth.configuration.Organization;
import sonia.webapp.bibauth.filter.StoreRequestUriFilter;
import sonia.webapp.bibauth.util.LDAPConnectionUtil;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Component
public class SoniaIdentityAuthenticationProvider
  implements AuthenticationProvider
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    SoniaIdentityAuthenticationProvider.class.getName());

  UserDetails isValidUser(String username, String password)
    throws HttpUnauthorizedException
  {
    UserDetails user = null;

    LOGGER.debug("isValidUser");
    LOGGER.trace("username={} password={}", username, password);

    String requestUri = StoreRequestUriFilter.getRequestUri();
    LOGGER.debug("requestUri={}", requestUri);

    String organizationName
      = (requestUri != null && requestUri.startsWith("/auth/"))
      ? requestUri.substring(6)
      : null;

    Configuration configuration = Configuration.getActiveConfiguration();
    Organization organization = null;

    for (Organization org : configuration.getOrganizations())
    {
      if (org.getName().equals(organizationName))
      {
        organization = org;
        break;
      }
    }

    LOGGER.debug("organization={}", organization);

    if (organization != null)
    {
      try ( LDAPConnection connection
        = LDAPConnectionUtil.getInstance().getConnection())
      {
        String filter = MessageFormat.format(
          organization.getSearchFilter(), username);

        LOGGER.debug("filter={}", filter);

        SearchRequest searchRequest = new SearchRequest(
          organization.getBaseDn(),
          organization.getSearchScope(),
          filter, new String[]
          {
            configuration.getLdapBarcodeAttributeName()
          });

        SearchResult searchResult = connection.search(searchRequest);

        if (searchResult != null
          && searchResult.getResultCode() == ResultCode.SUCCESS
          && searchResult.getEntryCount() == 1)
        {
          LOGGER.debug("searchResult.size={}", searchResult.getEntryCount());
          Entry entry = searchResult.getSearchEntries().get(0);
          String barcode = entry.getAttributeValue(
            configuration.getLdapBarcodeAttributeName());

          if (barcode != null && barcode.trim().length() > 0)
          {
            BindResult bindResult = connection.bind(entry.getDN(), password);

            if (bindResult.getResultCode() == ResultCode.SUCCESS)
            {
              LOGGER.debug("bind success, username={} barcode={}",
                username, barcode);
              user = User
                .withUsername(username)
                .password(barcode) // use password as barcode
                .roles("USER_ROLE")
                .build();
            }
            else
            {
              LOGGER.debug("Bind for user '{}' failed.", username);
            }
          }
        }
        else
        {
          LOGGER.debug("User '{}' not found.", username);
          throw new HttpUnauthorizedException(
            ApiExceptionHandler.RESPONSE_NOT_FOUND);
        }
      }
      catch (LDAPException | GeneralSecurityException ex)
      {
        LOGGER.error("LDAP ERROR: {}", ex.getMessage());
      }
    }
    else
    {
      LOGGER.error("ERROR: unknown organization name: {}", organizationName);
    }
    return user;
  }

  @Override
  public Authentication authenticate(Authentication authentication)
  {
    LOGGER.debug("authenticate");

    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    try
    {
      UserDetails userDetails = isValidUser(username, password);

      if (userDetails != null)
      {
        return new SoniaAuthenticationToken(
          username, "xxxxxx",
          userDetails.getAuthorities(), userDetails.getPassword());
      }
      else
      {
        LOGGER.debug("ApiExceptionHandler.RESPONSE_INVALID_CERDENTIALS");
        throw new BadCredentialsException(
          ApiExceptionHandler.RESPONSE_INVALID_CERDENTIALS);
      }
    }
    catch (HttpUnauthorizedException ex)
    {
      LOGGER.debug("HttpUnauthorizedException: {}", ex.getMessage());
      throw new BadCredentialsException(ex.getMessage());
    }
  }

  @Override
  public boolean supports(Class<?> authenticationType)
  {
    return authenticationType
      .equals(UsernamePasswordAuthenticationToken.class);
  }
}
