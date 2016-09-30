package org.deri.nettopo.model.house;

/**
 * This interface represents the high level structure of the labyrinth. The
 * structure directly influences the appearance of the labyrinth wall.
 * 
 * @author Chun Wu
 * 
 */
public interface HouseStructure {
	/**
	 * Implemented class should return a integer representing the grid numbers
	 * of the labyrinth square side
	 * 
	 * @return
	 */
	public int getGridBase();

	/**
	 * array of boolean indicating whether there should be a wall segement at
	 * the horizontal specified 0-based index position of the grid. The index is
	 * counted left to right and up to down
	 * 
	 * @return
	 */
	public boolean[] getHorizontalSegments();

	/**
	 * array of boolean indicating whether there should be a wall segement at
	 * the horizontal specified 0-based index position of the grid. The index is
	 * counted up to down and left to right
	 * 
	 * @return
	 */
	public boolean[] getVerticalSegements();
}
