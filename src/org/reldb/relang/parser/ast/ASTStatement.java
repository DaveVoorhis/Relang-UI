/* Generated By:JJTree: Do not edit this line. ASTStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=org.reldb.relang.parser.BaseASTNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.reldb.relang.parser.ast;

public
class ASTStatement extends SimpleNode {
  public ASTStatement(int id) {
    super(id);
  }

  public ASTStatement(Relang p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(RelangVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=351bf54389e45a32803f8a39d42b5450 (do not edit this line) */
