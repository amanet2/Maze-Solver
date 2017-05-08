import java.awt.event.WindowEvent;
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

public class MazeSolver
{
	private JFrame frame;	//make the frame

	public class puzzleNode
	{
		private final int xCoord;
		private final int yCoord;

		private puzzleNode nextNode;

		public puzzleNode(int ecks, int why)
		{
			xCoord = ecks;
			yCoord = why;

			nextNode = null;
		}
	}

	public class puzzleStack
	{
		private puzzleNode head;

		public puzzleStack()
		{
			head = null;
		}

		public void push(puzzleNode toPush)
		{	
			if(head == null)
				head = toPush;
			else
			{
				puzzleNode n = head;

				while(n.nextNode != null)
				{
					n = n.nextNode;
				}
				
				n.nextNode = toPush;
			}
		}

		public void pop()
		{
			if(head != null)
			{
				puzzleNode i = head;
	
				if(i.nextNode != null)
				{
					while(i.nextNode != null)
					{
						puzzleNode nex = i.nextNode;

						if(nex.nextNode != null)
						{
							i = i.nextNode;
						}
						else
							i.nextNode = null;
					}
		
				}
				else //head is only elemnt
				{
					head = null;
				}
			}
			else
			{
				System.out.println("\nTRIED TO POP EMPTY STACK\n");
			}
		}

		public puzzleNode getTop()
		{
			if(head != null)
			{
				puzzleNode n = head;

				while(n.nextNode != null)
				{
					n = n.nextNode;
				}
				return n;
			}
			else
				return null;
		}
	}
	
	public class puzzle
	{
		private int xsize;
  	        private int ysize;
  		private int xstart;
		private int ystart;
		private int xend;
		private int yend;

		private char[][] config;
		
		public puzzle()
		{
			xsize = 0;
	  	        ysize = 0;
	  		xstart = 0;
			ystart = 0;
			xend = 0;
			yend = 0;
		}
	
		public void printPuzzle()
		{
			for(int i=0;i<xsize;i++)
			{
				for(int j=0;j<ysize;j++)
				{
					if(i == xstart-1 && j == ystart-1)
						System.out.print("s");
					else
						System.out.print(config[i][j]);	
				}
				System.out.println("");
			}
		}

		public int test(int x, int y)
		{
			//return 0 for good
			//-1 for bad

			//test right
			//printf("\nTEST %i %i\n",x,y);
			if(x >= 0 && x < xsize && y >= 0 && y < ysize)
			{
				if(config[x][y] != 'v' && config[x][y] != 'o')
					return 0;
			}

			return -1;
		}
	}

