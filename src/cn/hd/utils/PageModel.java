package cn.hd.utils;

import java.util.List;

/**
 * 分页条目的JavaBean
 * @author xzl
 * @param <T>
 *
 */
public class PageModel<T> {
	/**
	 * 表示的是分页的数据
	 */
	private List<T> result;
	/**
	 * 当前页面
	 */
	private int currentPage;
	/**
	 * 每页显示的记录数
	 */
	private int pageSize;
	/**
	 * 总记录数
	 */
	private int totalCount;
	/**
	 * 总页数
	 */
	private int pageCount;
	public PageModel(List<T> result, int currentPage, int pageSize, int totalCount, int pageCount) {
		super();
		this.result = result;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.pageCount = pageCount;
	}
	public PageModel() {
		super();
	}
	public List<T> getResult() {
		return result;
	}
	public void setResult(List<T> result) {
		this.result = result;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getPageCount() {
		//计算总页数
		return (this.totalCount-1)/pageSize+1;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	@Override
	public String toString() {
		return "PageModel [result=" + result + ", currentPage=" + currentPage + ", pageSize=" + pageSize
				+ ", totalCount=" + totalCount + ", pageCount=" + pageCount + "]";
	}

}

