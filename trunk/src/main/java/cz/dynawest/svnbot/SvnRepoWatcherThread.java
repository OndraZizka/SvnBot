
package cz.dynawest.svnbot;


import cz.dynawest.svnbot.config.beans.PathChannelsBindBean;
import cz.dynawest.svnbot.ex.SvnBotException;
import java.util.*;
import java.util.logging.*;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


/**
 * Thread which checks SVN log periodically and notifies the announcer about the new ones.
 * Currently announces all commits to this repo on all channels.
 *
 * TODO: Eventually split to some SvnCommitAnnouncer, which would get LogEntry
 *       and dispatch it approprietly.
 * 
 * @author Ondrej Zizka
 */
public class SvnRepoWatcherThread extends Thread
{
  private static final Logger log = Logger.getLogger( SvnRepoWatcherThread.class.getName() );


  /**
   *   SVN repo.
   */
  private SVNRepository repository;


  /**
   *   SVN path to IRC channel binds.
   */
  private List<PathChannelsBindBean> pathChannelBinds = new ArrayList();
  public List<PathChannelsBindBean> getPathChannelBinds() {    return pathChannelBinds;  }
  public List<String> getAllChannels(){
    List<String> channels = new ArrayList();
    for( PathChannelsBindBean bind : pathChannelBinds ) {
      channels.addAll( bind.channels );
    }
    return channels;
  }

  /**
   *   IRC announcer.
   */
  private final SvnCommitAnnouncer announcer;


  /**
   *   Announcement IRC msg formatter.
   */
  IrcAnnouncementFormatter formatter = new IrcAnnouncementFormatter("r%rev by %user changed %commonPath: %msg"); // TODO: Config

  /**
   *   Check interval, in seconds. Must be > 5.
   */
  public int checkInterval = 60;
  public void setCheckInterval( int sec ) { if( sec > 5 ) this.checkInterval = sec;  }


  /**
   *   Last seen revision.
   */
  private long lastRevision = -1;  // TODO: Load from persisted state.
  public synchronized long getLastRevision() {    return lastRevision;  }
  public synchronized void setLastRevision( long lastRevision ) {    this.lastRevision = lastRevision;  }



  /**
   *   Stop flag - run() ends if this is set to true.
   */
  boolean stop;
  public synchronized boolean isStop() {    return stop;  }
  public synchronized void setStop( boolean stop ) {    this.stop = stop;  }



  // --- Const --- //


  /** Init */
  private void init() {
    DAVRepositoryFactory.setup( );
  }


