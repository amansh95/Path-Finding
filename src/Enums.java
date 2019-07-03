/*********************************************
/*  Enums.java
/*  
/*  COMP3411 Artificial Intelligence
/*  UNSW Session 1, 2013
*/

public class Enums {
    
    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }
    
    public enum Symbol {
    	AGENT_NORTH {
    		public String toString () {
    			return "^";
    		}
    	},
    	AGENT_SOUTH {
    		public String toString () {
    			return "v";
    		}
    	},
    	AGENT_EAST {
    		public String toString () {
    			return ">";
    		}
    	},
    	AGENT_WEST {
    		public String toString () {
    			return "<";
    		}
    	},
        AXE {
            public String toString () {
                return "a";
            }
        },
        BOMB {
            public String toString () {
                return "d";
            }
        },
        DOOR {
            public String toString () {
                return "-";
            }
        },
        EMPTY {
            public String toString () {
                return " ";
            }
        },
        GOLD {
            public String toString () {
                return "g";
            }
        },
        KEY {
            public String toString () {
                return "k";
            }
        },
        TREE {
            public String toString () {
                return "T";
            }
        },
        UNKNOWN {
            public String toString () {
                return "X";
            }
        },
        WALL {
            public String toString () {
                return "*";
            }
        },
        WATER {
            public String toString () {
                return "~";
            }
        }
    }
    
    public static Symbol charToEnum (char c) {
    
    	switch (c) {	
    		case 'a':
    			return Symbol.AXE;
   		
    		case 'd':
    			return Symbol.BOMB;
    		
    		case '-':
    			return Symbol.DOOR;
    		
    		case ' ':
    			return Symbol.EMPTY;
    		
    		case 'g':
    			return Symbol.GOLD;
    			
    		case 'k':
    			return Symbol.KEY;
    		
    		case 'T':
    			return Symbol.TREE;
    		
    		case 'X':
    			return Symbol.UNKNOWN;
    		
    		case '*':
    			return Symbol.WALL;
    		
    		case '~':
    			return Symbol.WATER;
    	}
    	
    	// TODO Make a separate Symbol enum for 'error'
    	return Symbol.UNKNOWN;
    }
    
    public static Symbol agentDirection (Direction d) {
    	
    	switch (d) {
    		case NORTH:
    			return Symbol.AGENT_NORTH;
    			
    		case SOUTH:
    			return Symbol.AGENT_SOUTH;
    			
    		case EAST:
    			return Symbol.AGENT_EAST;
    			
    		case WEST:
    			return Symbol.AGENT_WEST;
    	}
    	
    	// By default, return NORTH/UP
    	return Symbol.AGENT_NORTH;
    }
    
}