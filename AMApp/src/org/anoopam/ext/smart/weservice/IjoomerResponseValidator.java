package org.anoopam.ext.smart.weservice;

import android.content.Context;

import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

/**
 * This Class Contains All Method Related To IjoomerResponseValidator.
 * 
 * @author tasol
 * 
 */
public class IjoomerResponseValidator  {

	private String errorMessage;
	private int responseCode = 108;
    private Context mContext;

	/**
	 * Constructor
	 * 
	 * @param mContext
	 *            {@link Context}
	 */
	public IjoomerResponseValidator(Context mContext) {

        this.mContext = mContext;
//
	}


	/**
	 * This method used to get error message.
	 * 
	 * @return represented {@link String}
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * This method used to set message.
	 * 
	 * @param errorMessage
	 *            represented error message
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * This method used to get response code.
	 * 
	 * @return represented {@link Integer}
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * This method used to set reponse code.
	 * 
	 * @param responseCode
	 *            represented response code
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}


	/**
	 * This method used to remove unnecessary filed from response.
	 * 
	 * @param data
	 *            represented json object
	 */
	private void removeUnnacessaryFields(JSONObject data) {
		data.remove("code");
		data.remove("full");
		data.remove("notification");
		data.remove("pushNotificationData");
        data.remove("total");
        data.remove("timeStamp");
        data.remove("unreadMessageCount");

		
	}


    public boolean validateResponse(String jsonString,AjaxStatus status) {

        if(status.getCode()==-101){
            setResponseCode(599);
            setErrorMessage(mContext.getString(mContext.getResources().getIdentifier("code" + responseCode, "string", mContext.getPackageName())));
            return false;
        }

        try{
            System.out.println("WSResponse : " +jsonString);
            response = new JSONObject(jsonString);
            if (response.has("code")) {
                try {
                    int code = Integer.parseInt(response.getString("code"));

                    setResponseCode(code);

                    if (response.has("message") && response.getString("message").length()>0) {
                        setErrorMessage(response.getString("message"));
                    }else{
                        try{
                            setErrorMessage(mContext.getString(mContext.getResources().getIdentifier("code" + responseCode, "string", mContext.getPackageName())));
                        }catch (Exception e){
                        }
                    }



                    removeUnnacessaryFields(response);


                    if (code == 200 || code == 703) {
                        return true;
                    } else {
                        return false;
                    }

                } catch (Throwable e) {
                    setResponseCode(108);
                }
            } else {
                setResponseCode(108);
            }

            if(errorMessage== null || errorMessage.length()<=0){
                setErrorMessage("Server may be down please try again later.");
            }


            removeUnnacessaryFields(response);
        }catch (Exception e){
            setResponseCode(108);
            setErrorMessage(mContext.getString(mContext.getResources().getIdentifier("code" + responseCode, "string", mContext.getPackageName())));
            e.printStackTrace();
        }

        return false;
    }

    public JSONObject getResponse() {
        return response;
    }

    private JSONObject response;
}
