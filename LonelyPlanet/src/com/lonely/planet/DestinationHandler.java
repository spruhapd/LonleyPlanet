package com.lonely.planet;

import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;

public class DestinationHandler implements ElementHandler {

	private ElementParser<Destination> parser;
	private Renderer<Destination> renderer;

	public DestinationHandler(ElementParser<Destination> parser,
	    Renderer<Destination> renderer) {
		super();
		this.parser = parser;
		this.renderer = renderer;
	}

	@Override
	public void onEnd(ElementPath path) {
		Element node = path.getCurrent();
		try {
			Destination d = parser.parseElement(node);
			renderer.render(d);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			node.detach();
		}
	}

	@Override
	public void onStart(ElementPath path) {
		Element node = path.getCurrent();
		node.detach();
	}
}
