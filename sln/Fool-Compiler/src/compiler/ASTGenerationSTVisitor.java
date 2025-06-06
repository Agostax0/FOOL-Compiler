package compiler;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import compiler.AST.*;
import compiler.FOOLParser.*;
import compiler.lib.*;
import static compiler.lib.FOOLlib.*;

public class ASTGenerationSTVisitor extends FOOLBaseVisitor<Node> {

	String indent;
    public boolean print;
	
    ASTGenerationSTVisitor() {}    
    ASTGenerationSTVisitor(boolean debug) { print=debug; }
        
    private void printVarAndProdName(ParserRuleContext ctx) {
        String prefix="";        
    	Class<?> ctxClass=ctx.getClass(), parentClass=ctxClass.getSuperclass();
        if (!parentClass.equals(ParserRuleContext.class)) // parentClass is the var context (and not ctxClass itself)
        	prefix=lowerizeFirstChar(extractCtxName(parentClass.getName()))+": production #";
    	System.out.println(indent+prefix+lowerizeFirstChar(extractCtxName(ctxClass.getName())));                               	
    }
        
    @Override
	public Node visit(ParseTree t) {
    	if (t==null) return null;
        String temp=indent;
        indent=(indent==null)?"":indent+"  ";
        Node result = super.visit(t);
        indent=temp;
        return result; 
	}

	@Override
	public Node visitProg(ProgContext c) {
		if (print) printVarAndProdName(c);
		return visit(c.progbody());
	}

	@Override
	public Node visitLetInProg(LetInProgContext c) {
		if (print) printVarAndProdName(c);
		List<DecNode> declist = new ArrayList<>();
		//dichiarazione delle classi
		for (CldecContext clDec: c.cldec()) declist.add((DecNode) visit(clDec));
		//dichiarazione di funzioni e parametri
		for (DecContext dec : c.dec()) declist.add((DecNode) visit(dec));
		return new ProgLetInNode(declist, visit(c.exp()));
	}

	@Override
	public Node visitNoDecProg(NoDecProgContext c) {
		if (print) printVarAndProdName(c);
		return new ProgNode(visit(c.exp()));
	}

