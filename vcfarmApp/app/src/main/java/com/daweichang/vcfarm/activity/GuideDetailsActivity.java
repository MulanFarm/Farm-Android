package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.mode.ArticleMode;
import com.daweichang.vcfarm.widget.ShowToast;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/3/27.
 * 档案详情
 */
public class GuideDetailsActivity extends BaseActivity {
    @BindView(R.id.textTitle)
    TextView textTitle;
    @BindView(R.id.textTime)
    TextView textTime;
    @BindView(R.id.webView)
    WebView webView;
    private ArticleMode mode;
    private AlertDialog.Builder builder;
    private String url;

    public static void open(Context context, ArticleMode mode) {
        Intent intent = new Intent(context, GuideDetailsActivity.class);
        intent.putExtra("json", new Gson().toJson(mode));
        context.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        mode = new Gson().fromJson(json, ArticleMode.class);
        textTitle.setText(mode.title);
        textTime.setText(mode.create_date);
        url = mode.url;

        webView.setBackgroundColor(0);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                ShowToast.alertShortOfWhite(GuideDetailsActivity.this, url);
                if (url.equals("about:blank")) {
                } else {
                    webView.loadUrl(url);
                }
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                builder.setMessage(message);
                builder.show();
                result.cancel();
                return true;
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                onBackPressed();
            }
        });
        webView.loadUrl(url);
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tishi);
        builder.setPositiveButton(R.string.queding, null);
    }

    @OnClick(R.id.imgBack)
    public void onClick() {
        if (webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
        } else {
            onBackPressed();
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
