package com.example.account;

public class Constants {

	// Config
	  public static final int TAB_INDEX_DIARY_LIST = 0;
	  
	  public static final String TAG = "51";
	  
	  public static final String NULL_STRING = "null";
	  
	  public static final int ACCOUNT_MORE_INFO_CATEGORY = 0;
	  public static final int ACCOUNT_MORE_INFO_BRAND = 1;
	  public static final int ACCOUNT_MORE_INFO_POSITION = 2;
	  public static final int ACCOUNT_MORE_INFO_TEXT = 3;
	  public static final int ACCOUNT_MORE_INFO_IMAGE = 4;
	  
	  public static final int ACCOUNT_MORE_INFO_TYPE_TEXT = 0;
	  public static final int ACCOUNT_MORE_INFO_TYPE_IMAGE = 1;
	  
		/**
		 * need to do nothing
		 */
		public static final int ACCOUNT_ITEM_ACTION_NEED_NOTHING = 0;
		
		/**
		 * need to create on server
		 */
		public static final int ACCOUNT_ITEM_ACTION_NEED_SYNC_ADD = 1;
		

		/**
		 * need to update on server
		 */
		public static final int ACCOUNT_ITEM_ACTION_NEED_SYNC_UP = 2;

		/**
		 * need to delete on server
		 */
		public static final int ACCOUNT_ITEM_ACTION_NEED_SYNC_DELETE = 3;

}