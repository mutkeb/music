package fm.douban.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class HttpUtil {
    //  构建必要的http，header
    public Map<String,String> buildHeader(String referer,String host,String cookie){
        Map<String,String> header = new HashMap<>();
        if(!referer.isEmpty()){
            header.put("referer",referer);
        }
        if(!host.isEmpty()){
            header.put("host",host);
        }
        if(!cookie.isEmpty()){
            header.put("cookie",cookie);
        }
        return header;
    }

    //  根据输入的url，读取页面内容并返回
    public String getContent(String url,Map<String,String> headers){
        //  okHttpClients实例
        OkHttpClient okHttpClient = new OkHttpClient();
        //  定义一个request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Referer",headers.get("referer"))
                .addHeader("Host",headers.get("host"))
                .addHeader("Cookie",headers.get("cookie"))
                .build();
        //  返回结果字符串
        String result = null;
        try {
            //  执行请求
            Response response = okHttpClient.newCall(request).execute();
            result = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
