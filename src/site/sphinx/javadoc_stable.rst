
#######################################################################
API 5.3
#######################################################################

Base Package: com.manticore.jsqlformatter


..  _com.manticore.jsqlformatter:
***********************************************************************
Base
***********************************************************************

..  _com.manticore.jsqlformatter.JSQLFormatter.BackSlashQuoting

=======================================================================
BackSlashQuoting
=======================================================================

[YES, NO]


..  _com.manticore.jsqlformatter.JSQLFormatter.BreakLine

=======================================================================
BreakLine
=======================================================================

[NEVER, AS_NEEDED, AFTER_FIRST, ALWAYS]


..  _com.manticore.jsqlformatter.JSQLFormatter.FormattingOption

=======================================================================
FormattingOption
=======================================================================

[SQUARE_BRACKET_QUOTATION, BACKSLASH_QUOTING, OUTPUT_FORMAT, KEYWORD_SPELLING, FUNCTION_SPELLING, OBJECT_SPELLING, SEPARATION, INDENT_WIDTH, SHOW_LINE_NUMBERS, STATEMENT_TERMINATOR]


..  _com.manticore.jsqlformatter.JSQLFormatter.OutputFormat

=======================================================================
OutputFormat
=======================================================================

[PLAIN, ANSI, HTML, RTF, XSLFO]


..  _com.manticore.jsqlformatter.JSQLFormatter.Separation

=======================================================================
Separation
=======================================================================

[BEFORE, AFTER]


..  _com.manticore.jsqlformatter.JSQLFormatter.ShowLineNumbers

=======================================================================
ShowLineNumbers
=======================================================================

[YES, NO]


..  _com.manticore.jsqlformatter.JSQLFormatter.Spelling

=======================================================================
Spelling
=======================================================================

[UPPER, LOWER, CAMEL, KEEP]


..  _com.manticore.jsqlformatter.JSQLFormatter.SquaredBracketQuotation

=======================================================================
SquaredBracketQuotation
=======================================================================

[AUTO, YES, NO]


..  _com.manticore.jsqlformatter.JSQLFormatter.StatementTerminator

=======================================================================
StatementTerminator
=======================================================================

[SEMICOLON, NONE, GO, BACKSLASH]


..  _com.manticore.jsqlformatter.Comment:

=======================================================================
Comment
=======================================================================

*extends:* :ref:`Object<java.lang.Object>` *implements:* :ref:`Comment><java.lang.Comparable<com.manticore.jsqlformatter.Comment>>` 

| **Comment** (absolutePosition, text)
|          int absolutePosition
|          :ref:`String<java.lang.String>` text


| *@Override*
| **compareTo** (o) → int
|          :ref:`Comment<com.manticore.jsqlformatter.Comment>` o
|          returns int



| *@Override*
| **toString** () → :ref:`String<java.lang.String>`
|          returns :ref:`String<java.lang.String>`




..  _com.manticore.jsqlformatter.CommentMap:

=======================================================================
CommentMap
=======================================================================

*extends:* :ref:`Comment><java.util.LinkedHashMap<java.lang.Integer,com.manticore.jsqlformatter.Comment>>` 

| **CommentMap** (sqlStr)
|          :ref:`String<java.lang.String>` sqlStr



                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` format

                |          :ref:`String<java.lang.String>` keyword

                |          :ref:`String<java.lang.String>` before

                |          :ref:`String<java.lang.String>` after

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            | *@SuppressWarnings*
| **insertComments** (sqlStrWithoutComments, outputFormat) → :ref:`StringBuilder<java.lang.StringBuilder>`
|          :ref:`StringBuilder<java.lang.StringBuilder>` sqlStrWithoutComments
|          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` outputFormat
|          returns :ref:`StringBuilder<java.lang.StringBuilder>`



| **getLength** () → int
|          returns int




..  _com.manticore.jsqlformatter.JSQLFormatter:

=======================================================================
JSQLFormatter
=======================================================================

*extends:* :ref:`Object<java.lang.Object>` 

| A powerful Java SQL Formatter based on the JSQLParser.

| **JSQLFormatter** ()


| **getSquaredBracketQuotation** () → :ref:`SquaredBracketQuotation<com.manticore.jsqlformatter.JSQLFormatter.SquaredBracketQuotation>`
|          returns :ref:`SquaredBracketQuotation<com.manticore.jsqlformatter.JSQLFormatter.SquaredBracketQuotation>`



