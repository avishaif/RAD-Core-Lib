package normalizers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import core.Constants;

public class Normalizer
{
	private Map<Integer, int[]> values;
	private Logger log;
	private final String os;

	public Normalizer(String os)
	{
		this.log = LogManager.getRootLogger();
		this.values = new HashMap<Integer, int[]>();
		this.os = os;
	}

	/**
	 * verifies whether the configuration file is valid.
	 * @return boolean true if valid false otherwise.
	 */
	public boolean checkSchema()
	{
		SchemaFactory inputScheme = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try
		{
			Schema inSchema = inputScheme.newSchema(new File("PriorityConfiguration.xsd"));
			Validator validator = inSchema.newValidator();
			validator.validate(new StreamSource(new File("PriorityConfiguration.xml")));
		} catch (SAXParseException e)
		{
			return false;
		} catch (SAXException e)
		{
			return false;
		} catch (IOException e)
		{
			return false;
		} catch (Exception e)
		{
			return false;
		}

		return true;
	}

	/**
	 * Creates a HashMap holding mapping values received from the configuration file.
	 * @return boolean true if mapping process was successful, false otherwise.
	 */
	public boolean mapValues()
	{

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Element root = null;
		Element element = null;
		DocumentBuilder db;
		NodeList nodelist;
		try
		{
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			if (log.isErrorEnabled())
				log.error(e);
			return false;
		}
		Document doc = null;
		try
		{
			doc = db.parse("PriorityConfiguration.xml");
		} catch (SAXException | IOException e)
		{
			e.printStackTrace();
			if (log.isErrorEnabled())
				log.error(e);
			return false;
		}
		int nodeCounter = 0;
		root = doc.getDocumentElement();
		nodelist = root.getElementsByTagName(os);
		element = (Element) nodelist.item(0);
		nodelist = element.getElementsByTagName("Asynchronized");
		element = (Element) nodelist.item(0);
		int policy = Integer.parseInt(element.getAttribute("PolicyValue"));
		nodelist = element.getElementsByTagName("priority");
		element = (Element) nodelist.item(0);
		for (int i = 0; i < nodelist.getLength(); i++)
		{
			nodeCounter++;
			element = (Element) nodelist.item(i);
			values.put(nodeCounter,
					new int[] {
							Integer.parseInt(element.getAttribute("value")),
							policy });

		}

		nodelist = root.getElementsByTagName(os);
		element = (Element) nodelist.item(0);
		nodelist = element.getElementsByTagName("SoftRT");
		element = (Element) nodelist.item(0);
		policy = Integer.parseInt(element.getAttribute("PolicyValue"));
		nodelist = element.getElementsByTagName("priority");
		for (int i = 0; i < nodelist.getLength(); i++)
		{
			nodeCounter++;
			element = (Element) nodelist.item(i);
			values.put(nodeCounter,
					new int[] {
							Integer.parseInt(element.getAttribute("value")),
							policy });

		}
		nodelist = root.getElementsByTagName(os);
		element = (Element) nodelist.item(0);
		nodelist = element.getElementsByTagName("HardRT");
		element = (Element) nodelist.item(0);
		policy = Integer.parseInt(element.getAttribute("PolicyValue"));
		nodelist = element.getElementsByTagName("priority");
		for (int i = 0; i < nodelist.getLength(); i++)
		{
			nodeCounter++;
			element = (Element) nodelist.item(i);
			values.put(nodeCounter,
					new int[] {
							Integer.parseInt(element.getAttribute("value")),
							policy });
		}

		return true;

	}

	/**
	 * returns the mapped values as represented for Linux OS.
	 * @param int value to receive a mapping to.
	 * @return int array containing the mapped policy/priority values.
	 */
	public int[] normalize(int value)
	{
		int[] vals = values.get(value);
		return vals;
	}

	/**
	 * returns the mapped value as represented for Windows OS.
	 * @param value int value to receive a mapping to.
	 * @param isProcess boolean indicating whether the mapping is intended for a process or a thread.
	 * @return int value of the mapped priority.
	 */
	public int normalize(int value, boolean isProcess)
	{
		int[] vals = null;
		if (isProcess)
		{
			vals = values.get(value);
			if (vals[0] == 1)
				return Constants.IDLE_PRIORITY_CLASS;
			if (vals[0] == 2)
				return Constants.BELOW_NORMAL_PRIORITY_CLASS;
			if (vals[0] == 3)
				return Constants.NORMAL_PRIORITY_CLASS;
			if (vals[0] == 4)
				return Constants.ABOVE_NORMAL_PRIORITY_CLASS;
			if (vals[0] == 5)
				return Constants.HIGH_PRIORITY_CLASS;
			if (vals[0] == 6)
				return Constants.REALTIME_PRIORITY_CLASS;
			if (vals[0] == 7)
				return Constants.REALTIME_PRIORITY_CLASS;
		} else
		{
			vals = values.get(value);
			if (vals[0] == 1)
				return Constants.THREAD_PRIORITY_IDLE;
			if (vals[0] == 2)
				return Constants.THREAD_PRIORITY_LOWEST;
			if (vals[0] == 3)
				return Constants.THREAD_PRIORITY_BELOW_NORMAL;
			if (vals[0] == 4)
				return Constants.THREAD_PRIORITY_NORMAL;
			if (vals[0] == 5)
				return Constants.THREAD_PRIORITY_ABOVE_NORMAL;
			if (vals[0] == 6)
				return Constants.THREAD_PRIORITY_HIGHEST;
			if (vals[0] == 7)
				return Constants.THREAD_PRIORITY_TIME_CRITICAL;
		}

		return -1;
	}
}
