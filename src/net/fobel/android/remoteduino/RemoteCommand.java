package net.fobel.android.remoteduino;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fobel.android.remoteduino.devices.WebArduinoIRDevice;


public class RemoteCommand implements Serializable {
	/* This class stores a remote command consisting of an (arbitrary) text
	 * label (to denote the command's function), along with the corresponding
	 * remote protocol/code. */
	private static final long serialVersionUID = -4352237220714175048L;
	public String label;
	public String protocol;
	public String code;
	public List<Integer> raw;
	
	public RemoteCommand(String label, String protocol, String code) {
		/* Create a new RemoteCommand instance based specific protocol and code values. */
		super();
		this.label = label;
		this.protocol = protocol;
		this.code = code;
	}
	
	
	public RemoteCommand(String label, String response) throws IOException {
		/* Create a new RemoteCommand instance based on response from "learn" command.
		 * We parse the "learn" response to obtain the protocol and code values. */
		super();
		this.label = label;
		String patternstr__code = "<h2>\\s*(.*?)\\s*</h2>";
		Pattern pattern__code = Pattern.compile(patternstr__code, Pattern.DOTALL | Pattern.MULTILINE);
		String patternstr__protocol = "Received\\s+(NEC|SONY|RC6|RC5)\\[(\\d+)\\]:";
//		String patternstr__protocol = "Received\\s+(NEC|SONY|RC6|RC5):";
		Pattern pattern__protocol = Pattern.compile(patternstr__protocol, Pattern.DOTALL | Pattern.MULTILINE);
		
		Matcher matcher = pattern__code.matcher(response);
		boolean matchFound = matcher.find();

		if(matchFound) {
	        this.code = "0x" + matcher.group(1);
		} else {
			// No known code found - check for raw data and save it!
			if(response.contains("unknown code")){
				protocol = WebArduinoIRDevice.kProtocolRaw;
				raw = new ArrayList<Integer>();
				boolean hitFirstMark = true;
				Pattern markPattern = Pattern.compile("(s|m)(\\d+)");
				matcher = markPattern.matcher(response);
				while(matcher.find()){
					if(!hitFirstMark && matcher.group(1).equals("s")) { 
						continue; // Wait until the first "m" before recording.
					}
					String code = matcher.group(2);
					raw.add(Integer.parseInt(code));
				}
				return;
			} else {
				throw new IOException("No code found!");
			}
		}
		
		matcher = pattern__protocol.matcher(response);
		matchFound = matcher.find();

		if(matchFound) {
	        this.protocol = matcher.group(2);
		}
	}
	
	
    public static RemoteCommand learn_command(String label, WebArduinoIRDevice irDevice) throws Exception {
    	/* Static method which waits for a learned command response and returns
    	 * a RemoteCommand instance based on the "learn" response. */
        String result = irDevice.learn();
        RemoteCommand cmd;
		try {
			cmd = new RemoteCommand(label, result);
		} catch(Exception e) {
			//Toast.makeText(this, "Error parsing response:\n" + result, Toast.LENGTH_SHORT).show();
			throw e;
		}
		return cmd;
	}
    
    public String send(WebArduinoIRDevice irDevice) {
    	if(WebArduinoIRDevice.kProtocolRaw.equals(protocol)){
    		return irDevice.sendRaw(raw);
    	}
    	return irDevice.send(code, protocol);
    }
}