import java.util.ArrayList;


public class ContactPoints {
	public Coordinate c1;
	public Coordinate c2;
	
	public ContactPoints(ArrayList<Coordinate> c) {
		if (c.size() >= 2) {
			this.c1 = c.get(c.size() - 2);
			this.c2 = c.get(c.size() - 1);
		}
	}
	
	public boolean compare(ArrayList<Coordinate> c) {;
		boolean retval = false;
		
		if (c.size() >= 2) {
			if (this.c1.equals(c.get(c.size() - 2)) &&
					this.c2.equals(c.get(c.size() - 1))) {
				retval = true;
			}
		}
		return retval;
	}
}
