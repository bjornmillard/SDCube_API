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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
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
			
			//
			//
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.newDocument();
				Element root = doc.createElement("sdcube");
				
	
				root.setAttribute("xsi:schemaLocation", "http://pipeline.med.harvard.edu/imagerail-metadata-1.0 imagerail-metadata-1.0.xsd");
				root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				root.setAttribute("xmlns", "http://pipeline.med.harvard.edu/imagerail-metadata-1.0");
				doc.appendChild(root);
				

			String xmlPath = model.getXMLpath();
			File f = new File(xmlPath);
			f.createNewFile();
			
//			FileWriter fstream = new FileWriter(xmlPath);
//			BufferedWriter out = new BufferedWriter(fstream);
//			String st = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
//			st += "<sdcube xsi:schemaLocation='http://pipeline.med.harvard.edu/imagerail-metadata-1.0 imagerail-metadata-1.0.xsd' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://pipeline.med.harvard.edu/imagerail-metadata-1.0'>\n";

			ArrayList<ExpDesign_Sample> TheExpDesigns = model.getSamples();
			int num = TheExpDesigns.size();
			// Sample loop
			for (int j = 0; j < num; j++) {
				ExpDesign_Sample es = TheExpDesigns.get(j);
				if (es.getDescriptions().size() > 0) {
		            //create child element, add an attribute, and add to root
		            Element sample = doc.createElement("Sample");
		            sample.setAttribute("id", es.getId());
		            root.appendChild(sample);
		            
	//				st += "\t<Sample id='" + es.getId() + "'>\n";
					ArrayList<ExpDesign_Description> edu = es.getDescriptions();
					int numU = edu.size();
					// description loop
					for (int k = 0; k < numU; k++) {
						ExpDesign_Description ed = edu.get(k);
					
			            Element desc = doc.createElement("Descriptor");
			            sample.appendChild(desc);
			            
			    
						
						
						if (ed.getType() != null)
						{
				            Element desc2 = doc.createElement("type");
				            desc.appendChild(desc2);
				            Text text = doc.createTextNode(ed.getType());
				            desc2.appendChild(text);
						}
							
						if (ed.getName() != null && ed.getName().compareTo("") != 0)
						{
				            Element desc2 = doc.createElement("name");
				            desc.appendChild(desc2);
				            Text text = doc.createTextNode(ed.getName());
				            desc2.appendChild(text);			   
						}
						
						if (ed.getValue() != null
								&& ed.getValue().compareTo("") != 0)
						{
						    Element desc2 = doc.createElement("value");
				            desc.appendChild(desc2);
				            Text text = doc.createTextNode(ed.getValue());
				            desc2.appendChild(text);
						}
						
						if (ed.getUnits() != null
								&& ed.getUnits().compareTo("") != 0)
						{
						    Element desc2 = doc.createElement("units");
				            desc.appendChild(desc2);
				            Text text = doc.createTextNode(ed.getUnits());
				            desc2.appendChild(text);
						}
						
						if (ed.getTimeValue() != null
								&& ed.getTimeValue().compareTo("") != 0)
						{
						    Element desc2 = doc.createElement("time");
				            desc.appendChild(desc2);
				            Text text = doc.createTextNode(ed.getTimeValue());
				            desc2.appendChild(text);
						}
						
						if (ed.getTimeUnits() != null
								&& ed.getTimeUnits().compareTo("") != 0)
						{
						    Element desc2 = doc.createElement("time_units");
				            desc.appendChild(desc2);
				            Text text = doc.createTextNode(ed.getTimeUnits());
				            desc2.appendChild(text);
						}
						
						if (ed.getCategory() != null
									&& ed.getCategory().compareTo("") != 0)
						{
						    Element desc2 = doc.createElement("category");
				            desc.appendChild(desc2);
				            Text text = doc.createTextNode(ed.getCategory());
				            desc2.appendChild(text);
						}
							
				}
				}
			}

			DOMSource domSource = new DOMSource(doc);
			TransformerFactory tFactory = TransformerFactory.newInstance();

			FileOutputStream out2 = new FileOutputStream(xmlPath);
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(domSource, new StreamResult(out2));
			
			
			
			
			
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
			dom = db.parse(xmlPath);

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
	 * Parses and returns All the unique descriptor names involved through all
	 * the samples
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<String> descNames
	 */
	static public ArrayList<String> parseUniqueDescriptorNames(String xmlPath) {

		ArrayList<ExpDesign_Sample> samples = parseSamples(xmlPath);
		if (samples == null || samples.size() == 0)
			return null;
		return getUniqueDescriptorNames(samples);
	}

	/**
	 * Returns All the unique descriptor names involved through all the samples
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<String> descNames
	 */
	static public ArrayList<String> getUniqueDescriptorNames(
			ArrayList<ExpDesign_Sample> samples) {
		ArrayList<String> names = new ArrayList<String>();
		Hashtable<String, String> hash = new Hashtable<String, String>();

		int len = samples.size();
		for (int i = 0; i < len; i++) {
			ArrayList<ExpDesign_Description> descs = samples.get(i)
					.getDescriptions();
			int num = descs.size();
			for (int j = 0; j < num; j++) {
				String name = descs.get(j).getName();
				if ((name == null || name.trim().equalsIgnoreCase(""))
						&& descs.get(j).getType()
								.equalsIgnoreCase("Measurement_Time"))
						name = "Measurement_Time";
				if (hash.get(name) == null)
					hash.put(name, name);
			}
		}

		for (Enumeration<String> enu = hash.keys(); enu.hasMoreElements();)
			names.add((String) enu.nextElement());

		return names;
	}

	/**
	 * Returns All the category names involved through all the samples
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<String> descNames
	 */
	static public ArrayList<String> getUniqueCategoryNames(
			ArrayList<ExpDesign_Sample> samples) {
		ArrayList<String> names = new ArrayList<String>();
		Hashtable<String, String> hash = new Hashtable<String, String>();

		int len = samples.size();
		for (int i = 0; i < len; i++) {
			ArrayList<ExpDesign_Description> descs = samples.get(i)
					.getDescriptions();
			int num = descs.size();
			for (int j = 0; j < num; j++) {
				String name = descs.get(j).getCategory();

				if (name != null)
					if (hash.get(name) == null)
						hash.put(name, name);
			}
		}

		for (Enumeration<String> enu = hash.keys(); enu.hasMoreElements();)
			names.add((String) enu.nextElement());

		return names;
	}

	/**
	 * Returns All the descriptor names of the given category
	 * 
	 * @author Bjorn Millard
	 * @return ArrayList<String> descNames
	 */
	static public ArrayList<String> getUniqueDescriptorNamesOfCategory(
			ArrayList<ExpDesign_Sample> samples, String category) {
		ArrayList<String> names = new ArrayList<String>();
		Hashtable<String, String> hash = new Hashtable<String, String>();

		int len = samples.size();
		for (int i = 0; i < len; i++) {
			ArrayList<ExpDesign_Description> descs = samples.get(i)
					.getDescriptions();
			int num = descs.size();
			for (int j = 0; j < num; j++) {
				String name = descs.get(j).getName();
				String cat = descs.get(j).getCategory();

				if (cat != null && cat.equalsIgnoreCase(category)
						&& name != null)
					if (hash.get(name) == null)
						hash.put(name, name);
			}
		}

		for (Enumeration<String> enu = hash.keys(); enu.hasMoreElements();)
			names.add((String) enu.nextElement());

		return names;
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
		String category = getStringValue(e, "category");


		ExpDesign_Description unit = new ExpDesign_Description();
		unit.setType(type);
		unit.setName(name);
		unit.setValue(value);
		unit.setUnits(units);
		unit.setTimeUnits(timeUnits);
		unit.setTime(timeVal);
		unit.setCategory(category);
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
