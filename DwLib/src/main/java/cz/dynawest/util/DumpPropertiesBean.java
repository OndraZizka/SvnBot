
package cz.dynawest.util;

import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * Vypíše předané properties do logu (level INFO).
 * @author Ondřej Žižka
 */
public class DumpPropertiesBean {

  private String label = "Properties:";
  public void setLabel( String label ) {    this.label = label;  }



  /**
   * Vypíše předané properties do logu (level INFO).
   * @param props
   */
  public void setProperties( Properties props ) {

    Logger log = Logger.getLogger( this.getClass().getName() );

    Set<String> names = props.stringPropertyNames();
    log.info(this.label);
    for( String name : new TreeSet<String>(names) ){
      
      String val;
      // Skryjeme hesla, ty do logu nechceme
      if(name.contains("pass"))
        val = "(hidden)";
      else
        val = props.getProperty( name );

      log.info( "    "+name+": "+val );
    }

  }

  


  
  
}// class BeanFactoryTest
