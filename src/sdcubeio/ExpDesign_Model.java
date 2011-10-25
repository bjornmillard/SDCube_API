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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;



public class ExpDesign_Model {

	private ArrayList<ExpDesign_Sample> TheExpDesigns;
	private String sdcPath;
	private String hdfPath;
	private String xmlPath;

	// Constructor
	public ExpDesign_Model(String sdcPath) {
		this.sdcPath = sdcPath;
		this.hdfPath = sdcPath + "/Data.h5";
		this.xmlPath = sdcPath + "/ExpDesign.xml";

		File f = new File(xmlPath);

		if (f.exists())
 {
			TheExpDesigns = ExpDesign_IO.parseSamples(xmlPath);
		}

		if (TheExpDesigns == null)
			TheExpDesigns = new ArrayList<ExpDesign_Sample>();
	}

	/**
	 * Returns the ExpDesign_Samples
	 * 
	 * @author BLM
	 * @return ArrayList<ExpDesign_Sample>
	 * */
	public ArrayList<ExpDesign_Sample> getSamples() {
		return TheExpDesigns;
	}

	/**
	 * returns a loaded sample with the given id
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            sampleID
	 * @return ExpDesign_Sample
	 */
	public ExpDesign_Sample getSample(String sampleID) {

		for (int i = 0; i < TheExpDesigns.size(); i++) {
			if (TheExpDesigns.get(i).getId().trim().equalsIgnoreCase(sampleID.trim()))
				return TheExpDesigns.get(i);
		}

		ExpDesign_Sample sam = new ExpDesign_Sample(sampleID);
		TheExpDesigns.add(sam);
		return sam;
	}


	/**
	 * Returns the XML file path
	 * 
	 * @author Bjorn Millard
	 * @return String path
	 * */
	public String getXMLpath() {
		return xmlPath;
	}

	/**
	 * Returns the HDF file path
	 * 
	 * @author Bjorn Millard
	 * @return String path
	 * */
	public String getHDFpath() {
		return hdfPath;
	}

	/**
	 * Adds a new ExpDesign_Sample to the total collection for this project.
	 * Note this only gets added to to the model and isnt writen out to the XML
	 * until the write() method is called.
	 * 
	 * @author Bjorn Millard
	 * @param ExpDesign_Sample
	 *            sample
	 * @return void
	 * */
	public void addSample(ExpDesign_Sample sample) {
		TheExpDesigns.add(sample);
	}

	/**
	 * Removes an ExpDesign_Sample from the collection in this project.  
	 * Note this only gets added to to the model and isnt writen out to the XML
	 * until the write() method is called.
	 * 
	 * @author Bjorn Millard
	 * @param String sampleID
	 * @return void
	 * */
	public void removeSample(String id) {
		int len = TheExpDesigns.size();
		for(int i = 0; i < len; i++)
		{
			ExpDesign_Sample sam = TheExpDesigns.get(i);
			if(sam.getId().equalsIgnoreCase(id))
			{
				TheExpDesigns.remove(i);
				i--;
				len--;
			}
			
		}
	}
	
	/**
	 * Remove description from the given sample with given sampleID
	 * 
	 * @author Bjorn Millard
	 * 
	 */
	public void removeDescription(String sampleID, ExpDesign_Description desc) {

		// getting the correct sample from the list
		int len = TheExpDesigns.size();
		ExpDesign_Sample sam = null;
		for (int i = 0; i < len; i++) {
			if (TheExpDesigns.get(i).getId().equalsIgnoreCase(sampleID)) {
				sam = TheExpDesigns.get(i);
				break;
			}
		}
		if (sam == null)
			return;

		sam.removeDescription(desc);
	}

	/**
	 * Removes all descriptions from the given sample of the given type
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            sampleID
	 * @param String
	 *            type
	 * */
	public void removeDescriptionOfType(String sampleID, String type) {
		// getting the correct sample from the list
		int len = TheExpDesigns.size();
		ExpDesign_Sample sam = null;
		for (int i = 0; i < len; i++) {
			if (TheExpDesigns.get(i).getId().equalsIgnoreCase(sampleID)) {
				sam = TheExpDesigns.get(i);
				break;
			}
		}
		if (sam == null)
			return;

		sam.removeDescriptionsOfType(type);
	}

	/**
	 * Read treatments of a specific well.
	 * 
	 * @param wellIdx
	 *            The well Index; for example 0 = "A1", 1 = "A2", etc.
	 * @return Returns an array of descriptions, which contains the treatments.
	 */
	public ExpDesign_Description[] getTreatments(String sampleID) {
		return getDescriptions(sampleID, "treatment");
	}

	/**
	 * Read measurement of a specific well.
	 * 
	 * @param wellIdx
	 *            The well Index; for example 0 = "A1", 1 = "A2", etc.
	 * @return Returns an array of descriptions, which contains the
	 *         measurements.
	 */
	public ExpDesign_Description[] getMeasurements(String sampleID) {
		return getDescriptions(sampleID, "measurement");
	}

