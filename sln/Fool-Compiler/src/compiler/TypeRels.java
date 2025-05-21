package compiler;

import compiler.lib.TypeNode;

public class TypeRels {
    public static boolean isSubtype(TypeNode a, TypeNode b) {
        return a.getClass().equals(b.getClass())
                || ((a instanceof AST.BoolTypeNode) && (b instanceof AST.IntTypeNode))
                || ((a instanceof AST.EmptyTypeNode) && (b instanceof AST.RefTypeNode))
                || ((a instanceof AST.RefTypeNode) && (b instanceof AST.EmptyTypeNode))
                ;
    }
}
