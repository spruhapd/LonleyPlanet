package com.lonely.planet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Taxonomy {

	private String taxName;
	private List<Node> nodes = new ArrayList<Node>();
	private Map<String, Node> dictionary = new HashMap<String, Node>();

	public String getTaxName() {
		return taxName;
	}

	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

		public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
		for (Node node : nodes) {
			dictionary.put(node.getNodeId(), node);
		}
	}

	public void addNode(Node node) {
		nodes.add(node);
		dictionary.put(node.getNodeId(), node);
	}

	public void removeNode(Node node) {
		nodes.remove(node);
		dictionary.remove(node.getNodeId());
	}

	public void clearNodes() {
		nodes.clear();
		dictionary.clear();
	}

	public void register(Node node) {
		dictionary.put(node.getNodeId(), node);
	}

	public void unregister(Node node) {
		dictionary.remove(node.getNodeId());
	}

	/**
	 * lookup the node in taxonomy by node's name assuming node's name is unique
	 * 
	 * @param id
	 * @return
	 */
	public Node lookup(String id) {
		return dictionary.get(id);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append("Taxonomy(name=")
		    .append(taxName).append(",nodes=[");
		for (Node node : nodes) {
			sb.append(node).append(",\n");
		}
		if (nodes.size() > 0) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("])");
		return sb.toString();
	}
}
