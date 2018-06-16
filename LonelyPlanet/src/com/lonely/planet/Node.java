package com.lonely.planet;

import java.util.ArrayList;
import java.util.List;

public class Node {

	private String nodeId;
	private String objectId;
	private String geoId;
	private String name;
	private List<Node> children = new ArrayList<Node>();;
	private Node parent;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getGeoId() {
		return geoId;
	}

	public void setGeoId(String geoId) {
		this.geoId = geoId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public void addChild(Node child) {
		child.setParent(this);
		children.add(child);
	}

	public void removeChild(Node child) {
		children.remove(child);
	}

	public void clearChildren() {
		children.clear();
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append("(name=").append(name)
		    .append(",nodeId=").append(nodeId).append(",objectId=")
		    .append(objectId).append(",geoId=").append(geoId)
		    .append(",children=[\n");
		for (Node child : children) {
			sb.append(child).append(",\n");
		}
		if (children.size() > 0) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("])");
		return sb.toString();
	}
}
