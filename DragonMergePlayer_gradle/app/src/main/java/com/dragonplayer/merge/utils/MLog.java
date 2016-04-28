package com.dragonplayer.merge.utils;

import android.util.Log;
import com.dragonplayer.merge.utils.Flag;

/**
 * 可輕易控制在發佈app時, 將 Flag.PUBLIC值設為true, 便不會產生Log, 也可將 class instance 丟進來, 自行產出該
 * class 名稱, 以方便查看log時追尋
 * 
 * @author Dexter
 * @since 2014.09.16
 *
 */
public class MLog {

	/**
	 * 在log中自動產生class名稱
	 * 
	 * @param obj
	 * @param message
	 */
	public static void v(Object obj, String message) {
		if (!Flag.PUBLIC) {
			Log.v(getClassName(obj), message);
		}
	}

	/**
	 * 在log中自動產生class名稱
	 * 
	 * @param obj
	 * @param message
	 */
	public static void d(Object obj, String message) {
		if (!Flag.PUBLIC) {
			Log.d(getClassName(obj), message);
		}
	}

	/**
	 * 在log中自動產生class名稱
	 * 
	 * @param obj
	 * @param message
	 */
	public static void i(Object obj, String message) {
		if (!Flag.PUBLIC) {
			Log.i(getClassName(obj), message);
		}
	}

	/**
	 * 在log中自動產生class名稱
	 * 
	 * @param obj
	 * @param message
	 */
	public static void w(Object obj, String message) {
		if (!Flag.PUBLIC) {
			Log.w(getClassName(obj), message);
		}
	}

	/**
	 * 在log中自動產生class名稱
	 * 
	 * @param obj
	 * @param message
	 */
	public static void e(Object obj, String message) {
		if (!Flag.PUBLIC) {
			Log.e(getClassName(obj), message);
		}
	}

	public static void v(String tag, String message) {
		if (!Flag.PUBLIC) {
			Log.v(tag, message);
		}
	}

	public static void i(String tag, String message) {
		if (!Flag.PUBLIC) {
			Log.i(tag, message);
		}
	}

	public static void d(String tag, String message) {
		if (!Flag.PUBLIC) {
			Log.d(tag, message);
		}
	}

	public static void w(String tag, String message) {
		if (!Flag.PUBLIC) {
			Log.w(tag, message);
		}
	}

	public static void e(String tag, String message) {
		if (!Flag.PUBLIC) {
			Log.e(tag, message);
		}
	}

	private static String getClassName(Object obj) {
		return obj.getClass().getSimpleName();
	}

}
