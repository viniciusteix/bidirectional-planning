package pacote;

import java.util.Vector;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

public class Bidirecional {
	Vector<Action> actionSet;		
	BDD goal;
	BDD initialState;
	BDD constraints;
	BDD acepExcuses;
	Progression progression;
	Regression regression;
	
	/* Constructor */
	public Bidirecional(ModelReader model) {
		this.actionSet = model.getActionSet();
		this.initialState = model.getInitialStateBDD();
		this.goal = model.getGoalSpec();		
		this.constraints = model.getConstraints();
		this.acepExcuses = model.getPosAcepGoalExcuses();
		this.progression = new Progression(model);
		this.regression = new Regression(model);
	}
	
	public boolean planFoward() {
		BDD reached_f = initialState.id();
		BDD foward = reached_f.id();
		BDD reached_b = goal.id();
		BDD backward = reached_b.id();
		BDD aux, aux1, aux2;
		int i = 0;
		int i_p = 0;
		int i_r = 0;
		
		while(foward.isZero() == false && backward.isZero() == false) {
			System.out.println("Global: " + i);
			aux = foward.and(backward.id());
			aux1 = foward.and(goal.id());
			aux2 = backward.and(initialState.id());
			
			if(aux.toString().equals("") == false || 
					aux1.toString().equals("") == false ||
					aux2.toString().equals("") == false) {
				System.out.println("The problem is solvable.");	
				return true;
			}
			aux.free();
			aux1.free();
			aux2.free();
			int s_foward = foward.nodeCount();
			int s_backward = backward.nodeCount();
			
			if(s_foward < s_backward) {
				i_p++;
				System.out.println("Progressiva: " + i_p);
				foward = progression.progression(foward);
				foward = foward.apply(reached_f, BDDFactory.diff);
				reached_f = reached_f.or(foward);
				reached_f = reached_f.and(constraints);
			}else {
				i_r++;
				System.out.println("Regressiva: " + i_r);
				backward = regression.regression(backward);
				backward = backward.apply(reached_b, BDDFactory.diff);
				reached_b = reached_b.or(backward);
				reached_b = reached_b.and(constraints);
			}
			i++;
		}
		System.out.println("The problem is unsolvable.");	
		return false;
	}
	
}
