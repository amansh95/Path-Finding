/*********************************************
/*  Agent.java 
/*  Sample Agent for Text-Based Adventure Game
/*  COMP3411 Artificial Intelligence
/*  UNSW Session 1, 2013
*/

/*
 * Question:
 * Briefly describe how your program works, including any algorithms and data structures employed, 
 * and explain any design decisions you made along the way.
 * 
 * The way our Agent works is as follows:
 * 
 * ==> PHASE 1: Explore
 * Our Agent uses the Pledge algorithm to explore as much as it can of the map, storing all the information 
 * it learns in a globalState. We chose to use the Pledge algorithm because it is a smarter wall following
 * algorithm that caters for 'islands' and prevents our agent getting stuck. It does this by storing the 
 * initial state and keep track of the number of turns (both left and right) made. The algorithm will stop
 * wall following when both the current direction is the same as the initial direction and the total sum of
 * left and right turns are zero.
 * Additionally, our Agent may stop exploring if an item appears in its view. At this point our Agent uses our
 * A* algorithm to find the shortest path possible. However, unlike later phases of our code, we restrict the
 * number of moves that our A* algorithm can search before 'giving up' on that item. This is because we expect
 * there to be limited information about how we can reach that item whilst still being in the explore phases. 
 * We decided to implement this tweak to our explore phases because it dramatically reduced the run time for 
 * some maps when our Pledge algorithm walked right by an item it could have very easily picked up.
 * 
 * ==> PHASE 2: Set Goal
 * Unfortunately our set goal phase that our Agent currently implements is not what we initially intended. 
 * We originally planned for our set goal phase to return an array list of points which were ordered according 
 * to which items we required in sequence to retrieve the gold successfully. It was planned o work as follows:
 * 		1. Keep track of all available items remaining on the map after exploration.
 * 		2. Set our first goal to be the gold (choose another known item if unavailable).
 * 		3. Using our A* algorithm (see phase 3), we compute the lowest cost distance required to 
 * 		   reach that goal and record which items were required to get there. The axe and key items
 * 		   were weighted less than the bombs as they are consumables.
 * 		4. Using recursion we then loop through all the items required to reach the original goal (the gold)
 * 		   and find the lowest cost to reach those. If there are more than one instance of the required item
 * 		   our Agent keeps trying to find the lowest cost path to one until it is found. If there are no paths
 * 		   available for that specific instance, our Agent will try the next instance. When a successful path
 * 		   is found for the intermediate goal, it is saved to be return later to the Agent.
 * 		5. The recursion base case is when the lowest cost path to the goal is free. This means that the Agent 
 * 		   can now directly go to the intermediate goal it has set and has successfully recorded a list of 
 * 		   sequential points of items that are required to reach the original goal.
 * 
 * ==> PHASE 3: Path
 * The third stage of our Agent is using A* algorithm to path to a specified goal location. After phase 2,
 * our Agent was [intended to be] supplied by an array list of goal points that are required to be reached in
 * sequence in order to successfully obtain the gold. Unfortunately, the current implementation of our Agent 
 * simply sets the gold to be the target position to path to (unless it already has it), otherwise return to the 
 * starting position. 
 * Regardless, our A* algorithm works and uses a very simple priority queue to keep track of generated children 
 * states to be expanded. In our state class we have a method called getChildren which takes the current state and
 * attempts to move left, right, forward and backwards, checking if each one is valid. For each valid move, we copied 
 * the current state and manipulated appropriately (updating the position, direction and any item pickups) then
 * finally add the child to a priority queue. We utilized the native priority implementation in Java and overrode 
 * the comparator method to order states based on the sum of their pastCost (number of turns and items used to get there) 
 * and futureCost (the Manhattan distance left to the goal) from lowest to highest.
 * 
 * ==> PHASE 4: Return
 * This phase is nothing more than setting the currentGoal to the original starting position and calling our A*
 * algorithm to find the lowest cost path to get there.
 * 
 * _______________
 * --- SUMMARY ---
 * 
 * Unfortunately, we were unable to implement the algorithm we had originally intended to generate the intermediary 
 * goals required to be reached in order to successfully retrieve the goal. Our attempt at this implementation is in
 * our GoalGenerator class. 
 * Our Agent does however manage to solve a variety of puzzles that are not too complicate which means that our A*
 * algorithm does in fact work. The only problem our Agent has is intelligently deciding which goals it is required 
 * to navigate to. 
 * 
 */