| **setSquaredBracketQuotation** (squaredBracketQuotation)
|          :ref:`SquaredBracketQuotation<com.manticore.jsqlformatter.JSQLFormatter.SquaredBracketQuotation>` squaredBracketQuotation


| **getBackSlashQuoting** () → :ref:`BackSlashQuoting<com.manticore.jsqlformatter.JSQLFormatter.BackSlashQuoting>`
|          returns :ref:`BackSlashQuoting<com.manticore.jsqlformatter.JSQLFormatter.BackSlashQuoting>`



| **setBackSlashQuoting** (backSlashQuoting)
|          :ref:`BackSlashQuoting<com.manticore.jsqlformatter.JSQLFormatter.BackSlashQuoting>` backSlashQuoting


| **getStatementTerminator** () → :ref:`StatementTerminator<com.manticore.jsqlformatter.JSQLFormatter.StatementTerminator>`
|          returns :ref:`StatementTerminator<com.manticore.jsqlformatter.JSQLFormatter.StatementTerminator>`



| **setStatementTerminator** (statementTerminator)
|          :ref:`StatementTerminator<com.manticore.jsqlformatter.JSQLFormatter.StatementTerminator>` statementTerminator


| **getSeparation** () → :ref:`Separation<com.manticore.jsqlformatter.JSQLFormatter.Separation>`
|          returns :ref:`Separation<com.manticore.jsqlformatter.JSQLFormatter.Separation>`



| **setSeparation** (separation)
|          :ref:`Separation<com.manticore.jsqlformatter.JSQLFormatter.Separation>` separation


| **getKeywordSpelling** () → :ref:`Spelling<com.manticore.jsqlformatter.JSQLFormatter.Spelling>`
|          returns :ref:`Spelling<com.manticore.jsqlformatter.JSQLFormatter.Spelling>`



| **setKeywordSpelling** (keywordSpelling)
|          :ref:`Spelling<com.manticore.jsqlformatter.JSQLFormatter.Spelling>` keywordSpelling


| **getFunctionSpelling** () → :ref:`Spelling<com.manticore.jsqlformatter.JSQLFormatter.Spelling>`
|          returns :ref:`Spelling<com.manticore.jsqlformatter.JSQLFormatter.Spelling>`



| **setFunctionSpelling** (functionSpelling)
|          :ref:`Spelling<com.manticore.jsqlformatter.JSQLFormatter.Spelling>` functionSpelling


| **getObjectSpelling** () → :ref:`Spelling<com.manticore.jsqlformatter.JSQLFormatter.Spelling>`
|          returns :ref:`Spelling<com.manticore.jsqlformatter.JSQLFormatter.Spelling>`



| **setObjectSpelling** (objectSpelling)
|          :ref:`Spelling<com.manticore.jsqlformatter.JSQLFormatter.Spelling>` objectSpelling


| **getOutputFormat** () → :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>`
|          returns :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>`



| **setOutputFormat** (outputFormat)
|          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` outputFormat


| **getIndentWidth** () → int
|          returns int



| **setIndentWidth** (indentWidth)
|          int indentWidth


| **getIndentString** () → :ref:`String<java.lang.String>`
|          returns :ref:`String<java.lang.String>`



| **setIndentString** (indentString)
|          :ref:`String<java.lang.String>` indentString



                |          :ref:`ExpressionList<?><net.sf.jsqlparser.expression.operators.relational.ExpressionList<?>>` parameters

                |          :ref:`BreakLine<com.manticore.jsqlformatter.JSQLFormatter.BreakLine>` breakLine

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          returns void


            
                |          :ref:`String<java.lang.String>` s

                |          returns :ref:`String<java.lang.String>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` format

                |          :ref:`String<java.lang.String>` keyword

                |          :ref:`String<java.lang.String>` before

                |          :ref:`String<java.lang.String>` after

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`String<java.lang.String>` s

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` format

                |          :ref:`String<java.lang.String>` hint

                |          :ref:`String<java.lang.String>` before

                |          :ref:`String<java.lang.String>` after

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` format

                |          :ref:`String<java.lang.String>` operator

                |          :ref:`String<java.lang.String>` before

                |          :ref:`String<java.lang.String>` after

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` format

                |          :ref:`String<java.lang.String>` value

                |          :ref:`String<java.lang.String>` before

                |          :ref:`String<java.lang.String>` after

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` format

                |          :ref:`String<java.lang.String>` alias

                |          :ref:`String<java.lang.String>` before

                |          :ref:`String<java.lang.String>` after

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` format

                |          :ref:`Alias<net.sf.jsqlparser.expression.Alias>` alias

                |          :ref:`String<java.lang.String>` before

                |          :ref:`String<java.lang.String>` after

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` format

                |          :ref:`String<java.lang.String>` objectName

                |          :ref:`String<java.lang.String>` before

                |          :ref:`String<java.lang.String>` after

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` format

                |          :ref:`String<java.lang.String>` function

                |          :ref:`String<java.lang.String>` before

                |          :ref:`String<java.lang.String>` after

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`OutputFormat<com.manticore.jsqlformatter.JSQLFormatter.OutputFormat>` format

                |          :ref:`String<java.lang.String>` type

                |          :ref:`String<java.lang.String>` before

                |          :ref:`String<java.lang.String>` after

                |          returns :ref:`StringBuilder<java.lang.StringBuilder>`


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          returns int


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          boolean moveToTab

                |          returns int


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`Delete<net.sf.jsqlparser.statement.delete.Delete>` delete

                |          int indent

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                    
                
            | **getAbsoluteFile** (filename) → :ref:`File<java.io.File>`
