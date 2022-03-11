import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;


public class HttpUtil {
    /**
     *
     * @param url
     * @param data
     * @return
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public static String doIgnoreVerifySSLPost(String url, JSONObject data, String headers)
        throws IOException, KeyManagementException, NoSuchAlgorithmException{

        SSLContext sslContext = SSLByPass.createIgnoreVerifySSL();
        final SSLConnectionSocketFactory sslsf;

        //设置协议http和https对应的处理socket链接工厂的对象
        sslsf = new SSLConnectionSocketFactory(sslContext,NoopHostnameVerifier.INSTANCE);
        final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build();

        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(100);

        //创建自定义的httpclient对象
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm)
                .build();

        String result = null;
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        //判断是否有headers
        if (headers == null || headers == ""){

        }
        else {
            JSONObject jsonObject = JSON.parseObject(headers);
            Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                httpPost.setHeader(entry.getKey().toString(),entry.getValue().toString());
            }
        }
        //装填参数
        StringEntity entity = new StringEntity(data.toString(), "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        //设置参数到请求对象中
        httpPost.setEntity(entity);
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200){
            //获取结果实体
            HttpEntity httpEntity = response.getEntity();
            //按指定编码转换结果实体为String类型
            result = EntityUtils.toString(httpEntity,"UTF-8");
        }
        response.close();
        return result;
    }


    /**
     * 绕过SSL证书，发送Get请求
     * @param url
     * @param params
     * @return
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public static String doIgnoreVerifySSLGet(String url, Map<String,Object> params)
            throws IOException, KeyManagementException, NoSuchAlgorithmException {
        //采用绕过验证的方式处理https请求
        SSLContext sslContext = SSLByPass.createIgnoreVerifySSL();
        final SSLConnectionSocketFactory sslsf;

        //设置协议http和https对应的处理socket链接工厂的对象
        sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build();

        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(100);

        //创建自定义的httpclient对象
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm)
                .build();

        String result = null;
        //装填参数
        StringBuffer param = new StringBuffer();
        if (params != null && !params.isEmpty()) {
            int i = 0;
            for (String key : params.keySet()) {
                if (i == 0) {
                    param.append("?");
                } else {
                    param.append("&");
                }
                param.append(key).append("=").append(params.get(key));
                i++;
            }
            url += param;
        }
        //创建get方式请求对象
        HttpGet httpGet = new HttpGet(url);
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200){
            //获取结果实体
            HttpEntity httpEntity = response.getEntity();
            //按指定编码转换结果实体为String类型
            result = EntityUtils.toString(httpEntity,"UTF-8");
        }

        //释放链接
        response.close();

        return result;
    }

}