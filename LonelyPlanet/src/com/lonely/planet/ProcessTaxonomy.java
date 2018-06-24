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

public class ProcessTaxonomy implements Renderer<Destination> {

	private static final Logger log = LoggerFactory.getLogger(ProcessTaxonomy.class);

	private File taxonomyXml;
	private File destinationXml;
	private File outputDir;
	private List<Taxonomy> taxonomies;
	private Template temp;

	public ProcessTaxonomy(String taxonomyFile, String destinationFile, String output)
	    throws LonelyPlanetException {
		taxonomyXml = new File(taxonomyFile);
		if (!taxonomyXml.canRead()) {
			throw new LonelyPlanetException("Issue while readig Taxonomy file ");
		}
		destinationXml = new File(destinationFile);
		if (!destinationXml.canRead()) {
			throw new LonelyPlanetException( "Issue wile reading Destinations file");
		}
		outputDir = new File(output);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		taxonomies = new ArrayList<Taxonomy>();
	}

	protected void init() throws Exception {
		Configuration conf = new Configuration();
		conf.setTemplateLoader(new ClassTemplateLoader(ProcessTaxonomy.class, ""));
		conf.setObjectWrapper(new DefaultObjectWrapper());
		temp = conf.getTemplate("lonelyplnetConf.ftl");
	}

	/**
	 * Process node recursively
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
	 * Parse taxonomy.xml and add the taxnomies.
	 * @throws Exception
	 */
	protected void processInputTaxonomy() throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(taxonomyXml);
		Element root = doc.getRootElement();
		Iterator it1, it2;
		Element ele1, ele2;
		Taxonomy taxonomy;
		String eleName;
		it1 = root.elementIterator("taxonomy");
		while (it1.hasNext()) {
			ele1 = (Element) it1.next();
			taxonomy = new Taxonomy();
			eleName = ele1.elementText("taxonomy_name");
			taxonomy.setTaxName(eleName);
			it2 = ele1.elementIterator("node");
			while (it2.hasNext()) {
				ele2 = (Element) it2.next();
				taxonomy.addNode(processNode(ele2, taxonomy));
			}
			taxonomies.add(taxonomy);
		}
	}

	/**
	 * Generate the html page 
	 */
	@Override
	public void render(Destination dest) {
		try {
			Taxonomy taxonomy = null;
			Node node = null;
			for (Taxonomy t : taxonomies) {
				Node n = t.lookup(dest.getAtlasId());
				if (n != null) {
					taxonomy = t;
					node = n;
				}
			}
			if (taxonomy == null || node == null) {
				throw new LonelyPlanetException("Can not found details taxonomy file " +
						dest.getAtlasId() + " , "+ dest.getTitle());
			}
			
			List<Node> ancestors = new LinkedList<Node>();
			Node parent = node.getParent();
			while (parent != null) {
				ancestors.add(0, parent);
				parent = parent.getParent();
			}
			List<Node> children = node.getChildren();
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("destination", dest);
			context.put("ancestors", ancestors); 
			context.put("children", children);
			File tFile = new File(outputDir, taxonomy.getTaxName());
			if (!tFile.exists())
				tFile.mkdirs();
			File destFile = new File(tFile, dest.getAtlasId() + ".html");
			Writer writer = new FileWriter(destFile);
			temp.process(context, writer);
			writer.flush();
			writer.close();
			dest = null;
		} catch (Exception e) {
			log.error("Exception "+e.getMessage());
		}
	}

	protected void processDestFile() throws Exception {
		SAXReader reader = new SAXReader();
		ElementHandler eleHandler = new DestinationHandler(new DestinationParser(),
		    this);
		reader.addHandler("/destinations/destination", eleHandler);
		reader.read(destinationXml);
	}

	public void execute() throws Exception {
		init();
		processInputTaxonomy();
		processDestFile();
	}

	public static void main(String[] args) throws Exception {
		args = new String[] { "taxonomy.xml", "destinations.xml", "output" };
		try {
			ProcessTaxonomy pt = new ProcessTaxonomy(args[0], args[1], args[2]);
			pt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
