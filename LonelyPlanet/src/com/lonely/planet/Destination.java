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

	
	public String html(String path) {
		String s = lookup(path);
		return s == null ? null : s.replaceAll("\n", "<br>");
	}

	public String lookup(String path) {
		String[] strArr = path == null ? new String[] {} : path.split("/");
		Object obj = info;
		Map map;
		List list;
		int i, j;
		for (i = 0; i < strArr.length; i++) {
			if (obj instanceof String) {
				return (String) obj;
			} else if (obj instanceof Map) {
				map = (Map) obj;
				obj = map.get(strArr[i]);
			} else if (obj instanceof List) {
				list = (List) obj;
				j = Integer.parseInt(strArr[i]);
				if (j >= 0 && j <list.size())
					obj = list.get(j);
			}
		}
		if (obj == null){
			return null;
		}else{
			return obj.toString();
		}
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
