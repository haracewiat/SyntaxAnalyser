/**
 *
 * 
 * @Author: Beata Haracewiat
 *
 *
 **/

import java.io.IOException;

public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

    public SyntaxAnalyser(String fileName) throws IOException {
        lex = new LexicalAnalyser(fileName);
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
        
        // Correct token 
        if (nextToken.symbol == symbol) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();  
        } 
        
        // Unexpected token
        else {
            myGenerate.reportError(nextToken, 
                    "\"" + Token.getName(symbol) + "\" token expected. " +
                    "Found \"" + nextToken.text + "\" instead " +
                    "on line " + nextToken.lineNumber);
        }

    }

    /** Reads a nonterminal of type &lt; statement list &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; statement list &gt; ::= &lt; statement &gt; | 
     *                      &lt; statement list &gt; ; &lt; statement &gt;

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _statementList_() throws IOException, CompilationException {
        
        myGenerate.commenceNonterminal("StatementList");

        _statement_();

        while (nextToken.symbol == Token.semicolonSymbol) {

            acceptTerminal(Token.semicolonSymbol);
            
            _statement_();
            
        };

        myGenerate.finishNonterminal("StatementList");

    }

    /** Reads a nonterminal of type &lt; statement &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; statement &gt; ::= &lt; assignment statement &gt; | 
     *                 &lt; if statement &gt; | 
     *                 &lt; while statement &gt; | 
     *                 &lt; procedure statement &gt; | 
     *                 &lt; until statement &gt; | 
     *                 &lt; for statement &gt;

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _statement_() throws IOException, CompilationException {

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
 
    /** Reads a nonterminal of type &lt; assignment statement &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; assignment statement &gt; ::= identifier := &lt; expression &gt; | 
     *                            identifier := stringConstant

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _assignmentStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("AssignmentStatement");

        acceptTerminal(Token.identifier);

        acceptTerminal(Token.becomesSymbol);
    
        if (nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
        }

        else {
            _expression_();
        }
    
        myGenerate.finishNonterminal("AssignmentStatement");

    }

    /** Reads a nonterminal of type &lt; if statement &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; if statement &gt; ::= if &lt; condition &gt; then &lt; statement list &gt; end if | }
     *                    if &lt; condition &gt; then &lt; statement list &gt; else &lt; statement list &gt; end if 

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _ifStatement_() throws CompilationException, IOException {

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

    /** Reads a nonterminal of type &lt; while statement &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; while statement &gt; ::= while &lt; condition &gt; loop &lt; statement list &gt; end loop

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _whileStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("WhileStatement");

        acceptTerminal(Token.whileSymbol);

        _condition_();

        acceptTerminal(Token.loopSymbol);

        _statementList_();

        acceptTerminal(Token.endSymbol);

        acceptTerminal(Token.loopSymbol);

        myGenerate.finishNonterminal("WhileStatement");

    }

    /** Reads a nonterminal of type &lt; procedure statement &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; procedure statement &gt; ::= call identifier ( &lt; argument list &gt; )

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _procedureStatement_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ProcedureStatement");

        acceptTerminal(Token.callSymbol);

        acceptTerminal(Token.identifier);

        acceptTerminal(Token.leftParenthesis);

        _argumentList_();

        acceptTerminal(Token.rightParenthesis);

        myGenerate.finishNonterminal("ProcedureStatement");

    }

    /** Reads a nonterminal of type &lt; until statement &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; until statement &gt; ::= do &lt; statement list &gt; until &lt; condition &gt;

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _untilStatement_() throws IOException, CompilationException {

        myGenerate.commenceNonterminal("UntilStatement");

        acceptTerminal(Token.doSymbol);

        _statementList_();

        acceptTerminal(Token.untilSymbol);

        _condition_();

        myGenerate.finishNonterminal("UntilStatement");

    }

    /** Reads a nonterminal of type &lt; for statement &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; for statement &gt; ::= for ( &lt; assignment statement &gt; ; &lt; condition &gt; ; &lt; assignment statement &gt; ) do &lt; statement list &gt; end loop

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _forStatement_() throws CompilationException, IOException {

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

    /** Reads a nonterminal of type &lt; argument list &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; argument list &gt; ::= identifier | 
     *                     &lt; argument list &gt; , identifier

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _argumentList_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("ArgumentList");

        acceptTerminal(Token.identifier);

        while (nextToken.symbol == Token.commaSymbol) {
            
            acceptTerminal(Token.commaSymbol);
            
            acceptTerminal(Token.identifier); 
            
        } 
        
        myGenerate.finishNonterminal("ArgumentList");

    }

    /** Reads a nonterminal of type &lt; condition &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; condition &gt; ::= identifier &lt; conditional operator &gt; identifier | 
     *                 identifier &lt; conditional operator &gt; numberConstant | 
     *                 identifier &lt; conditional operator &gt; stringConstant 

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _condition_() throws CompilationException, IOException {

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

    /** Reads a nonterminal of type &lt; conditional operator &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; conditional operator &gt; ::=  &gt; |  &gt;= | = | /= | &lt;  | &lt; =

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _conditionalOperator_() throws CompilationException, IOException {

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
                myGenerate.reportError(nextToken,   
                        "Unrecognized conditional operator on line " + nextToken.lineNumber + ". " +
                        "A conditional operator should be one of the following: " +
                        Token.getName(Token.greaterEqualSymbol) + ", " +
                        Token.getName(Token.greaterThanSymbol) + ", " +
                        Token.getName(Token.equalSymbol) + ", " +
                        Token.getName(Token.notEqualSymbol) + ", " +
                        Token.getName(Token.lessEqualSymbol) + " or " +
                        Token.getName(Token.lessThanSymbol) + ".");


        }

        myGenerate.finishNonterminal("ConditionalOperator");

    }

    /** Reads a nonterminal of type &lt; expression &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; expression &gt; ::= &lt; term &gt; | 
     *                  &lt; expression &gt; + &lt; term &gt; | 
     *                  &lt; expression &gt; - &lt; term &gt;

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _expression_() throws CompilationException, IOException {

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

    /** Reads a nonterminal of type &lt; term &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; term &gt; ::= &lt; factor &gt; | 
     *            &lt; term &gt; * &lt; factor &gt; | 
     *            &lt; term &gt; / &lt; factor &gt;

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _term_() throws CompilationException, IOException {

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

    /** Reads a nonterminal of type &lt; factor &gt;. The grammar rule for this non-terminal
     * is as follows:
     * &lt; factor &gt; ::= identifier | 
     *              numberConstant | 
     *              ( &lt; expression &gt; )

	  @throws IOException in the event that the next token cannot be read.
      @throws CompilationException in the event of a compilation error.
	 */
    private void _factor_() throws CompilationException, IOException {

        myGenerate.commenceNonterminal("Factor");
 
        switch (nextToken.symbol) {

            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;

            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;

            case Token.leftParenthesis:
                acceptTerminal(Token.leftParenthesis);

                _expression_();

                acceptTerminal(Token.rightParenthesis);    
                break;

            default:
                myGenerate.reportError(nextToken, 
                        "Unknown token on line " + nextToken.lineNumber + ". " +
                        "A factor should be one of the following: " +
                        Token.getName(Token.identifier) + ", " +
                        Token.getName(Token.numberConstant) + " or " +
                        Token.getName(Token.leftParenthesis) + ".");

        }

        myGenerate.finishNonterminal("Factor");

    }

}