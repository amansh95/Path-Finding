import java.util.ArrayList;


public class Explore {
	
	// VARIABLES
	private State s;
	
	private int phase;
	
	public boolean followingWall;
	private boolean requiredAxe;
	private boolean requiredKey;
	
	private int turnCount;
	
	private Enums.Direction initialDirection;
	
	public boolean addContactPoints;
	public ArrayList<ContactPoints> contactPoints;
	
	private ArrayList<Coordinate> coordinatesSeen;
	
	
	// CONSTANTS
	private static final int PLEDGE_PHASE = 1;
	private static final int CENTRE_PHASE = 2;
	
	
	
	public Explore(State s) {
		this.s = s;
		this.phase = PLEDGE_PHASE;
		this.followingWall = false;
		this.requiredAxe = false;
		this.requiredKey = false;
		this.turnCount = 0;
		this.initialDirection = s.direction;
		this.coordinatesSeen = new ArrayList<Coordinate>();
		
		this.addContactPoints = false;
		this.contactPoints = new ArrayList<ContactPoints>();
	}
	
	public char run() {
		char move = '?';	// Should never happen
		
		// Restart explore phase if required item was obtained
		if (s.axe == true && requiredAxe == true) {
			contactPoints.clear();
			requiredAxe = false;
		} else if (s.key == true && requiredKey == true) {
			contactPoints.clear();
			requiredKey = false;
		}
		
		// Walk around the OUTSIDE of the map
		if (phase == PLEDGE_PHASE) {
			
			// Move forward if possible
			if (followingWall == false) {
				
				if (this.canMoveForward()) {
					move = moveForward();
				} else {
					
					// When we can't move forward, turn right and start wall flower
					move = turnRight();
					followingWall = true;
					
					// Add new contact points when setting followingWall
					this.addContactPoints = true;
				}
				
			} else {
			 	
				// Keeping our left hand on the wall...
				
				// Move left if possible
				if (s.lastMove() == 'o' || s.lastMove() == 'c') {
					move = moveForward();
				} else if (this.canMoveLeft() == true && s.lastMove() != 'l') {
					move = turnLeft();
				} 
				// Move forward if possible
				else if (this.canMoveForward() == true) {
					move = moveForward();
					// Update c2
					if (addContactPoints == true) {
						
						ContactPoints cps = new ContactPoints(coordinatesSeen);
						contactPoints.add(cps);
						addContactPoints = false;
						
					} else if (compareContactPoints()) {
						
						// Stop Pledge
						phase = CENTRE_PHASE;
					}
				}
				// Otherwise turn right
				else {
					move = turnRight();
				}

				// Stop following the wall when pledge condition is met
				if (s.direction == initialDirection && turnCount == 0) {
					followingWall = false;
				}
				
			}
		} 
		
		// Explore the remaining CENTRE of the map
		else if (phase == CENTRE_PHASE) {
			
			// TODO For now we will just finish exploring 
			// Later, we should explore all remaining unknown islands
			
			
		}
		
		return move;
	}
	
	// Check that the last two points visited have been
	// visited previously 
	private boolean compareContactPoints() {
		boolean retval = false;
		
		for (ContactPoints cps : contactPoints) {
			if (cps.compare(coordinatesSeen)) {
				retval = true;
			}
		}
		
		return retval;
	}
	
	public boolean canMoveForward() {
 		Enums.Symbol inFront = s.map.getSymbolAtCoord(s.coordinateInFront());
 		
 		// See if we can go there
 		boolean isValid = s.validMove(inFront);
		
		// Check if we could move left BUT DIDN'T have a key or axe
		if (isValid == false) {
			if (inFront == Enums.Symbol.DOOR) {
				requiredKey = true;
			} else if (inFront == Enums.Symbol.TREE) {
				requiredAxe = true;
			}
		}
		
 		return s.validMove(inFront);
 	}
	
	private boolean canMoveLeft() {
		
		// Look to the left
		Enums.Symbol onLeft = s.map.getSymbolAtCoord(s.coordinateOnLeft());
		
		// See if we can go there
		boolean isValid = s.validMove(onLeft);
		
		// Check if we could move left BUT DIDN'T have a key or axe
		if (isValid == false) {
			if (onLeft == Enums.Symbol.DOOR) {
				requiredKey = true;
			} else if (onLeft == Enums.Symbol.TREE) {
				requiredAxe = true;
			}
		}
		
		return isValid;
	}
	
	public boolean stillExploring() {
		
		// TODO 
		if (phase == CENTRE_PHASE) {
			return false;
		}
		
		return true;
	}
	
	private char turnLeft() {
		s.turnLeft();
		this.turnCount--;
		return 'l';
	}
	
	private char turnRight() {
		s.turnRight();
		this.turnCount++;
		return 'r';
	}
	
	private char moveForward() {
		if (coordinatesSeen.size() == 0) {
			coordinatesSeen.add(s.c);
		}
		char retval = s.moveForward();
		
		// Only update the explored coordinates if we actually move forward
		if (retval == 'f') {
			coordinatesSeen.add(s.c);
		} else {
			addContactPoints = true;
			
			contactPoints.clear();
		}
		
		return retval;
	}
}