import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import org.jgrapht.*;
import org.jgrapht.graph.*;

public class ContextFlowGraph {

	public static DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
			DefaultEdge.class);
	public static Scanner sc = null;
	public static String isif;
	public static String ifdataend;
	public static String whst;
	public static String curline;
	public static String prev;
	public static boolean trigif = false; 
	public static boolean nextif = false;
	public static boolean trigelse = false;
	public static ArrayList<String> ifstmnts,frstmnts ,whstmnts, endifdatastmnts, dostmnts;

	public static void main(String[] args) throws Exception {
		
		//initialize the variables
		ifstmnts = new ArrayList<String>();
		frstmnts = new ArrayList<String>();
		whstmnts = new ArrayList<String>();
		endifdatastmnts = new ArrayList<String>();
		dostmnts = new ArrayList<String>();
		
		//parse the files
		sc = new Scanner(new File("testing1.txt"));

		//Always start with "Begin"
		prev = "Begin";
		directedGraph.addVertex(prev);
		
		while (sc.hasNextLine()) {
			curline = sc.nextLine();
			prsdata();
		}
		
		//Always stop with "End"
		curline = "End";
		directedGraph.addVertex(curline);
		directedGraph.addEdge(prev, curline);
		
		
		//make graph
		Graph<String, DefaultEdge> g = directedGraph;
		boolean selfReferencesAllowed = false;

		JFrame frame = new JFrame();
		frame.getContentPane().add(new TouchgraphPanel<String, DefaultEdge>(g, selfReferencesAllowed));
		frame.setPreferredSize(new Dimension(1000, 1000));
		frame.setTitle("Flow Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		try {
			Thread.sleep(5000000);
		} catch (InterruptedException ex) {
		}
	}

	//change method name
	public static void elsespark() {
		directedGraph.addVertex(curline);
		directedGraph.addEdge(prev, curline);
		directedGraph.addEdge(endifdatastmnts.get(endifdatastmnts.size() - 1), curline);
		endifdatastmnts.remove(endifdatastmnts.size() - 1);
		prev = curline;
		trigelse = false;
		trigif = false;
	}

	//change method name
	public static void ifspark() {
		directedGraph.addVertex(curline);
		directedGraph.addEdge(prev, curline);
		directedGraph.addEdge(ifstmnts.get(ifstmnts.size() - 1), curline);
		prev = curline;
		trigif = false;
		endifdatastmnts.remove(endifdatastmnts.size() - 1);
	}

	//change method name
	public static void prsdata() {
		if (curline.contains("if")) {
			if (nextif) {
				directedGraph.addVertex(curline);
				directedGraph.addEdge(prev, curline);
				directedGraph.addEdge(ifstmnts.get(ifstmnts.size() - 1), curline);
				ifstmnts.remove(ifstmnts.size() - 1);
				ifstmnts.add(curline);
				prev = curline;
				nextif = false;	
			} else {
				parse();
				ifstmnts.add(curline);
			}
			
			while (!((curline = sc.nextLine()).trim().equals("}"))) {
				prsdata();
			}
			
			endifdatastmnts.add(prev);
			curline = sc.nextLine();
			if (curline.contains("else")) {
				prsdata();
			} else {
				if (curline.contains("if")) {
					nextif = true;
					prsdata();
				} else {
					trigif = true;
					ifspark();
					ifstmnts.remove(ifstmnts.size() - 1);
				}
			}
		} else if (curline.contains("else")) {
			curline = sc.nextLine();
			directedGraph.addVertex(curline);
			directedGraph.addEdge(ifstmnts.get(ifstmnts.size() - 1), curline);
			ifstmnts.remove(ifstmnts.size() - 1);
			prev = curline;
			while (!((curline = sc.nextLine()).trim().equals("}"))) {
				prsdata();
			}
			trigif = false;
			trigelse = true;
		} else if (curline.contains("while")) {
			parse();
			whstmnts.add(curline);
			while (!((curline = sc.nextLine()).trim().equals("}"))) {
				prsdata();
			}
			directedGraph.addEdge(prev, whstmnts.get(whstmnts.size() - 1));
			prev = whstmnts.get(whstmnts.size() - 1);
			whstmnts.remove(whstmnts.size() - 1);
		} else if (curline.contains("for")) {
			directedGraph.addVertex(curline);
			directedGraph.addEdge(prev, curline);
			frstmnts.add(curline);
			prev = curline;
			while (!((curline = sc.nextLine()).trim().equals("}"))) {
				prsdata();
			}
			directedGraph.addEdge(prev, frstmnts.get(frstmnts.size() - 1));
			prev = frstmnts.get(frstmnts.size() - 1);
			frstmnts.remove(frstmnts.size() - 1);
		} else if(curline.contains("do")) {
			parse();
			dostmnts.add(curline);
			directedGraph.addVertex(curline);
			directedGraph.addEdge(prev, curline);
			while (!((curline = sc.nextLine()).trim().contains("}"))) {
				prsdata();
			}
			curline = curline.replace("}", "");
			directedGraph.addVertex(curline);
			directedGraph.addEdge(prev, curline);
			directedGraph.addEdge(curline, dostmnts.get(dostmnts.size() - 1));
			prev = curline;			
		} else {
			parse();
		}
	}
	
	//change method name
	public static void parse() {
		if (trigif) {
			ifspark();
		} else if (trigelse) {
			elsespark();
		} else {
			directedGraph.addVertex(curline);
			directedGraph.addEdge(prev, curline);
			prev = curline;
		}
	}
	//end of main
}
