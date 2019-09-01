package org.indachat.jsbridge;

import android.content.Context;
import android.content.res.AssetManager;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BridgeUtil {
	final static String YY_OVERRIDE_SCHEMA = "yy://";
	final static String YY_RETURN_DATA = YY_OVERRIDE_SCHEMA + "return/";//格式为   yy://return/{function}/returncontent
	final static String YY_FETCH_QUEUE = YY_RETURN_DATA + "_fetchQueue/";
	final static String EMPTY_STR = "";
	final static String UNDERLINE_STR = "_";
	final static String SPLIT_MARK = "/";
	
	final static String CALLBACK_ID_FORMAT = "JAVA_CB_%s";
	final static String JS_HANDLE_MESSAGE_FROM_JAVA = "javascript:WebViewJavascriptBridge._handleMessageFromNative('%s');";
	final static String JS_FETCH_QUEUE_FROM_JAVA = "javascript:WebViewJavascriptBridge._fetchQueue();";
	public final static String JAVASCRIPT_STR = "javascript:";

	// 例子 javascript:WebViewJavascriptBridge._fetchQueue(); --> _fetchQueue
	public static String parseFunctionName(String jsUrl){
		return jsUrl.replace("javascript:WebViewJavascriptBridge.", "").replaceAll("\\(.*\\);", "");
	}

	// 获取到传递信息的body值
	// url = yy://return/_fetchQueue/[{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
	public static String getDataFromReturnUrl(String url) {
		if(url.startsWith(YY_FETCH_QUEUE)) {
			// return = [{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
			return url.replace(YY_FETCH_QUEUE, EMPTY_STR);
		}

		// temp = _fetchQueue/[{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
		String temp = url.replace(YY_RETURN_DATA, EMPTY_STR);
		String[] functionAndData = temp.split(SPLIT_MARK);

		if(functionAndData.length >= 2) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < functionAndData.length; i++) {
				sb.append(functionAndData[i]);
			}
			// return = [{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
			return sb.toString();
		}
		return null;
	}

	// 获取到传递信息的方法
	// url = yy://return/_fetchQueue/[{"responseId":"JAVA_CB_1_360","responseData":"Javascript Says Right back aka!"}]
	public static String getFunctionFromReturnUrl(String url) {
		// temp = _fetchQueue/[{"responseId":"JAVA_CB_1_360","responseData":"Javascript Says Right back aka!"}]
		String temp = url.replace(YY_RETURN_DATA, EMPTY_STR);
		String[] functionAndData = temp.split(SPLIT_MARK);
		if(functionAndData.length >= 1){
			// functionAndData[0] = _fetchQueue
			return functionAndData[0];
		}
		return null;
	}

	
	
	/**
	 * js 文件将注入为第一个script引用
	 * @param view WebView
	 * @param url url
	 */
	public static void webViewLoadJs(WebView view, String url){
		String js = "var newscript = document.createElement(\"script\");";
		js += "newscript.src=\"" + url + "\";";
		js += "document.scripts[0].parentNode.insertBefore(newscript,document.scripts[0]);";
		view.loadUrl("javascript:" + js);
	}

	/**
	 * 这里只是加载lib包中assets中的 WebViewJavascriptBridge.js
	 * @param view webview
	 * @param path 路径
	 */
    public static void webViewLoadLocalJs(WebView view, String path){

        //String jsContent = assetFile2Str(view.getContext(), path);

        String jsContent = "!function(){if(!window.WebViewJavascriptBridge){var e,n,a=[],t=[],i={},r=\"yy\",c=\"__QUEUE_MESSAGE__/\",d={},s=1,o=window.WebViewJavascriptBridge={init:function(e){if(o._messageHandler)throw new Error(\"WebViewJavascriptBridge.init called twice\");o._messageHandler=e;var n=t;t=null;for(var a=0;a<n.length;a++)p(n[a])},send:function(e,n){u({data:e},n)},registerHandler:function(e,n){i[e]=n},callHandler:function(e,n,a){u({handlerName:e,data:n},a)},_fetchQueue:function(){var e=JSON.stringify(a);a=[],\"[]\"!==e&&(n.src=r+\"://return/_fetchQueue/\"+encodeURIComponent(e))},_handleMessageFromNative:function(e){console.log(e),t&&t.push(e),p(e)}},l=document;!function(n){(e=n.createElement(\"iframe\")).style.display=\"none\",n.documentElement.appendChild(e)}(l),function(e){(n=e.createElement(\"iframe\")).style.display=\"none\",e.documentElement.appendChild(n)}(l);var f=l.createEvent(\"Events\");f.initEvent(\"WebViewJavascriptBridgeReady\"),f.bridge=o,l.dispatchEvent(f)}function u(n,t){if(t){var i=\"cb_\"+s+++\"_\"+(new Date).getTime();d[i]=t,n.callbackId=i}a.push(n),e.src=r+\"://\"+c}function p(e){setTimeout(function(){var n,a=JSON.parse(e);if(a.responseId){if(!(n=d[a.responseId]))return;n(a.responseData),delete d[a.responseId]}else{if(a.callbackId){var t=a.callbackId;n=function(e){u({responseId:t,responseData:e})}}var r=o._messageHandler;a.handlerName&&(r=i[a.handlerName]);try{r(a.data,n)}catch(e){\"undefined\"!=typeof console&&console.log(\"WebViewJavascriptBridge: WARNING: javascript handler threw.\",a,e)}}})}}();";
        view.loadUrl("javascript:" + jsContent);
    }

	/**
	 * 解析assets文件夹里面的代码,去除注释,取可执行的代码
	 * @param c context
	 * @param urlStr 路径
	 * @return 可执行代码
	 */
	public static String assetFile2Str(Context c, String urlStr){
		InputStream in = null;
		try{
			in = c.getAssets().open(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (line != null && !line.matches("^\\s*\\/\\/.*")) { // 去除注释
                    sb.append(line);
                }
            } while (line != null);

            bufferedReader.close();
            in.close();
 
            return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
}
