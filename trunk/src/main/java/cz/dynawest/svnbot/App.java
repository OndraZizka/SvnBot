package cz.dynawest.svnbot;

import cz.dynawest.svnbot.ex.SvnBotConfigException;
import cz.dynawest.logging.LoggingUtils;
import cz.dynawest.svnbot.config.JaxbConfigReader;
import cz.dynawest.svnbot.config.beans.ConfigBean;
import cz.dynawest.svnbot.config.beans.IrcServerBean;
import cz.dynawest.svnbot.config.beans.PathChannelsBindBean;
import cz.dynawest.svnbot.config.beans.SvnRepoInfoBean;
import cz.dynawest.svnbot.ex.SvnBotException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.tmatesoft.svn.core.SVNException;

/**
 * SvnBot test app.
 *
 */
public class App
{
    private static final Logger log = Logger.getLogger( App.class.getName() );


    /** Configuration. */
    private ConfigBean config;

    /** Watcher threads. */
    private Map<String, SvnRepoWatcherThread> watchers = new HashMap();

    /** IRC bot. */
    private SvnPircBot pircbot;

    private AtomicBoolean running = new AtomicBoolean( false );

    final App self = this;

    private SvnCommitAnnouncer announcer = new SvnCommitAnnouncer() {
      @Override  public void announceCommit( String message, List<String> channels ) {
        for( String channel : channels ) {
          self.pircbot.sendMessage( "#"+ channel, message );
        }
      }
    };


    

    /** Init. */
    private void init() throws SvnBotException {
      
        LoggingUtils.initLogging();

        // Read configuration.
        this.config = new JaxbConfigReader().load();

        // Verify configuration.
        String ver = this.verifyConfig();
        if( ver != null )
          throw new SvnBotConfigException( ver );

        // Apply configuration.
        this.applyConfig();
    }



    /**
     * Applies given config to the application.
     * TODO: Make it "real apply" - callable any time.
     */
    private void applyConfig( ConfigBean config ) throws SvnBotException
    {
      
        // Configure the IRC bot.
        IrcServerBean server = config.irc.servers.get( 0 );

        // If the bot is already connected to some other server, disconnect and dispose it.
        if( this.pircbot != null ){
          if( !this.pircbot.isConnected() ){
            //this.pircbot.dispose(); // TODO: PircBot bug!
            this.pircbot = null;
          }
          else{
            if( this.pircbot.getPort() != server.port  ||  StringUtils.equals( this.pircbot.getServer(), server.host ) ){
              this.pircbot.disconnect();
              this.pircbot.dispose();
              this.pircbot = null;

              this.pircbot = new SvnPircBot( server.host, server.port, config.irc.nick );
              this.pircbot.connectHandleNick();
            }
          }
        }

        // No bot (yet)?
        if( this.pircbot == null ){
          this.pircbot = new SvnPircBot( server.host, server.port, config.irc.nick );
        }

        // Debug channel.
        this.pircbot.joinWhenConnected( this.config.settings.debugChannel );

        // Create watcher threads.  TODO: Really apply - change existing, remove unmatched.
        for( SvnRepoInfoBean repo : config.repositories ){
          SvnRepoWatcherThread watcherThread;
          try {
            if( StringUtils.isEmpty( repo.getUser() )){
              watcherThread = new SvnRepoWatcherThread( repo.getUrl(), this.announcer );
            }else if( StringUtils.isEmpty( repo.getPass() )){
              String pass = System.getProperty("repo."+repo.getName()+".pass", "");
              watcherThread = new SvnRepoWatcherThread( repo.getUrl(), repo.getUser(), pass, this.announcer );
            }
            else{
              watcherThread = new SvnRepoWatcherThread( repo.getUrl(), repo.getUser(), repo.getPass(), this.announcer );
            }
            watcherThread.setCheckInterval( repo.checkInterval );
            
            // Path-channel bind. Currently binds "/" (repo root) to all channels.
            // TODO: Add real bindings, to allow usage of paths to filter the commits.
            PathChannelsBindBean bindChannelsToRoot = new PathChannelsBindBean( "/" );
            for( String channel : repo.getChannels() ){
              bindChannelsToRoot.channels.add( channel );
              this.pircbot.joinWhenConnected( channel );
            }
            watcherThread.getPathChannelBinds().add( bindChannelsToRoot );

            watcherThread.setRepoName( repo.getName() );

            // Store this thread under a name.
            watchers.put( repo.getName(), watcherThread );
          }
          catch( SVNException ex ) {
            log.severe("Error when creating watcher thread: "+ex.getMessage());
          }
        }

    }// applyConfig()
    
    private void applyConfig() throws SvnBotException {
      this.applyConfig( this.config );
    }




    /**
     *  Starts the application - the bot and the watchers.
     */
    private void run() throws SvnBotException{
      if( this.running.get() )
        log.severe("Called run() when already running!");

      if( !pircbot.isConnected() ){
        log.info("Connecting "+pircbot.getName()+" to "+pircbot.getHost_()+":"+pircbot.getPort_()+"...");
        pircbot.connectHandleNick();
        this.joinChannels();
      }

      for( SvnRepoWatcherThread watcher : this.watchers.values() ){
        watcher.start();
      }

      this.running.set( true );
    }



    /**
     * Stop the threads, disconnect the bot.
     */
    private void stop(){

      // Stop the threads.
      for( Entry<String, SvnRepoWatcherThread> e : this.watchers.entrySet() ){
        e.getValue().setStop( true );
        e.getValue().interrupt();
      }

      // Wait for them to die.
      for( SvnRepoWatcherThread watcher : this.watchers.values() ){
        if( !watcher.isAlive() )
          continue;
        try {
          watcher.join( 200 );
          if( watcher.isAlive() )
            log.warning("Watcher still alive: "+watcher.getName());
        }
        catch( InterruptedException ex ) { /* */ }
      }

      this.watchers.clear();

      // Stop the bot.
      this.pircbot.disconnect();
      this.pircbot.dispose();

      this.running.set( false );
    }




    /** Main */
    public static void main( String[] args )
    {
      try {
        App app = new App();
        app.init();
        app.applyConfig();
        app.run();
      }
      catch( Throwable ex ){
        ex.printStackTrace();
      }
    }// main()



    /**
     *  Verifies that the config is OK - nothing missing etc.
     */
    private String verifyConfig() {
      if( this.config == null )
        return "No config.";

      // IRC.
      if( this.config.irc == null )
        return "No irc config.";

      if( this.config.irc.servers == null )
        return "No IRC servers object in config.";

      if( this.config.irc.servers.size() == 0 )
        return "No configured IRC servers.";

      // Repos.
      if( this.config.repositories == null )
        return "No repos config.";

      if( this.config.repositories.size() == 0 )
        return "No configured repositories.";

      return null;
    }


    /**
     *  Joins channels where the bot should be.
     */
    private void joinChannels() {
      if( this.config.settings.debugChannel != null )
      this.pircbot.joinChannel( this.config.settings.debugChannel );
      //this.config.repositories
    }


    
}// class
