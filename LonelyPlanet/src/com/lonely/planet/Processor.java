package com.lonely.planet;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class Processor implements Renderer<Destination> {

	private static final Logger logger = LoggerFactory.getLogger(Processor.class);

	private File taxonomyXml;
	private File destinationXml;
	private File outputDir;
	private List<Taxonomy> taxonomies;
	private Template temp;

	public Processor(String taxonomy, String destination, String output)
	    throws GenericException {
		taxonomyXml = new File(taxonomy);
		if (!taxonomyXml.canRead()) {
			throw new GenericException(
			    "Taxonomy file (%s) is not readable, please check!", taxonomy);
		}
		destinationXml = new File(destination);
		if (!destinationXml.canRead()) {
			throw new GenericException(
			    "Destinations file (%s) is not readable, please check!", destination);
		}
		outputDir = new File(output);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		if (!outputDir.canWrite()) {
			throw new GenericException(
			    "Output directory (%s) is not writable, please check!", output);
		}
		taxonomies = new ArrayList<Taxonomy>();
	}

	protected void initTemplate() throws Exception {
		Configuration conf = new Configuration();
		conf.setTemplateLoader(new ClassTemplateLoader(Processor.class, ""));
		conf.setObjectWrapper(new DefaultObjectWrapper());
		temp = conf.getTemplate("destination.ftl");
	}

	/**
	 * recursively process node
	 * 
	 * @param ele
	 * @return
	 * @throws Exception
	 */
	protected Node processNode(Element ele, Taxonomy taxonomy) throws Exception {
		Node node = new Node();
		node.setName(ele.elementText("node_name"));
		node.setNodeId(ele.attributeValue("atlas_node_id"));
		node.setObjectId(ele.attributeValue("ethyl_content_object_id"));
		node.setGeoId(ele.attributeValue("geo_id"));
		Iterator it = ele.elementIterator("node");
		Node child;
		while (it.hasNext()) {
			child = processNode((Element) it.next(), taxonomy);
			node.addChild(child);
			taxonomy.register(child);
		}
		return node;
	}

	/**
	 * Parse taxonomy.xml and store all taxnomies with nodes in taxonomies.
	 * Supposely this xml file is not huge and taxnomies are required during
	 * navigation lookup and generation for destinations, so a list is used to
	 * hold all parsed taxonomy data.
	 * 
	 * @throws Exception
	 */
	protected void processTaxonomy() throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(taxonomyXml);
		Element root = doc.getRootElement();
		Iterator it1, it2;
		Element ele1, ele2;
		Taxonomy taxonomy;
		String name;
		it1 = root.elementIterator("taxonomy");
		while (it1.hasNext()) {
			ele1 = (Element) it1.next();
			taxonomy = new Taxonomy();
			name = ele1.elementText("taxonomy_name");
			taxonomy.setName(name);
			it2 = ele1.elementIterator("node");
			while (it2.hasNext()) {
				ele2 = (Element) it2.next();
				taxonomy.addNode(processNode(ele2, taxonomy));
			}
			taxonomies.add(taxonomy);
		}
	}

	/**
	 * render the destination using template to generate the html page using
	 * freemarker
	 */
	@Override
	public void render(Destination d) {
		try {
			Taxonomy taxonomy = null;
			Node node = null;
			// atlas_id is unique and linked to node's atlas_node_id
			for (Taxonomy t : taxonomies) {
				Node n = t.lookup(d.getAtlasId());
				if (n != null) {
					taxonomy = t;
					node = n;
				}
			}
			if (taxonomy == null || node == null) {
				throw new GenericException("%s(%s) cannot be found in taxonomy file!",
				    d.getAtlasId(), d.getTitle());
			}
			// set render context
			// parent nodes for parent navigation
			List<Node> ancestors = new LinkedList<Node>();
			Node p = node.getParent();
			while (p != null) {
				ancestors.add(0, p);
				p = p.getParent();
			}
			List<Node> children = node.getChildren();
			Map<String, Object> ctx = new HashMap<String, Object>();
			ctx.put("destination", d);
			ctx.put("ancestors", ancestors); // ancestors navigation
			ctx.put("children", children); // children navigations
			// destination's atlas_id == node's atlas_node_id
			// different folder structures and file name strategies can be used
			File tf = new File(outputDir, taxonomy.getName());
			if (!tf.exists())
				tf.mkdirs();
			File df = new File(tf, d.getAtlasId() + ".html");
			Writer w = new FileWriter(df);
			temp.process(ctx, w);
			w.flush();
			w.close();
			// if ("355064".equals(d.getAtlasId())) throw new
			// GenericException("test render exception!");
			logger.info("processed {}: ancestors({}), children({})", d.getTitle(),
			    ancestors.size(), children.size());
			// set d = null for the favour of gc
			d = null;
		} catch (Exception e) {
			logger.error("render exception {} on destination\n{}", e, d);
		}
	}

	protected void processDestination() throws Exception {
		SAXReader reader = new SAXReader();
		ElementHandler handler = new DestinationHandler(new DestinationParser(),
		    this);
		reader.addHandler("/destinations/destination", handler);
		reader.read(destinationXml);
	}

	public void process() throws Exception {
		initTemplate();
		processTaxonomy();
		processDestination();
	}

	protected static void help() {
		StringBuilder sb = new StringBuilder()
		    .append("Syntax: \n")
		    .append("processor ")
		    .append("<taxonomy xml> <destinations xml> <output directory>\n")
		    .append("  taxonomy xml - xml file name for taxonomy data\n")
		    .append("  destinations xml - xml file name for destinations data\n")
		    .append(
		        "  output directory - output directory to save generated html files\n");
		System.out.println(sb.toString());
	}

	protected static void error(String format, Object... tokens) {
		System.err.println(String.format(format, tokens));
	}

	public static void main(String[] argv) throws Exception {
		
		argv = new String[] { "taxonomy.xml", "destinations.xml", "output", };
		
		if (argv.length != 3) {
			help();
			return;
		}
		try {
			Processor p = new Processor(argv[0], argv[1], argv[2]);
			p.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
