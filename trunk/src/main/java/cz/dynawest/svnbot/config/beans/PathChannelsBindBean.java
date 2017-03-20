
package cz.dynawest.svnbot.config.beans;


import java.io.Serializable;
import java.util.*;



/**
 * Binding between a SVN path and IRC channels.
 *
 * @author Ondrej Zizka
 */
public class PathChannelsBindBean implements Serializable
{

  /**
   * SVN path to bind with channels.
   */
  public final String path;

  /**
   * IRC channel names, without #.
   */
  public final List<String> channels;


  public PathChannelsBindBean( String path ) {
    this.path = path;
    this.channels = new ArrayList();
  }

  public PathChannelsBindBean( String path, List<String> channels_ ) {
    this.path = path;
    if( channels_ == null )
      //throw new IllegalArgumentException( "channels is null." );
      channels_ = new ArrayList();
    this.channels = channels_;
  }



}// class PathChannelsBindBean
