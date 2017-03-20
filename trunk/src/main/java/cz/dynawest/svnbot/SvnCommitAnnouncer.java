
package cz.dynawest.svnbot;

import java.util.List;

/**
 *
 * @author Ondrej Zizka
 */
public interface SvnCommitAnnouncer {

  public void announceCommit( String message, List<String> channels );

}// class SvnCommitAnnouncer
