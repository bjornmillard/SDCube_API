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

//import imagerailio.ProjectHDFConnector;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


/**
 * 
 * @author Bjorn Millard & Michael Menden
 *
 */
public class MetaDataXMLParser
{
	private MicrotiterPlate plate;
	
	/**
	 * Constructor.
	 * @throws MetaDataXMLParserException
	 * @throws FileNotFoundException
	 */
	public MetaDataXMLParser( String H5FilePath) throws MetaDataXMLParserException, FileNotFoundException
	{
//		FileReader xml = new FileReader( H5FilePath + "/Meta/ExpDesign.xml");
//		ProjectXMLHandler handler = new ProjectXMLHandler(projectPath, plateIdx);
//		XMLReader xr;
//		try
//		{
//			xr = XMLReaderFactory.createXMLReader();
//			xr.setContentHandler(handler);
//			xr.setErrorHandler(handler);
//			xr.parse(new InputSource(xml));
//			plate = handler.getPlate();
//		}
//		catch (SAXException e)
//		{
//			throw new MetaDataXMLParserException("Cannot parse the XML meta imagerailio file: " + e.getMessage());
//		}
//		catch (IOException e)
//		{
//			throw new MetaDataXMLParserException("Cannot open the XML meta imagerailio file: " + e.getMessage());
//		}
	}
	
	/**
	 * Get the project structure.
	 */
	public MicrotiterPlate getPlate()
	{
		return plate;
	}
	
	/**
	 * Nested class.
	 */
	public class MetaDataXMLParserException extends Exception
	{
		/**
		 * For serializing the Class
		 */
		private static final long serialVersionUID = 6671397455786252L;
		
		MetaDataXMLParserException(String msg)
		{
			super("MetaDataReaderException: " + msg);
		}
	}
	
	/**
	 * Nested class.
	 */
	class ProjectXMLHandler extends DefaultHandler
	{
		
		private final Logger logger = Logger.getLogger("xml.MetaDataParser.ProjectXMLHandler");
		
		private MicrotiterPlate plate;
		private int plateSize;
		
		private int wellID;
		private ExpDesign_Sample well;
		
		/**
		 * Constructor.
		 */
		public ProjectXMLHandler ( String projPath, int plateID)
		{
			// get size of the plate (necessary for index to well name converter)
			// try
			// {
			// plateSize = (new ProjectHDFConnector(new
			// File(projPath).getParentFile().getAbsolutePath())).readPlateSize(plateID);
			// }
			// catch (H5IO_Exception ex)
			// {
			// logger.log(Level.SEVERE,
			// "XML meta imagerailio reader cannot read plate size: ", ex);
			// }
		}
		
		/**
		 * Is called at the beginning of an XML tag.
		 */
		public void startElement(String namespaceURI, String localName,
								 String qName, Attributes attr)
		{
//			if (qName.equals("plate"))
//			{
//				plate = new MicrotiterPlate();
//			}
//			else if (qName.equals("well"))
//			{
//				wellID = IdxConverter.well2index( attr.getValue("id"), plateSize);
//				well = new ExpDesign_Sample(""+wellID);
//				plate.addWell(well);
//			}
//			else
//			{
//				String descName = attr.getValue("name");
//				String descValue = attr.getValue("value");
//				String descUnits = attr.getValue("units");
//				String descTimeValue = attr.getValue("time");
//				String descTimeUnits = attr.getValue("time_units");
//				Description desc = null;
//				// if ( descName == null)
//				// desc = new Description(qName, descValue);
//				// else if ( descValue == null)
//				// desc = new Description(qName, descName, "");
//				// else if (descUnits == null)
//				// desc = new Description(qName, descName, descValue, "",
//				// descTimeValue, descTimeUnits);
//				// else
//					desc = new Description(qName, descName, descValue, descUnits, descTimeValue, descTimeUnits);
//				
//				well.addDesc(desc);
//			}
		}
		
		/**
		 * Return the result.
		 */
		public MicrotiterPlate getPlate()
		{
			return plate;
		}
	}
}
