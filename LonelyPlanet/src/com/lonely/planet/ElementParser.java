package com.lonely.planet;

import org.dom4j.Element;

public interface ElementParser<T> {

	public T parseElement(Element ele);

}
