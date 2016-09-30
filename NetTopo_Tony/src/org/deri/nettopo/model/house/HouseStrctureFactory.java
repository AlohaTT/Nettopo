package org.deri.nettopo.model.house;

/**
 * Factory of the labyrinth structure
 * 
 * @author 
 * 
 */
public class HouseStrctureFactory {
	/**
	 * Factory method producing instance of the labyrinth structure 
	 * 
	 * @param level:
	 *            Difficulty level of the structure
	 * @return
	 */
	public static HouseStructure getStructure(int level) {
		switch (level) {
		case 1:
			return new Structure();
			
		/* any other difficulty levels could be put here */
		case 2:  
		case 3:
		}
		return null;
	}
}
