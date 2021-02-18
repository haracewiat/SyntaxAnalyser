/**
 *
 *
 * @Author: Beata Haracewiat
 *
 *
 **/

public class Generate extends AbstractGenerate {
    
    public Generate() {
        super();
    }

    // public Generate(String explanatoryMessage) {

    // }

    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {

        // indicate what the next erroneous token is, 
        // what the parser is trying to recognise at this point, 
        // and the line number where the error is recognised
        System.out.println(explanatoryMessage);


        // The method should finish by throwing the exception 
        // CompilationException, which should eventually be caught 
        // by the parse method in the SyntaxAnalyser class
        throw new CompilationException("unexpected token. " + explanatoryMessage);
    }

}
