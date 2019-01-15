package vn.ifactory.rxjavawithretrofitexample.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SonLV on 01/14/2019.
 */


public class ToDo {
    @SerializedName("todoId")
    private int todoId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("create_date")
    private String createDate;

    @SerializedName("userId")
    private int userId;

    public int getTodoId() {
        return todoId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
