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

        acceptTerminal(Token.beginSymbol);

        myGenerate.commenceNonterminal("StatementPart");

        _statementList_();

        myGenerate.finishNonterminal("StatementPart");

        acceptTerminal(Token.endSymbol);

    }

    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        
        if (nextToken.symbol == symbol) {

            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
            
        } else {

            myGenerate.reportError(nextToken, 
                        "\"" + Token.getName(symbol) + "\" token expected. " +
                        "Found \"" + nextToken.text + "\" instead " +
                        "on line " + nextToken.lineNumber);
            
        }
    }


    void _statementList_() throws IOException, CompilationException {
        
        myGenerate.commenceNonterminal("StatementList");

        _statement_();

        while (nextToken.symbol == Token.semicolonSymbol) {

            acceptTerminal(Token.semicolonSymbol);
            
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
                myGenerate.reportError(nextToken, 
                    "Unknown statement type on line " + nextToken.lineNumber + ". " +
                    "A statement should start with one of the following tokens: " +
                    Token.getName(Token.identifier) + ", " +
                    Token.getName(Token.ifSymbol) + ", " +
                    Token.getName(Token.whileSymbol) + ", " +
                    Token.getName(Token.callSymbol) + ", " +
                    Token.getName(Token.untilSymbol) + " or " +
                    Token.getName(Token.forSymbol) + ".");

        }

        myGenerate.finishNonterminal("Statement");

    }
 
    void _assignmentStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("AssigmentStatement");

        acceptTerminal(Token.identifier);

        acceptTerminal(Token.becomesSymbol);
    
        if (nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
        }

        else {
            _expression_();
        }
    
        myGenerate.finishNonterminal("AssigmentStatement");

    }

    void _ifStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("IfStatement");

        acceptTerminal(Token.ifSymbol);

        _condition_();

        acceptTerminal(Token.thenSymbol);

        _statementList_();

        if (nextToken.symbol == Token.elseSymbol) {

            acceptTerminal(Token.ifSymbol);

            _condition_();

            acceptTerminal(Token.thenSymbol);

            _statementList_();

            acceptTerminal(Token.elseSymbol);

            _statementList_();

        }

        acceptTerminal(Token.endSymbol);

        acceptTerminal(Token.ifSymbol);

        myGenerate.finishNonterminal("IfStatement");

    }

    void _whileStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("WhileStatement");

        acceptTerminal(Token.whileSymbol);

        _condition_();

        acceptTerminal(Token.loopSymbol);

        _statementList_();

        acceptTerminal(Token.endSymbol);

        acceptTerminal(Token.loopSymbol);

        myGenerate.finishNonterminal("WhileStatement");

    }

    void _procedureStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("CallStatement");

        acceptTerminal(Token.callSymbol);

        acceptTerminal(Token.identifier);

        acceptTerminal(Token.leftParenthesis);

        _argumentList_();

        acceptTerminal(Token.rightParenthesis);

        myGenerate.finishNonterminal("CallStatement");

    }

    void _untilStatement_() throws IOException, CompilationException {

        myGenerate.commenceNonterminal("UntilStatement");

        acceptTerminal(Token.doSymbol);

        _statementList_();

        acceptTerminal(Token.untilSymbol);

        _condition_();

        myGenerate.finishNonterminal("UntilStatement");

    }

    void _forStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ForStatement");

        acceptTerminal(Token.forSymbol);

        acceptTerminal(Token.leftParenthesis);

        _assignmentStatement_();

        acceptTerminal(Token.semicolonSymbol);

        _condition_();

        acceptTerminal(Token.semicolonSymbol);

        _assignmentStatement_();

        acceptTerminal(Token.rightParenthesis);

        acceptTerminal(Token.doSymbol);

        _statementList_();

        acceptTerminal(Token.endSymbol);

        acceptTerminal(Token.loopSymbol);

        myGenerate.finishNonterminal("ForStatement");

    }

    void _argumentList_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ArgumentList");

        acceptTerminal(Token.identifier);

        while (nextToken.symbol == Token.commaSymbol) {
            
            acceptTerminal(Token.commaSymbol);
            
            acceptTerminal(Token.identifier); 
            
        } 
        
        myGenerate.finishNonterminal("ArgumentList");

    }

    void _condition_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Condition");

        acceptTerminal(Token.identifier);

        _conditionalOperator_();

        if (nextToken.symbol == Token.identifier){
            acceptTerminal(Token.identifier);
        }
        
        else if (nextToken.symbol == Token.numberConstant) {
            acceptTerminal(Token.numberConstant);
        }

        else if (nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
        }

        myGenerate.finishNonterminal("Condition");

    }

    void _conditionalOperator_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ConditionalOperator");
    
        switch (nextToken.symbol) {

            case Token.greaterEqualSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;

            case Token.greaterThanSymbol:
                acceptTerminal(Token.greaterThanSymbol);
                break;

            case Token.equalSymbol:
                acceptTerminal(Token.equalSymbol);
                break;

            case Token.notEqualSymbol:
                acceptTerminal(Token.notEqualSymbol);
                break;

            case Token.lessEqualSymbol:
                acceptTerminal(Token.lessEqualSymbol);
                break;

            case Token.lessThanSymbol:
                acceptTerminal(Token.lessThanSymbol);
                break;

            default:
                myGenerate.reportError(nextToken, "Unrecognized conditional operator.");


        }

        myGenerate.finishNonterminal("ConditionalOperator");

    }

    void _expression_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Expression");

        _term_();

        while (nextToken.symbol == Token.plusSymbol || 
            nextToken.symbol == Token.minusSymbol) {
            
            switch (nextToken.symbol) {

                case Token.plusSymbol:
                    acceptTerminal(Token.plusSymbol);
                    break;

                case Token.minusSymbol:
                    acceptTerminal(Token.minusSymbol);
                    break;

            }
            
            _term_(); 

        }     
        
        myGenerate.finishNonterminal("Expression");

    }

    void _term_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Term");

        _factor_();

        while (nextToken.symbol == Token.timesSymbol || 
            nextToken.symbol == Token.divideSymbol) {
            
            switch (nextToken.symbol) {

                case Token.timesSymbol:
                    acceptTerminal(Token.timesSymbol);
                    break;

                case Token.divideSymbol:
                    acceptTerminal(Token.divideSymbol);
                    break;

            }
            
            _term_(); 

        }

        myGenerate.finishNonterminal("Term");

    }

    void _factor_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Factor");
 
        if (nextToken.symbol == Token.identifier) {
            acceptTerminal(Token.identifier);
        }
        
        else if (nextToken.symbol == Token.numberConstant) {
            acceptTerminal(Token.numberConstant);
        }
        
        else if (nextToken.symbol == Token.rightParenthesis) {

            acceptTerminal(Token.leftParenthesis);

            _expression_();

            acceptTerminal(Token.rightParenthesis);

        }

        myGenerate.finishNonterminal("Factor");

    }

}