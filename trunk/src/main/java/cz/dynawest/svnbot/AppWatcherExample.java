package cz.dynawest.svnbot;

import cz.dynawest.logging.LoggingUtils;
import java.util.logging.Logger;

/**
 * SvnBot test app.
 *
 */
public class AppWatcherExample 
{
    private static final Logger log = Logger.getLogger( AppWatcherExample.class.getName() );


    /** Main */
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        LoggingUtils.initLogging();


        String url = "http://anonsvn.jboss.org/repos/embjopr/";
        String name = "anonymous";
        String password = "anonymous";
        long startRevision = -10;
        long endRevision = -1; //HEAD (the latest) revision


        
        try {

          log.info("Creating and starting the thread.");
          SvnRepoWatcherThread watcher = new SvnRepoWatcherThread( url, null );
          Thread watcherThread = new Thread( watcher );
          watcherThread.start();

          log.fine("Sleeping for 15 sec.");
          Thread.sleep( 600*1000 );

          log.fine("Stopping the thread.");
          watcher.setStop( true );
          watcherThread.interrupt();
        }
        catch( Throwable t ){
          log.severe( t.toString() );
        }

    }// main()

    
}// class
