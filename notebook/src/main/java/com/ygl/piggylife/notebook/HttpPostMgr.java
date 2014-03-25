package com.ygl.piggylife.notebook;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Post请求管理类
 * 可支持异步操作
 * Created by yanggavin on 14-3-20.
 */
public class HttpPostMgr {
    private String _postResContent = "";
    private String _url = "";
    private List<NameValuePair> _nvp = null;


    public HttpPostMgr(String url, List<NameValuePair> nvp){
        _url = url;
        _nvp = nvp;
    }

    /**
     * 获取POST请求返回结果字符串
     * @return
     */
    public String GetPostResContent(){
        if (!SysUtil.StringIsEmpty(_url) && _nvp != null){

            Thread thread = new Thread(){
                @Override
                public void run(){
                    InputStream inputStream = null;
                    HttpResponse mHttpResponse = null;
                    HttpEntity mHttpEntity = null;
                    try
                    {
                        // HttpPost对象实例
                        HttpPost httpPost = new HttpPost(_url);
                        httpPost.setEntity((HttpEntity)(new UrlEncodedFormEntity(_nvp, HTTP.UTF_8)));

                        // Http客户端对象
                        HttpClient httpClient = new DefaultHttpClient();

                        // 下面使用Http客户端发送请求，并获取响应内容

                        // 发送请求并获得响应对象
                        mHttpResponse = httpClient.execute(httpPost);

                        // 获得响应的消息实体
                        mHttpEntity = mHttpResponse.getEntity();

                        // 获取一个输入流
                        inputStream = mHttpEntity.getContent();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        // 逐行读取返回数据
                        String line = "";
                        while (null != (line = bufferedReader.readLine())){
                            _postResContent += line;
                        }
                    }
                    catch (Exception e)
                    {
                        _postResContent = "";
                        e.printStackTrace();
                    }
                    finally
                    {
                        try
                        {
                            inputStream.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();
            while(true){
                if (thread.getState() == Thread.State.TERMINATED){
                    break;
                }
            }
        }

        return _postResContent;
    }

    /**
     * 获取POST请求返回结果字符串，服务器端编码格式为GBK
     * @return
     */
    public String GetPostResContentWithGBK(){
        if (!SysUtil.StringIsEmpty(_url) && _nvp != null){

            Thread thread = new Thread(){
                @Override
                public void run(){
                    HttpResponse mHttpResponse = null;
                    HttpEntity mHttpEntity = null;
                    try
                    {
                        // HttpPost对象实例
                        HttpPost httpPost = new HttpPost(_url);
                        httpPost.setEntity((HttpEntity)(new UrlEncodedFormEntity(_nvp, HTTP.UTF_8)));

                        // Http客户端对象
                        HttpClient httpClient = new DefaultHttpClient();

                        // 下面使用Http客户端发送请求，并获取响应内容

                        // 发送请求并获得响应对象
                        mHttpResponse = httpClient.execute(httpPost);

                        // 获得响应的消息实体
                        mHttpEntity = mHttpResponse.getEntity();

                        // 获得响应的消息实体
                        _postResContent = EntityUtils.toString(mHttpEntity, "GBK");
                    }
                    catch (Exception e)
                    {
                        _postResContent = "";
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            while(true){
                if (thread.getState() == Thread.State.TERMINATED){
                    break;
                }
            }
        }

        return _postResContent;
    }
}
