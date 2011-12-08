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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;
/**
 * 
 * @author Bjorn Millard & Michael Menden
 * @param <T>
 * 
 */
public class H5IO<T> {
	
	private static final Logger logger = Logger.getLogger("hdf.HDFConnector");
	
	private int file_id      = -1;
	private int group_id     = -1;
	private int dataspace_id = -1;
	private int dataset_id   = -1;
	private int attr_id      = -1;
	private int memtype_id   = -1;
	private int filetype_id = -1;
	private int filespace_id = -1;
	private int datatype_id = -1;
	private int memspace_id  = -1;
	private int dcpl_id      = -1;

	/**
	 * Creates an HDF5 file at the file path given.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            filePath
	 * @return void
	 * @throws H5IO_Exception
	 */
	public void createHDF5(String fName) throws H5IO_Exception {
		try {
			file_id = H5.H5Fcreate(fName, HDF5Constants.H5F_ACC_TRUNC,
								   HDF5Constants.H5P_DEFAULT,
								   HDF5Constants.H5P_DEFAULT);
		}
		catch (HDF5LibraryException ex) {
			logger.log(Level.SEVERE, "Cannot create an HDF5file", ex);
			throw new H5IO_Exception("Cannot create an HDF5file: " + ex.getMessage());
		}
		finally {
			closeHDF5();
		}
	}

	/**
	 * Open the HDF5 file at the given file path.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            filePath
	 * @return void
	 * @throws H5IO_Exception
	 */
	public void openHDF5(String fName) throws H5IO_Exception {
		try {
			// Open file using the default properties.
			file_id = H5.H5Fopen(fName, HDF5Constants.H5F_ACC_RDWR,
					HDF5Constants.H5P_DEFAULT);
		}
		catch (HDF5LibraryException ex) {
			logger.log(Level.SEVERE, "Not able to open " + fName, ex);
			throw new H5IO_Exception("Not able to open " + fName + ": ");
		}
	}

	/**
	 * Returns the currently opened HDF5 file ID
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param null
	 * @return int FileID
	 * */
	public int getH5F_ID() {
		return file_id;
	}

	/**
	 * Opens the group at the given path. The root of this path is relative the
	 * currently opened HDF5 file path. **NOTE the parent HDF5 file must already
	 * be opened
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            pathToGroup
	 * @return void
	 * */
	public void openGroup(String pathToGroup) {
		try {
			// Open file using the default properties.
			group_id = H5.H5Gopen(getH5F_ID(), pathToGroup);
		}
		// Group does not exist
		catch (Exception e) {
			System.out.println("Group does not exist!!!");
			e.printStackTrace();
		}
	}

	/**
	 * Closes the currently opened group
	 * 
	 * @author Bjorn Millard
	 * @param null
	 * @return void
	 * */
	public void closeGroup() {
		try {
			if (group_id >= 0) {
				H5.H5Gclose(group_id);
				group_id = -1;
			}
		} catch (HDF5LibraryException ex) {

		}
	}