|          :ref:`String<java.lang.String>` filename
|          returns :ref:`File<java.io.File>`



| **getAbsoluteFileName** (filename) → :ref:`String<java.lang.String>`
|          :ref:`String<java.lang.String>` filename
|          returns :ref:`String<java.lang.String>`



| *@SuppressWarnings*
| **verify** (sqlStr, options) → :ref:`Exception><java.util.ArrayList<java.lang.Exception>>`
|          :ref:`String<java.lang.String>` sqlStr
|          :ref:`String[]<java.lang.String[]>` options
|          returns :ref:`Exception><java.util.ArrayList<java.lang.Exception>>`



| *@SuppressWarnings*
| **format** (sqlStr, options) → :ref:`String<java.lang.String>`
|          :ref:`String<java.lang.String>` sqlStr
|          :ref:`String[]<java.lang.String[]>` options
|          returns :ref:`String<java.lang.String>`



| **formatToJava** (sqlStr, indent, options) → :ref:`StringBuilder<java.lang.StringBuilder>`
|          :ref:`String<java.lang.String>` sqlStr
|          int indent
|          :ref:`String[]<java.lang.String[]>` options
|          returns :ref:`StringBuilder<java.lang.StringBuilder>`



| *@SuppressWarnings*
| **applyFormattingOptions** (options)
|          :ref:`String[]<java.lang.String[]>` options



                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`Merge<net.sf.jsqlparser.statement.merge.Merge>` merge

                |          int indent

                |          returns void


            
                |          :ref:`OutputClause<net.sf.jsqlparser.statement.OutputClause>` outputClause

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          returns void


            | **appendMergeUpdate** (update, builder, indent)
|          :ref:`MergeUpdate<net.sf.jsqlparser.statement.merge.MergeUpdate>` update
|          :ref:`StringBuilder<java.lang.StringBuilder>` builder
|          int indent


| *@SuppressWarnings*
| **appendMergeInsert** (insert, builder, indent, i)
|          :ref:`MergeInsert<net.sf.jsqlparser.statement.merge.MergeInsert>` insert
|          :ref:`StringBuilder<java.lang.StringBuilder>` builder
|          int indent
|          int i



                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`Insert<net.sf.jsqlparser.statement.insert.Insert>` insert

                |          int indent

                |          returns void


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`Update<net.sf.jsqlparser.statement.update.Update>` update

                |          int indent

                |          returns void


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`UpdateSet><java.util.List<net.sf.jsqlparser.statement.update.UpdateSet>>` updateSets

                |          int subIndent

                |          returns void


            
                |          :ref:`Select<net.sf.jsqlparser.statement.select.Select>` select

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          boolean breakLineBefore

                |          boolean indentFirstLine

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                        PMD.ExcessiveMethodLength
                    
                
            | **appendSelectItemList** (selectItems, builder, subIndent, i, bl, indent)
|          :ref:`SelectItem<?>><java.util.List<net.sf.jsqlparser.statement.select.SelectItem<?>>>` selectItems
|          :ref:`StringBuilder<java.lang.StringBuilder>` builder
|          int subIndent
|          int i
|          :ref:`BreakLine<com.manticore.jsqlformatter.JSQLFormatter.BreakLine>` bl
|          int indent


