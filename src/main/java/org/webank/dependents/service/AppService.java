package org.webank.dependents.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.webank.dependents.dto.NodeObject;
import org.webank.dependents.util.FileUtil;
import org.webank.dependents.util.LicenseUtl;
import org.webank.dependents.util.NodeUtl;

public class AppService {

	public static List<String> parserDependents(String filePath) {
		BufferedReader bis = null;
		List<String> dataList = new ArrayList<String>();
		try {
			bis = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
			String line = null;

			while ((line = bis.readLine()) != null) {
				if (line.indexOf(NodeUtl.DEPENDENTS_BEGIN) >= 0 || line.indexOf(NodeUtl.DEPENDENTS_END) >= 0) {
					StringBuffer buffStr = new StringBuffer();
					buffStr.append(line);
					buffStr.append(NodeUtl.TABLE_CHAR).append(NodeUtl.CONNECTOR_LICENSE)
							.append(NodeUtl.getArtifact(line));
					dataList.add(buffStr.toString());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return dataList;
	}

	public static String meger(List<String> dataList) throws Exception {

		List<NodeObject> nodeList = new ArrayList<NodeObject>();
		// 解析根节点
		for (int j = 0; j < dataList.size(); j++) {
			int currentLevel = NodeUtl.getLevel(dataList.get(j)); // 获取当前层级
			int index = j;
			NodeObject parentNode = null;
			if (currentLevel != 1) {// 说明有父节点
			    // 找到其父节点
				int parentIndex = 0;
				while (true) {
					index--;
					parentIndex = NodeUtl.getLevel(nodeList.get(index).getData());
					if (parentIndex == (currentLevel - 1)) {
						break;
					}
				}
				parentNode = nodeList.get(index); // 获取父节点
			}
			NodeObject currentNode = new NodeObject(currentLevel, dataList.get(j), parentNode);
			nodeList.add(currentNode);
		}

		for (NodeObject node : nodeList) {
			String newData = node.getData().substring(node.getLevel() * NodeUtl.SEPARATOR_CHAR_NUMBER);
			node.setData(newData);
		}

		List<NodeObject> root = new ArrayList<NodeObject>();
		// 将一级节点放入root中
		for (NodeObject node : nodeList) {
			if (node.getLevel() == 1 && !root.contains(node)) {
				root.add(node);
			}
		}

		buildNode(root, nodeList);
		StringBuffer sb = new StringBuffer();
		buildResult(root, sb);
		return sb.toString();
	}

	private static void buildResult(List<NodeObject> root, StringBuffer sb) {

		for (NodeObject nodeObject : root) {
			for (int i = 0; i < (nodeObject.getLevel() - 1) * NodeUtl.SEPARATOR_CHAR_NUMBER; i++) {
				if (i % NodeUtl.SEPARATOR_CHAR_NUMBER == 0) {
					sb.append(NodeUtl.DEPENDENTS_SEPARATOR_CHAR);
				} else {
					sb.append(NodeUtl.DEPENDENTS_CONCAT_CHAR);
				}
			}
			sb.append(nodeObject.getData()).append(NodeUtl.NEW_LINE);
			if (nodeObject.getChildNodeList() != null) {
				buildResult(nodeObject.getChildNodeList(), sb);
			}
		}
	}

	private static void buildNode(List<NodeObject> root, List<NodeObject> allNode) {
		// 重新构建子节点
		for (int i = 0; i < allNode.size(); i++) {
			NodeObject currentNode = allNode.get(i);
			if (currentNode.getParent() != null) {
				buildChild(root, allNode.get(i), currentNode.getParent());
			}
		}
	}

	private static void buildChild(List<NodeObject> nodeList, NodeObject currentNode, NodeObject pNode) {

		if (nodeList == null || nodeList.size() == 0) {
			return;
		}

		for (NodeObject nodeObject2 : nodeList) {
			if (nodeObject2.equals(pNode)) {
				if (nodeObject2.getChildNodeList() == null) {
					nodeObject2.setChildNodeList(new ArrayList<NodeObject>());
				}
				if (!nodeObject2.getChildNodeList().contains(currentNode)) {
					nodeObject2.getChildNodeList().add(currentNode);
				}
			} else {
				if (nodeObject2.getChildNodeList() != null && nodeObject2.getChildNodeList().size() > 0) {
					buildChild(nodeObject2.getChildNodeList(), currentNode, pNode);
				}
			}
		}
	}

	public static String buildResultWithLicense(String data) {

		String[] datas = data.split(NodeUtl.NEW_LINE);
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < datas.length; i++) {
			String value = datas[i];
			String[] values = value.split(NodeUtl.CONNECTOR_LICENSE);
			String artifact = values[1];
			String license = LicenseUtl.cacheLicenseMap.get(artifact);
			if(license == null || license.equals(LicenseUtl.DEFAULT_LICENSE)) {
				license = LicenseUtl.getLicense(artifact);
			}
			LicenseUtl.cacheLicenseMap.put(artifact, license);
			System.out.println(artifact + NodeUtl.CONNECTOR_LICENSE + license);
			result.append(values[0]).append(NodeUtl.CONNECTOR_LICENSE).append(NodeUtl.TABLE_CHAR).append(license)
					.append(NodeUtl.NEW_LINE);
		}
		
		//更新本地license缓存
		LicenseUtl.updateLocateCache();
		
		return result.toString();
	}

	public static void outPutResult(String data, String outPutPath) {
		FileUtil.saveData(data, outPutPath);
	}
}
