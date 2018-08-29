package pacote;
import java.util.Vector;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

public class Progression{
	Vector<Action> actionSet;		
	BDD goal;
	BDD initialState;
	BDD constraints;
	BDD acepExcuses;
	
	/* Constructor */
	public Progression(ModelReader model) {
		this.actionSet = model.getActionSet();
		this.initialState = model.getInitialStateBDD();
		this.goal = model.getGoalSpec();		
		this.constraints = model.getConstraints();
		this.acepExcuses = model.getPosAcepGoalExcuses();
	}	
	
	/* Foward search from the initial state, towards a goal state. */
	public boolean planForward(){
		BDD reached = initialState.id(); //accumulates the reached set of states.
		BDD Z = reached.id(); // Only new states reached	
		BDD aux;	
		int i = 0;
		
		Vector<BDD> excusesVec = new Vector<BDD>(); 
		
		while(Z.isZero() == false){
			System.out.println(i);
//			System.out.println(Z.toString());
//			System.out.println("init");
//			System.out.println(initialState.toString());
//			System.out.println(initialState.nodeCount());
//			initialState.printDot();
//			System.out.println("goal");
//			System.out.println(goal.toString());
//			System.out.println(goal.nodeCount());
//			goal.printDot();
//			System.out.println("and");
//			BDD bdd = initialState.and(goal.id());
//			System.out.println(bdd.toString());
//			System.out.println(bdd.nodeCount());
//			bdd.printDot();
//			break;
			aux = Z.and(goal.id());	
			if (aux.toString().equals("") == false) {
				System.out.println("The problem is solvable.");	
				return true;
			}
			aux.free();
//			aux = Z;					
			Z = progression(Z); 
//			aux.free();
			
//			aux = Z;
			Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
//			excusesVec.add(i,Z.id());
//			aux.free();
			
//			aux = reached;
			reached = reached.or(Z); //Union with the new reachable states
//			aux.free();
			
//			aux = reached;
			reached = reached.and(constraints);
//			aux.free();
			
			i++;
		}
		
		System.out.println("The problem is unsolvable.");
		
		return false;
	}
		
	/* Deterministic Progression of a formula by a set of actions */
	public BDD progression(BDD formula){
		BDD reg = null;	
		BDD teste = null;
		BDD aux = null;
		for (Action a : actionSet) {
			teste = progressionQbf(formula,a);
			
			aux = teste;
			teste = teste.and(constraints);
			aux.free();
						
			if(reg == null){
				reg = teste;
			}else{
				reg.orWith(teste);
			}	
		}
		return reg;
	}
	
	/* Propplan progression based on action: Qbf based computation */
	public BDD progressionQbf(BDD Y, Action a) {
		//System.out.println("regression qbf");
		BDD reg, aux;
		reg = Y.and(a.getPrecondition()); //(Y ^ effect(a))
		
		if(reg.isZero() == false){	
			aux = reg;
			reg = reg.exist(a.getChange()); //qbf computation
			aux.free();
				
			aux = reg;
			reg = reg.and(a.getEffect()); //precondition(a) ^ E changes(a). test
			aux.free();
				
			aux = reg;
			reg = reg.and(constraints);
			aux.free();
		}
		return  reg;
 	}
}
	