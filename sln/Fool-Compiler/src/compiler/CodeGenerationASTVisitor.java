package compiler;

import compiler.AST.*;
import compiler.lib.*;
import compiler.exc.*;

import java.util.ArrayList;

import static compiler.lib.FOOLlib.*;
import static compiler.svm.ExecuteVM.MEMSIZE;

public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {

	CodeGenerationASTVisitor() {}
	CodeGenerationASTVisitor(boolean debug) {super(false,debug);} //enables print for debugging


	@Override
	public String visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		String declCode = null;
		for (Node dec : n.declist) declCode=nlJoin(declCode,visit(dec));
		return nlJoin(
				"push 0",
				declCode, // generate code for declarations (allocation)
				visit(n.exp),
				"halt",
				getCode()
		);
	}

	@Override
	public String visitNode(ProgNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.exp),
				"halt"
		);
	}

	@Override
	public String visitNode(FunNode n) {
		if (print) printNode(n, n.id);
		String declCode = null, popDecl = null, popParl = null;
		for (Node dec : n.declist) {
			declCode = nlJoin(declCode, visit(dec));
			popDecl = nlJoin(popDecl, "pop");
		}
		for (int i = 0; i < n.parlist.size(); i++) popParl = nlJoin(popParl, "pop");
		String funl = freshFunLabel();
		putCode(
				nlJoin(
						funl + ":",
						"cfp", // set $fp to $sp value
						"lra", // load $ra value
						declCode, // generate code for local declarations (they use the new $fp!!!)
						visit(n.exp), // generate code for function body expression
						"stm", // set $tm to popped value (function result)
						popDecl, // remove local declarations from stack
						"sra", // set $ra to popped value
						"pop", // remove Access Link from stack
						popParl, // remove parameters from stack
						"sfp", // set $fp to popped value (Control Link)
						"ltm", // load $tm value (function result)
						"lra", // load $ra value
						"js"  // jump to popped address
				)
		);
		return "push " + funl;
	}

	@Override
	public String visitNode(VarNode n) {
		if (print) printNode(n,n.id);
		return visit(n.exp);
	}

	@Override
	public String visitNode(PrintNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.exp),
				"print"
		);
	}

	@Override
	public String visitNode(IfNode n) {
		if (print) printNode(n);
		String l1 = freshLabel();
		String l2 = freshLabel();
		return nlJoin(
				visit(n.cond),
				"push 1",
				"beq "+l1,
				visit(n.el),
				"b "+l2,
				l1+":",
				visit(n.th),
				l2+":"
		);
	}

	@Override
	public String visitNode(EqualNode n) {
		if (print) printNode(n);
		String l1 = freshLabel();
		String l2 = freshLabel();
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"beq "+l1,
				"push 0",
				"b "+l2,
				l1+":",
				"push 1",
				l2+":"
		);
	}

	@Override
	public String visitNode(LessEqualNode n) {
		if (print) printNode(n);
		String ok = freshLabel();
		String ko = freshLabel();
		String end = freshLabel();
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"bleq "+ok,
				"b "+ko,
				ok+":",
				"push 1",
				"b "+end,
				ko+":",
				"push 0",
				"b "+end,
				end+":"
		);
	}

	@Override
	public String visitNode(GreaterEqualNode n) {
		if (print) printNode(n);
		String ok = freshLabel();
		String gt = freshLabel();
		String ko = freshLabel();
		String end = freshLabel();
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"beq "+ok, //CONTROLLO SIANO UGUALI
				"b "+gt,
				gt+":", //INVERTO SINISTRA CON DESTRA ED APPLICO IL MINORE
				visit(n.right), //A > B == B < A
				visit(n.left),
				"bleq "+ok,
				"b "+ko,
				ok+":",
				"push 1",
				"b "+end,
				ko+":",
				"push 0",
				"b "+end,
				end+":"
		);
	}


	@Override
	public String visitNode(TimesNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"mult"
		);
	}

	@Override
	public String visitNode(DivNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"div"
		);
	}

	@Override
	public String visitNode(PlusNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"add"
		);
	}

	@Override
	public String visitNode(MinusNode n){
		if(print) printNode(n);
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"sub"
		);
	}

	@Override
	public String visitNode(NotNode n){
		if(print) printNode(n);
		String ok = freshLabel();
		String ko = freshLabel();
		String end = freshLabel();
		return nlJoin(
				visit(n.inner),
				"push 1",
				"beq "+ko, //1 == 1 mi pusha 0
				"b "+ok, //altrimenti 0 == 1 mi pusha 1
				ko+":", //difatti invertendo il valore sullo stack
				"push 0",
				"b "+ end,
				ok+":",
				"push 1",
				"b "+end,
				end+":"
		);
	}

	@Override
	public String visitNode(AndNode n){
		if(print) printNode(n);
		String okFirst = freshLabel();
		String okSecond = freshLabel();
		String ko = freshLabel();
		String end = freshLabel();
		return nlJoin(
				visit(n.left),
				"push 1",
				"beq "+ okFirst,
				"b "+ ko,
				okFirst +":",
					visit(n.right),
					"push 1",
					"beq "+ okSecond,
					"b "+ ko,
				okSecond +":",
					"push 1",
					"b "+end,
				ko+":",
					"push 0",
					"b "+end,
				end +":"
		);
	}

	@Override
	public String visitNode(OrNode n) {
		if (print) printNode(n);
		String ok = freshLabel();
		String ko = freshLabel();
		String end = freshLabel();
		return nlJoin(
				visit(n.left),
				"push 1",
				"beq "+ok,
				visit(n.right),
				"push 1",
				"beq "+ok,
				"b "+ ko,
				ok+":",
					"push 1",
					"b "+ end,
				ko+":",
					"push 0",
					"b "+ end,
				end+":"
		);
	}

	@Override
	public String visitNode(CallNode n) {
		if (print) printNode(n,n.id);
		String argCode = null, getAR = null;
		for (int i=n.arglist.size()-1;i>=0;i--) argCode=nlJoin(argCode,visit(n.arglist.get(i)));
		for (int i = 0;i<n.nl-n.entry.nl;i++) getAR=nlJoin(getAR,"lw");
		if(n.entry.offset >= 0){
			return nlJoin(
					/**
					 * dopo aver messo sullo stack l’Access Link impostandolo
					 * all’indirizzo ottenuto tramite risalita della catena statica (in base a differenza di nesting level di ID) e aver
					 * duplicato tale indirizzo sullo stack
					 */
					"lfp", // mi salvo il Control Link
					argCode, //genera il codice per gli argomenti passati
					"lfp", // mi salvo il Control Link dopo aver dichiarato gli argomenti
					getAR,
					"stm", //lo uso per duplicarlo
					"ltm", //lo carico
					"ltm", //lo duplico
					"lw", //nel caso del metodo
					"push"+n.entry.offset,
					"add",
					"lw",
					"js"
			);
		}
		else{
			return nlJoin(
					"lfp", // load Control Link (pointer to frame of function "id" caller)
					argCode, // generate code for argument expressions in reversed order
					"lfp",// retrieve address of frame containing "id" declaration
					getAR, // by following the static chain (of Access Links)
					"stm", // set $tm to popped value (with the aim of duplicating top of stack)
					"ltm", // load Access Link (pointer to frame of function "id" declaration)
					"ltm", // duplicate top of stack
					"push "+n.entry.offset, //push fun offset
					"add", // compute address of "id" declaration
					"lw", // load address of "id" function
					"js"  // jump to popped address (saving address of subsequent instruction in $ra)
			);
		}
	}

	@Override
	public String visitNode(IdNode n) {
		if (print) printNode(n,n.id);
		String getAR = null;
		for (int i = 0;i<n.nl-n.entry.nl;i++) getAR=nlJoin(getAR,"lw");
		return nlJoin(
				"lfp", getAR, // retrieve address of frame containing "id" declaration
				// by following the static chain (of Access Links)
				"push "+n.entry.offset, "add", // compute address of "id" declaration
				"lw" // load value of "id" variable
		);
	}

	@Override
	public String visitNode(BoolNode n) {
		if (print) printNode(n,n.val.toString());
		return "push "+(n.val?1:0);
	}

	@Override
	public String visitNode(IntNode n) {
		if (print) printNode(n,n.val.toString());
		return "push "+n.val;
	}

	@Override
	public String visitNode(ClassNode n){
		if (print) printNode(n);

		var classDispatchTable = new ArrayList<String>();
		/** PER OGNI METODO
		 * invoco la sua visit()
		 * – leggo l’etichetta a cui è stato posto il suo codice dal suo campo "label" ed il suo offset dal suo campo "offset"
		 * – aggiorno la Dispatch Table creata settando la posizione data dall’offset del metodo alla sua etichetta
		 */
		for(var method : n.methods){
			visit(method);

			var methodLabelAssigned = method.label;
			var methodOffset = method.offset;

			classDispatchTable.add(methodOffset, methodLabelAssigned);
		}

		String instructions = "";

		// metto valore di $hp sullo stack: sarà il dispatchpointer da ritornare alla fine

		instructions = nlJoin("lhp"); ///push in the stack the content of the HP register

		//creo sullo heap la Dispatch Table che ho costruito: la
		//scorro dall’inizio alla fine

		for(var label : classDispatchTable){
			//per ciascuna etichetta: la memorizzo a indirizzo in $hp ed incremento $hp
			instructions = nlJoin(
					instructions,
					//prendo l'indirizzo da $hp
					"push "+label, //[label]
					"lhp",		   //[hp, label]
					"sw", ///pop two values: the second one is written at the memory address pointed by the first one [1:hp, 2:label]
					"lhp",		   //[hp]
					"push 1",      //[1, hp]
					"add",		   //[hp+1]
					"shp" ///pop the top of the stack and copy it in the HP register
					);
		}

		return instructions;
	}

	@Override
	public String visitNode(MethodNode n){
		if (print) printNode(n);

		//genera un’etichetta nuova per il suo indirizzo e la mette nel suo campo "label" (aggiungere tale campo)
		n.label = freshFunLabel();
		//genera il codice del metodo (invariato rispetto a funzioni)

		String declCode = "", popDecl = "", popParl = "	";
		for(Node dec : n.decList){
			declCode = nlJoin(declCode, visit(dec));
			popDecl = nlJoin(popDecl, "pop");
		}

		for(ParNode par : n.parList) {
			popParl = nlJoin(popParl, "pop");
		}

		/**
		 * lo inserisce in FOOLlib con putCode()
		 */
		putCode(
				nlJoin(
						n.label+":",
						"cfp", // set $fp to $sp value
						"lra", // load $ra value
						declCode, // generate code for local declarations (they use the new $fp!!!)
						visit(n.exp), // generate code for function body expression
						"stm", // set $tm to popped value (function result)
						popDecl, // remove local declarations from stack
						"sra", // set $ra to popped value
						"pop", // remove Access Link from stack
						popParl, // remove parameters from stack
						"sfp", // set $fp to popped value (Control Link)
						"ltm", // load $tm value (function result)
						"lra", // load $ra value
						"js"  // jump to popped address
				)
		);

		//– ritorna codice vuoto (null)
		return null;
	}

	@Override
	public String visitNode(EmptyNode n){
		if (print) printNode(n);
		return "push -1";  //sicuramente diverso da object pointer di ogni oggetto creato
	}

	@Override
	public String visitNode(ClassCallNode n){
		/**
		 * ID1.ID2()
		 */
		if (print) printNode(n);
		String argCode = null, getAR = null;
		for (int i=n.args.size()-1;i>=0;i--) argCode=nlJoin(argCode,visit(n.args.get(i)));
		for (int i = 0;i<n.nestingLevel-n.entry.nl;i++) getAR=nlJoin(getAR,"lw");
		String instructions =  nlJoin(
				/**
				 * inizia la costruzione dell’AR del metodo ID2 invocato:
				 * aver messo sullo stack il Control Link e il valore dei
				 * parametri
				 */
				"lfp", // load Control Link (pointer to frame of function "id" caller)
				argCode, // generate code for argument expressions in reversed order
				/**
				 * recupera valore dell'ID1 (object pointer) dall'AR dove è
				 * dichiarato
				 */
				"lfp",  // retrieve address of frame containing "id" declaration
				getAR, // by following the static chain (of Access Links)
				/**
				 *  per settare a tale valore l’Access Link mettendolo sullo
				 * stack e, duplicandolo
				 */
				"push "+n.entry.offset,
				"add", // compute address of "id1" declaration
				"lw", //mi carico il valore di id1
				"stm",
				"ltm",
				"ltm", // duplicato
				"lw",
				"push " + (n.methodEntry.offset),
				"add",// compute address of "id1" usage
				/**
				 * per recuperare (usando l’offset di ID2 nella dispatchtable riferita dal dispatch pointer dell’oggetto)
				 * l'indirizzo del metodo a cui saltare
				 */
				"lw", // load address of "id2" method
				"js"  // jump to popped address (saving address of subsequent instruction in $ra)

		);
		return instructions;
	}

	public String visitNode(NewNode n){
		if(print) printNode(n);
		String argCode = "", argToHeap = "";
		for(var arg : n.args){
			argCode = nlJoin(argCode, visit(arg));
			argToHeap = nlJoin(
					argToHeap,
					"lhp",//[val] //metto sullo stack il puntatore dell'heap
					"sw",//[hp, val] //metto il valore sotto all'indirizzo di heap

					"lhp", //incremento hp
					"push 1",
					"add",
					"shp" //mi salvo il nuovo hp
			);
		}

		String modHp = "";
		/**
		 * scrive a indirizzo $hp il dispatch pointer recuperandolo da
		 * contenuto indirizzo MEMSIZE + offset classe ID
		 */
		modHp = nlJoin(
				"push " + (MEMSIZE + n.entry.offset),
					   "lw" //scrivo ad HP il valore del push
		);

		/**
		 * carica sullo stack il valore di $hp (indirizzo object pointer da ritornare)
		 * e incrementa $hp
		 */
		String finalHp = "";
		finalHp = nlJoin(
				"lhp",
				"sw", //mi salvo ad hp il valore di hp
				"lhp",// da lasciare nello stack
				"lhp", //per incrementarlo
				"push 1",
				"add",
				"shp"
		);

		return nlJoin(
				argCode, //metto il codice degli argomenti nello stack
				argToHeap, //li sposto nell heap
				modHp,
				finalHp
		);
	}

	public String visitNode(EmptyTypeNode n){
		System.out.println("NON CI DOVRESTI ENTRARE QUI");

		return "";
	}

}