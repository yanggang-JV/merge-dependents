package org.webank.dependents.dto;

import java.util.List;

/**
 * 节点对象
 *  
 * @author Administrator
 *
 */
public class NodeObject {

	/**
	 * 节点层级
	 */
	private int level;

	/**
	 * 节点数据
	 */
	private String data;

	/**
	 * 父节点对象
	 */
	private NodeObject parent;

	/**
	 * 子节点集合
	 */
	private List<NodeObject> childNodeList;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public NodeObject getParent() {
		return parent;
	}

	public void setParent(NodeObject parent) {
		this.parent = parent;
	}

	public List<NodeObject> getChildNodeList() {
		return childNodeList;
	}

	public void setChildNodeList(List<NodeObject> childNodeList) {
		this.childNodeList = childNodeList;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public NodeObject(int level, String data, NodeObject parent) {
		super();
		this.level = level;
		this.data = data;
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "NodeObject [level=" + level + ", data=" + data + ", parent=" + parent + ", childNodeList="
				+ (childNodeList == null ? 0 : childNodeList.size()) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeObject other = (NodeObject) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
}
