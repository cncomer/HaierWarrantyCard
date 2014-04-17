package com.shwy.bestjoy.bjnote.varrantcard.provider;

import android.net.Uri;

import com.google.zxing.client.android.history.ContactsDBHelper;
import com.shwy.bestjoy.bjnote.myhome.goods.GoodsManager;

public class BjnoteContent {

	public static final String AUTHORITY = "com.shwy.bestjoy.bjnote.varrantcard.provider";
    // The notifier authority is used to send notifications regarding changes to messages (insert,
    // delete, or update) and is intended as an optimization for use by clients of message list
    // cursors (initially, the email AppWidget).
    public static final String NOTIFIER_AUTHORITY = "com.shwy.bestjoy.bjnote.notifier";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    
    // All classes share this
    public static final String RECORD_ID = "_id";

    public static final String[] COUNT_COLUMNS = new String[]{"count(*)"};

    /**
     * This projection can be used with any of the EmailContent classes, when all you need
     * is a list of id's.  Use ID_PROJECTION_COLUMN to access the row data.
     */
    public static final String[] ID_PROJECTION = new String[] {
        RECORD_ID
    };
    public static final int ID_PROJECTION_COLUMN = 0;

    public static final String ID_SELECTION = RECORD_ID + " =?";
    
    
    public static class ReceivedContact extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "receive");
    }
    
    public static class MyCard extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "mycard");
    }
    
    public static class MyMemo extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "mymemo");
    }
    
    public static class ScanedContact extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "scan");
    }
    /**��������*/
    public static class ExchangeTopic extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "exchangetopic");
    }
    /**���������Ա*/
    public static class ExchangeTopicList extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "exchangetopiclist");
    }
    
    /**Ȧ������*/
    public static class CircleTopic extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "circletopic");
    	/**��ѯ�������������Ȧ���б�*/
    	public static String getCircleTopic(String tel) {
    		return "http://www.mingdown.com/cell/getQuanByCell.aspx?cell="+ tel;
    	}
    	
    	public static String getCircleTopicIcon(String circleId) {
    		return "http://www.mingdown.com/image/"+ circleId + ".jpg";
    	}
    }
    
    /**Ȧ�ӳ�Ա�б�*/
    public static class CircleTopicList extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "circletopiclist");
    	/**��ѯĳһȦ�ӳ�Ա�б�*/
    	public static String getCircleTopicList(String qId) {
    		return "http://www.mingdown.com/cell/getQMbers.aspx?qmd="+ qId;
    	}
    }
    
    /**Ȧ�ӳ�Ա��ϸҳ��*/
    public static class CircleMemberDetail extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "circlememberdetail");
    	/**��ѯȦ�ӳ�Ա��Ƭǽ*/
    	public static String getCircleMemberDetail(String pmd) {
    		//http://www.mingdown.com/cell/getPersonFile.aspx?pmd=20100000031
    		return "http://www.mingdown.com/cell/getPersonFile.aspx?pmd="+ pmd;
    	}
    	
    	public static String getThumbnailPhoto(String photoAddr) {
    		return "http://www.mingdown.com/photo/small/"+ photoAddr;
    	}
    	
    	public static String getLargePhoto(String photoAddr) {
    		return "http://www.mingdown.com/photo/"+ photoAddr;
    	}
    }
    
    /**Ȧ�ӳ�Ա��ϸҳ��*/
    public static class CircleQuanDetail extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "quanphoto");
    	
    	/**�鿴Ȧ��ᣬhttp://www.mingdown.com/cell/qimg.aspx?qmd=*/
    	public static String getCircleQuanPhotoes(String qmd) {
    		return "http://www.mingdown.com/cell/qimg.aspx?qmd="+ qmd;
    	}
    	
    	public static String getThumbnailPhoto(String photoAddr) {
    		return "http://www.mingdown.com/photo/small/"+ photoAddr;
    	}
    	
    	public static String getLargePhoto(String photoAddr) {
    		return "http://www.mingdown.com/photo/"+ photoAddr;
    	}
    }
    
    //http://www.mingdown.com/cell/SeeQuanLeaveMessage.aspx?qmd=200100000090&MD=20100000031
    public static class Feedback extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "feedback");
    	/***
    	 * �������Բ�ѯ��ַ
    	 * @param qmd Ȧmd
    	 * @param pmd  ���˵���md
    	 * @return
    	 */
    	public static String buildFeedback(String qmd, String pmd) {
    		String serverUri = "http://www.mingdown.com/cell/SeeQuanLeaveMessage.aspx?qmd="+qmd+"&MD="+ pmd;
    		return serverUri;
    	}
    	/**
    	 * http://www.mingdown.com/cell/leaveMessage.aspx?lm=Urlencode(From|to|content|0)
    	 * @return  http://www.mingdown.com/cell/leaveMessage.aspx?lm=
    	 */
    	public static String buildPostFeedbackBaseUrl() {
    		return "http://www.mingdown.com/cell/leaveMessage.aspx?lm=";
    	}
    }
    /**��½�˺�*/
    public static class Accounts extends BjnoteContent {
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "accounts");
    	
    	public static String buildAccountAvator(String md) {
    		return "http://www.mingdown.com/image/"+ md + ".jpg";
    	}
    }
    
    /**��½�˺�*/
    public static class ZHT extends BjnoteContent {
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "zht");
    	private static final boolean DEBUG = false;
    	public static final String TEL_WHERE = HaierDBHelper.CONTACT_TEL +"=?";
    	
    	public static String buildZhtCardImage(String md) {
    		return "http://www.mingdown.com/zht/"+ md + ".png";
    	}
    	public static String buildZhtCardThumbnailImage(String md) {
    		return "http://www.mingdown.com/zht/"+ md + "s.png";
    	}
    	
    	public static String buildAllZhtCard(String tel) {
    		if (DEBUG) {
    			tel = "13816284988";
    		} 
    		return "http://www.mingdown.com/zht/getzhhuiIDs.ashx?Cell=" + tel;
    	}
    }
    
    
    /**�ҵļ����*/
    public static class HomeGoods extends BjnoteContent {
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "homegoods");
    	/**����Ԥ����Ʊ*/
    	public static final Uri PREVIEW_BILL_CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "homegoods/preview/bill");
    	/**����Ԥ����Ʒ*/
    	public static final Uri PREVIEW_AVATOR_CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "homegoods/preview/avator");
    	
    	public static final Uri PREVIEW_TEMP_BILL_CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "homegoods/preview/billtemp");
    	public static final Uri PREVIEW_TEMP_AVATOR_CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "homegoods/preview/avatortemp");
    	public static boolean DEBUG = false;		
    	/**��Ʒ����*/
    	public static final Uri FEEDBACK_CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "homegoods/feedback");
    	/***
  	   * ������ƷԤ��ͼ��ַwww.51cck.com/KYǰ9λ����/KY.jpg
  	   * @return
  	   */
  	  public static String getGoodsAvatorUrl(String ky) {
  		  String ky9 = ky.substring(0,9);
  		  StringBuilder sb = new StringBuilder(GoodsManager.GOODS_INTRODUCTION_BASE);
  		  sb.append(ky9).append("/").append(ky).append(".jpg");
  		  return sb.toString();
  	  }
  	  
  	  /**
  	   * ��½�˺ŵ�ʱ�������ҵļ����
  	   * @return http://www.mingdown.com/cell/gethome1.aspx?Cell=�����ֻ��
  	   */
  	  public static String getAllGoodsUrlForTel(String cell) {
  		  if (DEBUG) {
  			  cell = "13327820218";
  		  }
  		 StringBuilder sb = new StringBuilder("http://www.mingdown.com/cell/gethome.ashx?Cell=");
  		 sb.append(cell);
  		 return sb.toString();
  	  }
  	 /**
  	  * 
  	  * @param sid ��Ʒ�������ϵ�������
  	  * @return http://www.mingdown.com/cell/delweibao1.ashx?h=
  	  */
  	 public static String getGoodsDeleteUrlForSid(String sid) {
		 StringBuilder sb = new StringBuilder("http://www.mingdown.com/cell/delweibao1.ashx?h=");
		 sb.append(sid);
		 return sb.toString();
	  }
    }
    
    /**��½�˺�*/
    public static class MyLife extends BjnoteContent {
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "mylife");
    	public static final Uri CONSUME_CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "mylife/consume");
    	public static final boolean DEBUG = false;
    	
    	public static String buildZhtCardThumbnailImage(String md) {
    		return "http://www.mingdown.com/zht/"+ md + "s.png";
    	}
    	
    	public static String buildAllMyLifeForTel(String tel) {
    		if (DEBUG) {
    			tel = "13816284988";
    		} 
    		return "http://www.mingdown.com/cell/get2B.ashx?Cell=" + tel;
    	}
    }
}
