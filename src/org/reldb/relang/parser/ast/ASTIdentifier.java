/* Generated By:JJTree: Do not edit this line. ASTIdentifier.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=org.reldb.relang.parser.BaseASTNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.reldb.relang.parser.ast;

public
class ASTIdentifier extends SimpleNode {
  public ASTIdentifier(int id) {
    super(id);
  }

  public ASTIdentifier(Relang p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(RelangVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=567f17b43300c296c6f39243e77e3bd4 (do not edit this line) */
