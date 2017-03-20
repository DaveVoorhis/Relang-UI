/* Generated By:JavaCC: Do not edit this line. RelangVisitor.java Version 5.0 */
package org.reldb.relang.parser.ast;

public interface RelangVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTCode node, Object data);
  public Object visit(ASTStatement node, Object data);
  public Object visit(ASTBlock node, Object data);
  public Object visit(ASTParmlist node, Object data);
  public Object visit(ASTFnBody node, Object data);
  public Object visit(ASTFnDef node, Object data);
  public Object visit(ASTReturnExpression node, Object data);
  public Object visit(ASTAssignment node, Object data);
  public Object visit(ASTIfStatement node, Object data);
  public Object visit(ASTForLoop node, Object data);
  public Object visit(ASTWrite node, Object data);
  public Object visit(ASTCall node, Object data);
  public Object visit(ASTArgList node, Object data);
  public Object visit(ASTOr node, Object data);
  public Object visit(ASTAnd node, Object data);
  public Object visit(ASTCompEqual node, Object data);
  public Object visit(ASTCompNequal node, Object data);
  public Object visit(ASTCompGTE node, Object data);
  public Object visit(ASTCompLTE node, Object data);
  public Object visit(ASTCompGT node, Object data);
  public Object visit(ASTCompLT node, Object data);
  public Object visit(ASTAdd node, Object data);
  public Object visit(ASTSubtract node, Object data);
  public Object visit(ASTTimes node, Object data);
  public Object visit(ASTDivide node, Object data);
  public Object visit(ASTUnaryNot node, Object data);
  public Object visit(ASTUnaryPlus node, Object data);
  public Object visit(ASTUnaryMinus node, Object data);
  public Object visit(ASTFnInvoke node, Object data);
  public Object visit(ASTIdentifier node, Object data);
  public Object visit(ASTDereference node, Object data);
  public Object visit(ASTInteger node, Object data);
  public Object visit(ASTRational node, Object data);
  public Object visit(ASTTrue node, Object data);
  public Object visit(ASTFalse node, Object data);
}
/* JavaCC - OriginalChecksum=8cb2bbac8a3e6407a839d57c61151257 (do not edit this line) */
