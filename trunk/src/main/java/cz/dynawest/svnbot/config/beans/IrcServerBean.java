
package cz.dynawest.svnbot.config.beans;

import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Ondrej Zizka
 */
public class IrcServerBean {

   @XmlAttribute public String host;
   @XmlAttribute public int port = 6667;

   //@XmlElementWrapper(name="autoJoinChannels")
   @XmlElement
   @XmlList public List<String> autoJoinChannels;

}// class
