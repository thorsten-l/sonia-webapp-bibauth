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

//~--- non-JDK imports --------------------------------------------------------

import lombok.Getter;
import lombok.Setter;

import org.kohsuke.args4j.Option;

/**
 *
 * @author Dr. Thorsten Ludewig (t.ludewig@ostfalia.de)
 */
public class Options
{

  /**
   * Field description
   */
  @Option(
    name = "--help",
    aliases = "-h",
    usage = "Displays this help"
  )
  @Getter
  @Setter
  private boolean displayHelp = false;

  /**
   * Field description
   */
  @Option(
    name = "--version",
    aliases = "-v",
    usage = "Display programm version"
  )
  @Getter
  @Setter
  private boolean displayVersion = false;

  /**
   * Field description
   */
  @Option(
    name = "--generate",
    aliases = "-g",
    usage = "Generate random password"
  )
  @Getter
  @Setter
  private int generatePasswordLength = 0;
  
  /**
   * Field description
   */
  @Option(
    name = "--encrypt",
    aliases = "-e",
    usage = "Encrypt given password"
  )
  @Getter
  @Setter
  private String password = null;

  /**
   * Field description
   */
  @Option(
    name = "--check",
    aliases = "-c",
    usage = "Check given config file"
  )
  @Getter
  @Setter
  private String checkConfigFile = null;

  @Option(
    name = "--write-sample-configuration",
    usage = "Write sample configuration file"
  )
  @Getter
  @Setter
  private boolean writeSampleConfiguration = false;

}
