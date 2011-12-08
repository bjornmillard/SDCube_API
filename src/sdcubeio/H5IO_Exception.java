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

/**
 * Exception thrown during reading,writing and manipulating HDF5 files
 * 
 * @author Bjorn Millard & Michael Menden
 */
public class H5IO_Exception extends Exception{

	/**
	 * For serializing this class.
	 */
	private static final long serialVersionUID = -5180345606650892919L;

	H5IO_Exception(String msg) {
		super("H5IO_Exception: " + msg);
	}
}