	private void initPuzzleFromFile(puzzle p, String fileName){ //read file line by line, takes string as parameter
		String line; //initialize string to represent line in file
        	try { //try
           		 // FileReader reads text files in the default encoding.
            		FileReader fileReader = 
                		new FileReader(fileName); //fileReader instance

			    // Always wrap FileReader in BufferedReader.
			    BufferedReader bufferedReader = 
				new BufferedReader(fileReader);
            
            		ArrayList<String> list = new ArrayList<>();
            
		    while((line = bufferedReader.readLine()) != null) { //until we reach empty space in file
		    	list.add(line);
		    	}

			int counter = 0;	//0 for sizes, 1 for starts, 2 for ends
			int xpos;
			int ypos;
			for(int x = 0; x < list.size(); x++)
			{
				String[] toks = list.get(x).split(" ");
				
				if(toks.length == 2)	//always should be two
				{	
					if(counter == 0)
					{
						if(Integer.valueOf(toks[0]) <= 0 || Integer.valueOf(toks[1]) <= 0)
						{
							//incorrect size dimensions
							System.err.println("\nINCORRECT DIMENSIONS SUPPLIED near line " + x +"\n");
							//System.exit(1);
						}
						else
						{
							//first number read are the sizes
							p.xsize = Integer.valueOf(toks[0]);
							p.ysize = Integer.valueOf(toks[1]);
						
							//now init the config to be all -
							p.config = new char[p.xsize][p.ysize];

							for(int i = 0; i < p.xsize; i++)
							{
								for(int j = 0; j < p.ysize; j++)
								{
									p.config[i][j] = '-';
								}
							}

							counter++;

						}
					}
					else if(counter == 1)
					{

						if(p.xsize < Integer.valueOf(toks[0]) || p.ysize < Integer.valueOf(toks[1]) || Integer.valueOf(toks[0]) <= 0 || Integer.valueOf(toks[1]) <= 0)
						{
							//start piece is set outside valid range
							System.err.println("\nSTART PIECE ATTEMPT TO SET OUTSIDE VALID RANGE ON LINE " + x + "\n");
							//System.exit(1);
						}
						else
						{
							//second are starts
							p.xstart = Integer.valueOf(toks[0]);
							p.ystart = Integer.valueOf(toks[1]);

							counter++;
						}
					}
					else if(counter == 2)
					{
						if(p.xsize < Integer.valueOf(toks[0]) || p.ysize < Integer.valueOf(toks[1]) || Integer.valueOf(toks[0]) <= 0 || Integer.valueOf(toks[1]) <= 0)
						{
							//start piece is set outside valid range
							System.err.println("\nEND PIECE ATTEMPT TO SET OUTSIDE VALID RANGE\n");
							//System.exit(1);
						}
						else
						{
							//third set are for the ends
							p.xend = Integer.valueOf(toks[0]);
							p.yend = Integer.valueOf(toks[1]);

							p.config[p.xend-1][p.yend-1]='e';
			
							counter++;
						}
					}
					else
					{
						//obstacles
						xpos = Integer.valueOf(toks[0]);
						ypos = Integer.valueOf(toks[1]);
						//System.out.println(xpos + "," + ypos);

						if(xpos > p.xsize || ypos > p.ysize || xpos <= 0 || ypos <= 0 || (xpos == p.xend && ypos == p.yend) || (xpos == p.xstart && ypos == p.ystart))
						{
							//position is outside acceptable range
							System.err.println("\nOBSTACLE SET OUTSIDE VALID RANGE IN FILE ON LINE " + x + "\n");
							//System.exit(1);
						}
						else
							p.config[xpos-1][ypos-1] = 'o';
					}
				}
				else
				{
					if(toks.length > 0)
					{
						if(!(toks[0].equalsIgnoreCase("")))
						{
							System.err.println("\nSomething wrong with the input file near line " + x + "\nThe most likely reason is that you have multiple spaces in between two of your numbers, no spaces, leading, or trailing spaces.\nPlease reformat your input file like so: Each line of the form: '# #' i.e. one number followed by a space followed by a number\n");
							//System.exit(1);
						}
					}
				}
			}
	
		}
		catch(FileNotFoundException ex) { //catch exception for file not there
		    System.err.println(
		        "Unable to open file '" + 
		        fileName + "' [FILE NOT FOUND]");
			System.exit(1);				
		}
		catch(IOException ex) { //catch exception for file corrupted
		    System.err.println(
		        "Error reading file '" 
		        + fileName + "'");
			System.exit(1);					
		    // Or we could just do this: 
		    // ex.printStackTrace();
		}
	}	

	private void startThings(String path)
	{
		puzzle p = new puzzle();

		initPuzzleFromFile(p, path);
		
		if(p.xend != 0 && p.yend != 0)
		{

			System.out.println("xsize,ysize: " + p.xsize + "," + p.ysize + "\nxstart,ystart: " + p.xstart + "," + p.ystart + "\nxend,yend: " + p.xend + "," + p.yend);

			System.out.println("START STATE\n========");
			p.printPuzzle();	
			System.out.println("\n\n");
			try{
			dfsSolve(p);
			}
			catch(Exception e)
			{
				System.err.println("\nCaught Exception...\n");
			}
		}
		else
		{
			System.err.println("\nNot enough valid information to make a puzzle in " + path + "\n");
			System.exit(1);
		}
	}

	private void dfsSolve(puzzle p) throws Exception

