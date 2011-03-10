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