import java.util.*;
import java.io.*;
import java.net.*;

public class Agent {
	
	// Class constants
	public static final int STAGE_1_EXPLORE = 0;
	public static final int STAGE_2_SETGOAL = 1;	
	public static final int STAGE_3_PATH 	= 2;
	public static final int STAGE_4_RETURN 	= 3;
	

	public static final int ITEM_MOVE_LIMIT = 10;

	// Class instance variables
	State globalState;
	Explore explorer;
	GoalGenerator goalGenerator;
	
	ArrayList<Character> pathToExecute;
	
	int stage;
	
	ArrayList<Point> itemsUnreachable;
	
	ArrayList<Point> nearbyItems;
	Point currentGoal;
	boolean gettingNeabyItem;
	boolean continueExploring;

	public Agent() {
		
		itemsUnreachable = new ArrayList<Point>();
		continueExploring = false;
	
		// Initialize class instances of Map and State variables
		globalState = new State();
		explorer = new Explore(globalState);
		
		pathToExecute = new ArrayList<Character>();
		gettingNeabyItem = false;
		// First stage of AI will be exploring the surrounding environment 
		stage = STAGE_1_EXPLORE;
	}


	public char get_action(char view[][]) {
		
		// Wait for testing purposes
		try {
			Thread.sleep(25);
		} catch (Exception e) {
			
		}
		
		// Update the map in the globalState with the current view
        this.globalState.map.updateMap(view, globalState);
        //globalState.map.printMap(globalState.c,globalState.direction);
		int phase = stage+1;
        System.out.println("\nPHASE :"+phase+" location ="+globalState.c.x+" ,"+globalState.c.y);

		// 1. Explorer everywhere we can FIRST!
		if (stage == STAGE_1_EXPLORE) {
			
			// If there is a pickup nearby DO IT!
			nearbyItems = this.globalState.map.findNearItems(this.globalState.c);
			
			// Check whether any of the items in the view are possibly reachable
			int i;
			for (i = 0; i < nearbyItems.size(); i++) { 
				Point item = nearbyItems.get(i);
				if (listContains(itemsUnreachable, item) == true) {
					i++;
				} else {
					break;
				}
			}
			
			// If there are items closeby that are possibly reachable try to get them
			if (nearbyItems.size() > 0 && i < nearbyItems.size()) {
				
				currentGoal = nearbyItems.get(i);
				// If the item is unreachable continue exploring
				if (listContains(itemsUnreachable, currentGoal) == false) {
						gettingNeabyItem = true;
						stage = STAGE_3_PATH;
					System.out.println("SWITCHED TO PHASE 3");
						explorer.followingWall = false;
				} else {
					char moveToMake = explorer.run();
					return moveToMake;
				}
				
			} else {
				// set a boolean used for exploration cases
				if (continueExploring == true) {
					continueExploring = false;
				}
				// Explore
				char moveToMake = explorer.run();
				
				// If we have the gold, go straight to STAGE_4_RETURN
		        if (globalState.gold == true) {
		        	stage = STAGE_4_RETURN;
		        } else if (explorer.stillExploring() == false) {
		        	stage = STAGE_2_SETGOAL;

		        }
		        return moveToMake;
			}
			
		} 
		
		// 2. Try to get the gold
		else if (stage == STAGE_2_SETGOAL) {
			if(globalState.gold == false) {
				currentGoal = this.globalState.map.findItem(Enums.Symbol.GOLD).get(0);
				
			} else {
				currentGoal = new Point(80, 80, Enums.Symbol.EMPTY);
			}
			stage++;
			
			//State pathState = new State(globalState);
			//pathState.giveItemsOnMap();
			//pathState.printState();
			
			//goalGenerator = new GoalGenerator(pathState);
			
			/*goalGenerator = new GoalGenerator(new State(globalState));
			
			// Generate an ArrayList of Points that are required to be reach
			// in order to get the gold
			goalGenerator.run();
			
			// TESTING A* WORKS
			ArrayList<Point> goalList = goalGenerator.getGoalList();
			System.out.println("\n\n\nGENERATED GOAL LIST (size = "+goalList.size()+": \n");
			int count = 1;
			for (Point p : goalList) {
				System.out.println("goal "+count+" = " + p.symbol);
				count++;
			}
			
			stage++;*/
		}
		
		// 3. Take the first goal and path to it
		// 	  Repeat until all goals have been made
		else if (stage == STAGE_3_PATH) {
			
			// If there are moves to perform, do them
			if (pathToExecute.size() > 0) {
				
				char moveToMake = pathToExecute.remove(0);
				
				globalState.makeMove(moveToMake);
				
				// If we were picking up an item while exploring, go back to exploring
				if (pathToExecute.size() == 0 && gettingNeabyItem == true) {
					stage = STAGE_1_EXPLORE;
					gettingNeabyItem = false;
					this.explorer.contactPoints.clear();
					this.explorer.addContactPoints = true;
					System.out.println("Switching to 1");
				} else if (pathToExecute.size() == 0){
					stage = STAGE_2_SETGOAL;
					System.out.println("Switching to 2");
				}
				
				return moveToMake;
				
			} else {
				System.out.println("A* to :'"+currentGoal.symbol+"'");
				// Reset the past cost before performing A*
				State copy = new State(globalState);
				copy.pastCost = 0;
				Path pathFinder = new Path(copy, currentGoal);
				// If in the exploration stage still, limit the moves A* is allowed
				// This account for unreachable items
				if (gettingNeabyItem == true) {
					pathFinder.pastCostLimit = ITEM_MOVE_LIMIT;
				}
				
				ArrayList<Character> moves = pathFinder.movesToPoint();
				pathFinder.printVisited();
				
				// If moves is not null the goal is reachable, add the moves to the list of moves to execute
				if (moves != null) {
					System.out.println("Adding path\n");
					pathToExecute.addAll(moves);
				// If not, the item is unreachable, continue exploring
				} else if (moves == null && gettingNeabyItem == true) {
					itemsUnreachable.add(currentGoal);
					stage = STAGE_1_EXPLORE;
					gettingNeabyItem = false;
					continueExploring = true;
					this.explorer.contactPoints.clear();
					this.explorer.addContactPoints = true;
				}
			}
		}
		
		// 4. Return the start [0,0]
		else if (stage == STAGE_4_RETURN) {
			System.out.println("RETURNING\n");
			currentGoal = new Point(80, 80, Enums.Symbol.EMPTY);
			stage--;
		}

        return 0;
   }

