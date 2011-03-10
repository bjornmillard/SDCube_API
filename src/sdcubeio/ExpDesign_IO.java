package sdcubeio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is responsible to write and read the XML meta  of an
 * IRproject. The XML meta  is description of an experiment; for
 * example the XML meta  could be the treatment, measurement, time
 * point, etc. of a well.
 * 
 * @author Bjorn Millard & Michael Menden
 * 
 */
public class ExpDesign_IO {

	/**
	 * Stores the changes we made to the XML file on the hard disc.
	 */
	static public void write(ExpDesign_Model model) {
		try {
			String xmlPath = model.getXMLpath();

			File f = new File(xmlPath);
			f.createNewFile();
			

			// System.out.println("<><><> Writing new XML File:" + xmlPath);

			FileWriter fstream = new FileWriter(xmlPath);
			BufferedWriter out = new BufferedWriter(fstream);

			String st = "<?xml version=\"1.0\"?>\n";
			st += "<sdcube xsi:schemaLocation='http://pipeline.med.harvard.edu/imagerail-metadata-1.0 imagerail-metadata-1.0.xsd' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://pipeline.med.harvard.edu/imagerail-metadata-1.0'>\n";

			ArrayList<ExpDesign_Sample> TheExpDesigns = model.getSamples();
			int num = TheExpDesigns.size();
			// Sample loop
			for (int j = 0; j < num; j++) {
				ExpDesign_Sample es = TheExpDesigns.get(j);
				if (es.getDescriptions().size() > 0) {
				st += "\t<Sample id='" + es.getId() + "'>\n";
				ArrayList<ExpDesign_Description> edu = es.getDescriptions();
				int numU = edu.size();
				// description loop
				for (int k = 0; k < numU; k++) {
					ExpDesign_Description ed = edu.get(k);
					st += "\t\t<Descriptor>\n";
					if (ed.getType() != null)
						st += "\t\t\t<type>" + ed.getType() + "</type>\n";
					if (ed.getName() != null && ed.getName().compareTo("") != 0)
						st += "\t\t\t<name>" + ed.getName() +"</name>\n";
					if (ed.getValue() != null
							&& ed.getValue().compareTo("") != 0)
						st +="\t\t\t<value>" + ed.getValue() +"</value>\n";
					if (ed.getUnits() != null
							&& ed.getUnits().compareTo("") != 0)
						st += "\t\t\t<units>"+ ed.getUnits() +"</units>\n";
					if (ed.getTimeValue() != null
							&& ed.getTimeValue().compareTo("") != 0)
						st += "\t\t\t<time>" + ed.getTimeValue()+"</time>\n";
					if (ed.getTimeUnits() != null
							&& ed.getTimeUnits().compareTo("") != 0)
						st+="\t\t\t<time_units>"+ed.getTimeUnits()+"</time_units>\n";	
					st += "\t\t</Descriptor>\n";
				}
					st += "\t</Sample>\n";
				}
			}
			st+="</sdcube>\n";
			out.write(st);
			out.flush();
			out.close();

			// Encoding it to the HDF5 file
			// H5IO h = new H5IO();
			// h.writeFileToHDF5(xmlPath, model.getHDFpath(),
			// "./Meta/ExpDesign.xml");
			// h = null;

			// h.readFileFromHDF5(model.getHDFpath(), "./Meta/ExpDesign.xml",
			// "/Users/blm13/Desktop/xml_" + Math.random() + ".xml");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Parses and returns All the samples contained within the given XML path
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            ID
	 * @return ArrayList<ExpDesign_Sample>
	 */
	static public ArrayList<ExpDesign_Sample> parseSamples(
String xmlPath) {

		ArrayList<ExpDesign_Sample> samples = new ArrayList<ExpDesign_Sample>();
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			dom = db.parse(new File(xmlPath));

		} catch (Exception e) {
			System.out.println("");
			return null;
		}

		// get the root element
		Element docEle = dom.getDocumentElement();

		// get a nodelist of elements
		NodeList nl = docEle.getElementsByTagName("Sample");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				// add it to list
				samples.add(parseSample(el));
			}
		}
		return samples;
	}

	/**
	 * Parses the samples that have the given ID. Note there technically could
	 * be 2 or more samples with same ID, so we return an ArrayList of
	 * samples... Most of the time there is only 1.
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            ID
	 * @return ArrayList<ExpDesign_Sample>
	 */
	static public ArrayList<ExpDesign_Sample> parseSamplesWid(
String xmlPath,
			String id) {
		ArrayList<ExpDesign_Sample> samples = new ArrayList<ExpDesign_Sample>();
		// Now finding all samples within the XML doc that have the given ID
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			dom = db.parse(xmlPath);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		// get the root element
		Element docEle = dom.getDocumentElement();

		// get a nodelist of elements
		NodeList nl = docEle.getElementsByTagName("Sample");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the employee element
				Element el = (Element) nl.item(i);
				// check if it has a given ID
				if (el.getAttribute("id").equals(id))
					samples.add(parseSample(el));
			}
		}
		return samples;
	}

	/**
	 * Parses the samples that have the given ID. Note there technically could
	 * be 2 or more samples with same ID, so we return an ArrayList of
	 * samples... Most of the time there is only 1.
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            ID
	 * @return ArrayList<ExpDesign_Sample>
	 */
	static public ArrayList<ExpDesign_Sample> parseSamplesWids(String xmlPath,
			String[] ids) {
		ArrayList<ExpDesign_Sample> samples = new ArrayList<ExpDesign_Sample>();
		// Now finding all samples within the XML doc that have the given ID
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			dom = db.parse(xmlPath);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		// get the root element
		Element docEle = dom.getDocumentElement();

		// get a nodelist of elements
		NodeList nl = docEle.getElementsByTagName("Sample");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the employee element
				Element el = (Element) nl.item(i);
				// check if it has a given ID
				for (int j = 0; j < ids.length; j++) {
					if (el.getAttribute("id").equals(ids[j]))
						samples.add(parseSample(el));
				}

			}
		}
		return samples;
	}

	/**
	 * Parses the samples that contain ALL of the given XML tags. The "AND"
	 * refers to an AND gate, requiring all the tags to be present for the
	 * sample to quality
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            [] tagsToLookFor
	 * @return ArrayList<ExpDesign_Sample>
	 */
	static public ArrayList<ExpDesign_Sample> parseSamplesWithDescriptorNames_AND(
			String xmlInput, String[] tags) {
		ArrayList<ExpDesign_Sample> allSamples = parseSamples(xmlInput);
		ArrayList<ExpDesign_Sample> samples = new ArrayList<ExpDesign_Sample>();
		if (allSamples == null || allSamples.size() == 0)
			return null;

		int numTags = tags.length;
		int len = allSamples.size();
		System.out.println("Checking " + numTags + " names against " + len
				+ " samples");
		for (int i = 0; i < len; i++) {
			int counter = 0;
			ArrayList<ExpDesign_Description> descriptors = allSamples.get(i)
					.getDescriptions();
			int num = descriptors.size();
			for (int j = 0; j < num; j++) {
				String name = descriptors.get(j).getName();
				for (int k = 0; k < numTags; k++) {
					if (name.equalsIgnoreCase(tags[k])) {
						counter++;
					}
				}

			}
			// If we found all tages in this sample, add it to our return list
			if (counter == numTags)
				samples.add(allSamples.get(i));
		}

		return samples;
	}

	/**
	 * Parses the samples that contain ANY of the given XML tags. The "OR"
	 * refers to an OR gate, requiring only 1 of the tags to be present for the
	 * sample to quality
	 * 
	 * @author Bjorn Millard
	 * @param String
	 *            [] tagsToLookFor
	 * @return ArrayList<ExpDesign_Sample>
	 */
	static public ArrayList<ExpDesign_Sample> parseSamplesWithDescriptorNames_OR(
			String xmlPath, String[] tags) {
		ArrayList<ExpDesign_Sample> allSamples = parseSamples(xmlPath);
		ArrayList<ExpDesign_Sample> samples = new ArrayList<ExpDesign_Sample>();
		if (allSamples == null || allSamples.size() == 0)
			return null;

		int numTags = tags.length;
		int len = allSamples.size();
		System.out.println("Checking " + numTags + " names against " + len
				+ " samples");
		for (int i = 0; i < len; i++) {
			ArrayList<ExpDesign_Description> descriptors = allSamples.get(i)
					.getDescriptions();
			int num = descriptors.size();
			boolean foundOne = false;
			for (int j = 0; j < num; j++) {
				String name = descriptors.get(j).getName();
				for (int k = 0; k < numTags; k++) {
					if (name.equalsIgnoreCase(tags[k])) {
						samples.add(allSamples.get(i));
						foundOne = true;
						break;
					}
				}
				if (foundOne)
					break;
			}
		}

		return samples;
	}

	/**
	 * Parses and returns a sample from the given XML element block
	 * 
	 * @author Bjorn Millard
	 * @return Sample
	 */
	static public ExpDesign_Sample parseSample(Element el) {
		// Create a new Employee with the value read from the xml nodes
		ExpDesign_Sample sample = new ExpDesign_Sample();// name,id,age,type);
		sample.setId(el.getAttribute("id"));

		// for each <sample> element get values
		// String name = getTextValue(el,"Name");
		NodeList nl = el.getElementsByTagName("Descriptor");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the employee element
				Element e = (Element) nl.item(i);
				// create the Sample object
				ExpDesign_Description unit = parseDescriptor(e);
				sample.addDescription(unit);

			}
		}
		return sample;
	}

	/**
	 * Parses and returns an ExpDesign_Description from the given XML element
	 * block
	 * 
	 * @athor Bjorn Millard
	 * @return ExpDesign_Description
	 */
	static public ExpDesign_Description parseDescriptor(Element e) {

		String type = getStringValue(e, "type");
		String name = getStringValue(e, "name");
		String value = getStringValue(e, "value");
		String units = getStringValue(e, "units");
		String timeUnits = getStringValue(e, "time_units");
		String timeVal = getStringValue(e, "time");

		ExpDesign_Description unit = new ExpDesign_Description();
		unit.setType(type);
		unit.setName(name);
		unit.setValue(value);
		unit.setUnits(units);
		unit.setTimeUnits(timeUnits);
		unit.setTime(timeVal);
		return unit;
	}

	/**
	 * Takes an XML element and the tag name, look for the tag and get the text
	 * content i.e for <employee><name>John</name></employee> xml snippet if the
	 * Element points to employee node and tagName is 'name' I will return John
	 */
	static public String getStringValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	/**
	 * Calls getTextValue and returns a int value
	 */
	static public int getIntValue(Element ele, String tagName) {
		// in production application you would catch the exception
		return Integer.parseInt(getStringValue(ele, tagName));
	}

	/**
	 * Calls getTextValue and returns a float value
	 */
	static public float getFloatValue(Element ele, String tagName) {
		// in production application you would catch the exception
		return Float.parseFloat(getStringValue(ele, tagName));
	}

	/**
	 * Calls getTextValue and returns a float value
	 */
	static public double getDoubleValue(Element ele, String tagName) {
		// in production application you would catch the exception
		return Double.parseDouble(getStringValue(ele, tagName));
	}

}
