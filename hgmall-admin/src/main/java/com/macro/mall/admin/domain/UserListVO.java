package com.macro.mall.admin.domain;

import java.util.List;

/**
 * 用户列表VO
 */
public class UserListVO {
    private List<UserVO> users;
    private Long total;
    private Integer page;
    private Integer size;

    public List<UserVO> getUsers() {
        return users;
    }

    public void setUsers(List<UserVO> users) {
        this.users = users;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}











