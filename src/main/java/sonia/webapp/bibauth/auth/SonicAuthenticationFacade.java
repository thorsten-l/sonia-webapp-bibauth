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
