package sonia.webapp.bibauth;

import java.io.File;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sonia.webapp.bibauth.configuration.Configuration;
import sonia.webapp.bibauth.crypto.AES256;
import sonia.webapp.bibauth.crypto.AppSecretKey;
import sonia.webapp.bibauth.crypto.PasswordGenerator;

@SpringBootApplication
public class SoniaWebappBibauthApplication
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    SoniaWebappBibauthApplication.class.getName());

  private final static Options OPTIONS = new Options();

  public static void main(String[] args)
  {
    CmdLineParser parser = new CmdLineParser(OPTIONS);

    try
    {
      parser.parseArgument(args);
    }
    catch (CmdLineException ex)
    {
      LOGGER.error("Command line error\n");
      parser.printUsage(System.out);
      System.exit(-1);
    }

    if (OPTIONS.isDisplayHelp())
    {
      System.out.println("\nUsage: ./bibauth.jar [options]\n");
      parser.printUsage(System.out);
      System.exit(0);
    }

    if (OPTIONS.isWriteSampleConfiguration())
    {
      Configuration.writeSampleConfiguration();
      System.exit(0);
    }

    if (OPTIONS.getGeneratePasswordLength() > 0)
    {
      AES256 cipher = new AES256(AppSecretKey.getSecret());
      String password = PasswordGenerator.generate(OPTIONS.
        getGeneratePasswordLength());
      System.out.println("\npassword:  '" + password + "'");
      System.out.println("encrypted: '" + cipher.encrypt(password) + "'\n");
      System.exit(0);
    }

    if (OPTIONS.getPassword() != null)
    {
      AES256 cipher = new AES256(AppSecretKey.getSecret());
      System.out.println("\npassword:  '" + OPTIONS.getPassword() + "'");
      System.out.println("encrypted: '" + cipher.encrypt(OPTIONS.getPassword())
        + "'\n");
      System.exit(0);
    }

    if (OPTIONS.getCheckConfigFile() != null)
    {
      try
      {
        Configuration c = Configuration.load(new File(OPTIONS.getCheckConfigFile()));
        LOGGER.debug("{}",c);
        LOGGER.info( "Configuration OK");
      }
      catch( Exception e )
      {
        System.err.println( "-----------------------" );
      }
       
      System.exit(0);
    }

    if (OPTIONS.isDisplayVersion())
    {
      BuildProperties buildProperties = BuildProperties.getInstance();
      System.out.println("\nProject name    : " + buildProperties.
        getProjectName());
      System.out.println("Project version : " + buildProperties.
        getProjectVersion() + "\n");
      System.exit(0);
    }

    LOGGER.debug( "{}", Configuration.getActiveConfiguration());
    SpringApplication.run(SoniaWebappBibauthApplication.class, args);
  }
}
