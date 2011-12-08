/**
   SDCube Programming Library
   Software for the creation and manipulation of semantically-typed
   data hypercubes

   Copyright (C) 2011 Bjorn Millard <bjornmillard@gmail.com>

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as
   published by the Free Software Foundation, either version 3 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this program.  If not, see
   <http://www.gnu.org/licenses/>.
 */

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
