package com.example.account;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.example.module.Account;
import com.example.module.ImageItem;
import com.example.module.MoreInfoItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AccountStartActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

	private EditText m_InputEditText = null;
	private ListView m_MoreInfoList = null;
	private Resources mResources = null;
	private Button m_ButtonPreview = null;
	private Button m_ButtonFinish = null;
	private Account m_CurrentAccount = null;
	private Long m_CurrentAccountId;
	private Boolean m_bCreateNewAccount = false;
	private Context mContext = null;
	protected ArrayList<MoreInfoItem> mMoreInfoListDataSource = new ArrayList<MoreInfoItem>();
	protected AccountMoreInfoListAdapter m_MoreInfoAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(Constants.TAG, "------ AccountStartActivity---onCreate-----");

		setContentView(R.layout.activity_account_start);

		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);

		mResources = this.getResources();
		mContext = this;

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			Long id = bundle.getLong("id");
			Log.i(Constants.TAG, "-----AccountStartActivity- accountId-------" + id);
			m_CurrentAccount = Account.load(Account.class, id);
			m_bCreateNewAccount = false;
		} else {
			Log.i(Constants.TAG, "-----AccountStartActivity- new account-------");
			m_CurrentAccount = new Account();
			m_bCreateNewAccount = true;
		}
	
		m_CurrentAccount.save();
		m_CurrentAccountId = m_CurrentAccount.getId();
		
		m_InitEditText();
		//m_InitBottomButton();// preview/finish
		
		//getWindow().setSoftInputMode(
		//		WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		m_InitMoreInfoList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account_start, menu);
		Log.i(Constants.TAG, "-----AccountStartActivity- onCreateOptionsMenu-------");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Log.i(Constants.TAG, "-----AccountStartActivity- onOptionsItemSelected-------");
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.start_account_finish:
			m_SaveAccount();
			finish();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private boolean m_InitEditText() {
		m_InputEditText = (EditText) findViewById(R.id.start_input_value);
		
		if(false == m_bCreateNewAccount)
		{
			DecimalFormat df = new DecimalFormat("#,##0.00");

			String afterFormat = df.format(m_CurrentAccount.Cost);

			Log.i(Constants.TAG, "--before format" + m_CurrentAccount.Cost + "---afterFormat--------" + afterFormat);

			m_InputEditText.setText(afterFormat);

			m_InputEditText.setSelection(m_InputEditText.length());
		}

		m_InputEditText.addTextChangedListener(new TextWatcher() {
			private boolean m_bChanged = false;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (m_bChanged) {// ----->如果字符未改变则返回
					return;
				}
				String str = s.toString();

				m_bChanged = true;
				String cuttedStr = str;
				/* 删除字符串中的dot */
				for (int i = str.length() - 1; i >= 0; i--) {
					char c = str.charAt(i);
					if ('.' == c) {
						cuttedStr = str.substring(0, i) + str.substring(i + 1);
						break;
					}
				}
				/* 删除前面多余的0 */
				int NUM = cuttedStr.length();
				int zeroIndex = -1;
				for (int i = 0; i < NUM - 2; i++) {
					char c = cuttedStr.charAt(i);
					if (c != '0') {
						zeroIndex = i;
						break;
					} else if (i == NUM - 3) {
						zeroIndex = i;
						break;
					}
				}
				if (zeroIndex != -1) {
					cuttedStr = cuttedStr.substring(zeroIndex);
				}
				/* 不足3位补0 */
				if (cuttedStr.length() < 3) {
					cuttedStr = "0" + cuttedStr;
				}
				/* 加上dot，以显示小数点后两位 */
				cuttedStr = cuttedStr.substring(0, cuttedStr.length() - 2) + "."
						+ cuttedStr.substring(cuttedStr.length() - 2);

				Log.i(Constants.TAG, "--before replace---" + cuttedStr);

				cuttedStr = cuttedStr.replace(",", "");

				Log.i(Constants.TAG, "--after replace---" + cuttedStr);

				Double currentValue = Double.valueOf(cuttedStr.toString());

				DecimalFormat df = new DecimalFormat("#,##0.00");

				String afterFormat = df.format(currentValue);

				Log.i(Constants.TAG, "--before format" + cuttedStr + "---afterFormat--------" + afterFormat);

				m_InputEditText.setText(afterFormat);

				m_InputEditText.setSelection(m_InputEditText.length());

				Log.i(Constants.TAG,
						"--m_CurrentAccount.Cost--" + m_CurrentAccount.Cost + "---currentValue--------" + currentValue);

				m_CurrentAccount.Cost = currentValue;
				if(m_CurrentAccount.isSyncOnServer())
				{
					m_CurrentAccount.SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP;
				}
				m_CurrentAccount.save();
				m_bChanged = false;
			}

		});
		return true;
	}

	private boolean m_LoadMoreInfoDataSrc() {
		TypedArray infoItems = mResources.obtainTypedArray(R.array.more_info_list_text);

			m_CurrentAccount = Account.load(Account.class, m_CurrentAccountId);
			
			mMoreInfoListDataSource.clear();
			for (int index = 0; index < infoItems.length(); index++) {
				MoreInfoItem item = null;
				switch (index) {
				case Constants.ACCOUNT_MORE_INFO_IMAGE: {
					
					String firstImage = "";

						List<ImageItem> images = ImageItem.GetImageItems(m_CurrentAccount);
						Log.i(Constants.TAG, "---GetImageItems ---images.size-" + images.size());
						if (images.size() > 0) {
							Log.i(Constants.TAG, "----images.get(0).Path-" + images.get(0).Path);
		
							firstImage = images.get(0).Path;
						}
	
					item = new MoreInfoItem(infoItems.getString(index), firstImage, Constants.ACCOUNT_MORE_INFO_TYPE_IMAGE);
				}
					break;
				case Constants.ACCOUNT_MORE_INFO_CATEGORY: {
					
					item = new MoreInfoItem(infoItems.getString(index), m_CurrentAccount.Category,
							Constants.ACCOUNT_MORE_INFO_TYPE_TEXT);
				}
					break;
				case Constants.ACCOUNT_MORE_INFO_BRAND: {
					
					item = new MoreInfoItem(infoItems.getString(index), m_CurrentAccount.Brand,
							Constants.ACCOUNT_MORE_INFO_TYPE_TEXT);
				}
					break;
				case Constants.ACCOUNT_MORE_INFO_POSITION: {
					
					item = new MoreInfoItem(infoItems.getString(index), m_CurrentAccount.Position,
							Constants.ACCOUNT_MORE_INFO_TYPE_TEXT);
				}
					break;
				case Constants.ACCOUNT_MORE_INFO_TEXT: {
					Log.i(Constants.TAG, "--m_CurrentAccount.Comments --" + m_CurrentAccount.Comments);
					item = new MoreInfoItem(infoItems.getString(index), m_CurrentAccount.Comments,
							Constants.ACCOUNT_MORE_INFO_TYPE_TEXT);
				}
					break;
				default:
					break;
				}
	
				// TODO initial the value
				mMoreInfoListDataSource.add(item);
			}
			infoItems.recycle();
		
		return true;
	}

	private boolean m_InitMoreInfoList() {
		m_LoadMoreInfoDataSrc();
		m_MoreInfoList = (ListView) findViewById(R.id.account_more_info_list);
		m_MoreInfoAdapter = new AccountMoreInfoListAdapter(this, mMoreInfoListDataSource);

		m_MoreInfoList.setAdapter(m_MoreInfoAdapter);

		m_MoreInfoList.setOnItemClickListener(AccountStartActivity.this);
		//m_MoreInfoAdapter.updateUI();

		return true;
	}

	/*
	private boolean m_InitBottomButton() {
		m_ButtonPreview = (Button) findViewById(R.id.account_preview);
		m_ButtonPreview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, AccountDetailActivity.class);
				
				intent.putExtra("id", m_CurrentAccount.getId());
				startActivity(intent);
			}

		});
		m_ButtonFinish = (Button) findViewById(R.id.account_finish);
		m_ButtonFinish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_SaveAccount();
				finish();
			}

		});
		return true;
	}
	*/

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		switch (position) {
		case Constants.ACCOUNT_MORE_INFO_CATEGORY: {
			Log.i(Constants.TAG, "--onItemClick--ACCOUNT_MORE_INFO_CATEGORY--");
			Intent intent = new Intent();
			intent.setClass(this, AccountAddCategoryActivity.class);
			
			Bundle mBundle = new Bundle();
			if (m_CurrentAccount == null) {
				Log.i(Constants.TAG, "--m_CurrentAccount == null--");
			}
			Log.i(Constants.TAG, "-m_CurrentAccount-id--" + m_CurrentAccount.getId());

			mBundle.putDouble("value", m_CurrentAccount.Cost);
			intent.putExtras(mBundle);
			
			startActivityForResult(intent, Constants.ACCOUNT_MORE_INFO_CATEGORY);
		}
			break;
		case Constants.ACCOUNT_MORE_INFO_BRAND:
		 {
				Log.i(Constants.TAG, "--onItemClick--ACCOUNT_MORE_INFO_BRABD--");
				Intent intent = new Intent();
				intent.setClass(this, AccountAddBrandActivity.class);
				
				Bundle mBundle = new Bundle();
				if (m_CurrentAccount == null) {
					Log.i(Constants.TAG, "--m_CurrentAccount == null--");
				}
				Log.i(Constants.TAG, "-m_CurrentAccount-id--" + m_CurrentAccount.getId());

				mBundle.putString("category", m_CurrentAccount.Category);
				intent.putExtras(mBundle);
				
				startActivityForResult(intent, Constants.ACCOUNT_MORE_INFO_BRAND);
			}
			break;
		case Constants.ACCOUNT_MORE_INFO_POSITION:
		 {
				Log.i(Constants.TAG, "--onItemClick--ACCOUNT_MORE_INFO_POSITION--");
				Intent intent = new Intent();
				intent.setClass(this, AccountAddPositionActivity.class);
				
				startActivityForResult(intent, Constants.ACCOUNT_MORE_INFO_POSITION);
			}
			break;
		case Constants.ACCOUNT_MORE_INFO_TEXT: {
			Log.i(Constants.TAG, "--onItemClick--ACCOUNT_MORE_INFO_TEXT--");
			Intent intent = new Intent();
			intent.setClass(this, AccountAddCommentActivity.class);
			Bundle mBundle = new Bundle();
			if (m_CurrentAccount == null) {
				Log.i(Constants.TAG, "--m_CurrentAccount == null--");
			}
			Log.i(Constants.TAG, "-m_CurrentAccount-id--" + m_CurrentAccount.getId());

			mBundle.putString("content", m_CurrentAccount.Comments);
			intent.putExtras(mBundle);
			startActivityForResult(intent, Constants.ACCOUNT_MORE_INFO_TEXT);
		}
			break;
		case Constants.ACCOUNT_MORE_INFO_IMAGE: {
			Log.i(Constants.TAG, "--onItemClick--ACCOUNT_MORE_INFO_IMAGE--");
			Intent intent = new Intent();
			intent.setClass(this, AccountImageSelectorActivity.class);
			startActivityForResult(intent, Constants.ACCOUNT_MORE_INFO_IMAGE);
		}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(Constants.TAG, "onActivityResult" + "requestCode" + requestCode + "\n resultCode=" + resultCode);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case Constants.ACCOUNT_MORE_INFO_TEXT:{
				if (data != null) {
					String Comments = data.getStringExtra("content");
					Log.i(Constants.TAG, "----Comments--" + Comments);
					m_CurrentAccount.Comments = Comments;
					if(m_CurrentAccount.isSyncOnServer())
					{
						m_CurrentAccount.SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP;
					}
					m_CurrentAccount.save();
				}
			}
			break;
			case Constants.ACCOUNT_MORE_INFO_CATEGORY:{
				if (data != null) {
					String Category = data.getStringExtra("category");
					Log.i(Constants.TAG, "----Category--" + Category);
					m_CurrentAccount.Category = Category;
					if(m_CurrentAccount.isSyncOnServer())
					{
						m_CurrentAccount.SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP;
					}
					m_CurrentAccount.save();
				}
			}
			break;
			case Constants.ACCOUNT_MORE_INFO_BRAND:{
				if (data != null) {
					String brand = data.getStringExtra("brand");
					Log.i(Constants.TAG, "----brand--" + brand);
					m_CurrentAccount.Brand = brand;
					if(m_CurrentAccount.isSyncOnServer())
					{
						m_CurrentAccount.SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP;
					}
					m_CurrentAccount.save();
				}
			}
			break;
			
			case Constants.ACCOUNT_MORE_INFO_POSITION:{
				if (data != null) {
					String position = data.getStringExtra("position");
					Log.i(Constants.TAG, "----position--" + position);
					m_CurrentAccount.Position = position;
					if(m_CurrentAccount.isSyncOnServer())
					{
						m_CurrentAccount.SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP;
					}
					m_CurrentAccount.save();
				}
			}
			break;
			
			case Constants.ACCOUNT_MORE_INFO_IMAGE: {
				if (data != null) {
					ArrayList<String> imageSelectedlist = data.getStringArrayListExtra("images");

					Log.i(Constants.TAG, "----imageSelectedlist-data size--" + imageSelectedlist.size());
					ActiveAndroid.beginTransaction();
					try {

						List<ImageItem> existImagelist = m_CurrentAccount.Imageitems();

						Log.i(Constants.TAG, "----existImagelist-data size--" + existImagelist.size());

						for (int index = 0; index < imageSelectedlist.size(); index++) {
							String addPath = imageSelectedlist.get(index);
							
							Log.i(Constants.TAG, "--ACCOUNT_MORE_INFO_IMAGE--" + addPath);
							
							boolean bExistImage = false;
							for(int findIndex = 0; findIndex < existImagelist.size(); findIndex++)
							{
								Log.i(Constants.TAG, "--existImagelist.get(findIndex).Path---" + existImagelist.get(findIndex).Path);
								Log.i(Constants.TAG, "-----findIndex--"+ findIndex +"---addPath---" + addPath);

								if(existImagelist.get(findIndex).Path.equals(addPath))
								{
									bExistImage = true;
									Log.i(Constants.TAG, "----bExistImage---" + bExistImage);
									break;
								}
							}
							
							if(!bExistImage)
							{
								Log.i(Constants.TAG, "--ACCOUNT_MORE_INFO_IMAGE --add new Image--" + addPath);

								ImageItem item = new ImageItem();
								item.Path = addPath;
								item.account = m_CurrentAccount;
								item.save();
								
								if(m_CurrentAccount.isSyncOnServer())
								{
									m_CurrentAccount.SyncStatus = Constants.ACCOUNT_ITEM_ACTION_NEED_SYNC_UP;
								}
								m_CurrentAccount.save();
							}

						}
						ActiveAndroid.setTransactionSuccessful();
					} finally {
						ActiveAndroid.endTransaction();
					}
				} else {
					Log.i(Constants.TAG, "----ACCOUNT_MORE_INFO_IMAGE-data == null-");
				}
			}
				break;
			default:
				break;
			}
			m_LoadMoreInfoDataSrc();
			m_MoreInfoAdapter.replaceDataSrc(mMoreInfoListDataSource);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private Boolean m_SaveAccount() {
		if (m_CurrentAccount != null) {
			Log.i(Constants.TAG, "--m_CurrentAccount save id ---" + m_CurrentAccount.getId());

			m_CurrentAccount.save();

			Log.i(Constants.TAG, "--m_CurrentAccount cost---" + m_CurrentAccount.Cost);

		}
		return true;
	}
		
}
