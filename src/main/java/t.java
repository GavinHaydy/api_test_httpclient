import com.alibaba.fastjson.JSONObject;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class t {
    @Test(enabled = true, description = "测试绕过SSL证书Post方法")
    public void doIgnoreVerifySSLPostTest() throws IOException, NoSuchAlgorithmException, KeyManagementException{
        String url = "http://139.9.221.111/api/user/login";
        JSONObject param = new JSONObject();
        param.put("phone","13558622779");
        param.put("password","2d3383fa392936ad7847c50a0bb4a58e");
//        String headers = "{\"cs\":\"菜鸟教程\",\"rs\":\"135468245\"}";
        String headers = "";

        //调用方法
        String response = HttpUtil.doIgnoreVerifySSLPost(url, param, headers);
        //断言返回结果是否为空
//        Assert.assertNotNull(response);
        System.out.println("【doIgnoreVerifySSLPost】"+response);
    }
}