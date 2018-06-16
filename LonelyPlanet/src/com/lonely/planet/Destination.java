package com.lonely.planet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Destination {

	private String atlasId;
	private String assetId;
	private String title;
	private String titleAscii;
	private Map<String, Object> info = new LinkedHashMap<String, Object>();

	public String getAtlasId() {
		return atlasId;
	}

	public void setAtlasId(String atlasId) {
		this.atlasId = atlasId;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleAscii() {
		return titleAscii;
	}

	public void setTitleAscii(String titleAscii) {
		this.titleAscii = titleAscii;
	}

	public Map<String, Object> getInfo() {
		return info;
	}

	public void setInfo(Map<String, Object> info) {
		this.info = info;
	}

	/**
	 * lookup content in info map and transfer it to html format \n -> <br>
	 * etc.
	 * 
	 * @param path
	 * @return
	 */
	public String html(String path) {
		String s = lookup(path);
		return s == null ? null : s.replaceAll("\n", "<br>");
	}

	/**
	 * lookup content in info map
	 * 
	 * @param path
	 * @return
	 */
	public String lookup(String path) {
		String[] ss = path == null ? new String[] {} : path.split("/");
		Object v = info;
		Map m;
		List l;
		int i, k;
		for (i = 0; i < ss.length; i++) {
			if (v instanceof String) {
				return (String) v;
			} else if (v instanceof Map) {
				m = (Map) v;
				v = m.get(ss[i]);
			} else if (v instanceof List) {
				l = (List) v;
				k = CommonUtil.getInt(ss[i]); // k = 0 if ss[i] is not a number
				if (k >= 0 && k < l.size())
					v = l.get(k);
			}
		}
		return (v == null) ? null : v.toString();
	}

	public String getContent() {
		return lookup("introductory/introduction/overview");
	}

	public void setContent() {
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append("Destination(")
		    .append("atlasId=").append(atlasId).append(",assetId=").append(assetId)
		    .append(",title=").append(title).append(",titleAscii=")
		    .append(titleAscii).append(",info=").append(info).append(")");
		return sb.toString();
	}
}
