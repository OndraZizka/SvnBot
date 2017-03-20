
package cz.dynawest.svnbot.config.beans;


import java.io.Serializable;
import java.util.*;
import java.util.logging.*;
import javax.xml.bind.annotation.*;


/**
 *
 * @author Ondrej Zizka
 */
@XmlRootElement(name="svnbotConfig")
public class ConfigBean implements Serializable
{
   @XmlElement
   public SettingsBean settings;

   @XmlElement
   public IrcBean irc;

   @XmlElement(name="repository")
   @XmlElementWrapper(name="repositories")
   public List<SvnRepoInfoBean> repositories;


}// class ConfigBean
