
package cz.dynawest.svnbot.ex;


import java.io.Serializable;


/**
 *
 * @author Ondrej Zizka
 */
public class SvnBotException extends Exception implements Serializable
{

  public SvnBotException( Throwable cause ) {
    super( cause );
  }


  public SvnBotException( String message, Throwable cause ) {
    super( message, cause );
  }


  public SvnBotException( String message ) {
    super( message );
  }

  

}// class SvnBotException
