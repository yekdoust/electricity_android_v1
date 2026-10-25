package com.example.electricity1405;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        webView.setBackgroundColor(0xFFF4F6F9);
        webView.setWebViewClient(new WebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Android's native print dialog supports "Save as PDF" without a PDF library.
        webView.addJavascriptInterface(new PrintBridge(), "AndroidBridge");
        webView.loadUrl("file:///android_asset/index.html");
        setContentView(webView);
    }

    private final class PrintBridge {
        @JavascriptInterface
        public void printPdf() {
            runOnUiThread(() -> {
                if (webView == null) {
                    Toast.makeText(MainActivity.this, "صفحه برنامه آماده چاپ نیست.", Toast.LENGTH_LONG).show();
                    return;
                }
                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                if (printManager == null) {
                    Toast.makeText(MainActivity.this, "سرویس چاپ اندروید در دسترس نیست.", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    PrintAttributes attributes = new PrintAttributes.Builder()
                            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                            .setResolution(new PrintAttributes.Resolution("pdf", "PDF", 300, 300))
                            .setMinMargins(new PrintAttributes.Margins(360, 360, 360, 360))
                            .build();
                    printManager.print(
                            "گزارش محاسبه برق ۱۴۰۵",
                            webView.createPrintDocumentAdapter("گزارش محاسبه برق ۱۴۰۵"),
                            attributes
                    );
                } catch (RuntimeException ex) {
                    Toast.makeText(MainActivity.this, "باز کردن پنجره چاپ ناموفق بود: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
