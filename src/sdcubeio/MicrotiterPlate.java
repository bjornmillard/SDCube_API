package sdcubeio;

import java.util.ArrayList;

/**
 * 
 * @author Bjorn Millard & Michael Menden
 *
 */
public class MicrotiterPlate {

	private ArrayList<ExpDesign_Sample> wells;
	
	/**
	 * Constructor.
	 */
	public MicrotiterPlate() {
		wells = new ArrayList<ExpDesign_Sample>();
	}
	
	/**
	 * Replace a well.
	 */
	public void replaceWell( ExpDesign_Sample well) {
		// dropWell(well.getIdx());
		wells.add(well);
	}
	
	/**
	 * Add a well.
	 */
	public void addWell( ExpDesign_Sample well) {
		wells.add(well);
	}
	
	/**
	 * Drop the well if it exist.
	 */
	public void dropWell( int wellIdx) {
//		for (int i=0; i<wells.size(); i++) {
//			if (wellIdx == wells.get(i).getIdx()) {
//				wells.remove(i);
//			}
//		}
	}
	
	/**
	 * Get all wells.
	 */
	public ArrayList<ExpDesign_Sample> getWells() {
		return wells;
	}
	
	/**
	 * Get a well.
	 */
	public ExpDesign_Sample getWell( int wellIdx) {
//		ExpDesign_Sample well = null;
//		for (int i=0; i<wells.size(); i++) {
//			if (wellIdx == wells.get(i).getIdx()) {
//				well = wells.get(i);
//			}
//		}
//		return well;
		return null;
	}

	/**
	 * Get well size.
	 */
	public int getWellSize() {
		return wells.size();
	}
}
