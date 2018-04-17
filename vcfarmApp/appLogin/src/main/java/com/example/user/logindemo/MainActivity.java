package com.example.user.logindemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private TextView tv_content;
    private Button  btn_login;
    private EditText et_contactId, et_password;
    private String contactID;//用户账号
    private String password;//用户密码
    private String sessionId, error_code;

    private static String CURRENT_SERVER = "http://api1.cloudlinks.cn/";
    private static String LOGIN_URL = CURRENT_SERVER + "Users/LoginCheck.ashx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mContext = this;
        initComponent();
    }

    private void initComponent() {
        et_contactId = (EditText) findViewById(R.id.et_contactId);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_content = (TextView) findViewById(R.id.tv_content);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (checkContactInput()) {
                    tv_content.setText("登陆中。。。。。");
                    new LoginTask(contactID, password).execute();
                } else {
                    Toast.makeText(mContext, "账号密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //用户账户密码非空检查
    public boolean checkContactInput() {
        contactID = et_contactId.getText().toString().trim();
        password = et_password.getText().toString().trim();
        return !("").equals(contactID) && !("").equals(password);
    }

    class LoginTask extends AsyncTask {
        String username;
        String password;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        protected Object doInBackground(Object... params) {
            return login(username, password);
        }

        protected void onPostExecute(Object object) {
            //这里对请求结果进行处理
            try {
                parseObj(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 解析登陆之后返回的json数据
     *
     * @param object
     * @throws Exception
     */
    public void parseObj(Object object) throws Exception {
        JSONObject json = (JSONObject) object;
        error_code = json.getString("error_code");
        String r1 = json.getString("P2PVerifyCode1");
        String r2 = json.getString("P2PVerifyCode2");
        Log.e("wzytest",r1+":"+r2);
        if (error_code.equals("0")) {
            tv_content.setText("登陆成功" + "sessionId:" + sessionId +","+ "r1:" + r1 +","+"r2:"+r2);
        } else {
            tv_content.setText("登陆失败");
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
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,10000);
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
