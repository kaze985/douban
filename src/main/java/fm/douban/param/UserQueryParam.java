package fm.douban.param;

import fm.douban.model.User;

public class UserQueryParam extends User {
    private int pageNum=1;
    private int pageSize=10;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
