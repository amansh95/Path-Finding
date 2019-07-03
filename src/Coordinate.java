
public class Coordinate {
	public int x;
	public int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Coordinate c) {
		return this.x == c.x && this.y == c.y;
	}
}
