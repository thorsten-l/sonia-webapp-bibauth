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
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
public class XmSearchScopeAdapter extends XmlAdapter<String, SearchScope>
{
  @Override
  public SearchScope unmarshal(String value) throws Exception
  {
    SearchScope searchScope = SearchScope.BASE;
    
    switch( value )
    {
      case "ONE":
        searchScope = SearchScope.ONE;
        break;
      case "SUB":
        searchScope = SearchScope.SUB;
        break;
      case "SUBORDINATE_SUBTREE":
        searchScope = SearchScope.SUBORDINATE_SUBTREE;
        break;
    }
    
    return searchScope;
  }

  @Override
  public String marshal(SearchScope searchScope) throws Exception
  {
    return searchScope.getName();
  }
}
