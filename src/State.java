/*********************************************
/*  State.java 
/*  
/*  COMP3411 Artificial Intelligence
/*  UNSW Session 1, 2013
*/

import java.util.*;

public class State implements Comparable<State>{

    // Class variables
    public int bombs;
    
    public boolean key;
    public boolean axe;
    public boolean gold;
    
    public Enums.Direction direction;
    
    public Coordinate c;
    
    public int pastCost;
    public int futureCost;
    
    public int moveCount;
    
    public ArrayList<Character> movesMade;
    
    public Map map;
    
    // Class Constants
    public static final int COST_ITEM = 1;
    public static final int COST_MOVE = 1;
    public static final int COST_TURN = 1;
    public static final int COST_BOMB = 2;
    
    // Creator method
    public State() {

    	this.moveCount = 0;
        this.bombs = 0;
        this.key = false;
        this.axe = false;
        this.gold = false;
        this.pastCost = 0;
        this.direction = Enums.Direction.NORTH;        
        this.c = new Coordinate(Map.MAP_WIDTH/2, Map.MAP_HEIGHT/2);
        this.movesMade = new ArrayList<Character>();
        this.map = new Map();
        
    }
    
    public State (State s) {
    	this.moveCount = s.moveCount;
    	this.axe = s.axe;
    	this.bombs = s.bombs;
    	this.direction = s.direction;
    	this.gold = s.gold;
    	this.key = s.key;
    	this.pastCost = s.pastCost;
    	this.c = new Coordinate(s.c.x, s.c.y);
    	this.movesMade = new ArrayList<Character>(s.movesMade);
    	this.map = new Map(s.map);
    }
    
    
    // Print method used for debugging
    public void printState() {
    	System.out.println("| bombs = "+ bombs +" | key = "+ key +" | axe = "+ axe +" | direction = "+ direction +" | posX = "+ c.x +" | posY = "+ c.y +" |");
    	System.out.print("\n Moves Made:");
    	for(char c : this.movesMade) {
    		System.out.print(" " + c);
    	}
    	System.out.println("\nMoveCount = " + moveCount);
    }
 
 	// Move the agent forward (relative to their direction)
    // If removable object in front, then remove it before moving forward
    // ASSUMING it is legal to move forward
 	public char moveForward() {
 		
 		Coordinate coordInFront = coordinateInFront();
 		Enums.Symbol symbolInFront = this.map.getSymbolAtCoord(coordInFront);
 		
 		char moveMade = '?';
 		
 		if (symbolInFront == Enums.Symbol.TREE && this.axe == true) {
 			movesMade.add('c');
 	 		moveCount++;
 	 		moveMade = 'c';
 	 		pastCost += COST_ITEM;
 	 		
 		} else if (symbolInFront == Enums.Symbol.DOOR && this.key == true) {
 			movesMade.add('o');
 	 		moveCount++;
 	 		moveMade = 'o';
 	 		pastCost += COST_ITEM;
 	 		
 		} else if (this.bombs > 0 && (symbolInFront == Enums.Symbol.DOOR ||
 									  symbolInFront == Enums.Symbol.TREE ||
 									  symbolInFront == Enums.Symbol.WALL)){
 			movesMade.add('b');
 			moveCount++;
 			this.bombs--;
 			moveMade = 'b';
 			pastCost += COST_BOMB;
 			
 		} else {
 			movesMade.add('f');
 	 		moveCount++;
 	 		this.c = coordInFront;
 	 		moveMade = 'f';
 	 		pastCost += COST_MOVE;
 		}

 		// Pick up items
 		if (symbolInFront == Enums.Symbol.AXE) {
 			this.axe = true;
 		} else if (symbolInFront == Enums.Symbol.BOMB) {
 			this.bombs++;
 		} else if (symbolInFront == Enums.Symbol.KEY) {
 			this.key = true;
 		} else if (symbolInFront == Enums.Symbol.GOLD) {
 			this.gold = true;
 		}

 		return moveMade;
 	}
 	
