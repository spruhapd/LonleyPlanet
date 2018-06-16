package com.lonely.planet;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestinationParser implements ElementParser<Destination> {

	private static final Logger logger = LoggerFactory
	    .getLogger(DestinationParser.class);

	@Override
	public Destination parseElement(Element ele) {
		Destination d = new Destination();
		try {
			d.setAtlasId(ele.attributeValue("atlas_id"));
			d.setAssetId(ele.attributeValue("asset_id"));
			d.setTitle(ele.attributeValue("title"));
			d.setTitleAscii(ele.attributeValue("title-ascii"));
			parseInfo(ele, d.getInfo());
			// if ("Africa".equals(d.getTitle())) throw new
			// GenericException("test parse exception!");
		} catch (Exception e) {
			logger.error("parse exception {} on element\n{}", e, ele.asXML());
		}
		return d;
	}

	/**
	 * recursively parse other information as a map
	 * 
	 * @param e
	 * @param info
	 * @return
	 */
	protected Object parseInfo(Element e, Map<String, Object> info) {
		String name = e.getName();
		if (e.isTextOnly()) {
			String text = e.getText();
			info.put(name, text);
			return text;
		} else {
			Iterator it = e.elementIterator();
			Element ce;
			String cn;
			List cl;
			Object cv;
			Map<String, Object> m = new LinkedHashMap<String, Object>();
			while (it.hasNext()) {
				ce = (Element) it.next();
				cn = ce.getName();
				if (info.containsKey(cn)) { // already has one, turn to list
					cv = info.get(cn);
					if (cv instanceof List) {
						cl = (List) cv;
					} else {
						cl = new LinkedList();
						cl.add(cv);
						info.put(cn, cl);
					}
					cl.add(parseInfo(ce, m));
				} else {
					info.put(cn, parseInfo(ce, m));
				}
			}
			return info;
		}
	}
}
