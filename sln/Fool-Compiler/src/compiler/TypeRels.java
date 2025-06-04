package compiler;

import compiler.lib.TypeNode;

import java.util.Objects;

public class TypeRels {
    public static boolean isSubtype(TypeNode a, TypeNode b) {
        return a.getClass().equals(b.getClass()) //[2]
                || ((a instanceof AST.BoolTypeNode) && (b instanceof AST.IntTypeNode))
                || ((a instanceof AST.EmptyTypeNode) && (b instanceof AST.RefTypeNode)) //[1]
                || ( ((a instanceof AST.RefTypeNode) && (b instanceof AST.RefTypeNode))
                       && Objects.equals(((AST.RefTypeNode) a).classID, ((AST.RefTypeNode) b).classID))
                ;
    }
}


/*
class X(){}
   RefTypeNode
      |
      v
[1.] var x:X = null;  <- EmptyTypeNode



[2.] var x:X = new X(); <- RefTypeNode
      ^
      |
    RefTypeNode



[3.] var x:Y = new X(); <- RefTypeNode
      ^
      |
      RefTypeNode
In questo caso devo controllare non solo che RefTypeNode == RefTypeNode ma che anche il .classID == .classID
 */