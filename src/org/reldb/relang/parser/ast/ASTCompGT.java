/* Generated By:JJTree: Do not edit this line. ASTCompGT.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=org.reldb.relang.parser.BaseASTNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.reldb.relang.parser.ast;

public
class ASTCompGT extends SimpleNode {
  public ASTCompGT(int id) {
    super(id);
  }

  public ASTCompGT(Relang p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(RelangVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=8c25300ef02ccf4b8d42fb5ae92c184e (do not edit this line) */