	{
		puzzleStack pS = new puzzleStack();	//create stack

		pS.push(new puzzleNode(p.xstart-1,p.ystart-1));

		int endFound = 0;

		JButton[][] buttons = new JButton[p.xsize][p.ysize];	//buttons for gui

		frame = new JFrame("Solver Program");

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setLayout(new GridLayout(p.xsize,p.ysize));	//grid layout

		for(int i = 0; i < p.xsize; i++)	//add all buttons
		{
			for(int j = 0; j < p.ysize; j++)
			{
				buttons[i][j] = new JButton("");	//properties
				buttons[i][j].setEnabled(false);
               	buttons[i][j].setOpaque(true);
                
				if(p.config[i][j] == 'v') buttons[i][j].setBackground(Color.GRAY);	//no GRAY yet
				else if(p.config[i][j] == 'o') buttons[i][j].setBackground(Color.RED);	//We have Red

				frame.getContentPane().add(buttons[i][j]);	//add all buttons, regardless
			}
		}

		buttons[p.xstart-1][p.ystart-1].setBackground(Color.YELLOW);
		buttons[p.xend-1][p.yend-1].setBackground(Color.YELLOW);
		
		
		frame.setSize(300,300);
		//frame.pack();
		frame.setVisible(true);

		while(pS.head != null && endFound == 0)
		{
			Thread.sleep(100);
			puzzleNode n = pS.getTop();

			//check for win by looking at startx/y and endx/y
			if(n.xCoord == p.xend-1 && n.yCoord == p.yend-1)
			{
				p.printPuzzle();
				System.out.println("\nSOLUTION FOUND!!!\n====\n");
				puzzleNode enn = pS.head;
				while(enn != null)
				{
					System.out.println("Coord["+enn.xCoord+","+enn.yCoord+"]");
					enn = enn.nextNode;
				}
				
				//displayWinWindow(p,pS,1);
				endFound = 1;
			}
			else
			{
				//printf("\ncoords:[%i][%i]\n",n->coords[0],n->coords[1]);
			
				//look at coords on top of stack
				//if the coords (s) can be changed to
				//either a R,D,L,U new config, change
				//the current puzzle to reflect that
				//and push the new coords onto the stack
				//the test fails if the move we would make 
				//are a 'v' on the current puzzle
				//or a 'o' on the current puzzle
				//or are out of bounds.  the checks
				//will be the same as the ones found
				//for generating new puzzle configs
				//if no possibilities exist, pop the stack

				int madeMove = 0;
				//test right
				if(p.test(n.xCoord, n.yCoord + 1) == 0)
				{
					//was good
					//if(debug == true)
						//System.out.println("\nCAN MOVE RIGHT\n");
				
					p.config[n.xCoord][n.yCoord] = 'v';
					p.config[n.xCoord][n.yCoord+1] = 'v';

					buttons[n.xCoord][n.yCoord+1].setBackground(Color.GREEN);
				
					pS.push(new puzzleNode(n.xCoord,n.yCoord+1));
					madeMove = 1;
				}
				//test down
				if(p.test(n.xCoord+1,n.yCoord) == 0 && madeMove == 0)
				{
					//was good
					//if(debug == true)
						//System.out.println("\nCAN MOVE DOWN\n");
				
					p.config[n.xCoord][n.yCoord] = 'v';
					p.config[n.xCoord+1][n.yCoord] = 'v';

					buttons[n.xCoord+1][n.yCoord].setBackground(Color.GREEN);
				
					pS.push(new puzzleNode(n.xCoord+1,n.yCoord));
					madeMove = 1;
				}
				//test up
				if(p.test(n.xCoord-1,n.yCoord) == 0 && madeMove == 0)
				{
					//was good
					//if(debug == true)
						//System.out.println("\nCAN MOVE UP\n");
				
					p.config[n.xCoord][n.yCoord] = 'v';
					p.config[n.xCoord-1][n.yCoord] = 'v';

					buttons[n.xCoord-1][n.yCoord].setBackground(Color.GREEN);
				
					pS.push(new puzzleNode(n.xCoord-1,n.yCoord));
					madeMove = 1;
				}
				//test left
				if(p.test(n.xCoord,n.yCoord-1) == 0 && madeMove == 0)
				{
					//was good
					//if(debug == true)
						//System.out.println("\nCAN MOVE LEFT\n");
				
					p.config[n.xCoord][n.yCoord] = 'v';
					p.config[n.xCoord][n.yCoord-1] = 'v';

					buttons[n.xCoord][n.yCoord-1].setBackground(Color.GREEN);
				
					pS.push(new puzzleNode(n.xCoord,n.yCoord-1));
					madeMove = 1;
				}
			
				//printPuzzle(currentConfig);

				if(madeMove == 0)
				{
					if(pS.getTop() != null)
					{
						int xpos = pS.getTop().xCoord;
						int ypos = pS.getTop().yCoord;

						buttons[xpos][ypos].setBackground(Color.GRAY);
					}

					pS.pop();
				}


				buttons[p.xstart-1][p.ystart-1].setBackground(Color.YELLOW);
				buttons[p.xend-1][p.yend-1].setBackground(Color.YELLOW);
			}
		}

		if(endFound == 0)
		{
			p.printPuzzle();
			//displayWinWindow(p,pS,0);
			System.out.println("\nNO SOLUTION FOUND!!!\n");
		}

	}

	public static void main(String[] args) {
		MazeSolver solver = new MazeSolver();

		if (args.length == 1){
			solver.startThings(args[0]);
		solver.frame.dispatchEvent(new WindowEvent(solver.frame, WindowEvent.WINDOW_CLOSING));
		}else
		    System.err.println("INCORRECT NUMBER OF ARGUMENTS! USAGE: MazeSolver <mazefile>.txt");
        }
}