 	// Update the direction of the agent when it turns left
 	public void turnLeft() {
 		
 		movesMade.add('l');
 		moveCount++;
 		this.pastCost += COST_TURN;
 		
 		switch (direction) {
 			case NORTH:
 				direction = Enums.Direction.WEST;
 				break;
 				
 			case SOUTH:
 				direction = Enums.Direction.EAST;
 				break;
 				
 			case EAST:
 				direction = Enums.Direction.NORTH;
 				break;
 				
 			case WEST:
 				direction = Enums.Direction.SOUTH;
 				break;
 		}
 	}
 
 	// Update the direction of the agent when it turns right
 	public void turnRight() {
 		
 		movesMade.add('r');
 		moveCount++;
 		this.pastCost += COST_TURN;
 		
 		switch (direction) {
 			case NORTH:
 				direction = Enums.Direction.EAST;
 				break;
 				
 			case SOUTH:
 				direction = Enums.Direction.WEST;
 				break;
 				
 			case EAST:
 				direction = Enums.Direction.SOUTH;
 				break;
 				
 			case WEST:
 				direction = Enums.Direction.NORTH;
 				break;
 		}
 	}
 	
 	// Returns list of the possible children of the current state
 	public ArrayList<State> getChildren(Point goal) {
 		ArrayList<State> children = new ArrayList<State>();
 		Enums.Symbol nextPoint;
 		
 		// Left child
 		nextPoint = this.map.getSymbolAtCoord(coordinateOnLeft());
 		if (validChild(nextPoint)) {
 			State s = new State(this);
 			s.turnLeft();
 			s.moveForward();
 			if (s.lastMove() != 'f') {
 				s.moveForward();
 			}
 			s.calculateFutureCost(goal);
 			children.add(s);
 			
 		}
 		
 		// Right child
 		nextPoint = this.map.getSymbolAtCoord(coordinateOnRight());
 		if (validChild(nextPoint)) {
 			State s = new State(this);
 			s.turnRight();
 			s.moveForward();
 			
 			
 			if (s.lastMove() != 'f') {
 				s.moveForward();
 			}
 			s.calculateFutureCost(goal);
 			children.add(s);
 			
 		}
 		
 		// Forward child
 		nextPoint = this.map.getSymbolAtCoord(coordinateInFront());
 		if (validChild(nextPoint)) {
 			State s = new State(this);
 			s.moveForward();
 			if (s.lastMove() != 'f') {
 				s.moveForward();
 			}
 			s.calculateFutureCost(goal);
 			children.add(s);
 			
 		}
 		
 		// Back right
 		nextPoint = this.map.getSymbolAtCoord(coordinateBehind());
 		if (validChild(nextPoint)) {
 			State s = new State(this);
 			s.turnLeft();
 			s.turnLeft();
 			s.moveForward();
 			s.calculateFutureCost(goal);
 			children.add(s);
 			
 		}
 		
 		return children;
 	}
 	
 	// Returns the coordinate in front of the current location
 	public Coordinate coordinateInFront() {
 		Coordinate retval = new Coordinate(this.c.x, this.c.y);
 		
 		if (this.direction == Enums.Direction.NORTH) {
    		retval.y--;
    	} else if (this.direction == Enums.Direction.SOUTH) {
    		retval.y++;
    	} else if (this.direction == Enums.Direction.EAST) {
    		retval.x++;
    	} else if (this.direction == Enums.Direction.WEST) {
    		retval.x--;
    	}
 		
 		return retval;
 	}
 	
 // Returns the coordinate behind of the current location
 	public Coordinate coordinateBehind() {
 		Coordinate retval = new Coordinate(this.c.x, this.c.y);
 		
 		if (this.direction == Enums.Direction.NORTH) {
    		retval.y++;
    	} else if (this.direction == Enums.Direction.SOUTH) {
    		retval.y--;
    	} else if (this.direction == Enums.Direction.EAST) {
    		retval.x--;
    	} else if (this.direction == Enums.Direction.WEST) {
    		retval.x++;
    	}
 		
 		return retval;
 	}
 	
