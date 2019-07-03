/*********************************************
/*  Map.java 
/*  
/*  COMP3411 Artificial Intelligence
/*  UNSW Session 1, 2013
*/

import java.util.*;

public class Map implements Iterable<Point>{
    
    // Class constants
    public static final int MAP_WIDTH = 160;
    public static final int MAP_HEIGHT = 160;

    // Class variables
    private Point[][] map;
    
    //public Hashtable<Enums.Symbol, ArrayList<Point>> itemLocations;
    
    // Creator method
    public Map() {
        this.map = new Point[MAP_WIDTH][MAP_HEIGHT];
        
        //itemLocations = new Hashtable<Enums.Symbol, ArrayList<Point>>();

        // Set all coordinated of the map to UNKNOWN at the start
        for (int i=0; i < MAP_WIDTH; i++) {
            for (int j=0; j < MAP_HEIGHT; j++) {
            
            	this.map[i][j] = new Point(i, j, Enums.Symbol.UNKNOWN);
            }
        }
        
    }
    
    public Map(Map m) {
    	 this.map = new Point[MAP_WIDTH][MAP_HEIGHT];
    	 
    	 for (int i=0; i < MAP_WIDTH; i++) {
             for (int j=0; j < MAP_HEIGHT; j++) {
            	Point p = new Point(m.map[i][j].x, m.map[i][j].y, m.map[i][j].symbol);
            	 
             	this.map[i][j] = p;
             }
         }
    }
    
    // Update the explored map given a new view that has been explored
    // A state is passed in to find the direction of the agent
    // We assume the given view is a 5x5 square with the agent in the middle
    public void updateMap(char view[][], State state) {

		// NORTH
    	if (state.direction == Enums.Direction.NORTH) {
    	
    		int x_m = state.c.x - view.length/2;
    		for (int x_v=0; x_v < view.length; x_v++) {

    			int y_m = state.c.y - view[0].length/2;
    			
    			for (int y_v=0; y_v < view[0].length; y_v++) {
    				
    				if (x_m == state.c.x && y_m == state.c.y) {
    					map[x_m][y_m].symbol = Enums.Symbol.EMPTY;
    				} else {	
    					map[x_m][y_m].symbol = Enums.charToEnum(view[y_v][x_v]);
    				}
    				
    				y_m++;
    			}
    			
    			x_m++;
    		}
    	} 
    	
    	// SOUTH
    	else if (state.direction == Enums.Direction.SOUTH) {
    	
    		int x_m = state.c.x - view.length/2;
    		for (int x_v=view.length-1; x_v >= 0; x_v--) {
    			
    			int y_m = state.c.y - view[0].length/2;
    			for (int y_v=view[0].length-1; y_v >= 0; y_v--) {
    			
    				if (x_m == state.c.x && y_m == state.c.y) {
    					map[x_m][y_m].symbol = Enums.Symbol.EMPTY;
    				} else {	
    					map[x_m][y_m].symbol = Enums.charToEnum(view[y_v][x_v]);
    				}
    				
    				y_m++;
    			}
    			
    			x_m++;
    		}
    	} 
    	
    	// EAST
    	else if (state.direction == Enums.Direction.EAST) {
    	
    		int x_m = state.c.x - view.length/2;
    		
			for (int y_v=view[0].length-1; y_v >= 0; y_v--) {
    			
    			int y_m = state.c.y - view[0].length/2;
				for (int x_v=0; x_v < view.length; x_v++) {
    				if (x_m == state.c.x && y_m == state.c.y) {
    					map[x_m][y_m].symbol = Enums.Symbol.EMPTY;
    				} else {	
    					map[x_m][y_m].symbol = Enums.charToEnum(view[y_v][x_v]);
    				}
    				
    				y_m++;
    			}
    			
    			x_m++;
    		}
    	} 
    	
    	// WEST
    	else if (state.direction == Enums.Direction.WEST) {
    	
    		int x_m = state.c.x - view.length/2;
    		for (int y_v=0; y_v < view[0].length; y_v++) {
    		
    			
    			int y_m = state.c.y - view[0].length/2;
    			for (int x_v=view.length-1; x_v >= 0; x_v--) {
    				if (x_m == state.c.x && y_m == state.c.y) {
    					map[x_m][y_m].symbol = Enums.Symbol.EMPTY;
    				} else {	
    					map[x_m][y_m].symbol = Enums.charToEnum(view[y_v][x_v]);
    				}
    				
    				y_m++;
    			}
    			
    			x_m++;
    		}
    	}
    }
    
    public void clearLocation(Coordinate c) {
    	this.map[c.x][c.y].symbol = Enums.Symbol.EMPTY;
    }
    
