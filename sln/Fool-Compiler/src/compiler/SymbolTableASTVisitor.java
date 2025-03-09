package compiler;

import java.util.*;
import compiler.AST.*;
import compiler.exc.*;
import compiler.lib.*;

import javax.xml.transform.stream.StreamSource;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void,VoidException> {

	private List<Map<String, STentry>> symTable = new ArrayList<>();
	private Map<String, Map<String, STentry>> classTable = new HashMap<>();
	private int nestingLevel=0; // current nesting level
	private int decOffset=-2; // counter for offset of local declarations at current nesting level
	int stErrors=0;

	SymbolTableASTVisitor() {}
	SymbolTableASTVisitor(boolean debug) {super(debug);} // enables print for debugging

	private STentry stLookup(String id) {
		int j = nestingLevel;
		STentry entry = null;
		while (j >= 0 && entry == null)
			entry = symTable.get(j--).get(id);
		return entry;
	}

	@Override
	public Void visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		Map<String, STentry> hm = new HashMap<>();
		symTable.add(hm);
		//prima le classi poi le fun/var così se dichiaro un oggetto ha gia la classe dentro la symboltable
		for (Node cla: n.classList) { /*System.out.println("class: " + cla);*/ visit(cla); }
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		symTable.remove(0);
		return null;
	}

	@Override
	public Void visitNode(ProgNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(FunNode n) {
		if (print) printNode(n);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		List<TypeNode> parTypes = new ArrayList<>();
		for (ParNode par : n.parlist) parTypes.add(par.type);
		STentry entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes,n.getType()),decOffset--);
		//inserimento di ID nella symtable
		if (hm.put(n.id, entry) != null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		//creare una nuova hashmap per la symTable
		nestingLevel++;
		Map<String, STentry> hmn = new HashMap<>();
		symTable.add(hmn);
		int prevNLDecOffset=decOffset; // stores counter for offset of declarations at previous nesting level
		decOffset=-2;

		int parOffset=1;
		for (ParNode par : n.parlist)
			if (hmn.put(par.id, new STentry(nestingLevel,par.type,parOffset++)) != null) {
				System.out.println("Par id " + par.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		//rimuovere la hashmap corrente poiche' esco dallo scope
		symTable.remove(nestingLevel--);
		decOffset=prevNLDecOffset; // restores counter for offset of declarations at previous nesting level
		return null;
	}

	@Override
	public Void visitNode(VarNode n) {
		if (print) printNode(n);
		visit(n.exp);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		STentry entry = new STentry(nestingLevel,n.type,decOffset--);
		//inserimento di ID nella symtable
		if (hm.put(n.id, entry) != null) {
			System.out.println("Var id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		return null;
	}

	@Override
	public Void visitNode(PrintNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(IfNode n) {
		if (print) printNode(n);
		visit(n.cond);
		visit(n.th);
		visit(n.el);
		return null;
	}

	@Override
	public Void visitNode(EqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(LessEqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(GreaterEqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(TimesNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(DivNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(PlusNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(MinusNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(NotNode n) {
		if (print) printNode(n);
		visit(n.inner);
		return null;
	}

	@Override
	public Void visitNode(AndNode n){
		printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(OrNode n){
		printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(CallNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl = nestingLevel;
		}
		for (Node arg : n.arglist) visit(arg);
		return null;
	}

	@Override
	public Void visitNode(IdNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Var or Par id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl = nestingLevel;
		}
		return null;
	}

	@Override
	public Void visitNode(BoolNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}

	@Override
	public Void visitNode(IntNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}

	@Override
	public Void visitNode(ClassNode n){
		if(print) printNode(n);

		ClassTypeNode ctn = new ClassTypeNode(
				new ArrayList<>(), //allFields
				new ArrayList<>()  //allMethods
		);

		final int globalNestingLevel = 0;
		STentry entry = new STentry(globalNestingLevel, ctn, decOffset--);

		Map<String, STentry> hm = symTable.get(globalNestingLevel);

		if(hm.put(n.id, entry) != null){
			System.out.println("Class id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}

		//info per la class table

		Map<String, STentry> virtualTable = new HashMap<>();
		classTable.put(n.id, virtualTable);

		symTable.add(virtualTable);
		nestingLevel++; //dovrebbe essere 1

		//PARAMETRI
		List<FieldNode> uniqFields = new ArrayList<>();
		for(var field : n.fields){
			if(!uniqFields.contains(field)){
				uniqFields.add(field);
			}
			else{
				System.out.println("Par id " + field.id + ":" + field.type.toString() + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		}

		int fieldsOffset = -1;
		for(var uniqField : uniqFields){
			visit(uniqField);

			STentry fieldEntry = new STentry(nestingLevel, uniqField.type, fieldsOffset--);

			ctn.allFields.add(-fieldEntry.offset -1, fieldEntry.type); //il primo lo mette in posizione -1

			virtualTable.put(uniqField.id, fieldEntry);
		}

		//METODI
		List<MethodNode> uniqMethods = new ArrayList<>();
		for(var method : n.methods){
			if(!uniqMethods.contains(method)){
				uniqMethods.add(method);
			}
			else{
				System.out.println("Method id " + method.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		}

		final int prevOffset = decOffset;
		decOffset = 0;

		for(var uniqMethod : uniqMethods){
			visit(uniqMethod);

			var methodEntry = symTable.get(nestingLevel).get(uniqMethod.id).type;

			ctn.allMethods.add(uniqMethod.offset, methodEntry);
		}

		decOffset=prevOffset;
		symTable.remove(nestingLevel--);
		return null;
	}

	@Override
	public Void visitNode(FieldNode n){
		if(print) printNode(n);
		return null;
	}

	@Override
	public Void visitNode(MethodNode n){
		if(print) printNode(n);
		var virtualTable = symTable.get(nestingLevel);

        List<TypeNode> parTypes = new ArrayList<>(n.parList.stream().map(ParNode::getType).toList());

		var entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes, n.type), decOffset++);
		n.offset = entry.offset;

		//lo aggiungo alla virtual table
		virtualTable.put(n.id, entry);

		//integro dentro la symbol table
		nestingLevel++;
		Map<String, STentry> methodTable = new HashMap<>();
		symTable.add(methodTable);

		int prevOffset = decOffset;
		decOffset = -2;
		int parOffset = 1;

		for(ParNode par : n.parList) {
			if (methodTable.put(par.id, new STentry(nestingLevel,par.type, parOffset++)) != null) {
				System.out.println("Par id " + par.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		}

		for (Node dec : n.decList) visit(dec);

		visit(n.exp);

		symTable.remove(nestingLevel--);
		decOffset = prevOffset;

		return null;
	}

	@Override
	public Void visitNode(NewNode n){
		if(print) printNode(n);
		if(!classTable.containsKey(n.classId)){
			System.out.println("Class " + n.classId + " at line "+ n.getLine() +" not declared");
			stErrors++;
		}
		int globalNestingLevel = 0;
		n.entry = symTable.get(globalNestingLevel).get(n.classId);
		n.args.forEach(this::visit);
		System.out.println(symTable);
		return null;
	}

	@Override
	public Void visitNode(ClassCallNode n){
		if(print) printNode(n);

//		System.out.println(n);
//		System.out.println(symTable);
//		System.out.println(classTable);

		//oggetto chiamato entry
//		STentry classVarEntry = stLookup(n.varName);
//		System.out.println(classVarEntry);

		return null;
	}

	@Override
	public Void visitNode(EmptyNode n){
		if(print) printNode(n);
		return null;
	}

	@Override
	public Void visitNode(ClassTypeNode n){
		if(print) printNode(n);
		return null;
	}

	@Override
	public Void visitNode(RefTypeNode n){
		if(print) printNode(n);
		return null;
	}

	@Override
	public Void visitNode(EmptyTypeNode n){
		if(print) printNode(n);
		return null;
	}
}
