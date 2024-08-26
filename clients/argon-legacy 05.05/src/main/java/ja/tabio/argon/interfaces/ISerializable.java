package ja.tabio.argon.interfaces;

import com.alibaba.fastjson2.JSONObject;

public interface ISerializable {

    JSONObject serialize();

    void deserialize(JSONObject jsonObject);

}
