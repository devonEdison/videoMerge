package com.dragonplayer.merge.utils;

public class Constants {

	public Constants() {
	}

	public static final String FLURRY_KEY = "32Z87FFTZR57R8F89F47";
	public static int STORE = 0;
	public static final String amazon_store = "http://www.amazon.com/gp/mas/dl/android?p=";
	public static final String facebook_share_message;
	public static final String google_store = "https://play.google.com/store/apps/details?id=";
	public static final String samsung_store = "samsungapps://ProductDetail/";
	public static final String share_message = "Check out my #DragonPlayer!";
	public static final String store_url[] = {
		"https://play.google.com/store/apps/details?id=", "http://www.amazon.com/gp/mas/dl/android?p=", "samsungapps://ProductDetail/"
	};

	public static final String FACEBOOK_APP_ID = "211619498871712";

	public static final String TWITTER_CONSUMER_KEY = "8soLHVfwGLUDn43caYkNEg";
	public static final String TWITTER_CONSUMER_SECRET = "VuoSyQ35b6RYDGadSl8elwyN3bflkwcfWezaqCHSw";

	public static final String FACEBOOK_SHARE_MESSAGE = "Look at this great App!";
	public static final String FACEBOOK_SHARE_LINK = "http://www.sk2u.com.tw";
	public static final String FACEBOOK_SHARE_LINK_NAME = "dragon merge player project!";
	public static final String FACEBOOK_SHARE_LINK_DESCRIPTION = "Dragon Player!";
	public static final String FACEBOOK_SHARE_PICTURE = "http://cdn.androidcommunity.com/wp-content/uploads/2011/01/facebook-android-logo-1.jpg";
	public static final String FACEBOOK_SHARE_ACTION_NAME = "Android Simple Social Sharing";
	public static final String FACEBOOK_SHARE_ACTION_LINK = "https://github.com/nostra13/Android-Simple-Social-Sharing";
	public static final String FACEBOOK_SHARE_IMAGE_CAPTION = "Great image";

	public static final String TWITTER_SHARE_MESSAGE = "Look at this great App!";

	public static final class Extra {
		public static final String POST_MESSAGE = "com.nostra13.socialsharing.extra.POST_MESSAGE";
		public static final String POST_LINK = "com.nostra13.socialsharing.extra.POST_LINK";
		public static final String POST_PHOTO = "com.nostra13.socialsharing.extra.POST_PHOTO";
		public static final String POST_PHOTO_DATE = "com.nostra13.socialsharing.extra.POST_PHOTO_DATE";
		public static final String POST_LINK_NAME = "com.nostra13.socialsharing.extra.POST_LINK_NAME";
		public static final String POST_LINK_DESCRIPTION = "com.nostra13.socialsharing.extra.POST_LINK_DESCRIPTION";
		public static final String POST_PICTURE = "com.nostra13.socialsharing.extra.POST_PICTURE";
	}

	static {
		STORE = 0;
		facebook_share_message = (new StringBuilder("Check out my #DragonPlayer! ")).append(store_url[STORE]).toString();
	}
	
	
	public static final class Common{
		/* id of the user to follow. same as 'like' button*/
		/**
		 * Facebook id to follow. Should be an object id(String object in numeric form).<br> 
		 */ 
		
		public static final String FB_FOLLOW_PAGE_ID = "310301782350214";
		public static final String FB_FOLLOW_ID = "SK2U99";
		public static final String FB_PAGE_PREFIX = "https://www.facebook.com/";
		public static final String INTENT_FB_LIKE = "like";

	    public static final String[] permissions = {"publish_stream"};
	}
	public static final class SharedPreference{
		/* shared preference constants */
		public static final String NAME_COMMON = "preference";
	    public static final String NAME_FB_SESSION_PREF = "facebook-session";
	    
		public static final class NAME{
		    public static final String FB_SESSION_TOKEN = "access_token";
		    public static final String FB_SESSION_EXPIRES = "expires_in";
		    public static final String FB_SESSION_LAST_UPDATE = "last_update";
		    
		    public static final String FB_LIKED = "facebook-liked";
		}
		public static final class VALUE{
			public static final String FB_SESSION_TOKEN = null;
		    public static final long FB_SESSION_EXPIRES = 0;
		    public static final long FB_SESSION_LAST_UPDATE = 0;
		    
			public static final boolean FB_LIKED = false;
		}
	}
//	public static final class Facebook{
//		public static final class REQUEST{
//			public static final String FIELDS = "fields";
//		}
//	}
	public static final class ParseConstants{
		public static final class Facebook{
			public static final class Likes{
				public static final String LIKES = "likes";
				public static final String ID = "id";
				public static final String DATA = "data";
			}
			public static final class User{
				public static final String NAME = "name";
				public static final String ID = "id";
			}
		}
	}

}
