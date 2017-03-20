
package cz.dynawest.svnbot;




import java.util.*;
import java.util.logging.*;
import org.apache.commons.lang.StringUtils;
import org.tmatesoft.svn.core.SVNLogEntry;


/**
 *
 * @author Ondrej Zizka
 */
public class IrcAnnouncementFormatter 
{
  private static final Logger log = Logger.getLogger( IrcAnnouncementFormatter.class.getName() );

  
  
  /**
   *   IRC message format.
   *   Default:
   *    r123 by ozizka@redhat.com changes .../embjopr/jsfunit:  POM - added another Maven workaround.
   *    r910 by ozizka@redhat.com changed /branches/EmbJopr-1.4.0-SN-EAP5/jsfunit:
   */
  private String messageFormat = "r%rev by %user changes %commonPath: %msg";



  /**
   *  Const
   */
  public IrcAnnouncementFormatter( String messageFormat ) {
    this.messageFormat = messageFormat;
  }





  

  /**
   * Formats the announcement IRC message from the SVN log entry.
   * @param logEntry
   */
  public String formatAnnouncement( SVNLogEntry logEntry )
  {
    String announcement = this.messageFormat;

    // Make the log message shorter.
    String message = logEntry.getMessage();
		if( null == message ){
			message = "";
		}
		else if( message.contains("\n") ){
      int thirdEnter = StringUtils.lastOrdinalIndexOf(message, "\n", 3);
      if( thirdEnter != -1 )
        message = message.substring(0, thirdEnter);  // first N lines. TODO: Config
      if( !message.startsWith("\n") )
        message = "\n"+message;
    }

    // Paths
    String commonPath = "SVN metadata";
    int pathsNum = logEntry.getChangedPaths( ).size( );
    if ( pathsNum > 0 ) {
      String paths[] = new String[pathsNum];
      Set<String> changedPathsSet = logEntry.getChangedPaths().keySet();
      int i = 0;
      for( String path : changedPathsSet ) {
        paths[i++] = path;
      }
      commonPath = StringUtils.getCommonPrefix( paths );
      commonPath = StringUtils.abbreviate( commonPath, commonPath.length(), 40); // TODO: Config
    }


    announcement = announcement.replace( "%rev", ""+logEntry.getRevision() );
    announcement = announcement.replace( "%user", logEntry.getAuthor() );
    announcement = announcement.replace( "%msg", message );
    announcement = announcement.replace( "%commonPath", commonPath );
    //announcement.replace( "%", logEntry.get() );

    return announcement;
  }






}// class IrcAnnouncementFormatter