	/**
	 * Returns the number of children contained by the given group
	 * 
	 * @athor Bjorn Millard
	 * @param String
	 *            parentHDF5filePath, String pahtToGroupRelativeToHDF5path
	 * @return int NumberOfChildren
	 * @throws H5IO_Exception
	 */
	public int getGroupChildCount(String h5Path, String pathToGroup)
			throws H5IO_Exception {

		if (!existsGroup(h5Path, pathToGroup))
			return 0;

		openGroup(pathToGroup);


		long[] answer = new long[1];
		try {
			H5.H5Gget_num_objs(group_id, answer);
		} catch (HDF5LibraryException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		closeGroup();

		return (int) answer[0];
	}


	/**
	 * Returns a String[] with the names of the children within the given group
	 * 
	 * @athor Bjorn Millard
	 * @param String
	 *            parentHDF5filePath, String pahtToGroupRelativeToHDF5path
	 * @return String NamesOfChildren
	 * @throws H5IO_Exception
	 */
	public String[] getGroupChildNames(String h5Path, String pathToGroup)
			throws H5IO_Exception {

		int numC = getGroupChildCount(h5Path, pathToGroup);
		if (numC == 0)
			return null;

		openGroup(pathToGroup);

		String[] objNames = new String[numC];
		int[] objTypes = new int[numC];

		try {
			int names_found = H5.H5Gget_obj_info_all(file_id, pathToGroup,
					objNames, objTypes);
		} catch (Throwable err) {
			err.printStackTrace();
		}
		closeGroup();


		return objNames;
	}

	/**
	 * Checks if the given HDF file name exists
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            pathToHDF5file
	 * @return boolean Exists?
	 * 
	 */
	public boolean existHDF5(String filePath) {
		boolean result = false;
		try {
			// Open file using the default properties.
			file_id = H5.H5Fopen(filePath, HDF5Constants.H5F_ACC_RDWR,
					HDF5Constants.H5P_DEFAULT);
			result = true;
		}
		catch (HDF5LibraryException ex) {}
		return result;
	}

	/**
	 * Checks if the given group file name exists within the currently opened
	 * HDF5 file
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            pathToGroup within the currently opened HDF5 file
	 * @throws H5IO_Exception 
	 */
	public boolean existsGroup(String h5FilePath, String groupPath) throws H5IO_Exception {
		boolean result = false;
		closeGroup();
		try {
			group_id = H5.H5Gopen(getH5F_ID(), groupPath);
			if (group_id != -1)
			{
				closeGroup();
				return true;
			}
		} catch (Exception ex) {
		}
		finally{
		}
		return result;
	}

	// /**
	// * Checks if the given dataset name exists within the currently opened
	// HDF5
	// * file
	// *
	// * @author Bjorn Millard
	// * @param String
	// * pathToDatasetWithinOpenedHDF5file
	// */
	// public boolean existsDataset(String dsPath) {
	// boolean result = false;
	// try {
	// closeDataset();
	//		
	// dataset_id = H5.H5Dopen(getH5F_ID(), dsPath);
	// if (dataset_id != -1)
	// {
	// System.out.println("foundIT: " + dataset_id + "  " + dsPath);
	// closeDataset();
	// return true;
	// }
	// } catch (Exception ex) {
	// }
	// return result;
	// }

	/**
	 * Close the current HDF5 file.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param null
	 * @return void
	 * @throws H5IO_Exception
	 */
	public void closeHDF5() throws H5IO_Exception {
		try {
			if (file_id >= 0) {
				H5.H5Fflush(file_id, HDF5Constants.H5F_SCOPE_LOCAL);
				H5.H5Fclose(file_id);
				file_id = -1;
			}
		}
		catch (HDF5LibraryException ex) {
			logger.log(Level.SEVERE, "Not able to close the file", ex);
			throw new H5IO_Exception("Not able to close the file: " + ex.getMessage());
		}
	}

	/**
	 * Creates a String dataset with the given StringBuffer data array
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            pathToDatasetWRTcurrentlyOpenedHDF5file, StringBuffer[]
	 *            dataToWrite
	 * @return void
	 * 
	 * @throws H5IO_Exception
	 */
	public void writeStringDataset(String datasetPath, StringBuffer[] text)
			throws H5IO_Exception {
		try {
			// delete existing dataset
			if (existsDataset(datasetPath))
				removeDataset(datasetPath);

			int dim0 = text.length;

			// maximal size of String
			int maxTextSize = 0;
			for (int i = 0; i < dim0; i++) {
				if (text[i].length() > maxTextSize)
					maxTextSize = text[i].length() + 1;
			}
			long[] dims = { dim0 };
			byte[][] dset_data = new byte[dim0][maxTextSize];
			// Create file and memory dataset types. We will save
			// the strings as FORTRAN strings, therefore they do not need space
			// for the null terminator in the file.
			filetype_id = H5.H5Tcopy(HDF5Constants.H5T_FORTRAN_S1);
			H5.H5Tset_size(filetype_id, maxTextSize - 1);
			memtype_id = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
			H5.H5Tset_size(memtype_id, maxTextSize);
			// Create memory space. Setting maximum size to NULL sets the
			// maximum
			// size to be the current size.
			memspace_id = H5.H5Screate_simple(dims.length, dims, null);
			// Create the String dataset set
			dataset_id = H5.H5Dcreate(file_id, datasetPath, filetype_id,
					memspace_id, HDF5Constants.H5P_DEFAULT);
			// Write the dataset to the dataset set.
			for (int indx = 0; indx < dim0; indx++) {
				for (int jndx = 0; jndx < maxTextSize; jndx++) {
					if (jndx < text[indx].length())
						dset_data[indx][jndx] = (byte) text[indx].charAt(jndx);
					else
						dset_data[indx][jndx] = 0;
				}
			}
			H5.H5Dwrite(dataset_id, memtype_id, HDF5Constants.H5S_ALL,
					HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, dset_data);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot create '" + datasetPath, ex);
			throw new H5IO_Exception("Cannot create '" + datasetPath + "': "
					+ ex.getMessage());
		} finally {
			// End access to the dataset set and release resources used by it.
			closeDataset();
			// Terminate access to the memory space.
			closeMemSpace();
			// Terminate access to the file type.
			closeFileType();
			// Terminate access to the memory type.
			closeMemType();
		}
	}

	/**
	 * Creates a multidimensional dataset with unlimited dimensions. **NOTE:
	 * datasetType can be: "INT", "SHORT", "FLOAT", "DOUBLE" or "BYTE"
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            datasetPath, String datasetType, long[] HDF5ChunkingSize
	 * @return void
	 * @throws H5IO_Exception
	 */
	public void createDataset_unlimited(String datasetName, String type, long chunk[]) throws H5IO_Exception {
		try {
			// delete existing dataset
			if (existsDataset(datasetName))
				removeDataset(datasetName);

			long maxDims[] = new long[chunk.length];
			long counts[] = new long[chunk.length];
			long offsets[] = new long[chunk.length];
			for (int i=0; i<chunk.length; i++) {
				maxDims[i] = HDF5Constants.H5S_UNLIMITED;
				counts[i] = 1;
				offsets[i] = 0;
			}
			// Create the memory space with unlimited dimensions.
			memspace_id = H5.H5Screate_simple (chunk.length, counts, maxDims);
			// Modify dataset set creation properties and enable chunking
			dcpl_id = H5.H5Pcreate (HDF5Constants.H5P_DATASET_CREATE);
			H5.H5Pset_chunk ( dcpl_id, chunk.length, chunk);

			// Select the HDF type. If wrong values are stored in hyper cube,
			// this may be the reason!
			int hdfType = -1;
			if (type.toUpperCase().compareTo("INT") == 0 || type.toUpperCase().compareTo("INTEGER") == 0)
				hdfType = HDF5Constants.H5T_NATIVE_INT;
			else if (type.toUpperCase().compareTo("SHORT") == 0)
				hdfType = HDF5Constants.H5T_NATIVE_SHORT;
			else if (type.toUpperCase().compareTo("FLOAT") == 0)
				hdfType = HDF5Constants.H5T_NATIVE_FLOAT;
			else if (type.toUpperCase().compareTo("DOUBLE") == 0)
				hdfType = HDF5Constants.H5T_NATIVE_DOUBLE;
			else if (type.toUpperCase().compareTo("BYTE") == 0)
				hdfType = HDF5Constants.H5T_NATIVE_CHAR;
			// Create a new dataset set within the file using properties list.
		    dataset_id = H5.H5Dcreate (file_id, datasetName, hdfType, memspace_id, dcpl_id);
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot create '" + datasetName, ex);
			throw new H5IO_Exception("Cannot create '" + datasetName + "': " + ex.getMessage());
		}
		finally {
			closeDataset();
		    closeMemSpace();
		    closeFileSpace();
		    closePropertyList();
		}
	}

	/**
	 * Creates a multidimensional dataset with limited dimensions. **NOTE:
	 * datasetType can be: "INT", "SHORT", "FLOAT", "DOUBLE" or "BYTE"
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * 
	 * @param String
	 *            datasetPath, String datasetType, long[] maximalDimensionsSizes
	 * @throws H5IO_Exception
	 */
	public void createDataset(String datasetPath, String type, long[] maxDims)
			throws H5IO_Exception {
		try {
			// delete existing dataset
			if (existsDataset(datasetPath))
				removeDataset(datasetPath);


			long counts[] = new long[maxDims.length];
			long offsets[] = new long[maxDims.length];
			for (int i=0; i<maxDims.length; i++) {
				counts[i] = 1;
				offsets[i] = 0;
			}
			// Create the memory space with limited dimensions.
			memspace_id = H5.H5Screate_simple (maxDims.length, maxDims, null);
			// Select the HDF type. If wrong values are stored in hyper cube, this may the reason!
			int hdfType = -1;
			if (type.toUpperCase().compareTo("INT") == 0 || type.toUpperCase().compareTo("INTEGER") == 0)
				hdfType = HDF5Constants.H5T_NATIVE_INT;
			else if (type.toUpperCase().compareTo("SHORT") == 0)
				hdfType = HDF5Constants.H5T_NATIVE_SHORT;
			else if (type.toUpperCase().compareTo("FLOAT") == 0)
				hdfType = HDF5Constants.H5T_NATIVE_FLOAT;
			else if (type.toUpperCase().compareTo("DOUBLE") == 0)
				hdfType = HDF5Constants.H5T_NATIVE_DOUBLE;
			else if (type.toUpperCase().compareTo("BYTE") == 0)
				hdfType = HDF5Constants.H5T_NATIVE_CHAR;
			// Create a new dataset set within the file using properties list.
			dataset_id = H5.H5Dcreate(file_id, datasetPath, hdfType,
					memspace_id, HDF5Constants.H5P_DEFAULT);
		}
		catch (Exception ex) {
			// logger.log(Level.SEVERE, "Cannot create '" + datasetName, ex);
			throw new H5IO_Exception("Cannot create '" + datasetPath + "': "
					+ ex.getMessage());
		}
		finally {
			closeDataset();
		    closeMemSpace();
		    closeFileSpace();
		    closePropertyList();
		}
	}

	/**
	 * Writes a one dimensional dataset (Array) to a multidimensional dataset.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            datasetPath, DataObject dataObjectToWrite, int
	 *            dimensionToWriteTo, long[] offsetsWithinDataset
	 * @throws H5IO_Exception
	 */
	public void writeArray(String datasetName, DataObject dat1d, int dim0, long[] offsets) throws H5IO_Exception {
		try {
			// Open an existing multidimensional dataset set.
			dataset_id = H5.H5Dopen(file_id, datasetName);
			// Get the file space
			filespace_id = H5.H5Dget_space(dataset_id);
			// Get the dimensions of the multidimensional dataset set
			long rankMD = H5.H5Sget_simple_extent_ndims(filespace_id);
			long[] dimsMD = new long[(int)rankMD];
			H5.H5Sget_simple_extent_dims(filespace_id, dimsMD, null);
			// Set counts for multidimensional dataset set
			long count[] = new long[(int)rankMD];
			for (int i=0; i<rankMD; i++) {
				count[i] = 1;
			}
			count[dim0] = dat1d.getDimensions()[0];
			// Check if necessary to extend multidimensional dataset set.
			boolean extend = false;
			long[] size1D = new long[] { offsets[dim0] + dat1d.getDimensions()[0] };
			// dim0 has to grow.
			if (dimsMD[dim0] < size1D[0]) {
				dimsMD[dim0] = size1D[0];
				extend = true;
			}
			// Another dimension of the multidimensional dataset set has to
			// grow.
			for (int i=0; i<rankMD; i++) {
				if (dimsMD[i] < offsets[i] + 1) {
					dimsMD[i] = offsets[i] + 1;
					extend = true;
				}
			}
			// Extend the multidimensional dataset set if necessary.
			if (extend) {
				H5.H5Dextend (dataset_id, dimsMD);
				closeFileSpace();
			    filespace_id = H5.H5Dget_space (dataset_id);
			}
			// Select hyperslab.
		    H5.H5Sselect_hyperslab (filespace_id, HDF5Constants.H5S_SELECT_SET, offsets, null,
		    					   	count, null);
		    
		    // Define memory space
			long[] count1D = new long[] { dat1d.getDimensions()[0] };
		    memspace_id = H5.H5Screate_simple (1, count1D, null);
			// Write the dataset from hyperslab to file.
		    H5.H5Dwrite (dataset_id, dat1d.getHDFType(), memspace_id, filespace_id,
		                 HDF5Constants.H5P_DEFAULT, ((Data_1D)dat1d).getData());

		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot write to " + datasetName, ex);
			throw new H5IO_Exception("Cannot write to '" + datasetName + "': " + ex.getMessage());
		}
		finally {
			// Close resources
			closeDataset();
		    closeMemSpace();
		    closeFileSpace();
		}
	}

	/**
	 * Read an one dimensional datset (Array) out of a multidimensional dataset.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            datasetPathToRead, int dimensionalLocationOfArray, long[]
	 *            offsetsWithinDataset, long countOfArrayLength
	 * @return DataObject dataObjectRead
	 * @throws H5IO_Exception
	 */
	public Data_1D readArray(String datasetPath, int dim0, long offset,
			long count0) throws H5IO_Exception {
		Data_1D result = null;
		try {
			// Open an existing dataset set.
			dataset_id = H5.H5Dopen(file_id, datasetPath);
		    filespace_id = H5.H5Dget_space (dataset_id);
			String datasetName = getDatasetName(datasetPath);

		    // select hyperslab
		    H5.H5Sselect_hyperslab(filespace_id,  HDF5Constants.H5S_SELECT_SET,
					new long[] { offset }, null, new long[] { count0 }, null);
		    
		    // Define memory space
		    memspace_id = H5.H5Screate_simple (1, new long[]{count0}, null);
 
			filetype_id = H5.H5Dget_type(dataset_id);
			if (H5.H5Tget_class(filetype_id) == HDF5Constants.H5T_FLOAT) {
		    	// Float
				if (H5.H5Tget_size(filetype_id) == 4) {
		    		Float data_out[] = new Float [(int) count0];
			    	H5.H5Dread (dataset_id, filetype_id, memspace_id, filespace_id,
					  		HDF5Constants.H5P_DEFAULT, data_out);
					result = new Data_1D<Float>(data_out, Data_1D.FLOAT,
							datasetName);
		    	}
				// Double
		    	else if (H5.H5Tget_size(filetype_id) == 8) {
		    		Double data_out[] = new Double [(int) count0];
			    	H5.H5Dread (dataset_id, filetype_id, memspace_id, filespace_id,
					  		HDF5Constants.H5P_DEFAULT, data_out);
					result = new Data_1D<Double>(data_out, Data_1D.DOUBLE,
							datasetName);
		    	}
		    }
		    else if (H5.H5Tget_class(filetype_id) == HDF5Constants.H5T_INTEGER) {
		    	// Byte
		    	if (H5.H5Tget_size(filetype_id) == 1) {
		    		Byte data_out[] = new Byte [(int) count0];
			    	H5.H5Dread (dataset_id, filetype_id, memspace_id, filespace_id,
					  		HDF5Constants.H5P_DEFAULT, data_out);
					result = new Data_1D<Byte>(data_out, Data_1D.BYTE,
							datasetName);
		    	}
		    	// Short
		    	else if (H5.H5Tget_size(filetype_id) == 2) {
		    		Short data_out[] = new Short [(int) count0];
			    	H5.H5Dread (dataset_id, filetype_id, memspace_id, filespace_id,
					  		HDF5Constants.H5P_DEFAULT, data_out);
					result = new Data_1D<Short>(data_out, Data_1D.SHORT,
							datasetName);
		    	}
		    	// Integer
		    	else if (H5.H5Tget_size(filetype_id) == 4) {
		    		Integer data_out[] = new Integer [(int) count0];
			    	H5.H5Dread (dataset_id, filetype_id, memspace_id, filespace_id,
					  		HDF5Constants.H5P_DEFAULT, data_out);
					result = new Data_1D<Integer>(data_out, Data_1D.INTEGER,
							datasetName);
		    	}
		    }
		}
		catch (Exception ex) {
//			logger.log(Level.SEVERE, "Cannot read " + datasetName, ex);
			throw new H5IO_Exception("Cannot read '" + datasetPath + "': "
					+ ex.getMessage());
		}
		finally {
			// End access to the dataset set and release resources used by it.
		    closeDataset();
		    closeFileSpace();
		    closeFileType();
		    closeMemSpace();
		}
		return result;
	}

	/**
	 * Writes a two dimensional dataset (Matrix) to a multidimensional HDF5
	 * dataset.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            datasetPath, DataTwoDim dataObjectToWrite, int dimension1, int
	 *            dimension2, long[] offsetsWithinDimensionsOfDataset
	 * @throws H5IO_Exception
	 */
	public void writeMatrix(String datasetName, Data_2D dat2d, int dim0, int dim1, long[] offsets) throws H5IO_Exception {
		try {
			// Open an existing multidimensional dataset set.
			dataset_id = H5.H5Dopen(file_id, datasetName);
			// Get the file space
			filespace_id = H5.H5Dget_space(dataset_id);
			// Get the dimensions of the multidimensional dataset set
			long rankMD = H5.H5Sget_simple_extent_ndims(filespace_id);
			long[] dimsMD = new long[(int)rankMD];
			H5.H5Sget_simple_extent_dims(filespace_id, dimsMD, null);
			// Set counts for multidimensional dataset set
			long count[] = new long[(int)rankMD];
			for (int i=0; i<rankMD; i++) {
				count[i] = 1;
			}
			long[] count2D = dat2d.getDimensions();
			count[dim0] = count2D[0];
			count[dim1] = count2D[1];
			// Check if necessary to extend multidimensional dataset set.
			boolean extend = false;
			long size2D[] = new long[2];
			// long[] offsetMD = dc.getOffsets();
			size2D[0] = offsets[dim0] + count2D[0];
			size2D[1] = offsets[dim1] + count2D[1];
			// dim0 has to grow.
			if (dimsMD[dim0] < size2D[0]) {
				dimsMD[dim0] = size2D[0];
				extend = true;
			}
			// dim1 has to grow.
			if (dimsMD[dim1] < size2D[1]) {
				dimsMD[dim1] = size2D[1];
				extend = true;
			}
			// Another dimension of the multidimensional dataset set has to
			// grow.
			for (int i=0; i<rankMD; i++) {
				if (dimsMD[i] < offsets[i] + 1) {
					dimsMD[i] = offsets[i] + 1;
					extend = true;
				}
			}
			// Extend the multidimensional dataset set if necessary.h
			if (extend) {
				H5.H5Dextend (dataset_id, dimsMD);
				closeFileSpace();
			    filespace_id = H5.H5Dget_space (dataset_id);
			}
			// Select hyperslab.
		    H5.H5Sselect_hyperslab (filespace_id, HDF5Constants.H5S_SELECT_SET, offsets, null,
		    					   	count, null);
		    // Define memory space
			memspace_id = H5.H5Screate_simple(2, dat2d.getDimensions(), null);
			// Write the dataset from hyperslab to file.
		    H5.H5Dwrite (dataset_id, dat2d.getHDFType(), memspace_id, filespace_id,
		                 HDF5Constants.H5P_DEFAULT, dat2d.getData());

		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot write to " + datasetName, ex);
			throw new H5IO_Exception("Cannot write to '" + datasetName + "': " + ex.getMessage());
		}
		finally {
			// Close resources
			closeDataset();
		    closeMemSpace();
		    closeFileSpace();
		}
	}

	/**
	 * Extracts out the name of the dataset from the path
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            datasetPath
	 * @return String datasetName
	 * */
	public String getDatasetName(String path) {
		String name = null;
		int len = path.length();
		for (int i = 1; i < len; i++) {
			if (path.substring(len - 1 - i, len - i).equals("/"))
				return path.substring(len - i, len);
		}
		return name;
	}
	/**
	 * Creates all groups that are parent to the given object path if they don't already exist
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            objectPath
	 * @throws H5IO_Exception 
	 * */
	 public void createAllParentGroups(String h5FilePath, String pathToChildObject) throws H5IO_Exception {
		String path = pathToChildObject;
		int childStartIndex = 0;
		int len = path.length();
		//Getting index where child begins
		for (int i = 1; i < len; i++) 
			if (path.substring(len - 1 - i, len - i).equals("/"))
			{
				childStartIndex =  len-i;
				break;
			}
	
		//Find all "/"'s and create groups if they dont exist
		for (int i = 2; i < childStartIndex; i++) {
			if (path.substring(i, i+1).equals("/"))
			{
				String pathP = path.substring(0,i+1);
				if(!existsGroup(h5FilePath, pathP))
				{
					createGroup(h5FilePath, pathP);
				}
			}
		}
		
	}

	/**
	 * Reads a two dimensional dataset (matrix) out of a larger multidimensional
	 * dataset.
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            datasetPath, int dimension1, int dimension2, long[] offsets,
	 *            long countLengthOfDim1, long countLengthOfDim2
	 * @return DataTwoDim DataObjectRead
	 * @throws Exception
	 */
	public Data_2D readMatrix(String hdfFilePath, String datasetPath,
 long[] offsets, long count[])
			throws Exception {

		Data_2D result = null;
		try {
			int count0 = (int) count[0];
			int count1 = (int) count[1];
			String datasetName = getDatasetName(datasetPath);

			dataset_id = H5.H5Dopen(file_id, datasetPath);
			datatype_id = H5.H5Dget_type(dataset_id);

			if (H5.H5Tget_class(datatype_id) == HDF5Constants.H5T_FLOAT) {
		    	// Float
				if (H5.H5Tget_size(datatype_id) == 4) {
					Float data_out[][] = new Float[(int) count0][(int) count1];
					H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_FLOAT,
							HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
					  		HDF5Constants.H5P_DEFAULT, data_out);
					result = new Data_2D<Float>(data_out, "FLOAT", datasetName);
		    	}
				// Double
				else if (H5.H5Tget_size(datatype_id) == 8) {
					Double data_out[][] = new Double[(int) count0][(int) count1];
					H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_DOUBLE,
							HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
					  		HDF5Constants.H5P_DEFAULT, data_out);
					
					result = new Data_2D<Double>(data_out, "DOUBLE",
							datasetName);
		    	}
		    }
 else if (H5.H5Tget_class(datatype_id) == HDF5Constants.H5T_INTEGER) {
		    	// Byte
				if (H5.H5Tget_size(datatype_id) == 1) {
					Byte data_out[][] = new Byte[(int) count0][(int) count1];

					H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_CHAR,
							HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
					  		HDF5Constants.H5P_DEFAULT, data_out);
			
					result = new Data_2D<Byte>(data_out, "BYTE", datasetName);
				}
		    	// Short
				else if (H5.H5Tget_size(datatype_id) == 2) {
					Short data_out[][] = new Short[(int) count0][(int) count1];
					H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_SHORT,
							HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
					  		HDF5Constants.H5P_DEFAULT, data_out);

					result = new Data_2D<Short>(data_out, "SHORT", datasetName);
		    	}
		    	// Integer
				else if (H5.H5Tget_size(datatype_id) == 4) {
					Integer data_out[][] = new Integer[(int) count0][(int) count1];
					H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_INT,
							HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL,
					  		HDF5Constants.H5P_DEFAULT, data_out);
					result = new Data_2D<Integer>(data_out, "INTEGER",
							datasetName);
		    	}
		    }
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot read " + datasetPath, ex);
			throw new Exception("Cannot read '" + datasetPath + "': "
					+ ex.getMessage());
		}
		finally {
			// End access to the data set and release resources used by it.
		    closeDataset();
		    closeFileSpace();
		    closeFileType();
		    closeMemSpace();
		}
		return result;

	}

	/**
	 * Writes an attribute to a dataset.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            DatasetPath, String AttributeName, String AttributeDescription
	 * @throws H5IO_Exception
	 */
	public void writeAttribute(String datasetName, String attrName, String attrDesc) throws H5IO_Exception {

		try {
			// Open an existing dataset set.
			dataset_id = H5.H5Dopen(file_id, datasetName);
			// create memory space
			long[] tmp = { 1};
			memspace_id = H5.H5Screate_simple (1, tmp, null);
			// Create memory dataset types.
			memtype_id = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
			H5.H5Tset_size(memtype_id, attrDesc.length());
			// Create attribute.
			attr_id = H5.H5Acreate(dataset_id, attrName, memtype_id, memspace_id, HDF5Constants.H5P_DEFAULT);
			// Write attribute to dataset.
			byte[] attr = new byte[attrDesc.length()];
			for (int i = 0; i<attrDesc.length(); i++) {
				attr[i] = (byte) attrDesc.charAt(i);
			}
			H5.H5Awrite(attr_id, memtype_id, attr);
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot write " + attrName + " to " + datasetName, ex);
			throw new H5IO_Exception("Cannot write " + attrName + " to " + datasetName+ "': " + ex.getMessage());
		}
		finally {
			// Close resources
			closeDataset();
			closeMemSpace();
		    closeMemType();
		    closeAttribute();
		}
	}

	/**
	 * Read attributes from a dataset set.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            datasetPath
	 * @return String[][] Multi-AttributeText
	 * 
	 * @throws H5IO_Exception
	 */
	public String[][] readAttribute(String datasetName) throws H5IO_Exception {
		String[][] attributes = null;
		try {
			// Open an existing dataset set.
			dataset_id = H5.H5Dopen(file_id, datasetName);
			// Iterates over all attributes.
			attributes = new String[H5.H5Aget_num_attrs(dataset_id)][2];
			for (int i=0; i<H5.H5Aget_num_attrs(dataset_id); i++) {
				// Get attribute.
				attr_id = H5.H5Aopen_idx(dataset_id, i);
				// Create memory dataset types.
				memtype_id = H5.H5Aget_type( attr_id);
				// Get the name of the attribute.
				String[] attrName = new String[1];
				// WARNING: 200 is the max length of an attribute
				//          name which could be read...
				H5.H5Aget_name(attr_id, 200, attrName);
				attributes[i][0] = attrName[0];
				// Read an Attribute.
				byte[] attr = new byte[H5.H5Tget_size(memtype_id)];
				H5.H5Aread(attr_id, memtype_id, attr);
				attributes[i][1] = new String(attr);
			}
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot attributes from " + datasetName, ex);
			throw new H5IO_Exception("Cannot attributes from " + datasetName + ": " + ex.getMessage());
		}
		finally {
			// Close resources
			closeDataset();
		    closeMemType();
		    closeAttribute();
		}
		return attributes;
	}

	/**
	 * Get the dimensions of a multidimensional dataset.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            datasetPath
	 * @return long[] dimensionsOfMulti-dimensionalDataset
	 * 
	 * @throws H5IO_Exception
	 */
	public long[] getDimensions(String datasetName) throws H5IO_Exception {
		long dimsMD[];
		try {
			// Open an existing dataset set.
		    dataset_id = H5.H5Dopen (file_id, datasetName);
		    filespace_id = H5.H5Dget_space (dataset_id);
		    // Get the dimensions.
		    int rank = H5.H5Sget_simple_extent_ndims (filespace_id);
		    dimsMD = new long[rank];
		    H5.H5Sget_simple_extent_dims (filespace_id, dimsMD, null);
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "No access " + datasetName, ex);
			throw new H5IO_Exception("No access to '" + datasetName + "': " + ex.getMessage());
		}
		finally {
			// End access to the dataset set and release resources used by it.
		    closeDataset();
		    closeFileSpace();
		}
	    return dimsMD;
	}

	/**
	 * Get the java data type of the multidimensional dataset. **NOTE: possible
	 * outputs are: "Float", "Double", "Byte", "Short" or "Integer"
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            datsetPath
	 * @return String DataTypeName
	 * @throws H5IO_Exception
	 */
	public String getDataType( String datasetName) throws H5IO_Exception {
		String result = "no suggestion";
		try {
			dataset_id = H5.H5Dopen (file_id, datasetName);
			filetype_id = H5.H5Dget_type(dataset_id);

		    if (H5.H5Tget_class(filetype_id) == HDF5Constants.H5T_FLOAT) {
		    	if (H5.H5Tget_size(filetype_id) == 4) {
		    		result = "Float";
		    	}
		    	else if (H5.H5Tget_size(filetype_id) == 8) {
		    		result = "Double";
		    	}
		    }
		    else if (H5.H5Tget_class(filetype_id) == HDF5Constants.H5T_INTEGER) {
		    	if (H5.H5Tget_size(filetype_id) == 1) {
		    		result = "Byte";
		    	}
		    	else if (H5.H5Tget_size(filetype_id) == 2) {
		    		result = "Short";
		    	}
		    	else if (H5.H5Tget_size(filetype_id) == 4) {
		    		result = "Integer";
		    	}
 else if (H5.H5Tget_size(filetype_id) == 3) {
					result = "String";
				}
		    }
 else if (H5.H5Tget_class(filetype_id) == HDF5Constants.H5T_STRING) {
				result = "String";
			}

		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot read " + datasetName, ex);
			throw new H5IO_Exception("Cannot read '" + datasetName + "': " + ex.getMessage());
		}
		finally {
			// End access to the dataset set and release resources used by it.
		    closeDataset();
		    closeFileType();
		}
		return result;
	}

	/**
	 * Determine if a dataset is member of the HDF5 file.
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            DatasetPath
	 * @return boolean Member?
	 * 
	 * @throws H5IO_Exception
	 */
	public boolean existsDataset(String dsPath)
			throws H5IO_Exception {
		boolean result = true;
		try {
			dataset_id = H5.H5Dopen(file_id, dsPath);
		}
		catch (Exception ex) {
			result = false;
		}
		finally {
			closeDataset();
		}
		return result;
	}

	/**
	 * Removes a dataset or group from the hierarchy.
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            DatasetPath
	 * @return void
	 */
	public void removeDataset(String datasetName) {
		try {
			H5.H5Gunlink(file_id, datasetName);
			logger.log(Level.CONFIG, "Successfully unlinked/droped" + datasetName);
		}
		// Data set does not exist.
		catch (HDF5LibraryException ex) {
			logger.log(Level.CONFIG, "Dataset currently not exist...");
		}
	}

	/**
	 * Close the currently opened dataset.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param null
	 * @return void
	 * @throws H5IO_Exception
	 */
	private void closeDataset() throws H5IO_Exception {
		try {
			if (dataset_id >= 0){
				H5.H5Dclose(dataset_id);
				dataset_id = -1;
			}
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot close the dataset", ex);
			throw new H5IO_Exception("Cannot close the dataset: " + ex.getMessage());
		}
	}

	/**
	 * Close the memory type.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param null
	 * @return void
	 * @throws H5IO_Exception
	 */
	private void closeMemType() throws H5IO_Exception {
		try {
			if (memtype_id >= 0) {
				H5.H5Tclose(memtype_id);
				memtype_id = -1;
			}
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot close the memtype", ex);
			throw new H5IO_Exception("Cannot close the memtype: " + ex.getMessage());
		}
	}

	/**
	 * Close the file type.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param null
	 * @return void
	 * @throws H5IO_Exception
	 */
	private void closeFileType() throws H5IO_Exception {
		try {
			if (filetype_id >= 0) {
				H5.H5Tclose(filetype_id);
				filetype_id = -1;
			}
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot close the filetype", ex);
			throw new H5IO_Exception("Cannot close the filetype: " + ex.getMessage());
		}
	}

	/**
	 * Close the file space.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param null
	 * @return void
	 * @throws H5IO_Exception
	 */
	private void closeFileSpace() throws H5IO_Exception {
		try {
			if (filespace_id >= 0) {
				H5.H5Sclose(filespace_id);
				filespace_id = -1;
			}
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot close the file space", ex);
			throw new H5IO_Exception("Cannot close the file space: " + ex.getMessage());
		}
	}

	/**
	 * Close the memory space.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param null
	 * @return void
	 * @throws H5IO_Exception
	 */
	private void closeMemSpace() throws H5IO_Exception {
		try {
			if (memspace_id >= 0) {
				H5.H5Sclose(memspace_id);
				memspace_id = -1;
			}
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot close the property list", ex);
			throw new H5IO_Exception("Cannot close the property list: " + ex.getMessage());
		}
	}

	/**
	 * Close the property list.
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param null
	 * @return void
	 * @throws H5IO_Exception
	 */
	private void closePropertyList() throws H5IO_Exception {
		try {
			if (dcpl_id >= 0) {
				H5.H5Pclose(dcpl_id);
				dcpl_id = -1;
			}
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot close the property list", ex);
			throw new H5IO_Exception("Cannot close the property list: " + ex.getMessage());
		}
	}

	/**
	 * Close the attribute
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param null
	 * @return void
	 * @throws H5IO_Exception
	 */
	private void closeAttribute() throws H5IO_Exception {
		try {
			if (attr_id >= 0) {
				H5.H5Aclose(attr_id);
				attr_id = -1;
			}
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot close the attribute", ex);
			throw new H5IO_Exception("Cannot close the attribute: "
					+ ex.getMessage());
		}
	}

	/**
	 * Creates the 4 HDF5 groups that represent a data module tier: Data, Raw,
	 * Meta, Children.
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            HDF5filePath, String pathOfParentGroup groupName
	 * @return void
	 * @throws H5IO_Exception
	 */
	public void createDataModule_Skeleton(String hdfPath,
			String pathOfParentGroup) throws H5IO_Exception {
		createGroup(hdfPath, pathOfParentGroup + "/Data");
		createGroup(hdfPath, pathOfParentGroup + "/Meta");
		createGroup(hdfPath, pathOfParentGroup + "/Raw");
		createGroup(hdfPath, pathOfParentGroup + "/Children");
	}

	/**
	 * Creates a group.
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            HDF5filePath, String pathOfGroupInsideHDF5file groupName
	 * @return void
	 * @throws H5IO_Exception
	 */
	public void createGroup(String h5FilePath, String groupPathInsideFile)
			throws H5IO_Exception {


		//Creating parent groups if dont already exist
		createAllParentGroups(h5FilePath,groupPathInsideFile);
		

		try {
			try {
				group_id = H5.H5Gopen(file_id, groupPathInsideFile);
			}
			// Group does not exist till now.
			catch (Exception e) {
				group_id = H5.H5Gcreate(file_id, groupPathInsideFile,
						HDF5Constants.H5P_DEFAULT);
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot create the '"
					+ groupPathInsideFile + "' folder", ex);
			throw new H5IO_Exception("Cannot create the '"
					+ groupPathInsideFile
					+ "' folder: " + ex.getMessage());
		}
		// Close Group.
		finally {
			try {
				if (group_id >= 0) {
					H5.H5Gclose(group_id);
					group_id = -1;
				}
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Cannot close the folder", ex);
				throw new H5IO_Exception("Cannot close the folder: "
						+ ex.getMessage());
			}

		}
	}


	/**
	 * Writes a primitive float[] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, float[] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
			 float[] in) throws H5IO_Exception {
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);

		String datasetName = getDatasetName(path);

		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		Float[] dats = new Float[len];
		for (int i = 0; i < len; i++)
			dats[i] = new Float(in[i]);


		DataObject dataArray = new Data_1D<Float>(dats, Data_1D.FLOAT,
				datasetName);
		long[] dims = dataArray.getDimensions();
		createDataset(datasetPath, "Float", dims);
		writeArray(path, dataArray, 0, new long[] { 0 });

		// Add dataset type attributes:
		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_FLOAT");

	}

	/**
	 * Writes a primitive float[][] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, float[][] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
			 float[][] in) throws H5IO_Exception {
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);

		String datasetName = getDatasetName(path);

		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		int len2 = in[0].length;
		Float[][] dats = new Float[len][len2];
		for (int i = 0; i < len; i++)
			for (int j = 0; j < len2; j++)
				dats[i][j] = new Float(in[i][j]);

		Data_2D<Float> data = new Data_2D<Float>(dats, "FLOAT", datasetName);
		long[] dims = data.getDimensions();
		createDataset(datasetPath, "Float", dims);

		// Parameter: datasetName, Data_2D, dim0, dim1, offsets
		writeMatrix(datasetPath, data, 0, 1, new long[] { 0, 0 });
		// Add dataset type attributes:
		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_FLOAT");

	}

	/**
	 * Writes a primitive double[] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, double[] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
 double[] in)
			throws H5IO_Exception {
	
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);

		String datasetName = getDatasetName(path);

		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		Double[] dats = new Double[len];
		for (int i = 0; i < len; i++)
			dats[i] = new Double(in[i]);

		DataObject dataArray = new Data_1D<Double>(dats, Data_1D.DOUBLE,
				datasetName);
		long[] dims = dataArray.getDimensions();
		createDataset(datasetPath, "Double", dims);
		writeArray(path, dataArray, 0, new long[] { 0 });


		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_DOUBLE");
	}

	/**
	 * Writes a primitive double[][] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, double[][]
	 *            dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
			double[][] in) throws H5IO_Exception {
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);

		String datasetName = getDatasetName(path);
		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		int len2 = in[0].length;
		Double[][] dats = new Double[len][len2];
		for (int i = 0; i < len; i++)
			for (int j = 0; j < len2; j++)
				dats[i][j] = new Double(in[i][j]);

		Data_2D<Double> data = new Data_2D<Double>(dats, "DOUBLE", datasetName);
		long[] dims = data.getDimensions();
		createDataset(datasetPath, "Double", dims);

		// Parameter: datasetName, Data_2D, dim0, dim1, offsets
		writeMatrix(datasetPath, data, 0, 1, new long[] { 0, 0 });
		// Add dataset type attributes:
		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_DOUBLE");

	}

	/**
	 * Writes a primitive int[] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, int[] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
			 int[] in) throws H5IO_Exception {
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);

		String datasetName = getDatasetName(path);
		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		Integer[] dats = new Integer[len];
		for (int i = 0; i < len; i++)
			dats[i] = new Integer(in[i]);

		DataObject dataArray = new Data_1D<Integer>(dats, Data_1D.INTEGER,
				datasetName);
		long[] dims = dataArray.getDimensions();
		createDataset(datasetPath, "Integer", dims);
		writeArray(path, dataArray, 0, new long[] { 0 });

		// Add dataset type attributes:
		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_INTEGER");

	}

	/**
	 * Writes a primitive int[][] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, int[][] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
			 int[][] in) throws H5IO_Exception {
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);

		String datasetName = getDatasetName(path);
		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		int len2 = in[0].length;
		Integer[][] dats = new Integer[len][len2];
		for (int i = 0; i < len; i++)
			for (int j = 0; j < len2; j++)
				dats[i][j] = new Integer(in[i][j]);

		Data_2D<Integer> data = new Data_2D<Integer>(dats, "INTEGER",
				datasetName);
		long[] dims = data.getDimensions();
		createDataset(datasetPath, "Integer", dims);

		// Parameter: datasetName, Data_2D, dim0, dim1, offsets
		writeMatrix(datasetPath, data, 0, 1, new long[] { 0, 0 });
		// Add dataset type attributes:
		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_INTEGER");

	}

	/**
	 * Writes a primitive byte[] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, short[] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
			short[] in) throws H5IO_Exception {
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);

		String datasetName = getDatasetName(path);
		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		Short[] dats = new Short[len];
		for (int i = 0; i < len; i++)
			dats[i] = new Short(in[i]);

		DataObject dataArray = new Data_1D<Short>(dats, Data_1D.SHORT,
				datasetName);
		long[] dims = dataArray.getDimensions();
		createDataset(datasetPath, "Byte", dims);
		writeArray(path, dataArray, 0, new long[] { 0 });

		// Add dataset type attributes:
		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_SHORT");

	}

	/**
	 * Writes a primitive short[][] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, short[][]
	 *            dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
			short[][] in) throws H5IO_Exception {
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);


		String datasetName = getDatasetName(path);
		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		int len2 = in[0].length;
		Short[][] dats = new Short[len][len2];
		for (int i = 0; i < len; i++)
			for (int j = 0; j < len2; j++)
				dats[i][j] = new Short(in[i][j]);

		Data_2D<Short> data = new Data_2D<Short>(dats, "SHORT", datasetName);
		long[] dims = data.getDimensions();
		createDataset(datasetPath, "Byte", dims);

		// Parameter: datasetName, Data_2D, dim0, dim1, offsets
		writeMatrix(datasetPath, data, 0, 1, new long[] { 0, 0 });
		// Add dataset type attributes:
		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_SHORT");

	}

	/**
	 * Writes a primitive byte[] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, byte[] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
			 byte[] in) throws H5IO_Exception {

		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);
		

		String datasetName = getDatasetName(path);
		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		Byte[] dats = new Byte[len];
		for (int i = 0; i < len; i++)
			dats[i] = new Byte(in[i]);

		DataObject dataArray = new Data_1D<Byte>(dats, Data_1D.BYTE,
				datasetName);
		long[] dims = dataArray.getDimensions();
		createDataset(datasetPath, "Byte", dims);
		writeArray(path, dataArray, 0, new long[] { 0 });

		// Add dataset type attributes:
		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_BYTE");

	}

	/**
	 * Writes a primitive byte[][] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, byte[][]
	 *            dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
			byte[][] in) throws H5IO_Exception {
		
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);

		String datasetName = getDatasetName(path);
		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		int len2 = in[0].length;
		Byte[][] dats = new Byte[len][len2];
		for (int i = 0; i < len; i++)
			for (int j = 0; j < len2; j++)
				dats[i][j] = new Byte(in[i][j]);

		Data_2D<Byte> data = new Data_2D<Byte>(dats, "BYTE", datasetName);
		long[] dims = data.getDimensions();
		createDataset(datasetPath, "Byte", dims);

		// Parameter: datasetName, Data_2D, dim0, dim1, offsets
		writeMatrix(datasetPath, data, 0, 1, new long[] { 0, 0 });
		// Add dataset type attributes:
		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_BYTE");

	}

	/**
	 * Writes a primitive String[] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, String
	 *            datasetName, String[] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path,
			 String[] in) throws H5IO_Exception {
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);

		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		StringBuffer[] dats = new StringBuffer[len];
		for (int i = 0; i < len; i++)
			dats[i] = new StringBuffer(in[i]);

		writeStringDataset(datasetPath, dats);
	}


	/**
	 * Reads and returns the 1D string dataset from a given dataset path
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String pathToDesiredArrayLocation
	 * @return StringBuffer[] stringDatasetRead
	 * */
	public StringBuffer[] readDataset_String(String h5FilePath,
			String datasetPath)
			throws H5IO_Exception {
		StringBuffer[] str_data;
		try {
			// Open an existing dataset set.
			if (!existsDataset(datasetPath))
				return null;
			dataset_id = H5.H5Dopen(file_id, datasetPath);
			// Get dataset space.
			memspace_id = H5.H5Dget_space(dataset_id);
			long dim0 = H5.H5Sget_simple_extent_npoints(memspace_id);
			// Get the dataset type
			filetype_id = H5.H5Dget_type(dataset_id);
			// Get the size of the dataset type.
			// +1 make room for null terminator.
			int dim1 = H5.H5Tget_size(filetype_id) + 1;
			// Allocate space for dataset.
			byte[][] dset_data;
			dset_data = new byte[(int) dim0][dim1];
			str_data = new StringBuffer[(int) dim0];
			// Create the memory dataset type.
			memtype_id = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
			H5.H5Tset_size(memtype_id, dim1);
			// Read dataset.
			if ((dataset_id >= 0) && (memtype_id >= 0))
				H5.H5Dread(dataset_id, memtype_id, HDF5Constants.H5S_ALL,
						HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT,
						dset_data);
			byte[] tempbuf = new byte[dim1];
			for (int indx = 0; indx < (int) dim0; indx++) {
				for (int jndx = 0; jndx < dim1; jndx++) {
					tempbuf[jndx] = dset_data[indx][jndx];
				}
				str_data[indx] = new StringBuffer(new String(tempbuf));
			}
		}
 catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot read " + datasetPath, ex);
			throw new H5IO_Exception("Cannot read '" + datasetPath
					+ "': " + ex.getMessage());
		}
 finally {

		}
		return str_data;
	}

	/**
	 * Writes a generic T[] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, float[] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path, String datasetName,
			 T[] in) throws H5IO_Exception {
		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);

		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		// convert from primitive
		int len = in.length;
		Float[][] dats = new Float[len][1];
		for (int i = 0; i < len; i++)
			dats[i][0] = ((Float)in[i]);

		Data_2D<Float> data = new Data_2D<Float>(dats, "FLOAT", datasetName);
		long[] dims = data.getDimensions();
		createDataset(datasetPath, "Float", dims);

		// Parameter: datasetName, Data_2D, dim0, dim1, offsets
		writeMatrix(datasetPath, data, 0, 1, new long[] { 0, 0 });
		// Add dataset type attributes:
		writeAttribute(datasetPath, "dataType", "H5T_NATIVE_FLOAT");

	}
	/**
	 * Writes a generic T[][] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, float[] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path, String datasetName,
			 Data_2D dat2) throws H5IO_Exception {
		//Creating the parent groups of this dataset if they dont already exist
		String datasetPath = path;
		createAllParentGroups(h5FilePath, datasetPath);

		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		
		String type = dat2.getDataType();
		
		if(type.equalsIgnoreCase("Float"))
		{
			Data_2D<Float> data = new Data_2D<Float>(
					(Float[][]) dat2.getData(), "FLOAT", datasetName);
			long[] dims = data.getDimensions();
			createDataset(datasetPath, "Float", dims);
			// Parameter: datasetName, Data_2D, dim0, dim1, offsets
			writeMatrix(datasetPath, data, 0, 1, new long[] { 0, 0 });
			// Add dataset type attributes:
			writeAttribute(datasetPath, "dataType", "H5T_NATIVE_FLOAT");
		}
		else if(type.equalsIgnoreCase("Integer"))
		{
			Data_2D<Integer> data = new Data_2D<Integer>((Integer[][]) dat2
					.getData(), "INTEGER", datasetName);
			long[] dims = data.getDimensions();
			createDataset(datasetPath, "Integer", dims);
			// Parameter: datasetName, Data_2D, dim0, dim1, offsets
			writeMatrix(datasetPath, data, 0, 1, new long[] { 0, 0 });
			// Add dataset type attributes:
			writeAttribute(datasetPath, "dataType", "H5T_NATIVE_INTEGER");
		}
		else if(type.equalsIgnoreCase("Double"))
		{
			Data_2D<Double> data = new Data_2D<Double>((Double[][]) dat2
					.getData(), "DOUBLE", datasetName);
			long[] dims = data.getDimensions();
			createDataset(datasetPath, "Double", dims);
			// Parameter: datasetName, Data_2D, dim0, dim1, offsets
			writeMatrix(datasetPath, data, 0, 1, new long[] { 0, 0 });
			// Add dataset type attributes:
			writeAttribute(datasetPath, "dataType", "H5T_NATIVE_DOUBLE");
		}
		else if(type.equalsIgnoreCase("Byte"))
		{
			Data_2D<Byte> data = new Data_2D<Byte>((Byte[][]) dat2.getData(),
					"BYTE", datasetName);
			long[] dims = data.getDimensions();
			createDataset(datasetPath, "Byte", dims);
			// Parameter: datasetName, Data_2D, dim0, dim1, offsets
			writeMatrix(datasetPath, data, 0, 1, new long[] { 0, 0 });
			// Add dataset type attributes:
			writeAttribute(datasetPath, "dataType", "H5T_NATIVE_BYTE");
		}
 else if (type.equalsIgnoreCase("String")) {
			String[][] arr = (String[][]) dat2.getData();
			// Currently 2D String Matricies not supported
			// #==> convert to 1D
			String[] arr1 = new String[arr.length];
			for (int i = 0; i < arr.length; i++)
				arr1[i] = arr[i][0];

			writeDataset(h5FilePath, datasetPath, arr1);
			// Add dataset type attributes:
			writeAttribute(datasetPath, "dataType", "H5T_NATIVE_SCHAR");
		}
	}
	
	/**
	 * Writes a generic T[][] to HDF5 dataset
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            h5FilePath, String datasetPathFromProjectRoot, float[] dataset
	 * @return void
	 * */
	public void writeDataset(String h5FilePath, String path, String datasetName,
			 Data_1D dat1) throws H5IO_Exception {

		//Creating the parent groups of this dataset if they dont already exist
		createAllParentGroups(h5FilePath, path);
		
		String datasetPath = path;
		//check for and remove prior dataset for overwrite
		if(existsDataset(datasetPath))
			removeDataset(datasetPath);
		
		String type = dat1.getDataType();
		if(type.equalsIgnoreCase("Float"))
		{
			long[] dims = dat1.getDimensions();
			createDataset(datasetPath, "Float", dims);
			// Parameter: datasetName, Data_2D, dim0, dim1, offsets
			writeArray(datasetPath, dat1, 0, new long[] { 0 });
			// Add dataset type attributes:
			writeAttribute(datasetPath, "dataType", "H5T_NATIVE_FLOAT");
		}
		else if(type.equalsIgnoreCase("Integer"))
		{
			long[] dims = dat1.getDimensions();
			createDataset(datasetPath, "Integer", dims);
			// Parameter: datasetName, Data_2D, dim0, dim1, offsets
			writeArray(datasetPath, dat1, 0, new long[] { 0 });
			// Add dataset type attributes:
			writeAttribute(datasetPath, "dataType", "H5T_NATIVE_INTEGER");
		}
		else if(type.equalsIgnoreCase("Double"))
		{
			long[] dims = dat1.getDimensions();
			createDataset(datasetPath, "Double", dims);
			// Parameter: datasetName, Data_2D, dim0, dim1, offsets
			writeArray(datasetPath, dat1, 0, new long[] { 0 });
			// Add dataset type attributes:
			writeAttribute(datasetPath, "dataType", "H5T_NATIVE_DOUBLE");
		}
		else if(type.equalsIgnoreCase("Byte"))
		{
			long[] dims = dat1.getDimensions();
			createDataset(datasetPath, "Byte", dims);
			// Parameter: datasetName, Data_2D, dim0, dim1, offsets
			writeArray(datasetPath, dat1, 0, new long[] { 0 });
			// Add dataset type attributes:
			writeAttribute(datasetPath, "dataType", "H5T_NATIVE_BYTE");
		}
 else if (type.equalsIgnoreCase("String")) {
			String[] arr = (String[]) dat1.getData();
			writeDataset(h5FilePath, datasetPath, arr);
			// Add dataset type attributes:
			writeAttribute(datasetPath, "dataType", "H5T_NATIVE_SCHAR");
		}

	}
	
	
	/**
	 * Closes the dataset, memSpace, Filetype and memType with a single method
	 * 
	 * @author Bjorn Millard
	 * @param null
	 * @return void
	 * */
