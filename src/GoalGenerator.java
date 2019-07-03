import java.util.ArrayList;


public class GoalGenerator {
	private ArrayList<Point> goalList;
	
	// Copy of the global state of Agent
	private State s;
	
	// List of all items left on the board after exploring
	private ArrayList<Point> requiredItemsAvailable;
	
	public GoalGenerator(State s) {
		this.s = s;
		goalList = new ArrayList<Point>();
		requiredItemsAvailable = new ArrayList<Point>();
		
		// 1. Find all the remaining items on the map after we explore
		// 2. For each item found,
		//	  Check if we NEED it:
		//		- if we do, add it to our list of requiredItemsAvailable
		//		- if we don't, IDNORE that point
		ArrayList<Point> temp = s.map.findItemsOnMap();
		for (Point p : temp) {
			if (p.symbol == Enums.Symbol.AXE && s.axe == false) {
				requiredItemsAvailable.add(p);
			} else if (p.symbol == Enums.Symbol.KEY && s.key == false) {
				requiredItemsAvailable.add(p);
			} else if (p.symbol == Enums.Symbol.BOMB) {
				requiredItemsAvailable.add(p);
			}
		}
	}
	
	public ArrayList<Point> getGoalList() {
		return this.goalList;
	}
	
	public void run() {
		
		// If we can see the gold, our first goal is to get there
		ArrayList<Point> goldPoints = s.map.findItem(Enums.Symbol.GOLD);
		if (goldPoints.size() > 0) {
			
			goalList = pathToLocation(goldPoints.get(0));
			
		}
		
		// TODO What if we can't see the gold?
		else {
			
		}
		
	}
	
	// Find the lowest cost path to reach the goal and record all the items (and their points) 
	// required to get there
	// REPEAT RECURSIVELY until no items are required to reach goal
	private ArrayList<Point> pathToLocation(Point goal) {
		
		Path pathFinder = new Path(s, goal);
		
		// 
		ArrayList<ArrayList<Enums.Symbol>> itemsRequiredTESTED = new ArrayList<ArrayList<Enums.Symbol>>();
		
		//
		ArrayList<Point> itemsRequiredPoints = new ArrayList<Point>();
		
		// Find what items are needed to reach the goal for the lowest cost path
		// (SYMBOLS)
		ArrayList<Enums.Symbol> itemsRequired = pathFinder.itemsToPointRequired(requiredItemsAvailable);
		
		// Try all possible items combination to reach the goal
		while (itemsRequired != null) {
			
			// Check for termination case: no items required, i.e. go straight to goal
			if (itemsRequired.size() == 0) {
				return itemsRequiredPoints;
			} 
			
			// Find ALL THE POINTS on the map for each item required
			ArrayList<Point> points = new ArrayList<Point>();
		
			for (Enums.Symbol s : itemsRequired) {
				
				for (Point p : requiredItemsAvailable) {
					if (p.symbol == s) {
						points.add(p);
					}
				}
			}
	
			// ONE BY ONE:
			// Take the first point of the required item,
			// Added it to our ArrayList of points (itemsRequiredPoints)
			// Try to find the path to THAT item now, RECURSION
			// If not, take the NEXT point of the required item and repeat
			for (Point p : points) {
				
				// RECURSIVE STEP
				ArrayList<Point> temp;
				temp = pathToLocation(p);
				
				// ONLY add point p if we could path to it!
				if (temp == null) {
					// Do nothing
				} else {
					
					// add point p to the required points to visit because we are sure we can get there
					itemsRequiredPoints.add(p);
					
					// and add all the other points required to get to p
					itemsRequiredPoints.addAll(temp);
				}
			}
			
			// Check that we have found AT LEAST ONE 
			// VALID path of each item required to get to the initial goal
			boolean isValid = false;
			
			for (Enums.Symbol s : itemsRequired) {
				isValid = false;
				
				for (Point p : itemsRequiredPoints) {
					if (p.symbol == s) {
						isValid = true;
					}
				}
				
				// If we did NOT find a valid path to one of the required items 
				// that was needed to get to the initial goal
				if (isValid == false) {
					
					// Add the tested list to the global list of tested lists
					itemsRequiredTESTED.add(itemsRequired);
					
					// Test the next possible combination of items to the goal
					itemsRequired = pathFinder.itemsToPointRequired(requiredItemsAvailable);
					
					boolean alreadyTested = true;
					while (alreadyTested == true) {
						
						// Check if we are not trying to path to an item any items we have 
						// already tried to get but were unable to
						for (ArrayList<Enums.Symbol> testedList : itemsRequiredTESTED) {
							
							// Assume we haven't tested the current combination of items
							alreadyTested = false;
							
							// If we have already tried the specific combination of items
							// Find another list of required items (ask A* to find a different path
							// to the goal)
							if (itemsRequired.containsAll(testedList)) {
								itemsRequired = pathFinder.itemsToPointRequired(requiredItemsAvailable);
								
								break;
							}
						}
						
					}
					
					break;
				}
			}
			
			// Exit the while loop if all required items can be reached
			if (isValid == true) {
				return itemsRequiredPoints;
			}
		}
		
		return null;
	}

}
