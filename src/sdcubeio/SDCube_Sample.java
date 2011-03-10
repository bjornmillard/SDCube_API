package sdcubeio;

import java.util.ArrayList;


public class SDCube_Sample {

	/** The Experimental Design half of the data sample */
	private ExpDesign_Sample TheExpDesign;
	/** The data half of the data sample */
	private SDCube_DataModule TheDataModule;
	/** Index of this sample within the SDCube */
	private String ID;

	/**
	 * Constructor
	 * 
	 * @author Bjorn Millard
	 * 
	 * */
	public SDCube_Sample(SDCube_DataModule data, ExpDesign_Sample expDesign,
			String id) {
		TheDataModule = data;
		TheExpDesign = expDesign;
		//Add the id to the meta group
		ID = id;
		String[] dataArr = new String[] {id+""};
		TheDataModule.addMeta(new Data_1D(dataArr, "String", "Sample_ID"));
	}

	/**
	 * Returns the ExpDesign Object
	 * 
	 * @author Bjorn Millard
	 * @return ExpDesign
	 * */
	public ExpDesign_Sample getExpDesign() {
		return TheExpDesign;
	}

	/**
	 * Returns the DataModule Object
	 * 
	 * @author Bjorn Millard
	 * @return ExpDesign
	 * */
	public SDCube_DataModule getDataModule() {
		return TheDataModule;
	}

	/**
	 * Returns the ID of this Sample
	 * 
	 * @author Bjorn Millard
	 * @return String ID
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Writes this sample object to the given SDCube DHF5 file
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            pathToFile
	 * @throws H5IO_Exception
	 * */
	public void write(String filePathToSDCube)
			throws H5IO_Exception
 {
		H5IO io = new H5IO();
		io.openHDF5(filePathToSDCube);
		io.createGroup(filePathToSDCube, "./Children");
		io.createGroup(filePathToSDCube, "./Meta");
		io.createGroup(filePathToSDCube, "./Data");
		io.createGroup(filePathToSDCube, "./Raw");
		io.closeHDF5();
		
		ArrayList<SDCube_DataModule> arr = TheDataModule.getDataModules();
		int len = arr.size();
		for (int i = 0; i < len; i++)
			arr.get(i).write(filePathToSDCube);

	}

	/**
	 * Get description of this object
	 */
	public String toString() {
		String st = ">>>>>>SAMPLE id: " + getID() + "<<<<<<<<\n";
		st += "//////////////////////////////////////////////\n";
		st += "//////////////////////////////////////////////\n";
		st += getExpDesign().toString();
		st += "----------------------------------------------\n";
		st += getDataModule().toString();
		st += "//////////////////////////////////////////////\n";
		st += "//////////////////////////////////////////////\n";
		st += "\n\n";
		return st;
	}

}