public void closeAll() throws H5IO_Exception
{
		// End access to the dataset set and release resources used by it.
	closeDataset();
	// Terminate access to the memory space.
	closeMemSpace();
	// Terminate access to the file type.
	closeFileType();
	// Terminate access to the memory type.
	closeMemType();
		// close file
		closeFileSpace();
		closeHDF5();
}

	/**
	 * Reads a 1D array directly from of a 1D HDF5 dataset
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            hdf5FilePath, String pathToDatasetWithHDF5root
	 * @return DataObject DataObjectRead
	 * */
	public Data_1D readArr(String h5FilePath, String datasetPath)
			throws H5IO_Exception {
		// Open the HDF5 file
		if (existsDataset(datasetPath)) {
			long[] dims = getDimensions(datasetPath);
			int dimIndex = 0;
			if (dims != null && dims.length > 0)
				for (int i = 0; i < dims.length; i++)
					if (dims[i] != 1) {
						dimIndex = i;
						break;
					}

			// Parameter: datasetName, dim0, dim1, offsets, count0, count1
			Data_1D mat = (Data_1D) readArray(datasetPath, 0,
 0,
 dims[dimIndex]);

			return mat;
		}
		return null;
	}

	/**
	 * Reads a 2D dataset directly from of a 2D HDF5 dataset
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param String
	 *            hdf5FilePath, String pathToDatasetWithHDF5root
	 * @return DataTwoDim DataObjectRead
	 * */
	public DataObject readDataset(String h5FilePath, String datasetPath)
			throws H5IO_Exception {
		if (existsDataset(datasetPath)) {
			long[] dims = getDimensions(datasetPath);
			String type = getDataType(datasetPath);

			DataObject mat = null;
			if (dims.length == 2 && (dims[0] != 1 || dims[1] != 1))
			{
				try {
					mat = readMatrix(h5FilePath, datasetPath,
							new long[] { 0, 0 }, dims);
					return mat;
				} catch (Exception e) {
					System.err
							.println("**ERROR reading matrix: " + datasetPath);
					e.printStackTrace();
				}


			} else if (dims.length == 1
					|| (dims.length == 2 && (dims[0] == 1 || dims[1] == 1))) {
				if (!type.equalsIgnoreCase("String")) { // data array
				// System.out.println("Reading data array");

					try {
						mat = readArr(h5FilePath, datasetPath);
						return mat;
					} catch (Exception e) {
						System.err.println("**ERROR reading matrix: "
								+ datasetPath);
						e.printStackTrace();
					}

				} else // String dataset
				{
					// System.out.println("Reading string array");

					String dsName = getDatasetName(datasetPath);
					StringBuffer[] stb = readDataset_String(h5FilePath,
							datasetPath);
					String[][] data = new String[stb.length][1];
					for (int r = 0; r < stb.length; r++)
						data[r][0] = stb[r].toString();
					mat = new Data_2D<String>(data, Data_2D.STRING, dsName);

					return mat;

				}
			}
		 }
		return null;
	}


	/**
	 * Converts a File object into a byte[]
	 * 
	 * @author BLM
	 * @param File
	 *            FileToConvert
	 * @return byte[] byteArrayoutput
	 */
	public static byte[] toByteArray(File file) {
		byte[] b = new byte[(int) file.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.read(b);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Error Reading The File.");
			e1.printStackTrace();
		}
		return b;
	}

	/**
	 * Converts a byte[] to a File
	 * 
	 * @author BLM
	 * @param byte[] arrayToWRitetoFile
	 * @return String pathTofileToWriteTo
	 */
	public static void byteArrayToFile(byte[] arr, String pathToFile) {
		try {
			File f = new File(pathToFile);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(arr);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException ex)
		{
			System.out.println("FileNotFoundException : " + ex);
		}
		catch (IOException ioe)
		{
			System.out.println("IOException : " + ioe);
		}
	}

	/**
	 * Copy given file to HDF5 file path
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            SourceFilePathToCopy, String DestinationHDF5FilePath, String
	 *            intraHDF5fileDestinationPath
	 * @throws H5IO_Exception
	 * @throws NullPointerException
	 * @throws HDF5Exception
	 * @throws IllegalArgumentException
	 */
	public void writeFileToHDF5(String fileToCopy, String hdfFilePath,
			String datasetPath) throws IOException, H5IO_Exception,
			NullPointerException, IllegalArgumentException, HDF5Exception {

		//Creating parent groups if not exist
		createAllParentGroups(hdfFilePath, datasetPath);
		
		// 2^20 element buffer
		int BUFF_SIZE = 1048576;
		long timeStart = System.currentTimeMillis();
		byte[] buffer = new byte[BUFF_SIZE];
		// long[] count = { BUFF_SIZE };
		InputStream in = null;
		try {
			File file = new File(fileToCopy);
			int fileSize = (int) file.length();
			// Open our source file
			in = new FileInputStream(file);
			// Open our destination file
			openHDF5(hdfFilePath);
			// Create a destination HDF5 dataset of size len file
			long[] dims = { fileSize };

			//Unlinking old dataset
			removeDataset(datasetPath);
			// Get the dimensions of the multidimensional set
			memspace_id = H5.H5Screate_simple(1, new long[] { fileSize }, null);
			dataspace_id = H5.H5Screate_simple(1, dims, null);
			/* Create the dataset. */
			dataset_id = H5.H5Dcreate(file_id, datasetPath,
					HDF5Constants.H5T_NATIVE_CHAR, memspace_id,
					HDF5Constants.H5P_DEFAULT);
			// Get the file space
			filespace_id = H5.H5Dget_space(dataset_id);

			// offset is how much we have read so far
			long[] offset = new long[] { 0 };
			// count is the buffer size each time
			long[] count = new long[] { 0 };
			int h5pos = 0;
			while (true) {
				synchronized (buffer) {
					int amountRead = in.read(buffer);
					if (amountRead == -1) {
						break;
					}
					// Define hyperslab in the dataset.
					offset[0] = h5pos;
					count[0] = amountRead;

					// Select hyperslab.
				    H5.H5Sselect_hyperslab (filespace_id, HDF5Constants.H5S_SELECT_SET, offset, null,
				    					   	count, null);
				    // Define memory space
					long[] count1D = new long[] { amountRead };
				    memspace_id = H5.H5Screate_simple (1, count1D, null);
					// Write the dataset from hyperslab to file.
				    H5.H5Dwrite (dataset_id, HDF5Constants.H5T_NATIVE_CHAR, memspace_id, filespace_id,
				                 HDF5Constants.H5P_DEFAULT, buffer);

					h5pos += amountRead;
				}
			}
		} finally {


		}

	}

	/**
	 * Reads a file that has been encoded into an HDF5 byte[] and writes it to a
	 * standard file-system file
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            SourceHDF5filePath, String datasetPathInHDF5containingFile,
	 *            String DestinationFilePath
	 * @throws H5IO_Exception
	 * @throws NullPointerException
	 * @throws HDF5Exception
	 * @throws IllegalArgumentException
	 */
	public void readFileFromHDF5(String hdfFilePath, String datasetPathInHDF5,
			String destinationFilePath) throws IOException, H5IO_Exception,
			NullPointerException, IllegalArgumentException, HDF5Exception {

		// Open the file and the dataset.
		long timeStart = System.currentTimeMillis();
		OutputStream out = null;
		try {
			File file = new File(destinationFilePath);
			out = new FileOutputStream(file);

		// 2^20 element buffer
			int BUFF_SIZE = 1048576;
		long[] dims_out = { BUFF_SIZE };
		    openHDF5(hdfFilePath);
			 dataset_id = H5.H5Dopen(file_id, datasetPathInHDF5);

		dataspace_id = H5.H5Dget_space(dataset_id); /* dataspace handle */
			int rank = H5.H5Sget_simple_extent_ndims(dataspace_id);
			int status = H5.H5Sget_simple_extent_dims(dataspace_id, dims_out,
					null);

		int arrSize = (int)dims_out[0];
		int h5Pos = 0;
		int amountRead = (int) dims_out[0];

			// Read/write small buffer snippets of this file so dont have to
			// load it all into RAM at once
		while (h5Pos < arrSize) {
		    /* 
		     * Define hyperslab in the dataset. 
		     */

		long[] offset = new long[] { h5Pos };
		long[] count = new long[] { amountRead };
		status = H5.H5Sselect_hyperslab(dataspace_id,
				HDF5Constants.H5S_SELECT_SET, offset, null, count, null);

		    /*
		     * Define the memory dataspace.
		     */

		// The total byte[] length
			long arrayLength = dims_out[0];
		long[] dimsm = new long[] { arrayLength };
		int RANK_OUT = 1;
		memspace_id = H5.H5Screate_simple(RANK_OUT, dimsm, null);

		    /* 
		     * Define memory hyperslab. 
		     */
		long[] offset_out = new long[] { h5Pos };
		long[] count_out = new long[] { amountRead };
		status = H5
				.H5Sselect_hyperslab(memspace_id, HDF5Constants.H5S_SELECT_SET,
						offset_out, null, count_out, null);

		    /*
		     * Read data from hyperslab in the file into the hyperslab in 
		     * memory and display.
		     */
			byte[] data_out = new byte[amountRead];
			status = H5.H5Dread(dataset_id, H5.H5Dget_type(dataset_id),
					memspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT,
					data_out);

		// Writing byteArray to file
			out.write(data_out);
			
			
				// Updating data position counters
			 amountRead = arrSize - h5Pos;
			 h5Pos += amountRead;
				// amountReadArr[0] = amountRead;
				// posArr[0] = h5Pos;
		}
			
			
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}

				System.out.println("Time to Read: "
						+ (System.currentTimeMillis() - timeStart));

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	/**
	 * Copy one file to another file
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            SourceFilePath, String DestinationFilePath
	 * @return void
	 * */
	public static void copyFile(String from, String to) throws IOException {

		int BUFF_SIZE = 100000;
		byte[] buffer = new byte[BUFF_SIZE];
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			while (true) {
				synchronized (buffer) {
					int amountRead = in.read(buffer);
					if (amountRead == -1) {
						break;
					}
					out.write(buffer, 0, amountRead);
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}



}
