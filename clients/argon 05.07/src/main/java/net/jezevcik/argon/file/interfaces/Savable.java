package net.jezevcik.argon.file.interfaces;

import com.alibaba.fastjson2.JSONObject;

public interface Savable {

    JSONObject getData();

    void setData(JSONObject object);

}