package com.dragonplayer.merge.fragment;

import android.app.Activity;
import android.webkit.WebView;

public class GiftFragment {
	Activity activity;
	WebView webview;

	public GiftFragment(Activity activity, WebView webview) {
		super();
		this.activity = activity;
		this.webview = webview;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		String data = "<pre><p style=\"font-size:14px;font-family:'Apple LiGothic';color:#4F81BD;\">(活動說明)</p>"
				+ "<p style=\"font-size:14px;font-family:'Apple LiGothic';color:#C01900;\">新光金寶拼貼趣</p>"
				+ "<p style=\"font-size:14px;font-family:'Apple LiGothic';\">活動日期：暫定9/15 – 10/31</p><p style=\"font-size:14px;font-family:'Apple LiGothic';\">活動辦法：完成以下步驟即可參加抽獎喔！</p><ol><li style=\"font-size:14px;font-family:'Apple LiGothic';\">"
				+ "點選”隱藏版型”進行解鎖。"
				+ "</li><li style=\"font-size:14px;font-family:'"
				+ "Apple LiGothic';\">加入SK2U粉絲團後即可獲得<br s"
				+ "tyle=\"font-size:14px;font-family:'Apple LiGothi"
				+ "c';\">五款隱藏版型</br></li><li style=\"font-size:14"
				+ "px;font-family:'Apple LiGothic';\">選擇其中一款隱藏版型進行編"
				+ "輯</li><li style=\"font-size:14px;font-family:'Apple LiG"
				+ "othic';\">完成作品後點選”抽獎活動”，上傳作<br style=\"font-size:"
				+ "14px;font-family:'Apple LiGothic';\">品並填寫基本資料，成功送出後"
				+ "即可<br>參加抽獎。</br></li></ol><p style=\"font-size:14px;font-f"
				+ "amily:'Apple LiGothic';color:#C01900;\">活動獎項：</p>雙人香港來回機票2"
				+ "名、原燒優質原味燒肉雙<br/>人餐券2名、陶阪屋和風創意料理雙人餐券4名<br/>、"
				+ "TASTy西堤牛排雙人餐券1名、品田牧場日<br/>式豬"
				+ "排咖哩雙人餐券4名、聚北海道昆布鍋雙<br/>人餐券1名</p>"
				+ "<p style=\"font-size:14px;font-family:'Apple LiGothic'"
				+ ";\"></p><p style=\"font-size:14px;font-family:"
				+ "'Apple LiGothic';color:#C01900;\">注意事項：</p><ol><l"
				+ "i style=\"font-size:14px;font-family:'Apple LiGothic'"
				+ ";\">每人僅限中獎一次，本活動僅限<br/>台灣地區(含台澎金馬)中華民國<br/>國"
				+ "民參加。活動獎項寄送地址僅<br/>限中華民國境內。獎項不得折換<br/>現金或更換。主辦"
				+ "單位確認符合<br/>獲贈資格後將公布獲獎名單於活<br/>動官網，並由專人通知得獎用戶。"
				+ "</li><li style=\"font-size:14px;font-fa"
				+ "mily:'Apple LiGothic';\">依中華民國所得稅法規定"
				+ "，獎項<br/>超過1,000元，須開立扣繳憑單；<br/>超過(含)20,"
				+ "000元，需事先繳交<br/>10%稅金，依法辦理扣繳。故得<br/>獎人需提供主辦單"
				+ "位身份證影本<br/>且依規定填寫並繳交相關收據方<br/>可領獎，若不願意提供，則視為<br"
				+ "/>自動棄權，不具得獎資格。</li><li style=\"font-size:14px;font-family:'Apple LiGothic';\">除法令另有規定者外，主辦單位<br/>保留隨時修改、變更或終止本活<br/>動∕本注意事項之權利，活動如<br/>發生任何爭議，概以主辦單位活<br/>動網站之公告為準。</li><li style=\"font-size:14px;font-family:'Apple LiGothic';\">關於本活動相關問題，可撥打服<br/>務電話(02)7746-4100，服務時間<br/>：週一~五(9:30~18:00)或mail<br/>至<a href=\"mailto:service@skywind.com.tw\">service@skywind.com.tw</a>詢問。</li></ol></pre>";

		webview.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
	}

}
