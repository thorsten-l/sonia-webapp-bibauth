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

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Getter
public class SoniaAuthenticationToken extends UsernamePasswordAuthenticationToken
{
  private static final long serialVersionUID = 5458917446489807290L;
  
  public SoniaAuthenticationToken(
    Object principal, Object credentials,
    Collection<? extends GrantedAuthority> authorities,
    String barcode)
  {
    super(principal, credentials, authorities);
    this.barcode = barcode;
  }

  private final String barcode;
}
