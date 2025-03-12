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

		@Override
		public String toString() {
			return "BoolNode{" +
					"val=" + val +
					'}';
		}
	}

	public static class IntNode extends Node {
		Integer val;
		IntNode(Integer n) {val = n;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

		@Override
		public String toString() {
			return "IntNode{" +
					"val=" + val +
					'}';
		}
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

		@Override
		public String toString() {
			return "bool";
		}
	}

	public static class IntTypeNode extends TypeNode {

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

		@Override
		public String toString() {
			return "int";
		}
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
		protected TypeNode getType(){return type;}
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
		public TypeNode getType(){return this.type;}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}

	}
	public static class FieldNode extends DecNode{
		String id;

		@Override
		public TypeNode getType(){return this.type;}

		FieldNode(String name, TypeNode type){this.id = name; this.type = type;}
		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}


		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			FieldNode fieldNode = (FieldNode) o;
			return Objects.equals(id, fieldNode.id);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(id);
		}

		@Override
		public String toString() {
			return "id='" + id + '\'' + ", type=" + type;
		}
	}

	public static class FunNode extends DecNode {
		String id;
		List<ParNode> parlist;
		List<Node> declist;
		Node exp;
		FunNode(String i, TypeNode rt, List<ParNode> pl, List<Node> dl, Node e) {
			id=i; this.type=rt; parlist=pl; declist=dl; exp=e;}

		@Override
		public TypeNode getType(){return this.type;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ParNode extends DecNode {
		String id;
		ParNode(String i, TypeNode t) {id = i; this.type = t;}

		@Override
		public TypeNode getType(){return this.type;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			ParNode parNode = (ParNode) o;
			return Objects.equals(id, parNode.id) && Objects.equals(this.getType().toString(), parNode.getType().toString());
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(id);
		}

		@Override
		public String toString() {
			return '\'' + id + '\'' +
					":" + type;
		}
	}

	public static class VarNode extends DecNode {
		String id;
		Node exp;
		VarNode(String i, TypeNode t, Node v) {id = i; this.type = t; exp = v;}

		@Override
		public TypeNode getType(){return this.type;}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

		@Override
		public String toString() {
			return "VarNode{" +
					"id='" + id + '\'' +
					", exp=" + exp +
					", type=" + type +
					'}';
		}
	}

	public static class MethodNode extends DecNode{
		String id;
		List<ParNode> parList;
		List<Node> decList;
		Node exp;
		int offset = 0;
		
		String label;
		public MethodNode(String id, TypeNode retType, List<ParNode> parList, List<Node> decList, Node exp) {
			this.id = id;
			this.type = retType;
			this.parList = parList;
			this.decList = decList;
			this.exp = exp;
		}

		@Override
		public TypeNode getType(){return this.type;}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			MethodNode that = (MethodNode) o;
			return Objects.equals(id, that.id) && parList.equals(that.parList);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, parList);
		}

		@Override
		public String toString() {
			return "MethodNode{" +
					"parList=" + parList +
					", id='" + id + '\'' +
					", type=" + type +
					'}';
		}
	}

	public static class ClassCallNode extends Node{

		String varName;
		String methodName;
		List<Node> args;

		STentry entry;
		STentry methodEntry;

		public ClassCallNode(String classId, String methodId, List<Node> args) {
			this.varName = classId;
			this.methodName = methodId;
			this.args = args;
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}

		@Override
		public String toString() {
			return "ClassCallNode{" +
					"varName='" + varName + '\'' +
					", methodName='" + methodName + '\'' +
					", args=" + args +
					", entry=" + entry +
					", methodEntry=" + methodEntry +
					'}';
		}
	}

	public static class NewNode extends Node{

		String classId;
		List<Node> args = new ArrayList<>();
		STentry entry;

		public NewNode(String id, List<Node> args) {
			this.classId = id;
			this.args = args;
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}

		@Override
		public String toString() {
			return "NewNode{" +
					"classId='" + classId + '\'' +
					", args=" + args +
					", entry=" + entry +
					'}';
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
		List<ArrowTypeNode> allMethods = new ArrayList<>();

		public ClassTypeNode(List<ArrowTypeNode> allMethods, List<TypeNode> allFields) {
			this.allMethods = allMethods;
			this.allFields = allFields;
		}


		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}
		
		@Override
		public String toString() {
			return "class";
		}
	}

	public static class RefTypeNode extends TypeNode{

		String classID;

		public RefTypeNode(String classID) {
			this.classID = classID;
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}
	}

	public static class EmptyTypeNode extends TypeNode{

		public EmptyTypeNode() {
		}

		@Override
		public <S, E extends Exception> S accept(BaseASTVisitor<S, E> visitor) throws E {
			return visitor.visitNode(this);
		}
	}

}