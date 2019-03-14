package org.webank.dependents.util;

public class NodeUtl {

	public static final String EMPTY = "";

	public static final String NEW_LINE = "\r\n";

	public static final String CONNECTOR_LICENSE = "~~~~~";

	public static final String DEPENDENTS_BEGIN = "+---";

	public static final String DEPENDENTS_END = "\\---";

	public static final String DEPENDENTS_CONCAT_CHAR = "-";

	public static final String DEPENDENTS_SEPARATOR_CHAR = "|";

	public static final String TABLE_CHAR = "\t";

	public static final String SEPARATOR_CHAR = "--- ";

	public static final Integer SEPARATOR_CHAR_NUMBER = 5;

	public static int getLevel(String line) {
		int index = line.indexOf(SEPARATOR_CHAR);
		return index / SEPARATOR_CHAR_NUMBER + 1;
	}

	public static String getString(String line) {
		if (line != null) {
			return line.substring(getLevel(line) * SEPARATOR_CHAR_NUMBER);
		}
		return NodeUtl.EMPTY;
	}

	public static String getArtifact(String line) {
		String data = NodeUtl.getString(line);
		StringBuffer sb = new StringBuffer();
		String[] datas = data.split(":");
		if (datas.length == 3) { // 标准情况
			sb.append(datas[0]).append("/").append(datas[1]).append("/");
			String version = datas[2];
			version = processIg(version,"(*)");
			version = processIg(version,"(n)");
			version = processChange(version);
			sb.append(version);
			return sb.toString();
		} else {
			if (datas[1].indexOf("->") >= 0) {
				String[] versions = datas[1].split("->");
				sb.append(datas[0]).append("/").append(versions[0].trim()).append("/").append(versions[1].trim());
			} else if (datas[1].indexOf("(n)") >= 0) {
				String name = datas[1];
				name = processIg(name,"(*)");
				name = processIg(name,"(n)");
				sb.append(datas[0]).append("/").append(name);
			}
			return sb.toString();
		}	
	}

	private static String processIg(String version,String regex) {
		if (version.indexOf(regex) > 0) {// 鍖呭惈 (*) 蹇界暐渚濊禆
			return version.replace(regex, "").trim();
		}
		return version;
	}

	private static String processChange(String version) {
		if (version.indexOf("->") > 0) { // 鍖呭惈鐗堟湰鎸囧悜
			String[] versions = version.split("->");
			return versions[1].trim();
		}
		return version;
	}

}
