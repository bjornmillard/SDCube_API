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
import java.io.IOException;
import java.util.ArrayList;

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;



public class SDCube {

	/**
	 * The list of SDCube_Samples that compose this SDCube. NOTE: each sample
	 * has 2 parts: the data and the expDesign
	 */
	private ArrayList<SDCube_Sample> TheSamples;

	/**
	 * The Absolute file system path to the location of the HDF5 file that
	 * represents this SDCube folder
	 */
	private String sdcPath;

	private SDCube_DataModule TheRootDataModule;

	/**
	 * Basic Constructor
	 * 
	 * @author Bjorn Millard
	 * */
	public SDCube() {
		TheSamples = new ArrayList<SDCube_Sample>();	
	}


	/**
	 * Constructor requires the file system path to a valid SDCube HDF5 file
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            pathToSDCube
	 * 
	 * */
	public SDCube(String path) {
		this.sdcPath = path;
		TheSamples = new ArrayList<SDCube_Sample>();
	}

	/**
	 * Returns the complete list of ExpDesign_Samples contained within this
	 * SDCube
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<ExpDesign_Sample>
	 * */
	public ArrayList<ExpDesign_Sample> getExpDesigns() {

		ArrayList<ExpDesign_Sample> eps = new ArrayList<ExpDesign_Sample>();
		int len = TheSamples.size();
		for (int i = 0; i < len; i++)
			eps.add(TheSamples.get(i).getExpDesign());

		return eps;
	}

	/**
	 * Returns the String path that points to the location of this SDCube on the
	 * file system
	 * 
	 * @author Bjorn Millard
	 * @return String path
	 * */
	public String getPath() {
		return sdcPath;
	}

	/**
	 * Sets the path of this SDCube folder on the file system
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            path
	 */
	public void setPath(String path) {
		sdcPath = path;
	}

	 /**
	 * Add a collection of Samples to the SDCube
	 *
	 * @author Bjorn Millard
	 * @param SDCube_Sample
	 * */
	public void addSamples(ArrayList<SDCube_Sample> samples)
	 {
		int len = samples.size();
		for (int i = 0; i < len; i++)
			addSample(samples.get(i));
	 }
	
	 /**
	 * Add an ExpDesign_Description/DataModule combo as a single-SAMPLE to the SDCube
	 *
	 * @author Bjorn Millard
	 * @param SDCube_Sample
	 * */
	public void addSample(SDCube_Sample sample)
	 {
		// making sure they have the same ID
		sample.getDataModule().setId(sample.getID());
		sample.getExpDesign().setId(sample.getID());
		// TheRootDataModule.addDataModule(data);
		TheSamples.add(sample);
	 }

	
	/**
	 * Loads the given SDCube/HDF5 file path into the current Java SDCube object 
	 * 
	 * @author Bjorn Millard
	 * @param String pathToHDF5file
	 * @throws H5IO_Exception 
	 * */
	public void load(String sdcPath) throws H5IO_Exception {
		TheSamples = new ArrayList<SDCube_Sample>();
		this.sdcPath = sdcPath;
		// Loading the top level DataModule representing the samples
		String h5Path = sdcPath+ "/Data.h5";
		H5IO io = new H5IO();
		io.openHDF5(h5Path);
		
		TheRootDataModule = new SDCube_DataModule(h5Path, ".");
		TheRootDataModule.load(io);

		ArrayList<ExpDesign_Sample> eps = ExpDesign_IO
				.parseSamples(sdcPath
				+ "/ExpDesign.xml");

		// Now putting them all into samples
		String[] sNames = TheRootDataModule.getSampleNames();
		
		if(sNames!=null)
		{
			int len = sNames.length;
			for (int i = 0; i < len; i++) {
				SDCube_DataModule data = TheRootDataModule.getDataModule(i);
				//find the expD with same ID
				for (int j = 0; j < eps.size(); j++) {
					if (eps.get(j).getId().trim().equals(data.getId().trim()))
					{
						// System.out.println("Adding Sample: " + data.getId());
						TheSamples.add(new SDCube_Sample(data, eps.get(j), data.getId()));
						break;
					}
				}
			}
		}
		io.closeHDF5();
	}
	/**
	 * Loads the current SDCube Path into the current Java SDCube object 
	 * 
	 * @author Bjorn Millard
	 * @param String pathToHDF5file
	 * @throws H5IO_Exception 
	 * */
	public void load() throws H5IO_Exception {
		load(sdcPath);
	}
	/**
	 * Writes this Java SDCube object to the intialized SDCube/HDF5 file path
	 * 
	 * @author Bjorn Millard
	 * @throws H5IO_Exception
	 * */
	public void write() throws H5IO_Exception {
		write(sdcPath);
	}
	/**
	 * Writes this Java SDCube object to the given SDCube/HDF5 file path
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            pathToHDF5file
	 * @throws H5IO_Exception
	 * */
	public void write(String sdcubePath) throws H5IO_Exception {
		this.sdcPath = sdcubePath;
		String h5Path = sdcPath + "/Data.h5";
		String xmlPath = sdcPath + "/ExpDesign.xml";
		
		// Check if directory and files exist already... if not, create them
		File sdcFile = new File(sdcubePath);
		if (!sdcFile.exists())
			sdcFile.mkdir();

		// check if inner files exist or create them
			File h5File = new File(h5Path);
			File xmlFile = new File(xmlPath);
			if (!h5File.exists()) {
				try {
					h5File.createNewFile();
				} catch (IOException e) {
					System.err
.println("***ERROR creating H5 file***");
					e.printStackTrace();
				}
			}
			if (!xmlFile.exists()) {
				try {
					xmlFile.createNewFile();
				} catch (IOException e) {
					System.err
.println("***ERROR creating XML file***");
					e.printStackTrace();
				}
			}

			H5IO h5 = new H5IO();
			h5.openHDF5(h5Path);
			
			//Checking if samples already exist, and if so, rename new samples so dont conflict
			int numSamples = h5.getGroupChildCount(h5Path, "./Children");

			if(numSamples>0)
			{
				//rename them
			int numS = this.TheRootDataModule.getDataModules().size();
				for (int i = 0; i < numS; i++) {
					SDCube_DataModule dm = TheRootDataModule.getDataModule(i);
					String path = dm.getFilePath_Group();
				int indStart = 11;
					int indEnd = -1;
					if (path.length() > indStart) {
						int num = path.length();
						// Getting index where child begins
						indEnd = num - 1;
						for (int j = indStart; j < num - 1; j++)
		 {
							if (path.substring(j, j + 1).equals("/")) {
								indEnd = j;
								break;
							}
						}
					}
					dm.replacePath(
							"./Children/"
									+ path.substring(indStart, indEnd + 1)
											.trim()
 , "./Children/"
 + numSamples);


					numSamples++;
					
				}
			}

			
		// Writing all the data samples
		// TheSamples
		if (TheRootDataModule != null)
			TheRootDataModule.write(h5Path);
		else
		{
			TheRootDataModule = new SDCube_DataModule(h5Path, ".");
			TheRootDataModule.write(h5Path);
		}

		// Writing all the ExpDesign for the samples
		ExpDesign_Model model = new ExpDesign_Model(sdcPath);
		ExpDesign_IO.write(model);

		//Embedding the XML into the H5 file @ top level Meta group
		try {
			h5.writeFileToHDF5(xmlPath, h5Path, "./Meta/ExpDesign.xml");
			h5.closeHDF5();
		} catch (Exception e) {
			e.printStackTrace();
		}
		h5.closeHDF5();	

	}

