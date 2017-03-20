
package cz.dynawest.svnbot.config.beans;


import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;



/**
 *
 * @author Ondrej Zizka
 */
public class SvnRepoInfoBean implements Serializable
{

  @XmlAttribute
  public String getName() {    return name;  }
  public void setName( String name ) {    this.name = name;  }
  String name;

  @XmlAttribute
  public String getUrl() {    return url;  }
  public void setUrl( String url ) {    this.url = url;  }
  String url;

  @XmlAttribute
  public String getUser() {    return user;  }
  public void setUser( String user ) {    this.user = user;  }
  String user;

  @XmlAttribute
  public String getPass() {    return pass;  }
  public void setPass( String pass ) {    this.pass = pass;  }
  String pass;

  
  @XmlAttribute(name="checkInterval")
  public int checkInterval = 60;


  @XmlElement(name="ircChannels")
  @XmlList
  private List<String> channels;
  public List<String> getChannels() {    return channels;  }




  // -- Const -- //
  
  public SvnRepoInfoBean() { }
  public SvnRepoInfoBean( String url, String user, String pass ) {
    this.url = url;
    this.user = user;
    this.pass = pass;
  }







}// class SvnRepoInfoBean
