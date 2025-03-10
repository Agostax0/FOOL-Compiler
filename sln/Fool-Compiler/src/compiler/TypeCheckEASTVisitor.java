package compiler;

import compiler.AST.*;
import compiler.exc.*;
import compiler.lib.*;

import java.lang.reflect.Type;
import java.sql.Ref;

import static compiler.lib.FOOLlib.*;

//visitNode(n) fa il type checking di un Node n e ritorna:
//- per una espressione, il suo tipo (oggetto BoolTypeNode o IntTypeNode)
//- per una dichiarazione, "null"; controlla la correttezza interna della dichiarazione
//(- per un tipo: "null"; controlla che il tipo non sia incompleto) 
//
//visitSTentry(s) ritorna, per una STentry s, il tipo contenuto al suo interno
public class TypeCheckEASTVisitor extends BaseEASTVisitor<TypeNode,TypeException> {

	TypeCheckEASTVisitor() { super(true); } // enables incomplete tree exceptions 
	TypeCheckEASTVisitor(boolean debug) { super(true,debug); } // enables print for debugging

	//checks that a type object is visitable (not incomplete) 
	private TypeNode ckvisit(TypeNode t) throws TypeException {
		visit(t);
		return t;
	} 
	
	@Override
	public TypeNode visitNode(ProgLetInNode n) throws TypeException {
		if (print) printNode(n);
		for (Node dec : n.declist)
			try {
				visit(dec);
			} catch (IncomplException e) { 
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		return visit(n.exp);
	}

	@Override
	public TypeNode visitNode(ProgNode n) throws TypeException {
		if (print) printNode(n);
		return visit(n.exp);
	}

	@Override
	public TypeNode visitNode(FunNode n) throws TypeException {
		if (print) printNode(n,n.id);
		for (Node dec : n.declist)
			try {
				visit(dec);
			} catch (IncomplException e) { 
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		if ( !isSubtype(visit(n.exp),ckvisit(n.getType())) ) //check
			throw new TypeException("Wrong return type for function " + n.id,n.getLine());
		return null;
	}

	@Override
	public TypeNode visitNode(VarNode n) throws TypeException {
		if (print) printNode(n,n.id);
		if ( !isSubtype(visit(n.exp),ckvisit(n.type)) ) //check
			throw new TypeException("Incompatible value for variable " + n.id,n.getLine());
		return null;
	}

	@Override
	public TypeNode visitNode(PrintNode n) throws TypeException {
		if (print) printNode(n);
		return visit(n.exp);
	}

	@Override
	public TypeNode visitNode(IfNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.cond), new BoolTypeNode())) )
			throw new TypeException("Non boolean condition in if",n.getLine());
		TypeNode t = visit(n.th);
		TypeNode e = visit(n.el);
		if (isSubtype(t, e)) return e;
		if (isSubtype(e, t)) return t;
		throw new TypeException("Incompatible types in then-else branches",n.getLine());
	}

	@Override
	public TypeNode visitNode(EqualNode n) throws TypeException {
		if (print) printNode(n);
		TypeNode l = visit(n.left);
		TypeNode r = visit(n.right);
		if ( !(isSubtype(l, r) || isSubtype(r, l)) )
			throw new TypeException("Incompatible types in equal",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(LessEqualNode n) throws TypeException {
		if (print) printNode(n);
		TypeNode l = visit(n.left);
		TypeNode r = visit(n.right);
		if ( !(isSubtype(l, r) || isSubtype(r, l)) )
			throw new TypeException("Incompatible types in lesser-equal",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(GreaterEqualNode n) throws TypeException {
		if (print) printNode(n);
		TypeNode l = visit(n.left);
		TypeNode r = visit(n.right);
		if ( !(isSubtype(l, r) || isSubtype(r, l)) )
			throw new TypeException("Incompatible types in greater-equal",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(TimesNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.left), new IntTypeNode())
				&& isSubtype(visit(n.right), new IntTypeNode())) )
			throw new TypeException("Non integers in multiplication",n.getLine());
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(DivNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.left), new IntTypeNode())
				&& isSubtype(visit(n.right), new IntTypeNode())) )
			throw new TypeException("Non integers in division",n.getLine());
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(PlusNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.left), new IntTypeNode())
				&& isSubtype(visit(n.right), new IntTypeNode())) )
			throw new TypeException("Non integers in sum",n.getLine());
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(MinusNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.left), new IntTypeNode())
				&& isSubtype(visit(n.right), new IntTypeNode())) )
			throw new TypeException("Non integers in minus",n.getLine());
		return new IntTypeNode();
	}

	@Override
	public TypeNode visitNode(NotNode n) throws TypeException {
		if (print) printNode(n);
		if ( !(isSubtype(visit(n.inner), new BoolTypeNode())))
			throw new TypeException("Non boolean in not",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(AndNode n) throws TypeException {
		if (print) printNode(n);
		TypeNode l = visit(n.left);
		TypeNode r = visit(n.right);
		if ( ( !(isSubtype(l, new BoolTypeNode())) ) || !(isSubtype(r, new BoolTypeNode())))
			throw new TypeException("Non boolean in AND comparison",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(OrNode n) throws TypeException {
		if (print) printNode(n);
		TypeNode l = visit(n.left);
		TypeNode r = visit(n.right);
		if ( ( !(isSubtype(l, new BoolTypeNode())) ) || !(isSubtype(r, new BoolTypeNode())))
			throw new TypeException("Non boolean in OR comparison",n.getLine());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(CallNode n) throws TypeException {
		if (print) printNode(n,n.id);
		TypeNode t = visit(n.entry); // STentry visit
		if ( !(t instanceof ArrowTypeNode))
			throw new TypeException("Invocation of a non-function "+n.id,n.getLine());
		ArrowTypeNode at = (ArrowTypeNode) t;
		if ( !(at.parlist.size() == n.arglist.size()) )
			throw new TypeException("Wrong number of parameters in the invocation of "+n.id,n.getLine());
		for (int i = 0; i < n.arglist.size(); i++)
			if ( !(FOOLlib.isSubtype(visit(n.arglist.get(i)),at.parlist.get(i))) )
				throw new TypeException("Wrong type for "+(i+1)+"-th parameter in the invocation of "+n.id,n.getLine());
		return at.ret;
	}

	@Override
	public TypeNode visitNode(IdNode n) throws TypeException {
		if (print) printNode(n,n.id);
		TypeNode t = visit(n.entry); // STentry visit
		if (t instanceof ArrowTypeNode)
			throw new TypeException("Wrong usage of function identifier " + n.id,n.getLine());
		if(t instanceof ClassTypeNode)
			throw new TypeException("Wrong usage of class identifier " + n.id,n.getLine());
		return t;
	}

	@Override
	public TypeNode visitNode(BoolNode n) {
		if (print) printNode(n,n.val.toString());
		return new BoolTypeNode();
	}

	@Override
	public TypeNode visitNode(IntNode n) {
		if (print) printNode(n,n.val.toString());
		return new IntTypeNode();
	}

// gestione tipi incompleti	(se lo sono lancia eccezione)
	
	@Override
	public TypeNode visitNode(ArrowTypeNode n) throws TypeException {
		if (print) printNode(n);
		for (Node par: n.parlist) visit(par);
		visit(n.ret,"->"); //marks return type
		return null;
	}

	@Override
	public TypeNode visitNode(BoolTypeNode n) {
		if (print) printNode(n);
		return null;
	}

	@Override
	public TypeNode visitNode(IntTypeNode n) {
		if (print) printNode(n);
		return null;
	}

//

	@Override
	public TypeNode visitSTentry(STentry entry) throws TypeException {
		if (print) printSTentry("type");
		return ckvisit(entry.type); //check
	}

	//NON USATO
	@Override
	public TypeNode visitNode(FieldNode n){
		if (print) printNode(n);
		return null;
	}

	@Override
	public TypeNode visitNode(MethodNode n) throws TypeException{
		if (print) printNode(n,n.id);
		for(Node dec : n.decList){
			try {
				visit(dec);
			} catch (IncomplException e) {
			} catch (TypeException e) {
				System.out.println("Type checking error in a declaration: " + e.text);
			}
		}
		if(!isSubtype(visit(n.exp), ckvisit(n.getType())))
			throw new TypeException("Wrong return type for method " + n.id,n.getLine());
		return null;
	}

	@Override
	public TypeNode visitNode(ClassNode n) throws TypeException {
		if (print) printNode(n, n.id);
		for(var method : n.methods){
			try {
				visit(method);
			} catch (IncomplException e) {
			} catch (TypeException e) {
				System.out.println("Class method error in a declaration: " + e.text);
			}
		}

		for(var field : n.fields){
			try {
				visit(field);
			} catch (IncomplException e) {
			} catch (TypeException e) {
				System.out.println("Class field error in a declaration: " + e.text);
			}
		}
		return null;
	}

	@Override
	public TypeNode visitNode(ClassCallNode n) throws TypeException{
		if (print) printNode(n, n.varName + "." + n.methodName);
		//non servirebbe perché lo controlla già SymbolTableVisitor
		TypeNode t = visit(n.entry);
		if( !(t instanceof RefTypeNode)){
			throw new TypeException("Invocation of a non-class var"+n.varName,n.getLine());
		}
		TypeNode m = visit(n.methodEntry);
		if( !(m instanceof ArrowTypeNode)){
			throw new TypeException("Invocation of a non-class method"+n.varName,n.getLine());
		}
		//controllo sui parametri passati al metodo
		if( ((ArrowTypeNode) m).parlist.size() != n.args.size()){
			throw new TypeException("Wrong number of parameters in the invocation of "+n.methodName,n.getLine());
		}

		for(int i = 0; i < n.args.size(); i++){
			var decElem = n.args.get(i);
			var foundElem = ((ArrowTypeNode) m).parlist.get(i);

			if( !(FOOLlib.isSubtype(visit(decElem), visit(foundElem))) )
				throw new TypeException("Wrong type for "+decElem+"-th parameter in the invocation of "+n.methodName,n.getLine());
		}

		return ((ArrowTypeNode) m).ret;
	}

	@Override
	public TypeNode visitNode(NewNode n) throws TypeException{
		if (print) printNode(n);

		if(!(n.entry.type instanceof ClassTypeNode)){
			throw new TypeException("Called new keyword to a non-class " + n.classId,n.getLine());
		}

		var decArgs = n.args;
		var foundArgs = ((ClassTypeNode) n.entry.type).allFields;

		if(decArgs.size() != foundArgs.size())
			throw new TypeException("Wrong number of parameters in the instantiation of "+n.classId,n.getLine());

		for(int i = 0; i < decArgs.size(); i++){
			var decArg = decArgs.get(i);
			var foundArg = decArgs.get(i);

			if( !(FOOLlib.isSubtype(visit(decArg), visit(foundArg))))
				throw new TypeException("Wrong type for "+decArg+"-th parameter in the instantiation of "+n.classId,n.getLine());
		}
		return new RefTypeNode(n.classId);
	}
	@Override
	public TypeNode visitNode(ClassTypeNode n) throws TypeException {
		return null;
	}
	@Override
	public TypeNode visitNode(RefTypeNode n) throws TypeException {
		return null;
	}

}