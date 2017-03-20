
package cz.dynawest.svnbot;


import cz.dynawest.svnbot.ex.SvnBotException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;
import org.apache.commons.lang.StringUtils;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;


/**
 *
 * @author Ondrej Zizka
 */
class SvnPircBot extends PircBot
{
  private static final Logger log = Logger.getLogger( SvnPircBot.class.getName() );

  
  private String host;
  public String getHost_() {    return this.host;  }

  private int port;
  public int getPort_(){ return this.getPort() == -1 ? this.port : this.getPort(); }

  private String nick;



  private Set<String> channelsToJoin = new HashSet();

  public void joinWhenConnected( String channel ){
    channel = "#" + StringUtils.stripStart( channel, "#");
    if( this.isConnected() )
      joinChannel( channel );
    else
      channelsToJoin.add( channel );
  }


  public SvnPircBot( String host, int port, String nick ) {
    this.host = host;
    this.port = port;
    this.nick = nick;
  }





  /**
   * Connect; Handle the case when nick is already in use.
   *
   */
  void connectHandleNick( ) throws SvnBotException {
    connectHandleNick( this.host, this.port, this.nick );
  }

  /**
   * Connect; Handle the case when nick is already in use.
   */
  private void connectHandleNick( String host, int port, final String nick_ ) throws SvnBotException {

    // Try connecting until the nick is OK.
    // Other errors still throw an exception.
    try {
      int tries = 0;
      do {
        String nick = tries == 0 ? nick_ : nick_ + tries;
        try {
          this.setName( nick );
          this.connect( host, port );
          break;
        }
        catch( NickAlreadyInUseException ex ) {
          log.warning("Nick already in use: "+nick);
        }
      } while( true );
    }
    catch( IrcException ex ) {
      throw new SvnBotException( ex.getMessage(), ex );
    }
    catch( IOException ex ) {
      throw new SvnBotException( ex.getMessage(), ex );
    }

    // Join the channels.
    for( String channel : this.channelsToJoin ) {
      this.joinChannel( channel );
    }
    channelsToJoin.clear();

  }// connectHandleNick()



}// class SvnPircBot