| **appendColumnSelectItemList** (selectItems, builder, subIndent, i, bl, indent)
|          :ref:`Column>><java.util.List<net.sf.jsqlparser.statement.select.SelectItem<net.sf.jsqlparser.schema.Column>>>` selectItems
|          :ref:`StringBuilder<java.lang.StringBuilder>` builder
|          int subIndent
|          int i
|          :ref:`BreakLine<com.manticore.jsqlformatter.JSQLFormatter.BreakLine>` bl
|          int indent



                |          :ref:`OrderByElement><java.util.List<net.sf.jsqlparser.statement.select.OrderByElement>>` orderByElements

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                    
                
            
                |          :ref:`Expression<net.sf.jsqlparser.expression.Expression>` havingExpression

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          returns void


            
                |          :ref:`GroupByElement<net.sf.jsqlparser.statement.select.GroupByElement>` groupByElement

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          returns void


                
                
                    
                        
                        PMD.CyclomaticComplexity
                    
                
            
                |          :ref:`String<java.lang.String>` keyword

                |          :ref:`Expression<net.sf.jsqlparser.expression.Expression>` expression

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          returns void


            
                |          :ref:`Expression<net.sf.jsqlparser.expression.Expression>` whereExpression

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          returns void


            
                |          :ref:`Expression<net.sf.jsqlparser.expression.Expression>` qualifyExpression

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          returns void


            
                |          :ref:`Join><java.util.List<net.sf.jsqlparser.statement.select.Join>>` joins

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                    
                
            
                |          :ref:`WithItem<net.sf.jsqlparser.statement.select.WithItem>` withItem

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          int i

                |          int n

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                    
                
            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          :ref:`RowConstructor<?><net.sf.jsqlparser.expression.RowConstructor<?>>` rowConstructor

                |          returns void


            
                |          :ref:`String><java.util.Collection<java.lang.String>>` strings

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          boolean commaSeparated

                |          :ref:`BreakLine<com.manticore.jsqlformatter.JSQLFormatter.BreakLine>` breakLine

                |          returns void


            
                |          :ref:`String<java.lang.String>` s

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          int i

                |          int n

                |          boolean commaSeparated

                |          :ref:`BreakLine<com.manticore.jsqlformatter.JSQLFormatter.BreakLine>` breakLine

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                    
                
            
                |          :ref:`Expression<net.sf.jsqlparser.expression.Expression>` expression

                |          :ref:`Alias<net.sf.jsqlparser.expression.Alias>` alias

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          int i

                |          int n

                |          boolean commaSeparated

                |          :ref:`BreakLine<com.manticore.jsqlformatter.JSQLFormatter.BreakLine>` breakLine

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                        PMD.ExcessiveMethodLength
                    
                
            
                |          :ref:`ExpressionList<?><net.sf.jsqlparser.expression.operators.relational.ExpressionList<?>>` expressionList

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          :ref:`BreakLine<com.manticore.jsqlformatter.JSQLFormatter.BreakLine>` breakLine

                |          returns void


            
                |          :ref:`Expression><java.util.List<? extends net.sf.jsqlparser.expression.Expression>>` expressions

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          :ref:`BreakLine<com.manticore.jsqlformatter.JSQLFormatter.BreakLine>` breakLine

                |          returns void


            
                |          :ref:`FromItem<net.sf.jsqlparser.statement.select.FromItem>` fromItem

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          int i

                |          int n

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                    
                
            
                |          :ref:`Table<net.sf.jsqlparser.schema.Table>` table

                |          :ref:`Alias<net.sf.jsqlparser.expression.Alias>` alias

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          returns void


            
                |          :ref:`SetOperation<net.sf.jsqlparser.statement.select.SetOperation>` setOperation

                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          int indent

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                    
                
            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`Truncate<net.sf.jsqlparser.statement.truncate.Truncate>` truncate

                |          returns void


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`CreateTable<net.sf.jsqlparser.statement.create.table.CreateTable>` createTable

                |          int indent

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                        PMD.ExcessiveMethodLength
                    
                
            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`CreateIndex<net.sf.jsqlparser.statement.create.index.CreateIndex>` createIndex

                |          int indent

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                    
                
            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`CreateView<net.sf.jsqlparser.statement.create.view.CreateView>` createView

                |          int indent

                |          returns void


            
                |          :ref:`StringBuilder<java.lang.StringBuilder>` builder

                |          :ref:`Alter<net.sf.jsqlparser.statement.alter.Alter>` alter

                |          int indent

                |          returns void


                
                    
                        
                        PMD.CyclomaticComplexity
                        PMD.ExcessiveMethodLength
                    
                
            