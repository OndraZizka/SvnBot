
package cz.dynawest.svnbot.ex;

import cz.dynawest.svnbot.ex.SvnBotException;

/**
 *
 * @author Ondrej Zizka
 */
public class SvnBotIOException extends SvnBotException {

	public SvnBotIOException(String message) {
		super(message);
	}

	public SvnBotIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public SvnBotIOException(Throwable cause) {
		super(cause);
	}

	

}// class
