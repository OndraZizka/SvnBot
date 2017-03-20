
package cz.dynawest.logging;


import java.io.*;
import java.util.logging.*;


/**
 *
 * @author Ondrej Zizka
 */
public class LoggingUtils
{
  private static final Logger log = Logger.getLogger( LoggingUtils.class.getName() );


  // Look for #/logging.properties by default.
  private static final String DEFAULT_CLASSPATH_SEARCH_PATH = "/logging.properties";
  private static final String DW_DEFAULT_PROPS_PATH = "/cz/dynawest/logging/logging-default.properties";


  
  /** Sets up logging. Uses "#/logging.properties" as default path. */
  public static void initLogging() {
    initLogging("#"+DEFAULT_CLASSPATH_SEARCH_PATH); // #/logging.properties
  }


  /** Sets up logging. */
  public static void initLogging( String filePath ){

    boolean wasFromSysProp = true;
    String logConfigFile = System.getProperty("java.util.logging.config.file");
    if( logConfigFile == null ){
      logConfigFile = filePath;
      wasFromSysProp = false;
    }

    try {
      InputStream is;
      if( logConfigFile.startsWith("#") ){
        is = LoggingUtils.class.getResourceAsStream( logConfigFile.substring(1) );
        // "Use getClass().getClassLoader().findResource("path") instead."
      }else{
        is = new FileInputStream(logConfigFile);
      }

			log.info("Loading logging conf from: "+logConfigFile + (!wasFromSysProp ? "" : " (set in sys var java.util.logging.config.file)") );
      if( is == null ){
        log.warning("Log config file not found: "+logConfigFile+ "  Using LoggingUtils' default.");
        logConfigFile = DW_DEFAULT_PROPS_PATH;
        is = LoggingUtils.class.getResourceAsStream( DW_DEFAULT_PROPS_PATH );
      }

      LogManager.getLogManager().readConfiguration( is );
    }
    catch(IOException ex){
      System.err.println("Error loading logging conf from ["+logConfigFile+"]. Using JDK's default.");
    }
  }



}// class LoggingUtils