	@Override public Node visitTimesDiv(TimesDivContext c) {
		if(print) printVarAndProdName(c);

		Node n = null;
		//check se si tratta di moltiplicazione o divisione
		if(c.TIMES() != null){
			n = new TimesNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.TIMES().getSymbol().getLine());
		}
		if (c.DIV() != null) {
			n = new DivNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.DIV().getSymbol().getLine());
		}

		return n;
}

	@Override
	public Node visitPlusMinus(PlusMinusContext c){
		if(print) printVarAndProdName(c);
		Node n = null;
		//check se si tratta di somma o differenza
		if(c.PLUS() != null){
			n = new PlusNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.PLUS().getSymbol().getLine());
		}

		if(c.MINUS() != null){
			n = new MinusNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.MINUS().getSymbol().getLine());
		}

		return n;
	}

	@Override
	public Node visitComp(CompContext c){
		if(print) printVarAndProdName(c);
		Node n = null;
		//check di quale comparazione si parla
		if(c.EQ() != null){
			n = new EqualNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.EQ().getSymbol().getLine());
		}

		if(c.LE() != null){
			n = new LessEqualNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.LE().getSymbol().getLine());
		}

		if(c.GE() != null){
			n = new GreaterEqualNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.GE().getSymbol().getLine());
		}

		return n;
	}

	@Override
	public Node visitNot(NotContext c){
		if(print) printVarAndProdName(c);
		Node n = new NotNode(visit(c.exp()));
		n.setLine(c.NOT().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitAndOr(AndOrContext c){
		if(print) printVarAndProdName(c);
		Node n = null;
		//check se si tratta di and o or
		if(c.AND() != null){
			n = new AndNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.AND().getSymbol().getLine());
		}
		if(c.OR() != null){
			n = new OrNode(visit(c.exp(0)), visit(c.exp(1)));
			n.setLine(c.OR().getSymbol().getLine());
		}
		return n;
	}

	@Override
	public Node visitVardec(VardecContext c) {
		if (print) printVarAndProdName(c);
		Node n = null;
		if (c.ID()!=null) { //non-incomplete ST
			n = new VarNode(c.ID().getText(), (TypeNode) visit(c.type()), visit(c.exp()));
			n.setLine(c.VAR().getSymbol().getLine());
		}
        return n;
	}

	@Override
	public Node visitFundec(FundecContext c) {
		if (print) printVarAndProdName(c);
		List<ParNode> parList = new ArrayList<>();
		for (int i = 1; i < c.ID().size(); i++) { 
			ParNode p = new ParNode(c.ID(i).getText(),(TypeNode) visit(c.type(i)));
			p.setLine(c.ID(i).getSymbol().getLine());
			parList.add(p);
		}
		List<Node> decList = new ArrayList<>();
		for (DecContext dec : c.dec()) decList.add(visit(dec));
		Node n = null;
		if (!c.ID().isEmpty()) { //non-incomplete ST
			n = new FunNode(c.ID(0).getText(),(TypeNode)visit(c.type(0)),parList,decList,visit(c.exp()));
			n.setLine(c.FUN().getSymbol().getLine());
		}
        return n;
	}

	@Override
	public Node visitIntType(IntTypeContext c) {
		if (print) printVarAndProdName(c);
		return new IntTypeNode();
	}

	@Override
	public Node visitBoolType(BoolTypeContext c) {
		if (print) printVarAndProdName(c);
		return new BoolTypeNode();
	}

	@Override
	public Node visitIdType(IdTypeContext c){
		if (print) printVarAndProdName(c);
		return new RefTypeNode(c.ID().getText());
	}

	@Override
	public Node visitInteger(IntegerContext c) {
		if (print) printVarAndProdName(c);
		int v = Integer.parseInt(c.NUM().getText());
		return new IntNode(c.MINUS()==null?v:-v);
	}

	@Override
	public Node visitTrue(TrueContext c) {
		if (print) printVarAndProdName(c);
		return new BoolNode(true);
	}

	@Override
	public Node visitFalse(FalseContext c) {
		if (print) printVarAndProdName(c);
		return new BoolNode(false);
	}

	@Override
	public Node visitIf(IfContext c) {
		if (print) printVarAndProdName(c);
		Node ifNode = visit(c.exp(0));
		Node thenNode = visit(c.exp(1));
		Node elseNode = visit(c.exp(2));
		Node n = new IfNode(ifNode, thenNode, elseNode);
		n.setLine(c.IF().getSymbol().getLine());			
        return n;		
	}

	@Override
	public Node visitPrint(PrintContext c) {
		if (print) printVarAndProdName(c);
		return new PrintNode(visit(c.exp()));
	}

	@Override
	public Node visitPars(ParsContext c) {
		if (print) printVarAndProdName(c);
		return visit(c.exp());
	}

	@Override
	public Node visitId(IdContext c) {
		if (print) printVarAndProdName(c);
		Node n = new IdNode(c.ID().getText());
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitCall(CallContext c) {
		if (print) printVarAndProdName(c);		
		List<Node> arglist = new ArrayList<>();
		for (ExpContext arg : c.exp()) arglist.add(visit(arg));
		Node n = new CallNode(c.ID().getText(), arglist);
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitCldec(CldecContext c){
		if(print) printVarAndProdName(c); //c.ID().forEach(it -> System.out.println(it));
		List<FieldNode> fields = new ArrayList<>();
		List<MethodNode> methods = new ArrayList<>();
		//class identifier all'ID 0
		String id = c.ID(0).getText();
		//tutti gli ID escluso lo 0 contengono le dichiarazioni dei parametri della classe
		for (int i = 1; i < c.ID().size(); i++){
			//tipo e nome del parametro
			FieldNode fn = new FieldNode(c.ID(i).getText(), (TypeNode) visit(c.type(i - 1)));
			fn.setLine(c.ID(i).getSymbol().getLine());
			fields.add(fn);
		}
		//lista dei metodi dichiarati dalla classe
		for(MethdecContext methdec : c.methdec()) methods.add((MethodNode) visit(methdec));

		Node n = null;
		if(!c.ID().isEmpty()){
			n = new ClassNode(fields, methods, id);
			n.setLine(c.CLASS().getSymbol().getLine());
		}
		return n;
	}

	@Override
	public Node visitMethdec(MethdecContext c){
		if(print) printVarAndProdName(c);
		List<ParNode> parList = new ArrayList<>();
		//l'ID del metodo è il primo dichiarato
		String id = c.ID(0).getText();
		//Il tipo di ritorno è il primo tipo trovato
		TypeNode retType = (TypeNode) visit(c.type(0));
		for(int i = 1; i < c.ID().size(); i++){
			//l'indice 0 è riservato alla dichiarazione del metodo, i restanti sono i parametri del metodo
			ParNode parNode = new ParNode(c.ID(i).getText(), (TypeNode) visit(c.type(i)));
			parNode.setLine(c.ID(i).getSymbol().getLine());
			parList.add(parNode);
		}
		List<Node> decList = new ArrayList<>();
		for(DecContext dec : c.dec()) decList.add(visit(dec));
		Node n = null;
		if(!c.ID().isEmpty()){
			n = new MethodNode(id, retType, parList, decList, visit(c.exp()));
			n.setLine(c.FUN().getSymbol().getLine());
		}
		return n;
	}

	@Override
	public Node visitNew(NewContext c){
		if(print) printVarAndProdName(c);
		//ID della classe su cui si fa new
		String classId = c.ID().getText();
		List<Node> args = new ArrayList<>();
		//esploro ogni utilizzo dei parametri per inizializzare la classe
		for(var arg : c.exp()){args.add(visit(arg));}
		Node n = new NewNode(classId, args);
		n.setLine(c.ID().getSymbol().getLine());
		return n;
	}

	//chiamata al metodo
	@Override
	public Node visitClassCall(ClassCallContext c){
		if(print) printVarAndProdName(c);

		//caso dove mancano degli argomenti IDclasse . IDmetodo
		if(c.ID().size() != 2) return null;

		String varName = c.ID(0).getText();
		String methodName = c.ID(1).getText();

		List<Node> args = new ArrayList<>();
		//esploro ogni argomento dato al metodo
		for(var exp : c.exp()) args.add(visit(exp));

		Node n = new ClassCallNode(varName,methodName, args);
		n.setLine(c.ID(1).getSymbol().getLine());
		return n;
	}

	@Override
	public Node visitNull(NullContext c){
		if(print) printVarAndProdName(c);
		return new EmptyNode();
	}
}