 // Returns the coordinate to the left of the current location
 	public Coordinate coordinateOnLeft() {
 		Coordinate retval = new Coordinate(this.c.x, this.c.y);
 		
 		if (this.direction == Enums.Direction.NORTH) {
    		retval.x--;
    	} else if (this.direction == Enums.Direction.SOUTH) {
    		retval.x++;
    	} else if (this.direction == Enums.Direction.EAST) {
    		retval.y--;
    	} else if (this.direction == Enums.Direction.WEST) {
    		retval.y++;
    	}
 		
 		return retval;
 	} 
 	
 // Returns the coordinate to the right of the current location
 	public Coordinate coordinateOnRight() {
 		Coordinate retval = new Coordinate(this.c.x, this.c.y);
 		
 		if (this.direction == Enums.Direction.NORTH) {
    		retval.x++;
    	} else if (this.direction == Enums.Direction.SOUTH) {
    		retval.x--;
    	} else if (this.direction == Enums.Direction.EAST) {
    		retval.y++;
    	} else if (this.direction == Enums.Direction.WEST) {
    		retval.y--;
    	}
 		
 		return retval;
 	} 
 	
 	// Used for exploration, will not use consumables (bombs)
 	// Tests whether a move is valid in the current state
 	public boolean validMove(Enums.Symbol inFront) {
 		boolean retval = false;
 		
 		if (inFront == Enums.Symbol.EMPTY) {
 			retval = true;
 		}
 		
 		else if (inFront == Enums.Symbol.DOOR && this.key == true) {
 			retval = true;	
 		}
 		
 		else if (inFront == Enums.Symbol.TREE && this.axe == true) {
 			retval = true;	
 		}
 		
 		else if (inFront == Enums.Symbol.AXE || 
 				inFront == Enums.Symbol.KEY || 
 				inFront == Enums.Symbol.BOMB ||
 				inFront == Enums.Symbol.GOLD) {
 			retval = true;
 		}
 		
 		return retval;
 	}
 	
 	// Used for child generation
 	// Tests whether a move is valid in the current state
 	public boolean validChild(Enums.Symbol s) {
 		boolean retval = false;
 		
 		if (s == Enums.Symbol.EMPTY) {
 			retval = true;
 		} 
 		
 		else if (s == Enums.Symbol.DOOR && this.key == true) {
 			retval = true;	
 		} 
 		
 		else if (s == Enums.Symbol.TREE && this.axe == true) {
 			retval = true;	
 		}
 
 		else if (s == Enums.Symbol.AXE || 
 				s == Enums.Symbol.KEY || 
 				s == Enums.Symbol.BOMB ||
 				s == Enums.Symbol.GOLD) {
 			retval = true;
 		} else if (this.bombs > 0 && s != Enums.Symbol.WATER) {
 			retval = true;
 		}
 		
 		return retval;
 	}
 	
 	// Returns the last move made
 	public char lastMove() {
 		return this.movesMade.get(this.movesMade.size()-1);
 	}
 	
 	@Override
 	public int compareTo(State s) {
 		return this.getCost() - s.getCost();
 	}
	
	public boolean isEqual(State compareTo) {
		boolean retval = true;
		if (this.c.x != compareTo.c.x || 
				this.c.y != compareTo.c.y ) {
			retval = false;
		} else if (this.direction != compareTo.direction) {
			retval = false;
		}
		return retval;
	}
	
	// Use Manhattan to predict future cost
	public void calculateFutureCost(Point goal) {
		int cost = Math.abs(goal.x - this.c.x) + Math.abs(goal.y - this.c.y);
		this.futureCost = cost;
	}
	
	public int getCost() {
		return this.pastCost + this.futureCost;
	}
	
	// Given a move, apply the necessary changes to the state
	public void makeMove(char c) {
		if (c == 'f') {
			this.moveForward();
			
		} else if (c == 'o' || c == 'c' || c == 'b') {

			// Clear the item in front
			this.moveForward();

		} else if (c == 'l') {
			this.turnLeft();
			
		} else if (c == 'r') {
			this.turnRight();
			
		}
	}
}
