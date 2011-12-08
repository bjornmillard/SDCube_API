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


public class SDCube_DataModule {

	/** Represent the samples group folder */
	private ArrayList<SDCube_DataModule> TheSubSamples;
	/** Represent the data group folder */
	private ArrayList<DataObject> TheDataGroup;
	/** Represent the meta group folder */
	private ArrayList<DataObject> TheMetaGroup;
	/** Represent the raw group folder */
	private ArrayList<DataObject> TheRawFileArrays;
	/** File path to the SDCube folder */
	private String FilePath_SDCube;
	/** File path to the SDCube(HDF5) source file */
	private String FilePath_H5;
	/** File path to this sample group within(relative to)the SDCube file */
	private String FilePath_Group;
	/** unique ID of this datamodule */
	private String id;


	/**
	 * Constructor
	 * 
	 * @author Bjorn Millard
	 * */
	public SDCube_DataModule(String h5Path, String filePathGroup) {
		TheSubSamples = new ArrayList<SDCube_DataModule>();
		TheDataGroup = new ArrayList<DataObject>();
		TheMetaGroup = new ArrayList<DataObject>();
		TheRawFileArrays = new ArrayList<DataObject>();
		FilePath_H5 = h5Path;
		FilePath_Group = filePathGroup;
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
	 * Gets the unique sample id
	 * 
	 * @author Bjorn Millard
	 * @return String id.
	 */
	public String getId() {
		return id;
	}

	/** 
	 * Sets the H5 file this DataModule belongs to
	 * @author Bjorn Millard
	 * @param String newFilePath
	 * */
	public void setFilePath(String path)
	{
		FilePath_H5 = path;
	}
	/** 
	 * Sets the relative H5 group path of this DataModule
	 * @author Bjorn Millard
	 * @param String newFilePath
	 * */
	public void setGroupPath(String path)
	{
		FilePath_Group = path;
	}
	/**
	 * Prints off vital stats about this datamodule
	 * 
	 * @author Bjorn Millard
	 * */
	public String toString() {
		String st = "";
		st += ">>>>>> DataModule: " + getId() + "<<\n";
		st += "FilePath_Group: " + FilePath_Group + "\n";
		st += "FilePath_H5: " + FilePath_H5 + "\n";
		st += " TheSubSamples: " + TheSubSamples.size() + "\n";
		st += " TheDataGroup: " + TheDataGroup.size() + "\n";
		for (int i = 0; i < TheDataGroup.size(); i++) {
			st += "\t " + TheDataGroup.get(i).getDataType() + "  "
					+ TheDataGroup.get(i).getName() + "  rank: "
					+ TheDataGroup.get(i).getRank() + "\n";
		}
		st += " TheMetaGroup: " + TheMetaGroup.size() + "\n";
		for (int i = 0; i < TheDataGroup.size(); i++) {
			st += "\t " + TheMetaGroup.get(i).getDataType() + "  "
					+ TheMetaGroup.get(i).getName() + "  rank: "
					+ TheMetaGroup.get(i).getRank() + "\n";
		}
		st += " TheRawFileArrays: " + TheRawFileArrays.size() + "\n";

		return st;
	}

	/**
	 * Adds the given data modules to the samples group
	 * 
	 * @author Bjorn Millard
	 * @param ArrayList
	 *            <SDCube_DataModule> dataModules
	 * */
	public void addDataModules(ArrayList<SDCube_DataModule> dataModules) {
		TheSubSamples.addAll(dataModules);
	}

	/**
	 * Adds the given data module to the samples group
	 * 
	 * @author Bjorn Millard
	 * @param SDCube_DataModule
	 *            dataModules
	 * */
	public void addDataModule(SDCube_DataModule dataModule) {
		TheSubSamples.add(dataModule);
	}

	/**
	 * Adds the given datacubes to the data group
	 * 
	 * @author Bjorn Millard
	 * @param ArrayList
	 *            <Data_1D> datacubes
	 * */
	public void addData(ArrayList<DataObject> dataCubes) {
		TheDataGroup.addAll(dataCubes);
	}

	/**
	 * Adds the given datacube to the data group
	 * 
	 * @author Bjorn Millard
	 * @param datacube
	 * */
	public void addData(DataObject dataCube) {
		TheDataGroup.add(dataCube);
	}

	/**
	 * Adds the given datacubes to the meta group
	 * 
	 * @author Bjorn Millard
	 * @param ArrayList
	 *            <Data_1D> datacubes
	 * */
	public void addMeta(ArrayList<DataObject> dataCubes) {
		TheMetaGroup.addAll(dataCubes);
	}

	/**
	 * Adds the given datacube to the meta group
	 * 
	 * @author Bjorn Millard
	 * @param datacube
	 * */
	public void addMeta(DataObject dataCube) {
		TheMetaGroup.add(dataCube);
	}

	/**
	 * Adds the given File to the raw file group, but first converts it to a
	 * byte[]
	 * 
	 * @author Bjorn Millard
	 * @param File
	 * */
	public void addRaw(File fileToEncode) {
		byte[] arr = H5IO.toByteArray(fileToEncode);
		int len = arr.length;
		Byte[] arr2 = new Byte[len];
		for (int i = 0; i < len; i++)
			arr2[i] = new Byte(arr[i]);

		TheRawFileArrays.add(new Data_1D<Byte>(arr2, "BYTE", fileToEncode
				.getName()));
	}

	/**
	 * Adds the given byte[]'s to the raw file group
	 * 
	 * @author Bjorn Millard
	 * @param ArrayList
	 *            <byte[]> byte[] encoded files
	 * */
	public void addRaw(ArrayList<DataObject> byteArrayEncodedFiles) {
		TheRawFileArrays.addAll(byteArrayEncodedFiles);
	}

	/**
	 * Adds the given byte[] to the raw file group
	 * 
	 * @author Bjorn Millard
	 * @param byte[] encodedFile
	 * */
	public void addRaw(DataObject byteArrayEncodedFile) {
		TheRawFileArrays.add(byteArrayEncodedFile);
	}

	/**
	 * Returns the SDCube(HDF5) system file path that contains this data module
	 * 
	 * @author Bjorn Millard
	 * @return String FilePath
	 * */
	public String getFilePath_SDCube() {
		return FilePath_H5;
	}

	/**
	 * Returns the file path of this data module group relative to the SDCube
	 * file path
	 * 
	 * @author Bjorn Millard
	 * @return String FilePath
	 * */
	public String getFilePath_Group() {
		return FilePath_Group;
	}


	/**
	 * Returns all the DataModules
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<SDCube_DataModule>
	 * */
	public ArrayList<SDCube_DataModule> getDataModules() {
		return TheSubSamples;
	}

	/**
	 * Returns the DataModule at the given index
	 * 
	 * @author Bjorn Millard
	 * @param int index
	 * @return SDCube_DataModule
	 * */
	public SDCube_DataModule getDataModule(int index) {
		return TheSubSamples.get(index);
	}

	/**
	 * Returns all DataCubes in the DataGroup
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<SDCube_DataModule>
	 * */
	public ArrayList<DataObject> getDataGroup() {
		return TheDataGroup;
	}

	/**
	 * Returns all the DataCubes in the MetaGroup
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<SDCube_DataModule>
	 * */
	public ArrayList<DataObject> getMetaGroup() {
		return TheMetaGroup;
	}

	/**
	 * Returns all the byte[] arrays where each byte array represents a single
	 * file
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<byte[]>
	 * */
	public ArrayList<DataObject> getRawFileArrays() {
		return TheRawFileArrays;
	}
	/**
	 * Writes this DataModule object to the SDCube File already set 
	 * 
	 * @author Bjorn Millard
	 * @throws H5IO_Exception
	 * */
	public void write()
			throws H5IO_Exception {

		H5IO io = new H5IO();
		io.openHDF5(FilePath_H5);

		ArrayList<SDCube_DataModule> arr = TheSubSamples;
		int len = arr.size();
		for (int i = 0; i < len; i++)
 {
			SDCube_DataModule dm = arr.get(i);
			String path = dm.getFilePath_Group();

			dm.write(FilePath_H5);
		}

		// Writing the Data Group
		int numD = TheDataGroup.size();

		String path = FilePath_Group + "/Data";
		for (int i = 0; i < numD; i++) {
			DataObject dc = TheDataGroup.get(i);
			String dcPath = path + "/" + dc.getName();
			int rank = dc.getRank();	

			if(rank==1)
				io.writeDataset(FilePath_H5, dcPath, dc.getName(),
						((Data_1D) dc));
			else if (rank==2)
				io.writeDataset(FilePath_H5, dcPath, dc.getName(),
						((Data_2D) dc));
		}

		// Writing the Meta Group
		int numM = TheMetaGroup.size();
		path = FilePath_Group + "/Meta";
		for (int i = 0; i < numM; i++) {
			DataObject dc = TheMetaGroup.get(i);
			String dcPath = path + "/" + dc.getName();

			int rank = dc.getRank();
			if (rank == 1)
				io.writeDataset(FilePath_H5, dcPath, dc.getName(),
						((Data_1D) dc));
			else if (rank == 2)
				io.writeDataset(FilePath_H5, dcPath, dc.getName(),
						((Data_2D) dc));
		}

//		// Writing the Raw Group
//		int numR = TheRawFileArrays.size();
//		System.out.println("** WRITING " + numR + " rawObjects");
//		path = FilePath_Group + "/Raw";
//		for (int i = 0; i < numR; i++) {
//			DataObject dc = TheRawFileArrays.get(i);
//			String dcPath = path + "/" + dc.getName();
//			int rank = dc.getRank();
//			if (rank == 1)
		// io.writeDataset(FilePath_H5, dcPath, dc.getName(),
//						((Data_1D) dc));
		// }
		
		io.closeHDF5();

	}
	/**
	 * Writes this DataModule object to the given SDCube HDF5 file
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            pathToFile
	 * @throws H5IO_Exception
	 * */
	public void write(String filePathToSDCube)
			throws H5IO_Exception {

		 // Create the file if doesnt exist
		try {
			File f = new File(filePathToSDCube);
			if(!f.exists())
			f.createNewFile();
		 } catch (IOException e1) {
			 e1.printStackTrace();
		 }
		
		FilePath_H5 = filePathToSDCube;
		write();
	}
	/**
	 * Writes this DataModule object to the given SDCube HDF5 file path and Relative group path
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            pathToFile
	 * @throws H5IO_Exception
	 * */
	public void write(String filePathToSDCube, String relativeGroupPath)
			throws H5IO_Exception {

		 // Create the file if doesnt exist
		try {
			File f = new File(filePathToSDCube);
			if(!f.exists())
			f.createNewFile();
		 } catch (IOException e1) {
			 e1.printStackTrace();
		 }
		
		FilePath_H5 = filePathToSDCube;
		FilePath_Group = relativeGroupPath;

		write();
	}
	
	/**
	 *  When reading an SDCube file to create and SDCube Object, this loads all groups from the file into the Java Object.
	 *  @author Bjorn Millard
	 * @throws H5IO_Exception 
	 *  */
	public void load(H5IO io) throws H5IO_Exception
	{

		try {
			loadSamples(io);

		} catch (H5IO_Exception e) {
System.out.println("**Error loading Samples");
e.printStackTrace();
		}


		try {
			loadData(io);
		} catch (H5IO_Exception e) {
			System.out.println("**Error loading Data");
			e.printStackTrace();
		}
		try {
			loadMeta(io);
		} catch (H5IO_Exception e) {
			System.out.println("**Error loading Meta");
			e.printStackTrace();
		}
		

	}

	/**
	 * Loads all groups from the file into the Java Object.
	 * 
	 * @author Bjorn Millard
	 * @throws H5IO_Exception
	 * */
	public void loadSample(String sdcPath, String id) throws H5IO_Exception {
		H5IO io = new H5IO();
		// Find which sample refers to this ID, then reset the h5Group path to
		// that folder

		FilePath_H5 = sdcPath + "/Data.h5";
		FilePath_Group = null;
		io.openHDF5(FilePath_H5);
		int num = io.getGroupChildCount(FilePath_H5, "./Children");

		// Determine number of samples
		String childPath = "./Children";
		String[] names = io.getGroupChildNames(FilePath_H5, childPath);

		for (int i = 0; i < num; i++) {
			Data_2D dat = (Data_2D) io.readDataset(FilePath_H5, "./Children/"
					+ names[i]
					+ "/Meta/Sample_ID");
			String[][] data = (String[][]) dat.getData();
			if (data[0][0].trim().equalsIgnoreCase(id.trim())) {
				FilePath_Group = "./Children/" + names[i];
				break;
			}
		}
		io.closeHDF5();

		if (FilePath_Group == null)
			return;

		try {
			loadSamples(io);
		} catch (H5IO_Exception e) {
			System.out.println("**Error loading Samples");
			e.printStackTrace();
		}

		try {
			loadData(io);
		} catch (H5IO_Exception e) {
			System.out.println("**Error loading Data");
			e.printStackTrace();
		}
		try {
			loadMeta(io);
		} catch (H5IO_Exception e) {
			System.out.println("**Error loading Meta");
			e.printStackTrace();
		}
	}

	/**
	 * When reading an SDCube file to create and SDCube Object, this loads the
	 * Sample group from the file into the Java Object.
	 * 
	 * @author Bjorn Millard
	 * @throws H5IO_Exception
	 * */
	public void loadSamples(H5IO io) throws H5IO_Exception
	{
		// System.out.println("**Loading Samples for Level: "+FilePath_Group);
		//Determine number of samples
		String childPath = FilePath_Group + "/Children";
		String[] names = io.getGroupChildNames(FilePath_H5, childPath);

		if (names == null)
			return;
		int num = names.length;
		for (int i = 0; i < num; i++) {
			SDCube_DataModule mod = new SDCube_DataModule(FilePath_H5,
					FilePath_Group + "/Children/" + names[i]);
			mod.load(io);
			TheSubSamples.add(mod);
		}
	}

	/**
	 * When reading an SDCube file to create and SDCube Object, this loads the
	 * Meta group from the file into the Java Object.
	 * 
	 * @author Bjorn Millard
	 * @throws H5IO_Exception
	 * */
	public void loadMeta(H5IO io) throws H5IO_Exception
	{
		// System.out.println("**Loading Meta for Level: " + FilePath_Group);
		// Determine number of samples
		String childPath = FilePath_Group + "/Meta";
		String[] names = io.getGroupChildNames(FilePath_H5, childPath);
		if (names == null)
			return;

		for (int i = 0; i < names.length; i++)
 {
			TheMetaGroup.add(io.readDataset(FilePath_H5, FilePath_Group
					+ "/Meta/" + names[i]));
			if (names[i].equals("Sample_ID"))
				setId((String) ((Data_2D) TheMetaGroup.get(i)).getData()[0][0]);
		}

	}

	/**
	 * When reading an SDCube file to create and SDCube Object, this loads the
	 * Data group from the file into the Java Object.
	 * 
	 * @author Bjorn Millard
	 * @throws H5IO_Exception
	 * */
	public void loadData(H5IO io) throws H5IO_Exception
	{
		// Determine number of samples
		String childPath = FilePath_Group + "/Data";
		String[] names = io.getGroupChildNames(FilePath_H5, childPath);
		if (names == null)
			return;

		for (int i = 0; i < names.length; i++) {
			TheDataGroup.add(io.readDataset(FilePath_H5, FilePath_Group
					+ "/Data/" + names[i]));
		}
	}
	
	/**
	 * Returns the number of subsamples contained in the Samples Group
	 * @author Bjorn Millard
	 * @return int NumSamples
	 */
	public int getNumSamples()
	{
		try {
			return new H5IO().getGroupChildCount(FilePath_H5, FilePath_Group
					+ "/Children");
		} catch (H5IO_Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Returns a list of the subsample names contained in the Samples Group
	 * @athor Bjorn Millard
	 * @return String[]
	 */
	public String[] getSampleNames()
	{
		  try {

			return new H5IO().getGroupChildNames(FilePath_H5, FilePath_Group
					+ "/Children");
		} catch (H5IO_Exception e) {
			System.out.println("**ERROR getting sample names**");
			e.printStackTrace();
		}
		  
		return null;
	}

	/**
	 * Replaces a subsection of the relative H5 intrapath with a new path. This
	 * allows for renaming and moving paths around.
	 * 
	 * @author BLM
	 * @param String
	 *            oldPath
	 * @param String
	 *            newPath
	 * */
	public void replacePath(String oldPathFragment, String newPathFrag) {

		int len = TheSubSamples.size();
		for (int i = 0; i < len; i++)
			TheSubSamples.get(i).replacePath(oldPathFragment, newPathFrag);

		String st = FilePath_Group.replaceFirst(oldPathFragment,
				newPathFrag);
		FilePath_Group = st;

	}
}