	/**
	 * Collects together all the DataModules from "TheSamples" and creates a
	 * root DataModule for this SDCube.
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            sdcPath
	 * @return SDCube_DataModule
	 */
	public SDCube_DataModule getRootDataModule(String sdcPath) {
		SDCube_DataModule root = new SDCube_DataModule(sdcPath, ".");
		int len = TheSamples.size();
		for (int i = 0; i < len; i++)
			root.addDataModule(TheSamples.get(i).getDataModule());
		return root;
	}

	/**
	 * Finds, creates, and returns the SDCube_Sample with the given id. Note
	 * each sample has 2 parts, 1) ExpDesign 2)Data
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            id
	 * @return SDCube_Sample
	 */
	static public SDCube_Sample getSample(String sdcPath, String id) {

		String h5Path = sdcPath + "/Data.h5";
		String xmlPath = sdcPath + "/ExpDesign.xml";
		// Extracting the DataModule_Sample
		SDCube_DataModule data = new SDCube_DataModule(h5Path,
 ".");
		try {
			data.loadSample(sdcPath, id);
		} catch (H5IO_Exception e) {
			e.printStackTrace();
		}

		// Parsing the XML-ExpDesign_Sample
		ArrayList<ExpDesign_Sample> expD = ExpDesign_IO.parseSamplesWid(
				xmlPath, id);
		if (expD == null || data == null)
			return null;

		if (data == null || expD == null || expD.size() == 0
				|| expD.get(0) == null)
			return null;

		return new SDCube_Sample(data, expD.get(0), id);
	}

	/**
	 * Returns the complete list of Samples from this SDCube
	 * 
	 * @athor Bjorn Millard
	 * @return ArrayList<SDCube_Sample>
	 */
	static public ArrayList<SDCube_Sample> getSamples(String sdcPath) {
		String h5Path = sdcPath + "/Data.h5";
		String xmlPath = sdcPath + "/ExpDesign.xml";
		ArrayList<SDCube_Sample> samples = new ArrayList<SDCube_Sample>();
		// Getting all sample IDs inside this SDCube
		ArrayList<String> ids = getSampleIDs(sdcPath);
		int len = ids.size();
		System.out.println(len);
		
		// Extracting the DataModule_Sample
		for (int i = 0; i < len; i++) {
			SDCube_DataModule data = new SDCube_DataModule(h5Path, ".");
			try {
				data.loadSample(sdcPath, ids.get(i));
			} catch (H5IO_Exception e) {
				e.printStackTrace();
			}

			// Parsing the XML-ExpDesign_Sample
			ArrayList<ExpDesign_Sample> expD = ExpDesign_IO
					.parseSamplesWid(xmlPath, ids.get(i));

			if (data != null && expD != null && expD.size() != 0
					&& expD.get(0) != null)
				samples.add(new SDCube_Sample(data, expD.get(0), ids.get(i)));
			else
				System.err
.println("**ERROR: loading sample with id: "
						+ ids.get(i));
		}
		return samples;
	}

