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
public class ExpDesign_Sample
{
	
	private String id;
	private ArrayList<ExpDesign_Description> descriptions;
	
	/**
	 * Constructor.
	 */
	public ExpDesign_Sample()
	{
		id = null;
		descriptions = new ArrayList<ExpDesign_Description>();
	}

	public ExpDesign_Sample(String id) {
		this.id = id;
		descriptions = new ArrayList<ExpDesign_Description>();
	}
	
	/**
	 * Get the unique sample id
	 * @author Bjorn Millard
	 * @return String id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the unique sample id
	 * 
	 * @author Bjorn Millard
	 * @return String id.
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * Add a description to the well.
	 */
	public void addDescription( ExpDesign_Description unit)
	{
		descriptions.add(unit);
	}
	
	/**
	 * Drop a description.
	 */
	public void removeDescription(ExpDesign_Description desc) {

		if (desc != null)
			for (int i = 0; i < descriptions.size(); i++) {
				if (descriptions.get(i) != null)
					if (descriptions.get(i).isSame(desc)) {
						descriptions.remove(i);
						i--;
					}
			}
	}

	/**
	 * Removes all descriptions from this sample of the given type
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            type
	 */
	public void removeDescriptionsOfType(String type) {
		for (int i = 0; i < descriptions.size(); i++) {
			if (descriptions.get(i) != null)
				if (descriptions.get(i).getType().equalsIgnoreCase(type)) {
					descriptions.remove(i);
					i--;
				}
		}
	}

	/**
	 * Get all descriptions.
	 */
	public ArrayList<ExpDesign_Description> getDescriptions()
	{
		return descriptions;
	}
	
	/**
	 * Get description size.
	 */
	public int getNumDescriptions()
	{
		return descriptions.size();
	}

	/**
	 * Print this out
	 * 
	 */
	public String toString() {

		String st = "";
		st += ">>>>>> ExpDesign: " + id + "<<\n";
		for (int i = 0; i < getNumDescriptions(); i++)
			st += (i + 1) + ") " + getDescriptions().get(i) + "\n";


		return st;
	}
}
