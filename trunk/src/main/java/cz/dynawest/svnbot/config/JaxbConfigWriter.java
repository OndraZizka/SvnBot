
package cz.dynawest.svnbot.config;

import cz.dynawest.svnbot.ex.SvnBotIOException;
import cz.dynawest.svnbot.config.beans.ConfigBean;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbConfigWriter {

	public static void write( ConfigBean config, File toFile ) throws SvnBotIOException {
		try {

			JAXBContext jc = JAXBContext.newInstance(ConfigBean.class);
			Marshaller mc = jc.createMarshaller();
			mc.marshal(new Object(), System.out);

		}
		catch (JAXBException ex) {
			throw new SvnBotIOException( ex );
		}
	}

}// class
