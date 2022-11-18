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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dr. Thorsten Ludewig <t.ludewig@gmail.com>
 */
@Component
public class SonicAuthenticationFacade
{

  public Authentication getAuthentication()
  {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public String getBarcode()
  {
    String barcode = null;

    Authentication authentication = getAuthentication();

    if (authentication != null
      && authentication instanceof SoniaAuthenticationToken)
    {
      barcode = ((SoniaAuthenticationToken) authentication).getBarcode();
    }

    return barcode;
  }
}
