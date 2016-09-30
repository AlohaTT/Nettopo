package org.deri.nettopo.model.house;

/**
 * One implementation of the labyrinth structure. The structure is simple. So
 * the name is called level 1.
 * 
 * @author 
 * 
 */
public class Structure implements HouseStructure {
	public static final int BASE = 10;

	public int getGridBase() {
		return BASE;
	}

	public boolean[] getHorizontalSegments() {
		boolean[] horizontal = new boolean[BASE * (BASE + 1)];
		for (int i = 0; i < horizontal.length; i++) {
			horizontal[i] = false;
		}
		horizontal[0] = true;
		horizontal[1] = true;
		horizontal[2] = true;
		horizontal[3] = true;
		horizontal[4] = true;
		horizontal[5] = true;
		horizontal[6] = true;
		horizontal[7] = true;
		horizontal[8] = true;
		horizontal[9] = true;
		horizontal[34] = true;
		horizontal[35] = true;
		horizontal[37] = true;
		horizontal[38] = true;
		horizontal[39] = true;
		horizontal[40] = true;
		horizontal[41] = true;
		horizontal[43] = true;
		horizontal[80] = true;
		horizontal[81] = true;
		horizontal[83] = true;
		horizontal[84] = true;
		horizontal[85] = true;
		horizontal[87] = true;
		horizontal[99] = true;
		horizontal[100] = true;
		horizontal[101] = true;
		horizontal[102] = true;
		horizontal[103] = true;
		horizontal[104] = true;
		horizontal[105] = true;
		horizontal[106] = true;
		horizontal[107] = true;
		horizontal[108] = true;
		horizontal[109] = true;

		return horizontal;
	}

	public boolean[] getVerticalSegements() {
		boolean[] vertical = new boolean[BASE * (BASE + 1)];
		for (int i = 0; i < vertical.length; i++) {
			vertical[i] = false;
		}
		vertical[0] = true;
		vertical[1] = true;
		vertical[2] = true;
		vertical[3] = true;
		vertical[4] = true;
		vertical[5] = true;
		vertical[6] = true;
		vertical[7] = true;
		vertical[8] = true;
		vertical[9] = true;
		vertical[24] = true;
		vertical[25] = true;
		vertical[27] = true;
		vertical[40] = true;
		vertical[41] = true;
		vertical[42] = true;
		vertical[43] = true;
		vertical[58] = true;
		vertical[59] = true;
		vertical[88] = true;
		vertical[89] = true;
		vertical[100] = true;
		vertical[101] = true;
		vertical[102] = true;
		vertical[103] = true;
		vertical[104] = true;
		vertical[105] = true;
		vertical[106] = true;
		vertical[107] = true;
		vertical[108] = true;
		

		return vertical;
	}
}
