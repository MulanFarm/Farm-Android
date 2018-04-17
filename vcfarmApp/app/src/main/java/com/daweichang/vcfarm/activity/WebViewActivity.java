package com.daweichang.vcfarm.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daweichang.vcfarm.BaseActivity;
import com.daweichang.vcfarm.BaseService;
import com.daweichang.vcfarm.R;
import com.daweichang.vcfarm.widget.ShowToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * webView展示页
 */
public class WebViewActivity extends BaseActivity {
    @BindView(R.id.imgBack)
    ImageView imgBackBtn;
    @BindView(R.id.textTitle)
    TextView textTitle;
    @BindView(R.id.layoutTitle)
    RelativeLayout layoutTitle;
    @BindView(R.id.webView)
    WebView webView;
    private AlertDialog.Builder builder;
    private String url;
    private boolean isHtml;

    public static void open(Context context, String title, String content) {
        open(context, title, content, false);
    }

    public static void open(Context context, String title, String content, boolean isHtml) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", content);
        intent.putExtra("isHtml", isHtml);
        context.startActivity(intent);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        isHtml = intent.getBooleanExtra("isHtml", false);

        webView.setBackgroundColor(0);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
//            public void onLoadResource(WebView view, String url) {
//                if (!url.equals("about:blank")) {
//                    if (!url.contains("://"))
//                        url = BaseService.ImgURL + url;
//                }
//                ShowToast.alertShortOfWhite(WebViewActivity.this, url);
//                super.onLoadResource(view, url);
//            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                ShowToast.alertShortOfWhite(WebViewActivity.this, url);
                if (url.equals("about:blank")) {
                } else {
                    webView.loadUrl(url);
                }
                return true;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (isHtml) return;
                String title = view.getTitle();
                //textTitle.setText(TextUtils.isEmpty(title) ? "" : title);
            }

            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {// TODO 更换
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
        //“file:///android_asset/sample.html”
        if (isHtml) {
            textTitle.setText(title);
            webView.loadDataWithBaseURL(BaseService.ImgURL, url, "text/html", "utf-8", null);
        } else
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
