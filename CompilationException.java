
/**
 *
 * compilation exception analyser for 312 exercise.
 * 
 * This class has been provided to students
 *
 * @Author: Roger Garside, John Mariani, John Vidler, Paul Rayson
 *
 *
 **/

public class CompilationException extends Exception
{
	private static final int MAX_TRACE_DEPTH = 20;

	public CompilationException( String message ) {
		super( message );
	}

	public CompilationException( String message, CompilationException cause ) {
		super( message, cause );
	}

	public String toTraceString() {
		StringBuffer buffer = new StringBuffer();
		Throwable err = this;
		int maxDepth = MAX_TRACE_DEPTH;
		while( err != null && maxDepth-- > 0 ) {
			buffer.append( "\tCaused by " ).append( err.getMessage() ).append( "\r\n" );
			err = err.getCause();
		}

		if( maxDepth < 1 )
			buffer.append( "\t ... etc.\r\n" );

		return buffer.toString();
	}
} // end of class CompilationException