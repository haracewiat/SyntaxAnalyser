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

        // Begin
        isBegin();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        myGenerate.commenceNonterminal("StatementPart");

        // Statement list
        _statementList_();

        // end
        myGenerate.finishNonterminal("StatementPart");

        isEnd();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        // EOF
        myGenerate.insertTerminal(nextToken);


    }

    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        
        if (nextToken.symbol != symbol) {
            throw new CompilationException("Oops");
        }

        nextToken = lex.getNextToken();

    }




    /////////////////////////////////////////////////////////////

    void _statementList_() throws IOException, CompilationException {
        
        // Get every call statement (while loop)
        myGenerate.commenceNonterminal("StatementList");

        // <statement> (while?)

        _statement_();


        while (nextToken.symbol == Token.semicolonSymbol) {

            isSemicolon();
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
            
            _statement_();
            

        };

        // <statement list> ; <statement>
        myGenerate.finishNonterminal("StatementList");

    }

    void _statement_() throws IOException, CompilationException {

        myGenerate.commenceNonterminal("Statement");

        switch (nextToken.symbol) {

            // <assignment statement> 
            case Token.identifier:
                _assignmentStatement_();
                break;
            
            // <if statement> 
            case Token.ifSymbol:
                _ifStatement_();
                break;
            
            //<while statement> 
            case Token.whileSymbol:
                _whileStatement_();
                break;
            
            // <procedure statement> 
            case Token.callSymbol:
                _procedureStatement_();
                break;
            
            // <until statement> 
            case Token.untilSymbol:
                _untilStatement_();
                break;
            
            // <for statement>
            case Token.forSymbol:
                _forStatement_();
                break;
        }

        myGenerate.finishNonterminal("Statement");

    }
 
    void _assignmentStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("AssigmentStatement");

        // identifier := <expression>

        isIdentifier();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        isBecomesSymbol();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();
    
        // Expression OR stringConstant
        if (nextToken.symbol == Token.stringConstant) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        }

        else {
            _expression_();
        }
        

        

        myGenerate.finishNonterminal("AssigmentStatement");

    }

    void _ifStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("IfStatement");

        // if <condition> then <statement list> end if 
        isIf();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _condition_();

        isThen();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _statementList_();

      
        
        // else <statement list> end if
        if (nextToken.symbol == Token.elseSymbol) {

            isIf();
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();

            _condition_();

            isThen();
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();

            _statementList_();

            isElse();
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();

            _statementList_();



        }

        isEnd();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        isIf();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        // else {
        //     System.out.println("unknown..."); //TODO
        // }

        myGenerate.finishNonterminal("IfStatement");

    }

    void _whileStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("WhileStatement");
        
        // while <condition> loop <statement list> end loop

        isWhile();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _condition_();

        isLoop();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _statementList_();

        isEnd();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        isLoop();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        myGenerate.finishNonterminal("WhileStatement");


    }

    void _procedureStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("CallStatement");


        // call identifier ( <argument list> )

        isCall();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        isIdentifier();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        isLeftParenthesis();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _argumentList_();

        isRightParenthesis();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();


        myGenerate.finishNonterminal("CallStatement");


    }

    void _untilStatement_() throws IOException, CompilationException {

        myGenerate.commenceNonterminal("UntilStatement");


        // do <statement list> until <condition>
        isDo();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _statementList_();

        isUntil();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _condition_();

        myGenerate.finishNonterminal("UntilStatement");


    }

    void _forStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ForStatement");

        
        // for ( <assignment statement> ; <condition> ; <assignment statement> ) do <statement list> end loop
        isFor();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        isLeftParenthesis();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _assignmentStatement_();

        isSemicolon();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _condition_();

        isSemicolon();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _assignmentStatement_();

        isRightParenthesis();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        isDo();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _statementList_();

        isEnd();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        isLoop();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        myGenerate.finishNonterminal("ForStatement");


    }

    void _argumentList_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ArgumentList");


        //  identifier 
        do {
            isIdentifier();
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        } while (nextToken.symbol != Token.rightParenthesis);
        
        // <argument list> , identifier

        myGenerate.finishNonterminal("ArgumentList");


    }

    void _condition_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Condition");


        // identifier <conditional operator> identifier 
        isIdentifier();
        myGenerate.insertTerminal(nextToken);
        nextToken = lex.getNextToken();

        _conditionalOperator_();


        // identifier 
        if (nextToken.symbol == Token.identifier){
            isIdentifier();
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        }
        
        else if (nextToken.symbol == Token.numberConstant) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        }

        else if (nextToken.symbol == Token.stringConstant) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        }

        else {
            // TODO error
            System.out.println("Unknown type");
        }
        

        myGenerate.finishNonterminal("Condition");


    }

    void _conditionalOperator_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ConditionalOperator");
        
        // > | >= | = | /= | < | <=
        // int[] conditionalOperators = {Token.greaterThanSymbol, Token.greaterEqualSymbol, Token.equalSymbol}; TODO

        if (nextToken.symbol == Token.greaterEqualSymbol ||
            nextToken.symbol == Token.greaterEqualSymbol ||
            nextToken.symbol == Token.equalSymbol ||
            nextToken.symbol == Token.notEqualSymbol ||
            nextToken.symbol == Token.lessThanSymbol ||
            nextToken.symbol == Token.lessEqualSymbol ) {
            
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();

        } else {
            throw new CompilationException("Should be conditional operator");
        }


        myGenerate.finishNonterminal("ConditionalOperator");

   
        
    }

    void _expression_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Expression");

        _term_();

        if (nextToken.symbol == Token.plusSymbol ||
            nextToken.symbol == Token.minusSymbol ) {

            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();

            _term_();
        }

        myGenerate.finishNonterminal("Expression");


    }

    void _term_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Term");

        _factor_();

        if (nextToken.symbol == Token.timesSymbol ||
            nextToken.symbol == Token.divideSymbol ) {

            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();

            _term_();
        }

        

        myGenerate.finishNonterminal("Term");


    }

    void _factor_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Factor");




        // identifier 
        if (nextToken.symbol == Token.identifier) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        }
        
        // numberConstant 
        else if (nextToken.symbol == Token.numberConstant) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        }
        

        // ( <expression> ) 
        else if (nextToken.symbol == Token.rightParenthesis) {
            _expression_();
        }

        else {
            System.out.println("Not recognized factor");
            // TODO
        }
        
        myGenerate.finishNonterminal("Factor");


    }



    //////////////////////////////////////////////////////////////////////////////
    public void isBegin() throws CompilationException {
        if (nextToken.symbol != Token.beginSymbol) {
            throw new CompilationException("The code should begin with a 'begin' symbol.");
        }
    }

    public void isEnd() throws CompilationException {
        if (nextToken.symbol != Token.endSymbol) {
            throw new CompilationException("The code should end with an 'end' symbol.");
        }
    }

    public void isCall() throws CompilationException {
        if (nextToken.symbol != Token.callSymbol) {
            throw new CompilationException("The call statement should start with a 'call' symbol.");
        }
    }

    public void isDo() throws CompilationException {
        if (nextToken.symbol != Token.callSymbol) {
            throw new CompilationException("Should be 'do'");
        }
    }

    public void isIf() throws CompilationException {
        if (nextToken.symbol != Token.ifSymbol) {
            throw new CompilationException("Should be 'if'");
        }
    }

    public void isThen() throws CompilationException {
        if (nextToken.symbol != Token.thenSymbol) {
            throw new CompilationException("Should be 'then'");
        }
    }

    public void isUntil() throws CompilationException {
        if (nextToken.symbol != Token.callSymbol) {
            throw new CompilationException("Should be 'until'");
        }
    }

    public void isSemicolon() throws CompilationException {
        if (nextToken.symbol != Token.semicolonSymbol) {
            throw new CompilationException("Should be ';'");
        }
    }

    public void isFor() throws CompilationException {
        if (nextToken.symbol != Token.forSymbol) {
            throw new CompilationException("Should be 'for'");
        }
    }

    public void isLoop() throws CompilationException {
        if (nextToken.symbol != Token.loopSymbol) {
            throw new CompilationException("Should be 'loop'");
        }
    }

    public void isWhile() throws CompilationException {
        if (nextToken.symbol != Token.whileSymbol) {
            throw new CompilationException("Should be 'while'");
        }
    }

    public void isPlusSign() throws CompilationException {
        if (nextToken.symbol != Token.plusSymbol) {
            throw new CompilationException("Should be '+'");
        }
    }

    public void isMinusSign() throws CompilationException {
        if (nextToken.symbol != Token.minusSymbol) {
            throw new CompilationException("Should be '-'");
        }
    }

    public void isLeftParenthesis() throws CompilationException {
        if (nextToken.symbol != Token.leftParenthesis) {
            throw new CompilationException("Should be (");
        }
    }

    public void isRightParenthesis() throws CompilationException {
        if (nextToken.symbol != Token.rightParenthesis) {
            throw new CompilationException("Should be )");
        }
    }
    
    public void isElse() throws CompilationException {
        if (nextToken.symbol != Token.elseSymbol) {
            throw new CompilationException("Should be 'else'");
        }
    }

    public void isBecomesSymbol() throws CompilationException {
        if (nextToken.symbol != Token.becomesSymbol) {
            throw new CompilationException("Should be ':='");
        }
    }

    public void isIdentifier() throws CompilationException {
        // sequence of one or more letters (a to z, A to Z) 
        // and digits (0 to 9), 
        // starting with a letter, and
        // excluding all the reserved words:
        // (procedure, is, integer, etc).
        if (nextToken.symbol != Token.identifier) {
            throw new CompilationException("The identifier should...");
        }
    }


  
}
