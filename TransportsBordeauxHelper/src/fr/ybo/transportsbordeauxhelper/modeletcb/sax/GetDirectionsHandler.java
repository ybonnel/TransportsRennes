package fr.ybo.transportsbordeauxhelper.modeletcb.sax;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GetDirectionsHandler extends DefaultHandler {

	private static final String FORWARD = "forward";
	private static final String BACKWARD = "backward";
	private static final String OPTION = "option";

	private StringBuilder contenu;
	private String directionForward;
	private String directionBackward;
	private boolean forward = false;
	private boolean backward = false;

	
	
	/**
	 * @return the directionForward
	 */
	public final String getDirectionForward() {
		return this.directionForward;
	}

	/**
	 * @return the directionBackward
	 */
	public final String getDirectionBackward() {
		return this.directionBackward;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		contenu.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if (OPTION.equals(qName)) {
			if (forward) {
				directionForward = contenu.toString();
				forward = false;
			}
			if (backward) {
				directionBackward = contenu.toString();
				backward = false;
			}
		}
		contenu.setLength(0);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		contenu = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (qName.equals(OPTION)) {
			String value = attributes.getValue("value");
			if (FORWARD.equals(value)) {
				forward = true;
			}
			if (BACKWARD.equals(value)) {
				backward = true;
			}
		}
		contenu.setLength(0);
	}
}