    public Enums.Symbol getSymbolAtCoord(Coordinate c) {
    	return this.map[c.x][c.y].symbol;
    }
    
    // Print a ascii version of the entire map
//    public void printMap() {
//    
//    	// Set all coordinated of the map to UNKNOWN at the start
//        for (int y=65; y < 100; y++) {
//            for (int x=45; x < 105; x++) {
//                System.out.print(map[x][y].symbol + " ");
//            }
//            System.out.println();
//        }
//    }
    public void printMap(Coordinate c, Enums.Direction d) {
		System.out.println("\nWhat's seen:"+d+"\n");
    	// Set all coordinated of the map to UNKNOWN at the start
        for (int y=65; y < 100; y++) {
            for (int x=45; x < 105; x++) {
            	if (x == c.x && y == c.y) {
            		System.out.print(Enums.agentDirection(d));
            	} else {
            		System.out.print(map[x][y].symbol);
            	}
            }
            System.out.println();
        }
    }
    
    // Return a coordinate that is surrounded by the most UNKNOWN symbols
    // Used for exploring stage
    public Coordinate findMostUnknowns() {
    	
    	Coordinate retval = null;
    	int maxCount = 0;
    	
    	// For each UNKNOWN point on the map...
    	for (int i=0; i < MAP_WIDTH; i++) {
            for (int j=0; j < MAP_HEIGHT; j++) {
            
            	if (map[i][j].symbol == Enums.Symbol.UNKNOWN) {
            	
	            	// Count the number of UNKOWNS: 
	            	int tempCount = 0;
	            	boolean valid = true;
            		int k;
            		
	            	// ABOVE
	            	for (k=j; k >= 0 && map[i][k].symbol == Enums.Symbol.UNKNOWN; k--) {
	            		tempCount++;
	            	}
	            	if (k == 0) {
	            		valid = false;
	            	}
	            	
	            		
	            	// BELOW
	            	for (k=j; k < MAP_HEIGHT && map[i][k].symbol == Enums.Symbol.UNKNOWN; k++) {
	            		tempCount++;
	            	}
	            	if (k == MAP_HEIGHT) {
	            		valid = false;
	            	}
	            	
	            	// LEFT
	            	for (k=i; k >= 0 && map[k][j].symbol == Enums.Symbol.UNKNOWN; k--) {
	            		tempCount++;
	            	}
	            	if (k == 0) {
	            		valid = false;
	            	}
	            		
	            	// RIGHT
	            	for (k=i; k < MAP_WIDTH && map[k][j].symbol == Enums.Symbol.UNKNOWN; k++) {
	            		tempCount++;
	            	}
	            	if (k == MAP_WIDTH) { 
	            		valid = false;
	            	}
	            	
	            	// Update the new max count
	            	if (valid == true && tempCount > maxCount) {
	            		maxCount = tempCount;
	            		retval = map[i][j].convertToCoord();
	            	}
            		
            	} else {
            		// DO NOTHING
            	}
            }
        }
    	
    	return retval;
    }
    
    // Return an array list of points containing the specified item
    public ArrayList<Point> findItem(Enums.Symbol item) {
    	ArrayList<Point> points = new ArrayList<Point>();
    	
    	for (Point p : this) {
    		if (p.symbol == item) {
    			points.add(p);
    		}
    	}
    	
    	return points;
    }
    
    // Return a list of all Points on the map containing items
    public ArrayList<Point> findItemsOnMap() {
    	ArrayList<Point> points = new ArrayList<Point>();
    	
 		for (Point p : this) {
 			if (p.symbol == Enums.Symbol.AXE) {
 				points.add(p);
 			} else if (p.symbol == Enums.Symbol.KEY) {
 				points.add(p);
 			} else if (p.symbol == Enums.Symbol.BOMB) {
 				points.add(p);
 			} 
 		}
 		
 		return points;
 	}
    
	@Override
	public Iterator<Point> iterator() {
		ArrayList<Point> ret = new ArrayList<Point>();
		
		for (Point[] p : this.map) {
			for (Point q : p) {
				ret.add(q);
			}
		}
		return ret.iterator();
	}
	
	public ArrayList<Point> findNearItems (Coordinate c) {
		ArrayList<Point> list = new ArrayList<Point>();
		
		for (int y=c.y-2; y <= c.y+2; y++) {
            for (int x=c.x-2; x <= c.x+2; x++) {
            	if (this.map[x][y].symbol == Enums.Symbol.GOLD ||
            			this.map[x][y].symbol == Enums.Symbol.KEY ||
            			this.map[x][y].symbol == Enums.Symbol.AXE ||
            			this.map[x][y].symbol == Enums.Symbol.BOMB) {
            		list.add(new Point(this.map[x][y]));
            	}
            }
        }
		
		return list;
	}
    
}
