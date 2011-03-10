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

import ncsa.hdf.hdf5lib.HDF5Constants;

/**
 * Generic 1 dimensional data object
 * 
 * @author Bjorn Millard & Michael Menden
 */
public class Data_1D<T> implements DataObject {
	
	static public String DOUBLE = "DOUBLE";
	static public String INTEGER = "INTEGER";
	static public String SHORT = "SHORT";
	static public String FLOAT = "FLOAT";
	static public String BYTE = "BYTE";
	static public String STRING = "STRING";

	private T[] data;
	private int hdfType;
	private String name;
	private String dataType;

	public Data_1D(T[] data, String dataType, String name) {
		this.name = name;
		this.data = data;
		this.dataType = dataType;

		if (dataType.equalsIgnoreCase("INT")
				|| dataType.equalsIgnoreCase("INTEGER"))
			hdfType = HDF5Constants.H5T_NATIVE_INT;
		else if (dataType.equalsIgnoreCase("Short"))
			hdfType = HDF5Constants.H5T_NATIVE_SHORT;
		else if (dataType.equalsIgnoreCase("Float"))
			hdfType = HDF5Constants.H5T_NATIVE_FLOAT;
		else if (dataType.equalsIgnoreCase("Double"))
			hdfType = HDF5Constants.H5T_NATIVE_DOUBLE;
		else if (dataType.equalsIgnoreCase("Byte"))
			hdfType = HDF5Constants.H5T_NATIVE_CHAR;
		else if (dataType.equalsIgnoreCase("String"))
			hdfType = HDF5Constants.H5T_NATIVE_SCHAR;
	}

	/**
	 * Get the data array.
	 * 
	 * @param null
	 * @return T[] DataObjectInGenericForm
	 * @author Bjorn Millard & Michael Menden
	 */
	public T[] getData() {
		return data;
	}

	/** 
	 * Returns the dimenionality rank of this dataset
	 * @author Bjorn Millard
	 * @return int rank
	 * */
	public int getRank()
	{
		return 1;
	}
	
	/**
	 * Get the HDF type.
	 * 
	 * @param null
	 * @return int HDF5dataType
	 * @author Bjorn Millard & Michael Menden
	 */
	public int getHDFType() {
		return hdfType;
	}

	/**
	 * Gets the length of the data array
	 * 
	 * @param null
	 * @return int dataLength
	 * @author Bjorn Millard & Michael Menden
	 */
	public long[] getDimensions() {
		return new long[] { data.length };
	}

	/**
	 * Gets the string description of the data type. Ex: "Integer", "Float",
	 * "Double", "Short", "Byte"
	 * 
	 * @author Bjorn Millard & Michael Menden
	 * @param null
	 * @return String DataTypeName
	 * */
	public String getDataType() {
		return dataType;

		// System.out.println("type: " + hdfType);
		// if (hdfType == HDF5Constants.H5T_NATIVE_INT)
		// return "Integer";
		// else if (hdfType == HDF5Constants.H5T_NATIVE_FLOAT)
		// return "Float";
		// else if (hdfType == HDF5Constants.H5T_NATIVE_DOUBLE)
		// return "Double";
		// else if (hdfType == HDF5Constants.H5T_NATIVE_SHORT)
		// return "Short";
		// else if (hdfType == HDF5Constants.H5T_NATIVE_CHAR)
		// return "Byte";
		// else if (hdfType == HDF5Constants.H5T_NATIVE_SCHAR)
		// return "String";
		// return "null";
	}

	/**
	 * Returns the name of this DataObject
	 * 
	 * @author Bjorn Millard
	 * @return String name
	 * */
	public String getName() {
		return name;
	}
	
	/**
	 * Prints out the critical data for this object
	 * */
	public String toString() {
		String st = "";
		st += "Name: " + name + "\n";
		st += "DataType: " + dataType + "\n";
		st += "HDFType: " + hdfType + "\n";
		st += "Dimensions: " + data.length + "\n";
		return st;
	}
	
}
