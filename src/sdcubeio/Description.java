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
 * This class is an Description object, which could be a treatment, measurement, 
 * time point, etc. of a well.
 * 
 * @author Bjorn Millard & Michael Menden
 *
 */
public class Description
{
	
	private String id    = "";
	private String name  = "";
	private String value = "";
	private String units = "";
	private String time = "";
	private String time_units = "";
	
	/**
	 * Constructs a description and initializes with an ID and value
	 * @param id The id could be for example "treatment"
	 * @param value The value is dependent on the id; for example if the id is 
	 * "measurement", the value could be "intensity".
	 */

	

	/**
	 * Constructs a description and initializes with an ID, name and value
	 * @param id The id of the description; for example "treatment"
	 * @param name The name of the description; for example if the id is "treatment",
	 * the name could be "EGF".
	 * @param value The value is dependent on the id; for example if the id is 
	 * "measurement", the value could be "intensity".
	 * @param units The units could be "mmol", "mol", "mg", etc.
	 */
	public Description(String id, String name, String value,
			String units,
			String timeValue, String timeUnits)
	{
		if (id == null)
			id = "";
		if (name == null)
			name = "";
		if (value == null)
			value = "";
		if (units == null)
			units = "";
		if (timeValue == null)
			timeValue = "";
		if (timeUnits == null)
			timeUnits = "";

		this.id = id;
		this.name = name;
		this.value = value;
		this.units = units;
		this.time = timeValue;
		this.time_units = timeUnits;
	}
	
	/**
	 * Get the identifier of the description field: treatment, time point,...
	 * @return Returns the id.
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * Get the name of the description. In case of an treatment it could be "EGF"
	 * @return Returns the name.
	 */
	public String getName()
	{
		if(name==null)
			return "";
		return name;
	}
	
	/**
	 * Get the value.
	 * @return Returns the name.
	 */
	public String getValue()
	{
		if(value==null)
			return "";
		return value;
	}
	
	/**
	 * Get the units.
	 * @return Return the unit.
	 */
	public String getUnits()
	{
		if (units==null)
			return "";
		return units;
	}
	
	/**
	 * Get the time value.
	 * 
	 * @return Return the time.
	 */
	public String getTimeValue() {
		if (time == null)
			return "";
		return time;
	}

	/**
	 * Get the time units.
	 * 
	 * @return Return the unit.
	 */
	public String getTimeUnits() {
		if (time_units == null)
			return "";
		return time_units;
	}


	/** 
	 * Get a description of this description object
	 * @return Returns a description of this object.
	 */
	public String toString()
	{
		if (!value.equalsIgnoreCase(""))
			return name + " (" + value + " " + units + ")   @t=" + time + " "
					+ time_units;
		return name;
	}

	/**
	 * Determines if these two descriptions have identical fields
	 * 
	 * @return Returns true for equal or false for not equal fields.
	 */
	public boolean isSame(Description desc) {
		if (!id.equalsIgnoreCase(desc.getId()))
			return false;
		if (!name.equalsIgnoreCase(desc.getName()))
			return false;
		if (!value.equalsIgnoreCase(desc.getValue()))
			return false;
		if (!units.equalsIgnoreCase(desc.getUnits()))
			return false;
		if (!time.equalsIgnoreCase(desc.getTimeValue()))
			return false;
		if (!time_units.equalsIgnoreCase(desc.getTimeUnits()))
			return false;
		return true;
	}
}
