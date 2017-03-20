
package cz.dynawest.svnbot.config.beans;

import javax.xml.bind.annotation.*;

/**
 *
 * @author Ondrej Zizka
 */
public class SettingsBean {

   // JiraBot-related.
   @XmlAttribute public int maxJirasPerRequest = 3;
   @XmlAttribute public int minJiraPrefixLength = 2;

   // IRC-related.
   @XmlAttribute public boolean verbose = true;
   @XmlAttribute public boolean allowJoinCommand = false;
   @XmlAttribute public boolean acceptInvitation = true;
   @XmlAttribute public int repeatDelay = 666;
   @XmlAttribute public boolean leaveOnAsk = true;
   @XmlAttribute public String adminUser = "ozizka";
   @XmlAttribute public String smtpHost = "smtp.corp.redhat.com";
   @XmlAttribute public String debugChannel = "#some";
   @XmlAttribute public boolean unsecuredShutdown = false;

   
   // Catch-all HashMap. Is it needed?
   //@XmlAnyAttribute
   //public Map<String, String> other = new HashMap();


}// class
