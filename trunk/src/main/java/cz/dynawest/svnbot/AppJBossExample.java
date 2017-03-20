package cz.dynawest.svnbot;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Hello world!
 *
 */
public class AppJBossExample 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        DAVRepositoryFactory.setup( );

        String url = "http://anonsvn.jboss.org/repos/embjopr/";
        String name = "anonymous";
        String password = "anonymous";
        long startRevision = -10;
        long endRevision = -1; //HEAD (the latest) revision

        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( url ) );
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( name, password );
            repository.setAuthenticationManager( authManager );


            Collection logEntries = null;

            logEntries = repository.log( new String[] { "" } , null , startRevision , endRevision , true , true );

            for ( Iterator entries = logEntries.iterator( ); entries.hasNext( ); ) {
              SVNLogEntry logEntry = ( SVNLogEntry ) entries.next( );
              System.out.println( "---------------------------------------------" );
              System.out.println ("revision: " + logEntry.getRevision( ) );
              System.out.println( "author: " + logEntry.getAuthor( ) );
              System.out.println( "date: " + logEntry.getDate( ) );
              System.out.println( "log message: " + logEntry.getMessage( ) );

              if ( logEntry.getChangedPaths( ).size( ) > 0 ) {
                  System.out.println( );
                  System.out.println( "changed paths:" );
                  Set changedPathsSet = logEntry.getChangedPaths( ).keySet( );

                  // Paths
                  for ( Iterator changedPaths = changedPathsSet.iterator( ); changedPaths.hasNext( ); ) {
                      SVNLogEntryPath entryPath = ( SVNLogEntryPath ) logEntry.getChangedPaths( ).get( changedPaths.next( ) );
                      System.out.println( " "
                              + entryPath.getType( )
                              + " "
                              + entryPath.getPath( )
                              + ( ( entryPath.getCopyPath( ) != null ) ? " (from "
                                      + entryPath.getCopyPath( ) + " revision "
                                      + entryPath.getCopyRevision( ) + ")" : "" ) );
                  }
              }
          }


        } catch( Throwable t ){}

    }// main()
}// class
