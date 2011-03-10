package sdcubeio;


/**
 * This class is an ExpDesign_Description object, which could be a treatment, measurement,
 * time point, etc. of a sample.
 * 
 * @author Bjorn Millard & Michael Menden
 * 
 */
public class ExpDesign_Description {

	private String type = "";
	private String name = "";
	private String value = "";
	private String units = "";
	private String time = "";
	private String time_units = "";

	/**
	 * Constructs a description and initializes with an ID, name and value
	 * 
	 * @param type
	 *            The type of the description; for example "Treatment",
	 *            "Measurement, "Time_Point", etc
	 * @param name
	 *            The name of the description; for example if the id is
	 *            "treatment", the name could be "EGF".
	 * @param value
	 *            The value is dependent on the id; for example if the id is
	 *            "measurement", the value could be "intensity".
	 * @param units
	 *            The units could be "mmol", "mol", "mg", etc.
	 */
	public ExpDesign_Description(String type, String name, String value, String units,
			String timeValue, String timeUnits) {
		if (type == null)
			type = "";
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

		this.type = type;
		this.name = name;
		this.value = value;
		this.units = units;
		this.time = timeValue;
		this.time_units = timeUnits;
	}
	
	/** Empty constructor */
	public ExpDesign_Description() {
	}

	/**
	 * Get Description type (ex: Treatment, Measurement, etc...)
	 * @author Bjorn Millard
	 * @return String type
	 */
	public String getType() {
		return type;
	}

	
	/**
	 * Get the name of the description. In case of an treatment it could be
	 * "EGF"
	 * 
	 * @return Returns the name.
	 */
	public String getName() {
		if (name == null)
			return "";
		return name;
	}

	/**
	 * Get the value.
	 * 
	 * @return Returns the name.
	 */
	public String getValue() {
		if (value == null)
			return "";
		return value;
	}

	/**
	 * Get the units.
	 * 
	 * @return Return the unit.
	 */
	public String getUnits() {
		if (units == null)
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
	 * Set Description type (ex: Treatment, Measurement, etc...)
	 * 
	 * @author Bjorn Millard param String type
	 */
	public void setType(String st) {
		type = st;
	}


	/**
	 * set the name of the description. In case of an treatment it could be
	 * "EGF"
	 * 
	 * param the name.
	 */
	public void setName(String st) {
		name = st;
	}

	/**
	 * Set the value.
	 * 
	 * param the name.
	 */
	public void setValue(String st) {
		value = st;
	}

	/**
	 * Set the units.
	 * 
	 * @param the
	 *            unit.
	 */
	public void setUnits(String st) {
		units = st;
	}

	/**
	 * Set the time value.
	 * 
	 * @param the
	 *            time.
	 */
	public void setTime(String st) {
		time = st;
	}

	/**
	 * Set the time units.
	 * 
	 * @param the
	 *            unit.
	 */
	public void setTimeUnits(String st) {
		time_units = st;
	}

	/**
	 * Get a description of this description object
	 * 
	 * @return Returns a description of this object.
	 */
	public String toString() {
		if (value != null && !value.equalsIgnoreCase(""))
 {
			if (type == null)
				type = "";
			if (name == null)
				name = "";
			if (value == null)
				value = "";
			if (units == null)
				units = "";
			if (time == null)
				time = "";
			if (time_units == null)
				time_units = "";

			return name + " (" + value + " " + units + ")   @t=" + time + " "
					+ time_units;
		}
		return name;
	}

	/**
	 * Determines if these two descriptions have identical fields
	 * 
	 * @return Returns true for equal or false for not equal fields.
	 */
	public boolean isSame(ExpDesign_Description desc) {
		if (type != null && !type.equalsIgnoreCase(desc.getType()))
			return false;
		if (name != null && !name.equalsIgnoreCase(desc.getName()))
			return false;
		if (value != null && !value.equalsIgnoreCase(desc.getValue()))
			return false;
		if (units != null && !units.equalsIgnoreCase(desc.getUnits()))
			return false;
		if (time != null && !time.equalsIgnoreCase(desc.getTimeValue()))
			return false;
		if (time_units != null
				&& !time_units.equalsIgnoreCase(desc.getTimeUnits()))
			return false;
		return true;
	}
	
	/** 
	 * Writes this ExpDesign_Description to the XML file of the SDCube
	 * @author Bjorn Millard
	 * @param String h5FilePathForSDCube
	 * */
	public void write(String h5FilePath)
	{
		
	}

	/**
	 * returns a copy of this description
	 * 
	 * @author Bjorn Millard
	 * @return ExpDesign_Description copy
	 */
	public ExpDesign_Description getCopy() {
		return new ExpDesign_Description(type, name, value, units, time,
				time_units);
	}
	
}
