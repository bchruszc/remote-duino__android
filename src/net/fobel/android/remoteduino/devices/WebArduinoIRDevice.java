package net.fobel.android.remoteduino.devices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fobel.android.HttpHelper;

public class WebArduinoIRDevice {
	private static final String kCommandLearn = "learn";
	private static final String kCommandSend = "send";
	public static final String kProtocolRaw = "0";
	private static final String kURLParamProtocol = "p";
	private static final String kURLParamCode = "c";
	private static final String kURLParamRawMarks = "r";
	private String mUrl;

	public WebArduinoIRDevice(String url) {
		setUrl(url);
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		String newURL = url;
		if(!newURL.startsWith("http://")){
			newURL = "http://" + newURL;
		} 
		
		if(!newURL.endsWith("/")){
			newURL += "/";
		}
		mUrl = newURL;
	}

	
	public String learn() {
		return HttpHelper.process_get_request(mUrl + kCommandLearn);
	}

	/**
	 * Send the a request for the remote code/protocol to the appropriate IP
	 * address.
	 * @return 
	 */
	public String send(String code, String protocol) {
        Map<String, String> query_params = new HashMap<String, String>();
        query_params.put(kURLParamCode, code);
        query_params.put(kURLParamProtocol, protocol);
        
        return HttpHelper.process_get_request(mUrl + kCommandSend, query_params);
	}
	
	/**
	 * This int [] should contain the times in milliseconds for mark/nomark pairs.  For example
	 * { 1500, 350, 350, 350 } would produce:
	 * 1500ms on, 350ms off, 350ms on, 35ms 0off
	 * @param raw
	 * @return
	 */
	public String sendRaw(List<Integer> raw){
        Map<String, String> query_params = new HashMap<String, String>();
        query_params.put(kURLParamProtocol, kProtocolRaw);
        
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < raw.size(); i++){
        	buf.append(raw.get(i));
        	if(i < raw.size() - 1){
        		buf.append(",");
        	}
        }
        
        query_params.put(kURLParamRawMarks, buf.toString());
        return HttpHelper.process_get_request(mUrl + kCommandSend, query_params);
	}
}
