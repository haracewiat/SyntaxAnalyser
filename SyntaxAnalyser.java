/**
 *
 *
 * @Author: Beata Haracewiat
 *
 *
 **/


import java.io.IOException;
import java.io.PrintStream;


public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

	public LexicalAnalyser lex;
	public Token nextToken ;
	public Generate myGenerate = null;

    public SyntaxAnalyser(String fileName) throws IOException {
        this.lex = new LexicalAnalyser(fileName);
    } 

    @Override
    public void parse( PrintStream ps ) throws IOException
	{
		myGenerate = new Generate();
		try {
			nextToken = lex.getNextToken() ;
			_statementPart_() ;
			acceptTerminal(Token.eofSymbol) ;
			myGenerate.reportSuccess() ;
		}
		catch( CompilationException ex )
		{
			ps.println( "Compilation Exception" );
			ps.println( ex.toTraceString() );
		}
	}
    

    @Override
    public void _statementPart_() throws IOException, CompilationException {

        isBegin();

        myGenerate.commenceNonterminal("StatementPart");

        _statementList_();

        myGenerate.finishNonterminal("StatementPart");

        isEnd();

    }

    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        
        if (nextToken.symbol == symbol) {

            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
            
        } else {

            myGenerate.reportError(nextToken, 
                        Token.getName(symbol) + " token expected. " +
                        "Found " + nextToken.text + " instead " +
                        "on line " + nextToken.lineNumber);
            
        }
    }


    void _statementList_() throws IOException, CompilationException {
        
        myGenerate.commenceNonterminal("StatementList");

        _statement_();

        while (nextToken.symbol == Token.semicolonSymbol) {

            isSemicolon();
            
            _statement_();
            
        };

        myGenerate.finishNonterminal("StatementList");

    }

    void _statement_() throws IOException, CompilationException {

        myGenerate.commenceNonterminal("Statement");

        switch (nextToken.symbol) {

            case Token.identifier:
                _assignmentStatement_();
                break;
            
            case Token.ifSymbol:
                _ifStatement_();
                break;
            
            case Token.whileSymbol:
                _whileStatement_();
                break;
            
            case Token.callSymbol:
                _procedureStatement_();
                break;
            
            case Token.untilSymbol:
                _untilStatement_();
                break;
            
            case Token.forSymbol:
                _forStatement_();
                break;

            default:
                myGenerate.reportError(nextToken, "_statement_");

        }

        myGenerate.finishNonterminal("Statement");

    }
 
    void _assignmentStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("AssigmentStatement");

        isIdentifier();

        isBecomesSymbol();
    
        if (nextToken.symbol == Token.stringConstant) {
            isStringConstant();
        }

        else {
            _expression_();
        }
    
        myGenerate.finishNonterminal("AssigmentStatement");

    }

    void _ifStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("IfStatement");

        isIf();

        _condition_();

        isThen();

        _statementList_();

        if (nextToken.symbol == Token.elseSymbol) {

            isIf();

            _condition_();

            isThen();

            _statementList_();

            isElse();

            _statementList_();

        }

        isEnd();

        isIf();

        myGenerate.finishNonterminal("IfStatement");

    }

    void _whileStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("WhileStatement");

        isWhile();

        _condition_();

        isLoop();

        _statementList_();

        isEnd();

        isLoop();

        myGenerate.finishNonterminal("WhileStatement");

    }

    void _procedureStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("CallStatement");

        isCall();

        isIdentifier();

        isLeftParenthesis();

        _argumentList_();

        isRightParenthesis();

        myGenerate.finishNonterminal("CallStatement");

    }

    void _untilStatement_() throws IOException, CompilationException {

        myGenerate.commenceNonterminal("UntilStatement");

        isDo();

        _statementList_();

        isUntil();

        _condition_();

        myGenerate.finishNonterminal("UntilStatement");

    }

    void _forStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ForStatement");

        isFor();

        isLeftParenthesis();

        _assignmentStatement_();

        isSemicolon();

        _condition_();

        isSemicolon();

        _assignmentStatement_();

        isRightParenthesis();

        isDo();

        _statementList_();

        isEnd();

        isLoop();

        myGenerate.finishNonterminal("ForStatement");

    }

    void _argumentList_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ArgumentList");

        do {
            isIdentifier();
        } while (nextToken.symbol != Token.rightParenthesis);
        
        myGenerate.finishNonterminal("ArgumentList");

    }

    void _condition_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Condition");

        isIdentifier();

        _conditionalOperator_();

        if (nextToken.symbol == Token.identifier){
            isIdentifier();
        }
        
        else if (nextToken.symbol == Token.numberConstant) {
            isNumberConstant();
        }

        else if (nextToken.symbol == Token.stringConstant) {
            isStringConstant();
        }

        myGenerate.finishNonterminal("Condition");

    }

    void _conditionalOperator_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ConditionalOperator");
    
        isConditionalOperator();

        myGenerate.finishNonterminal("ConditionalOperator");

    }

    void _expression_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Expression");

        _term_();

        if (nextToken.symbol == Token.plusSymbol ) {
            isPlusSign(); 
            _term_(); 
        }

        else if (nextToken.symbol == Token.minusSymbol) {
            isMinusSign();
            _term_();
        }        
        
        myGenerate.finishNonterminal("Expression");

    }

    void _term_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Term");

        _factor_();

        if (nextToken.symbol == Token.timesSymbol ) {
            isTimesSign(); 
            _term_(); 
        }

        else if (nextToken.symbol == Token.divideSymbol) {
            isDivideSign();
            _term_();
        }

        myGenerate.finishNonterminal("Term");

    }

    void _factor_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Factor");
 
        if (nextToken.symbol == Token.identifier) {
            isIdentifier();
        }
        
        else if (nextToken.symbol == Token.numberConstant) {
            isNumberConstant();
        }
        
        else if (nextToken.symbol == Token.rightParenthesis) {

            isLeftParenthesis();

            _expression_();

            isRightParenthesis();

        }

        myGenerate.finishNonterminal("Factor");

    }



    //////////////////////////////////////////////////////////////////////////////
    public void isBegin() throws CompilationException, IOException {
        // if (nextToken.symbol != Token.beginSymbol) {
        //     throw new CompilationException("The code should begin with a 'begin' symbol.");
        // }
        // acceptTerminal(nextToken.symbol);
        acceptTerminal(Token.beginSymbol);
    }

    public void isEnd() throws CompilationException, IOException {
        if (nextToken.symbol != Token.endSymbol) {
            throw new CompilationException("The code should end with an 'end' symbol.");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isCall() throws CompilationException, IOException {
        if (nextToken.symbol != Token.callSymbol) {
            throw new CompilationException("The call statement should start with a 'call' symbol.");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isDo() throws CompilationException, IOException {
        if (nextToken.symbol != Token.callSymbol) {
            throw new CompilationException("Should be 'do'");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isIf() throws CompilationException, IOException {
        if (nextToken.symbol != Token.ifSymbol) {
            throw new CompilationException("Should be 'if'");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isThen() throws CompilationException, IOException {
        if (nextToken.symbol != Token.thenSymbol) {
            throw new CompilationException("Should be 'then'");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isUntil() throws CompilationException, IOException {
        if (nextToken.symbol != Token.callSymbol) {
            throw new CompilationException("Should be 'until'");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isSemicolon() throws CompilationException, IOException {
        if (nextToken.symbol != Token.semicolonSymbol) {
            throw new CompilationException("Should be ';'");
        }

        acceptTerminal(nextToken.symbol);
    }

    public void isFor() throws CompilationException, IOException {
        if (nextToken.symbol != Token.forSymbol) {
            throw new CompilationException("Should be 'for'");
        }

        acceptTerminal(nextToken.symbol);
    }

    public void isLoop() throws CompilationException, IOException {
        if (nextToken.symbol != Token.loopSymbol) {
            throw new CompilationException("Should be 'loop'");
        }

        acceptTerminal(nextToken.symbol);
    }

    public void isWhile() throws CompilationException, IOException {
        if (nextToken.symbol != Token.whileSymbol) {
            throw new CompilationException("Should be 'while'");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isPlusSign() throws CompilationException, IOException {
        if (nextToken.symbol != Token.plusSymbol) {
            throw new CompilationException("Should be '+'");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isMinusSign() throws CompilationException, IOException {
        if (nextToken.symbol != Token.minusSymbol) {
            throw new CompilationException("Should be '-'");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isTimesSign() throws CompilationException, IOException {
        if (nextToken.symbol != Token.timesSymbol) {
            throw new CompilationException("Should be '*'");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isDivideSign() throws CompilationException, IOException {
        if (nextToken.symbol != Token.divideSymbol) {
            throw new CompilationException("Should be '/'");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isLeftParenthesis() throws CompilationException, IOException {
        if (nextToken.symbol != Token.leftParenthesis) {
            throw new CompilationException("Should be (");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isRightParenthesis() throws CompilationException, IOException {
        if (nextToken.symbol != Token.rightParenthesis) {
            throw new CompilationException("Should be )");
        }
        acceptTerminal(nextToken.symbol);
    }
    
    public void isElse() throws CompilationException, IOException {
        if (nextToken.symbol != Token.elseSymbol) {
            throw new CompilationException("Should be 'else'");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isBecomesSymbol() throws CompilationException, IOException {
        if (nextToken.symbol != Token.becomesSymbol) {
            throw new CompilationException("Should be ':='");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isIdentifier() throws CompilationException, IOException {
        if (nextToken.symbol != Token.identifier) {
            throw new CompilationException("The identifier should...");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isConditionalOperator() throws CompilationException, IOException {
        if (nextToken.symbol != Token.greaterEqualSymbol &&
            nextToken.symbol != Token.greaterEqualSymbol &&
            nextToken.symbol != Token.equalSymbol &&
            nextToken.symbol != Token.notEqualSymbol &&
            nextToken.symbol != Token.lessThanSymbol &&
            nextToken.symbol != Token.lessEqualSymbol ) {
            
            throw new CompilationException("Should be conditional operator...");


        }
        acceptTerminal(nextToken.symbol);
    }

    public void isNumberConstant() throws CompilationException, IOException {
        if (nextToken.symbol != Token.numberConstant) {
            throw new CompilationException("The number constant should...");
        }
        acceptTerminal(nextToken.symbol);
    }

    public void isStringConstant() throws CompilationException, IOException {
        if (nextToken.symbol != Token.stringConstant) {
            throw new CompilationException("The string constant should...");
        }
        acceptTerminal(nextToken.symbol);
    }

  
}
