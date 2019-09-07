package vip.wush.zk.watcher;

import com.alibaba.fastjson.JSONArray;

/**
 * @ClassName: model
 * @Description: TODO
 * @Author: wush
 * @Date: 2019/7/16 18:00
 */

public class Model {

    private String name;

    private int age;


    public String toJson(){
        return JSONArray.toJSONString(this);
    }


    public static Model fromJson(String json){
        return JSONArray.parseObject(json, Model.class);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
