package compiler;

import java.util.*;
import compiler.lib.*;

public class AST {

	public static class ProgLetInNode extends Node {
		List<Node> classList;
		List<Node> declist;
		Node exp;
		ProgLetInNode(List<Node> cl, List<Node> d, Node e) {classList = cl;declist = d; exp = e;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ProgNode extends Node {
		Node exp;
		ProgNode(Node e) {exp = e;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}



	public static class PrintNode extends Node {
		Node exp;
		PrintNode(Node e) {exp = e;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class IfNode extends Node {
		Node cond;
		Node th;
		Node el;
		IfNode(Node c, Node t, Node e) {cond = c; th = t; el = e;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class EqualNode extends Node {
		Node left;
		Node right;
		EqualNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class TimesNode extends Node {
		Node left;
		Node right;
		TimesNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class PlusNode extends Node {
		Node left;
		Node right;
		PlusNode(Node l, Node r) {left = l; right = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class CallNode extends Node {
		String id;
		List<Node> arglist = new ArrayList<Node>();
		STentry entry;
		int nl;
		CallNode(String i, List<Node> p) {id = i; arglist = p;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class IdNode extends Node {
		String id;
		STentry entry;
		int nl;
		IdNode(String i) {id = i;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class BoolNode extends Node {
		Boolean val;
		BoolNode(boolean n) {val = n;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class IntNode extends Node {
		Integer val;
		IntNode(Integer n) {val = n;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ArrowTypeNode extends TypeNode {
		List<TypeNode> parlist;
		TypeNode ret;
		ArrowTypeNode(List<TypeNode> p, TypeNode r) {parlist = p; ret = r;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class BoolTypeNode extends TypeNode {

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class IntTypeNode extends TypeNode {

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class GreaterEqualNode extends Node{
		Node left;
		Node right;
		GreaterEqualNode(Node l, Node r) {left = l; right = r;}
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class LessEqualNode extends Node{
		Node left;
		Node right;
		LessEqualNode(Node l, Node r) {left = l; right = r;}
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class NotNode extends Node{
		Node inner;
		NotNode(Node n) {this.inner = n;}
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class MinusNode extends Node{
		Node left;
		Node right;
		MinusNode(Node l, Node r) {left = l; right = r;}
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class OrNode extends Node{
		Node left;
		Node right;
		OrNode(Node l, Node r) {left = l; right = r;}
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class DivNode extends Node{
		Node left;
		Node right;
		DivNode(Node l, Node r) {left = l; right = r;}
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class AndNode extends Node{
		Node left;
		Node right;
		AndNode(Node l, Node r) {left = l; right = r;}
		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

	}

	public static abstract class DecNode extends Node{
		protected TypeNode type;
		public TypeNode getType(){return type;}
	}

	public static class ClassNode extends DecNode{

		String id;
		List<FieldNode> fields = new ArrayList<>();
		List<MethodNode> methods = new ArrayList<>();

		public ClassNode(List<FieldNode> fields, List<MethodNode> methods, String id) {
			this.fields = fields;
			this.methods = methods;
			this.id = id;
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}

	}
	public static class FieldNode extends DecNode{
		String id;
		FieldNode(String name, TypeNode type){this.id = name; type = type;}
		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}

	}

	public static class FunNode extends DecNode {
		String id;
		List<ParNode> parlist;
		List<Node> declist;
		Node exp;
		FunNode(String i, TypeNode rt, List<ParNode> pl, List<Node> dl, Node e) {
			id=i; type=rt; parlist=pl; declist=dl; exp=e;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ParNode extends DecNode {
		String id;
		ParNode(String i, TypeNode t) {id = i; type = t;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class VarNode extends DecNode {
		String id;
		Node exp;
		VarNode(String i, TypeNode t, Node v) {id = i; type = t; exp = v;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class MethodNode extends DecNode{
		String id;
		List<ParNode> parList;
		List<Node> decList;
		Node exp;
		int offset = 0;
		public MethodNode(String id, TypeNode retType, List<ParNode> parList, List<Node> decList, Node exp) {
			this.id = id;
			type = retType;
			this.parList = parList;
			this.decList = decList;
			this.exp = exp;
		}


		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}
	}

	public static class ClassCallNode extends Node{

		public ClassCallNode() {
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}
	}

	public static class NewNode extends Node{

		public NewNode() {
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}
	}

	public static class EmptyNode extends Node{

		public EmptyNode() {
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}
	}

	public static class ClassTypeNode extends TypeNode{

		List<TypeNode> allFields = new ArrayList<>();
		List<TypeNode> allMethods = new ArrayList<>();

		public ClassTypeNode(List<TypeNode> allMethods, List<TypeNode> allFields) {
			this.allMethods = allMethods;
			this.allFields = allFields;
		}


		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}
	}

	public static class RefTypeNode extends Node{

		public RefTypeNode() {
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}
	}

	public static class EmptyTypeNode extends Node{

		public EmptyTypeNode() {
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}
	}

}