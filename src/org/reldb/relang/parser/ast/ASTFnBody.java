/* Generated By:JJTree: Do not edit this line. ASTFnBody.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=org.reldb.relang.parser.BaseASTNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.reldb.relang.parser.ast;

public
class ASTFnBody extends SimpleNode {
  public ASTFnBody(int id) {
    super(id);
  }

  public ASTFnBody(Relang p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(RelangVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=0eca25f6be56ea8add0d55fbfb8457d3 (do not edit this line) */