   void print_view( char view[][] )
   {
    int i,j;

    System.out.println("\n+ - - - - - +");
        for( i=0; i < 5; i++ ) {
            System.out.print("| ");
            for( j=0; j < 5; j++ ) {
                if(( i == 2 )&&( j == 2 )) {
                    System.out.print("^ ");
                }
                else {
                    System.out.print( view[i][j] + " ");
                }
            }
            System.out.println("|");
        }
        System.out.println("+ - - - - - +");
   }
   
   private boolean listContains(ArrayList<Point> list, Point p) {
	   for (Point z : list) {
		   if (p.x == z.x && p.y == z.y) {
			   return true;
		   }
	   }
	   return false;
   }

   public static void main( String[] args )
   {
      InputStream in  = null;
      OutputStream out= null;
      Socket socket   = null;
      Agent  agent    = new Agent();
      char   view[][] = new char[5][5];
      char   action   = 'F';
      int port;
      int ch;
      int i,j;

      if( args.length < 2 ) {
         System.out.println("Usage: java Agent -p <port>\n");
         System.exit(-1);
      }

      port = Integer.parseInt( args[1] );

      try { // open socket to Game Engine
         socket = new Socket( "localhost", port );
         in  = socket.getInputStream();
         out = socket.getOutputStream();
      }
      catch( IOException e ) {
         System.out.println("Could not bind to port: "+port);
         System.exit(-1);
      }
      
	  try { // scan 5-by-5 window around current location
         while( true ) {
            for( i=0; i < 5; i++ ) {
               for( j=0; j < 5; j++ ) {
                  if( !(( i == 2 )&&( j == 2 ))) {
                     ch = in.read();
                     if( ch == -1 ) {
                        System.exit(-1);
                     }
                     view[i][j] = (char) ch;
                  }
               }
            }
            //agent.print_view(view); 						// COMMENT THIS OUT BEFORE SUBMISSION
            action = agent.get_action(view);
            out.write( action );
         }
      }
      catch( IOException e ) {
         System.out.println("Lost connection to port: "+ port );
         System.exit(-1);
      }
      finally {
         try {
            socket.close();
         }
         catch( IOException e ) {}
      }
   }
   
   
}