  /**
   *   Initiates a SVN repo with the given URL.
   */
  public SvnRepoWatcherThread( String url, SvnCommitAnnouncer announcer ) throws SVNException {
    this.init();
    this.repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( url ) );
    this.announcer = announcer;
  }


  /**
   *   Initiates a SVN repo with the given URL, user name and password.
   */
  public SvnRepoWatcherThread( String url, String user, String pass, SvnCommitAnnouncer announcer ) throws SVNException {
    this.init();
    // URL
    this.repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( url ) );
    // Auth
    ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( user, pass );
    this.repository.setAuthenticationManager( authManager );
    // Announcer
    this.announcer = announcer;
  }





  /**
   *  Thread's run();  Periodically checks SVN repo for updates and handles the matching ones.
   */
  @Override
  public void run() {

    // --- Checking LOOP ---
    do{
      try {
        // Check.
        log.fine(this.getName() + ": Checking repository for updates.");
        List<String> announcements = this.checkRepoUpdates();
        if( announcements != null ){
          for( String announcement : announcements ) {
            announcer.announceCommit( announcement, this.getAllChannels() );
          }
        }
      }
      catch( SvnBotException ex ) {
        log.warning( ex.getMessage() );
      }


      // Sleep.
      try {
        Thread.sleep( this.checkInterval * 1000 );  // TODO: Config
      }
      // Handle if being stopped.
      catch( InterruptedException ex ) {
        log.fine( this.getName() + ": SVN watcher interrupted, checking for the stop flag." );
        if( this.isStop() ){
          log.fine( this.getName() + ": Stopping." );
          break;
        }
      }

      log.fine( this.getName() + ": Waking up.");
    }while( true );

  }


  /**
   *   Checks the repository for updates.
   *   Scans the log since the last checked.
   *
   *   @returns  A list of announcements.
   */
  private List<String> checkRepoUpdates() throws SvnBotException {

    
    // Revisions.
    long endRevision = -1; //HEAD (the latest) revision
    long startRevision = this.lastRevision +1;

    // If this is a first check (we don't remember last rev), only store the last rev number and skip.
    if( startRevision == 0 ){
      try {
        long latestRev = this.repository.getLatestRevision();
        log.info( this.getName() + ": This is a first check. Storing current last rev: "+latestRev);
        this.setLastRevision( latestRev );
      }
      catch( SVNException ex ) {
        log.severe( this.getName() + ": Error when getting last revision: "+ex.getMessage() );
      }
      return null;
    }



    List<String> announcements = new ArrayList();

    // Check.
    try {
      log.info( this.getName() + ": Checking for revisions since r"+this.lastRevision);

      long latestRev = this.repository.getLatestRevision();
      if( latestRev <= this.lastRevision )
        return null;

      Collection logEntries = this.repository.log( new String[] { "" }, null, startRevision, endRevision, true, true );
      for ( Iterator entries = logEntries.iterator( ); entries.hasNext( ); ) {
        SVNLogEntry logEntry = ( SVNLogEntry ) entries.next( );
        // Format a message.
        String announcement = this.formatter.formatAnnouncement( logEntry );
        log.info( this.getName() + ": Announcement: " + announcement );
        announcements.add( announcement );
      }
      
      this.lastRevision = latestRev;
    }
    catch( SVNException ex ) {
      throw new SvnBotException( "SVN error when checking the repo: "+ex.getMessage(), ex );
    }

    return announcements;
  }




  /**
   *   Checks the repository for updates.
   *   Scans the log since the last checked.
   *
   *   @returns  A list of announcements.
   */
  private List<String> checkRepoUpdatesOld() throws SvnBotException {

    List<String> announcements = null;
    boolean onlyStoreLastRevNumber = false;

    // Revisions.
    long endRevision = -1; //HEAD (the latest) revision
    long startRevision = lastRevision +1;
    // If no seen revision yet, skip this turn and remember the current rev as the last seen.
    if( startRevision == 0 ){
      onlyStoreLastRevNumber = true;
      startRevision = -1;
    } else {
      announcements = new ArrayList();
    }

    // Check.
    try {
      log.info("Checking for revisions since r"+startRevision);
      Collection logEntries = repository.log( new String[] { "" }, null, startRevision, endRevision, true, true );
      for ( Iterator entries = logEntries.iterator( ); entries.hasNext( ); ) {
        SVNLogEntry logEntry = ( SVNLogEntry ) entries.next( );
        // If this is a first check (we don't remember last rev), only store the last rev number and skip.
        if( onlyStoreLastRevNumber ){
          this.setLastRevision( logEntry.getRevision() );
          log.info("This is a first check. Storing current last rev: "+this.lastRevision);
          return null;
        }
        // Format a message.
        String announcement = formatter.formatAnnouncement( logEntry );
        log.fine( "Announcement: " + announcement );
        announcements.add( announcement );
      }

    }
    catch( SVNException ex ) {

      throw new SvnBotException( "SVN error when checking the repo: "+ex.getMessage(), ex );
    }

    return announcements;
  }



  /**
   * 
   */
  public void setRepoName( String name ){
    this.setName( THREAD_NAME_PREFIX + name );
  }
  public static final String THREAD_NAME_PREFIX = "SVN_watcher_";
  




}// class SvnRepoWatcherThread