	/**
	 * Returns the complete list of Sample_IDs contained within this SDCube
	 * 
	 * @athor Bjorn Millard
	 * @return ArrayList<String>
	 */
	static public ArrayList<String> getSampleIDs(String sdcPath) {
		ArrayList<String> ids = new ArrayList<String>();
		String h5Path = sdcPath + "/Data.h5";

		H5IO io = new H5IO();
		String[] names = null;
		try {
			names = io.getGroupChildNames(h5Path, "./Children");
		} catch (H5IO_Exception e) {
			System.out.println("**ERROR getting child count for group: "
					+ h5Path + "/Children");
		e.printStackTrace();
		}
		
		int len = names.length;
		try {
		for (int i = 0; i < len; i++) {
				Data_2D dat = (Data_2D) io.readDataset(h5Path, "./Children/"
						+ names[i] + "/Meta/Sample_ID");
			String[][] data = (String[][]) dat.getData();
			ids.add(data[0][0].trim());
		}
		} catch (H5IO_Exception e) {
			System.out.println("**ERROR reading sample IDs**");
			e.printStackTrace();
		}
		
		return ids;
	}

	/**
	 * Returns the Number of Samples contained within this SDCube
	 * 
	 * @athor Bjorn Millard
	 * @param String
	 *            sdcPath
	 * @return int numberOfSamples
	 */
	static public int getNumSamples(String sdcPath) {
		H5IO io = new H5IO();
		int num = 0;
		try {
			num = io.getGroupChildCount(sdcPath + "/Data.h5", "./Children");
		} catch (H5IO_Exception e) {
			System.err.println("**ERROR counting children for group: "
					+ sdcPath + "/Children");
			e.printStackTrace();
		}
		return num;
	}

	/**
	 * Extracts the embedded XML file from the HDF5-SDCube file and writes it to
	 * the given destination path on the users file system
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            sdcubePath
	 * @param String
	 *            destinationXMLPath
	 */
	static public void extractXMLfromH5(String sdcubePath,
			String destinationPath) {
		try {
			new H5IO().readFileFromHDF5(sdcubePath + "/Data.h5",
					"./Meta/ExpDesign.xml", destinationPath);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (H5IO_Exception e) {
			e.printStackTrace();
		} catch (HDF5Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a list of samples within this SDCube that have the given tags in
	 * their description in the XML ExpDesign file, NOTE this returns the OR of
	 * the names given, so if it has just 1 of them, then the sample will pass.
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<SDCube_Sample> selectSamples
	 * @param String
	 *            [] tagNames
	 */
	static public ArrayList<SDCube_Sample> getSamplesWithDescriptorNames_OR(
			String sdcPath, String[] names) {
		ArrayList<SDCube_Sample> samples = new ArrayList<SDCube_Sample>();
		String xmlPath = sdcPath + "/ExpDesign.xml";

		ArrayList<ExpDesign_Sample> eps = ExpDesign_IO
				.parseSamplesWithDescriptorNames_OR(xmlPath, names);
		if (eps == null || eps.size() == 0)
			return null;

		// Now retrieving the complete samples
		int len = eps.size();
		for (int i = 0; i < len; i++)
 {
			SDCube_Sample sam = getSample(sdcPath, eps.get(i).getId());
			if (sam != null)
				samples.add(sam);
		}
		return samples;
	}

	/**
	 * Returns a list of samples within this SDCube that have the given tags in
	 * their description in the XML ExpDesign file, NOTE this returns the AND of
	 * the names given, so it requires all the names to be present for the
	 * sample to pass
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<SDCube_Sample> selectSamples
	 * @param String
	 *            [] tagNames
	 */
	static public ArrayList<SDCube_Sample> getSamplesWithDescriptorNames_AND(String sdcPath,
 String[] names)
	{
		ArrayList<SDCube_Sample> samples = new ArrayList<SDCube_Sample>();
		String xmlPath = sdcPath + "/ExpDesign.xml";

		ArrayList<ExpDesign_Sample> eps = ExpDesign_IO
				.parseSamplesWithDescriptorNames_AND(xmlPath, names);
		if (eps == null || eps.size() == 0)
			return null;

		// Now retrieving the complete samples
		int len = eps.size();
		for (int i = 0; i < len; i++)
			samples.add(getSample(sdcPath, eps.get(i).getId()));

		return samples;
	}
}

