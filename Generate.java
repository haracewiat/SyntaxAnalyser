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

    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {

        System.out.println(token + ": " + explanatoryMessage);

        throw new CompilationException(token + ": " + explanatoryMessage);

    }

}
