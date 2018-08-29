package pacote;
import java.io.IOException;

/* Implementation of the algorithms that performs backward planning, using regression.
 * The method proposed by [Fourmann, 2000] and the method proposed  by "[Ritanen, 2008]" */

public class GUI {
	/* The main method receives a file containing the description of the planning  domain\problem 
	 * and calls the backward search. */
	public static void main(String[] args) throws IOException{
		//File containing the description of the planning domain-problem 
		String fileName = "/home/viviane/eclipse-workspace/Projeto Bidirecional/inputs/LOGISTICS-4-0-GROUNDED.txt";
//		String fileName = "roversteste.txt";
		String type = "propplan"; //"ritanen" or "propplan"
//		int nodenum = 9000000;
//		int cachesize =  900000;
		
		int nodenum = 50000000;
		int cachesize =  5000000;
		
		ModelReader model = new ModelReader();	
		model.fileReader(fileName, type, nodenum, cachesize);
		
		System.out.println(fileName.substring(fileName.lastIndexOf("/") + 1,fileName.lastIndexOf(".")));
		
		/*Performs progressive and regressive search in order to get the minimal modifications*/

		/* Problemas que da bug:
		 * keys3-1-v4unsol.txt~
		 * keys3-1v2unsol.txt~
		 * 
		 * */
		
//		Regression r = new Regression(model);
//		System.out.println("Regressive search");
//		long start = System.currentTimeMillis();
//		r.planBackward();
//		long end = System.currentTimeMillis();
//		System.out.println("Regressive search");
//		System.out.println(fileName);
//		System.out.println("Tempo gasto: " + (end - start));

//		Progression p = new Progression(model);
//		System.out.println("Progressive search");
//		long start = System.currentTimeMillis();
//		p.planForward();
//		long end = System.currentTimeMillis();
//		System.out.println("Progressive search");
//		System.out.println(fileName);
//		System.out.println("Tempo gasto: " + (end - start));
		
		Bidirecional b = new Bidirecional(model);
		System.out.println("Bidirectional search");
		long start = System.currentTimeMillis();
		b.planFoward();
		long end = System.currentTimeMillis();
		System.out.println("Bidirectional search");
		System.out.println(fileName);
		System.out.println("Tempo gasto: " + (end - start));
	}
}