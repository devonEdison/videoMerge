package com.dragonplayer.merge.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WebService {

	private static String NAMESPACE = "http://tempuri.org/";
	private static String URL = "http://211.78.89.41/ap/WService.asmx";
	private static String SOAP_ACTION = "http://tempuri.org/";

	public static String ChkAct(String ActId, String Tel, String webMethName) {
		String resTxt = null;
		// Create request
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		// Property which holds input parameters
		PropertyInfo id = new PropertyInfo();
		// Set Name
		id.setName("ActId");
		// Set Value
		id.setValue(ActId);
		// Set dataType
		id.setType(String.class);
		// Add the property to request object
		request.addProperty(id);

		// Property which holds input parameters
		PropertyInfo telphone = new PropertyInfo();
		// Set Name
		telphone.setName("Tel");
		// Set Value
		telphone.setValue(Tel);
		// Set dataType
		telphone.setType(String.class);
		// Add the property to request object
		request.addProperty(telphone);

		// Create envelope
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		// Set output SOAP object
		envelope.setOutputSoapObject(request);
		// Create HTTP call object
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		try {
			// Invole web service
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			// Get the response
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			// Assign it to fahren static variable
			resTxt = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return resTxt;
	}

	public static String JAct(String ActId , String userName, String Tel,String Email,String FB,String webMethName) {
		String resTxt = null;
		// Create request
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		// Property which holds input parameters
		PropertyInfo id = new PropertyInfo();
		// Set Name
		id.setName("ActId");
		// Set Value
		id.setValue(ActId);
		// Set dataType
		id.setType(String.class);
		// Add the property to request object
		request.addProperty(id);

		// Property which holds input parameters
		PropertyInfo userName_porp = new PropertyInfo();
		// Set Name
		userName_porp.setName("userName");
		// Set Value
		userName_porp.setValue(userName);
		// Set dataType
		userName_porp.setType(String.class);
		// Add the property to request object
		request.addProperty(userName_porp);
		
		
		// Property which holds input parameters
		PropertyInfo telphone = new PropertyInfo();
		// Set Name
		telphone.setName("Tel");
		// Set Value
		telphone.setValue(Tel);
		// Set dataType
		telphone.setType(String.class);
		// Add the property to request object
		request.addProperty(telphone);

		// Property which holds input parameters
		PropertyInfo Email_prop = new PropertyInfo();
		// Set Name
		Email_prop.setName("Email");
		// Set Value
		Email_prop.setValue(Email);
		// Set dataType
		Email_prop.setType(String.class);
		// Add the property to request object
		request.addProperty(Email_prop);
		
		
		// Property which holds input parameters
		PropertyInfo FB_prop = new PropertyInfo();
		// Set Name
		FB_prop.setName("FB");
		// Set Value
		FB_prop.setValue(FB);
		// Set dataType
		FB_prop.setType(String.class);
		// Add the property to request object
		request.addProperty(FB_prop);
		
		
		// Create envelope
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		// Set output SOAP object
		envelope.setOutputSoapObject(request);
		// Create HTTP call object
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		try {
			// Invole web service
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			// Get the response
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			// Assign it to fahren static variable
			resTxt = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return resTxt;
	}
	
	public static String Banner(String webMethName) {
		String resTxt = null;
		// Create request
		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		// Create envelope
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		// Set output SOAP object
		envelope.setOutputSoapObject(request);
		// Create HTTP call object
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		try {
			// Invole web service
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			// Get the response
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			// Assign it to fahren static variable
			resTxt = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			resTxt = "Error occured";
		}
		return resTxt;
	}
}
