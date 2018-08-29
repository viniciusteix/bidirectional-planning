package pacote;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

public class Regression{
	
	Vector<Action> actionSet;		
	BDD goalState;
	BDD initialState;
	BDD constraints;
	BDD acepExcuses;
	String type;
	
	/*For Ritanen's regression. */
	Hashtable<Integer,String> varTable;
//	Hashtable<Integer,BDD> epcPTable = new Hashtable<Integer, BDD>();
//	Hashtable<Integer,BDD> epcnotPTable = new Hashtable<Integer, BDD>();

	/* Constructor */
	public Regression(ModelReader model) {
		this.actionSet = model.getActionSet();
		this.initialState = model.getInitialStateBDD();
		this.goalState = model.getGoalSpec();		
		this.constraints = model.getConstraints();
//		this.acepExcuses = model.getPosAcepExcuses();
		this.type = model.getType();
		
//		if(type.equals("ritanen")){
//			this.varTable = model.getVarTable2();
//		}
	}	
	
	/* Backward search from the goal state, towards initial state. */
	public boolean planBackward() throws IOException{
		BDD reached = goalState.id(); //accumulates the reached set of states.
		BDD Z = reached.id(); // Only new states reached	
		BDD aux;	
		int i = 0;
//		Vector<BDD> excusesVec = new Vector<BDD>(); 
		
		while(Z.isZero() == false){
			System.out.println(i);
			aux = Z.and(initialState.id());	
			if (aux.equals(initialState.id())) {
				System.out.println("The problem is solvable.");	
				return true;
			}
			aux.free();
			aux = Z;					
			Z = regression(Z); 
			aux.free();
			
			aux = Z;
			Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
//			excusesVec.add(i,Z.id());		
			aux.free();
			
			aux = reached;
			reached = reached.or(Z); //Union with the new reachable states
			aux.free();			
			
			aux = reached;
			reached = reached.and(constraints);
			aux.free();
			
			i++;				
		}
		
		System.out.println("The problem is unsolvable.");
		return false;
	}
		
	/* Deterministic Regression of a formula by a set of actions */
	public BDD regression(BDD formula){
		BDD reg = null;	
		BDD teste = null;
		BDD aux = null;
		//constraints.printSet();
		for (Action a : actionSet) {
			teste = regressionQbf(formula,a);
//			if(type.equals("ritanen")){
//				teste =  regressionEpc(formula,a);
//			}else if(type.equals("propplan")){
//				teste = regressionQbf(formula,a);
//			}
			
			aux = teste;
			//System.out.println("sem contraint");
			//teste.printSet();
			teste = teste.and(constraints);
			aux.free();	
			//System.out.println("com contraint");
			//teste.printSet();
			
			if(reg == null){
				reg = teste;
			}else{
				reg.orWith(teste);
			}	
		}
		return reg;
	}
	
	/* Propplan regression based on action: Qbf based computation */
	public BDD regressionQbf(BDD Y, Action a) {
		//System.out.println("regression qbf");
		BDD reg, aux;
		reg = Y.and(a.getEffect()); //(Y ^ effect(a))
		
		if(reg.isZero() == false){
//			System.out.println(a.getName());
			
			//System.out.println(a.getName());
			aux = reg;
			reg = reg.exist(a.getChange()); //qbf computation
			aux.free();
				
			aux = reg;
			reg = reg.and(a.getPrecondition()); //precondition(a) ^ E changes(a). test
			aux.free();
			
			/*if(reg.toString().equals("") == false){
				System.out.println(a.getName());
			}*/
				
			aux = reg;
			reg = reg.and(constraints);
			aux.free();
		}
		return  reg;
 	}
	
	/*Ritanen's regression based on action: epc computation */
//	public BDD regressionEpc(BDD formula, Action a){
//		BDDFactory factory = a.getEffect().getFactory();
//		BDD epcP, epcNotP, prop, negprop;
//		
//		/* formulaR: obtained from formula by replacing every literal l by epc_l(e) v (l ^ epc_~l(e))*/
//		BDD formulaR = formula.id();
//		
//		/*formulaR computation: 
//			- For each p, if EPC_p(e) = true then EPC_p(e) v (l ^ EPC_~l(e)) = true 
//			- For each p, if EPC_~p(e) = true then EPC_p(e) v (l ^ EPC_~l(e)) = false */			
//		//Hashtable<Integer, Integer> changeHash = a.getChangeSetHash();
//
//			for(int i = 0; i < varTable.size(); i++){
//				prop = factory.ithVar(i); //BDD for proposition
//				negprop = factory.nithVar(i); //BDD for negation of the proposition
//				
//				epcP = a.getEpcPTable().get(i); 
//				epcNotP = a.getEpcnotPTable().get(i);
//						
//				if(epcP.equals(factory.one())){ //if (E_p(e)) = true)
//					formulaR.restrictWith(prop.id());
//				}else if(epcNotP.equals(factory.one())){
//					formulaR.restrictWith(negprop.id());
//				}
//				
//				prop.free();
//				negprop.free();
//			}
//		//System.out.println("bdd formula after epc: " + formula.nodeCount());
//		// regression(f,a) = precondition(a) and formula_r
//		BDD reg = a.getPrecondition().and(formulaR);		
//	
//		formulaR.free();
//		
//		/*if(reg.toString().equals("") == false){
//			System.out.println(a.getName());
//		}*/
//
//		return reg;	
//	}
	
	
	/*Strong regression of a set of states computed using QBF*/
	/*TODO: Modificar */
	/*public BDD strongRegressionQbf(BDD formula, Action a){		
		//System.out.println("regression qbf");
		BDD reg, aux;
		reg = formula.and(a.getEffect()); //(Y ^ effect(a))
		
		if(reg.isZero() == false){
			aux = reg;
			reg = reg.forAll(a.getChange()); //qbf computation
			aux.free();
				
			aux = reg;
			reg = reg.and(a.getPrecondition()); //precondition(a) ^ E changes(a). test
			aux.free();
			
			if(reg.toString().equals("") == false){
				System.out.println(a.getName());
			}
				
			aux = reg;
			reg = reg.and(constraints);
			aux.free();
		}
		
		return  reg;	
	}*/
	
}
	