	/**
	 * Read all descriptions of a specific sample of a given type
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            sampleID
	 * @param String
	 *            type
	 * @return ExpDesign_Description[].
	 */
	private ExpDesign_Description[] getDescriptions(String sampleID, String type) {

		// getting the correct sample from the list
		ExpDesign_Sample sam = getSample(sampleID);
		if (sam == null)
			return null;

		ArrayList<ExpDesign_Description> descriptions = sam.getDescriptions();
		ArrayList<ExpDesign_Description> descs = new ArrayList<ExpDesign_Description>();
		for (int i = 0; i < descriptions.size(); i++) {
			if (descriptions.get(i).getType().equalsIgnoreCase(type))
				descs.add(descriptions.get(i));
		}
		// converting to array
		int len = descs.size();
		ExpDesign_Description[] out = new ExpDesign_Description[len];
		for (int i = 0; i < len; i++)
			out[i] = descs.get(i);
		return out;
	}

	// /**
	// * Read the description of a specific well.
	// *
	// * @param wellIdx
	// * The well Index; for example 0 = "A1", 1 = "A2", etc.
	// * @return Returns the description.
	// */
	// public ExpDesign_Description readDescription(String sampleID) {
	// return readDesc(sampleID, "description");
	// }

	/**
	 * Read the date of a specific well.
	 * 
	 * @param String
	 *            SampleID
	 * @return Returns a description, which contains the date.
	 */
	public ExpDesign_Description getDate(String sampleID) {
		return getDescription(sampleID, "Date");
	}

	/**
	 * Read the time point of a specific well.
	 * 
	 * @param wellIdx
	 *            The well Index; for example 0 = "A1", 1 = "A2", etc.
	 * @return Returns a description, which contains the time point.
	 */
	public ExpDesign_Description getTimePoint(String sampleID) {
		return getDescription(sampleID, "Measurement_Time");
	}



	/**
	 * Returns an array of all descriptors contained within the given sampleID
	 * of the given descriptionType. Note the descType is typically:
	 * "Treatment", "Measurement", "Date", "Description"
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            sampleID
	 * @param String
	 *            descType
	 * @return ExpDesign_Description[]
	 * */
	public ExpDesign_Description getDescription(String sampleID, String descType) {

		ExpDesign_Sample sam = getSample(sampleID);
		if (sam == null) {
			return null;
		}

		ExpDesign_Description desc = null;
		ArrayList<ExpDesign_Description> descriptions = sam.getDescriptions();
		for (int i = 0; i < descriptions.size(); i++) {
			if (descriptions.get(i).getType().equalsIgnoreCase(descType)) {
				desc = descriptions.get(i);
				break;
			}
		}
		return desc;
	}

	/**
	 * Returns all the measurements within the given well as an ordered name
	 * (String) list
	 * 
	 * @param wellIdx
	 *            The well Index; for example 0 = "A1", 1 = "A2", etc.
	 * @return Returns all the measurements.
	 */
	public ArrayList<String> getAllMeasurementNames(String sampleID) {
		ArrayList<String> arr = new ArrayList<String>();
		ExpDesign_Description[] des = getMeasurements(sampleID);
		if (des != null && des.length > 0) {
			for (int i = 0; i < des.length; i++)
				arr.add(des[i].getName());
			Collections.sort(arr);
		}
		return arr;
	}

	/**
	 * Returns all the treatments within the given well as an ordered name
	 * (String) list
	 * 
	 * @author BLM
	 */
	public ArrayList<String> getAllTreatmentNames(String sampleID) {
		ArrayList<String> arr = new ArrayList<String>();
		ExpDesign_Description[] des = getTreatments(sampleID);
		if (des != null && des.length > 0) {
			for (int i = 0; i < des.length; i++)
				arr.add(des[i].getName());
			Collections.sort(arr);
		}
		return arr;
	}


	/**
	 * Adds the given sample description to the given sample with this model of
	 * the given ID
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            sampleID
	 * @param ExpDesign_Description
	 *            desc
	 * */
	public void addDescription(String sampleID, ExpDesign_Description desc) {
		ExpDesign_Sample sam = getSample(sampleID);
		if (sam != null)
 {
			sam.addDescription(desc);
			// System.out
			// .println("Adding Description to ExpDesign_Sample with ID: "
			// + sampleID);
			// System.out.println("--Complete Model:\n" + toString());
		}
		else {
			System.out.println("ERROR: no ExpDesign_Sample with ID: "
					+ sampleID);
			// System.out.println("--Complete Model:\n" + toString());
		}
	}

	/**
	 * Replaces the given sample description in the given sample with this model
	 * of the given ID
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            sampleID
	 * @param ExpDesign_Description
	 *            desc
	 * */
	public void replaceDescription(String sampleID, ExpDesign_Description desc) {
		ExpDesign_Sample sam = getSample(sampleID);
		if (sam != null) {
			sam.removeDescriptionsOfType(desc.getType());
			sam.addDescription(desc);
			// System.out
			// .println("Adding Description to ExpDesign_Sample with ID: "
			// + sampleID);
			// System.out.println("--Complete Model:\n" + toString());
		} else {
			System.out.println("ERROR: no ExpDesign_Sample with ID: "
					+ sampleID);
			// System.out.println("--Complete Model:\n" + toString());
		}
	}


	/**
	 * Print out of the model
	 * 
	 * @author Bjorn Millard
	 * @return String textOut
	 */
	public String toString() {
		String st = "";
		int len = TheExpDesigns.size();
		System.out.println("====> found: " + len
				+ " samples in ExpDesign_Model");
		for (int i = 0; i < len; i++) {

			st += TheExpDesigns.get(i).toString();
		}
		return st;
	}

	/**
	 * Writes this model to file
	 * 
	 * @author Bjorn Millard
	 */
	public void write() {
		ExpDesign_IO.write(this);
	}

}

