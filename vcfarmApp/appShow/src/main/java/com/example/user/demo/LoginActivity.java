package com.example.user.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.p2p.core.P2PHandler;
import com.p2p.core.utils.MD5;
import com.p2p.core.utils.MyUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    private static String CURRENT_SERVER = "http://api1.cloudlinks.cn/";
    private static String LOGIN_URL = CURRENT_SERVER + "Users/LoginCheck.ashx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进行音视频监控要在登陆之后进行
        new LoginTask("0810090", "111222").execute();
        finish();
    }


    class LoginTask extends AsyncTask {
        String username;
        String password;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected Object doInBackground(Object... params) {
            return login(username, password);
        }

        @Override
        protected void onPostExecute(Object object) {
            try {
                parseObj(object);
                Intent intent = new Intent(LoginActivity.this, PlayerActivity2.class);
//                Intent intent = new Intent(LoginActivity.this, MonitorActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void parseObj(Object object) throws Exception {
        JSONObject json = (JSONObject) object;
        String error_code = json.getString("error_code");
        if (error_code.equals("0")) {
            String rCode1 = json.getString("P2PVerifyCode1");
            String rCode2 = json.getString("P2PVerifyCode2");
            P2PHandler.getInstance().p2pInit(this, new P2PListener(), new SettingListener());
            P2PHandler.getInstance().p2pConnect("0810090", Integer.parseInt(rCode1), Integer.parseInt(rCode2));
        }
    }

    public JSONObject login(String username, String password) {
        if (MyUtils.isNumeric(username)) {
            username = String.valueOf((Integer.parseInt(username) | 0x80000000));
        }
        JSONObject jObject = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        MD5 md = new MD5();

        params.add(new BasicNameValuePair("User", username));
        params.add(new BasicNameValuePair("Pwd", md.getMD5ofStr(password)));

        params.add(new BasicNameValuePair("VersionFlag", "1"));
        params.add(new BasicNameValuePair("AppOS", "3"));

        params.add(new BasicNameValuePair("AppVersion", "3014666"));
        try {
            jObject = new JSONObject(doPost(params, LOGIN_URL));
            Log.e("my", jObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObject;
    }

    public String doPost(List<NameValuePair> params, String url)
            throws Exception {
        Log.e("my", "current-server:" + url);
        String result = null;
        // 鏂板缓HttpPost瀵硅薄
        HttpPost httpPost = new HttpPost(url);
        // 璁剧疆瀛楃闆�
        HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        // 璁剧疆鍙傛暟瀹炰綋
        httpPost.setEntity(entity);
        // 鑾峰彇HttpClient瀵硅薄
        HttpClient httpClient = new DefaultHttpClient();
        // 杩炴帴瓒呮椂
        httpClient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        // 璇锋眰瓒呮椂
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
        try {
            HttpResponse httpResp = httpClient.execute(httpPost);
            int http_code;
            if ((http_code = httpResp.getStatusLine().getStatusCode()) == 200) {
                result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
                Log.e("my", "original http:" + result);
            } else {
                // result = "{\"error_code\":998}";
                throw new Exception();
            }
            JSONObject jObject = new JSONObject(result);
            int error_code = jObject.getInt("error_code");
            Log.e("leleTest", "error_code=" + error_code);
            if (error_code == 1 || error_code == 29 || error_code == 999) {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
