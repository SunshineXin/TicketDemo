package com.luckysun.ticketdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.luckysun.ticketdemo.network.OnRequestListener;
import com.luckysun.ticketdemo.network.Session;
import com.luckysun.ticketdemo.util.DateUtils;
import com.luckysun.ticketdemo.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class TicketsDetailActivity extends Activity implements OnClickListener {
    public static final String TAG = "TicketsDetailActivity";
    private List<Ticket> mTicketList;

    private View mRootView;
    private ListView mListView;

    private TicketDetailAdapter mAdapter;

    private TextView mTitleTV;
    private ImageButton mSendTickeImageBtn;
    private EditText mResponseTicketET;
    //	private LoadingProgressDialog mLoadingDialog;
    private String mTitleStr;
    private int mTicketId;

    private TicketManager mTicketManager;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Log.d(TAG, "TicketsDetail handleMesage...");
//        	mTicketManager.isRun = false;
//        	isSend = true;
            if (bundle != null) {

//        		if(mTicketManager != null){
//                    //暂停接收客服消息的线程
//        			mTicketManager.pauseRefreshThread();
//        		}

                Log.d(TAG, "mTicketManager.isRun ： " + mTicketManager.isRun);

                @SuppressWarnings("unchecked")
                Ticket detail = (Ticket) bundle.getSerializable(TicketManager.TICKET_DETAIL);
                if (detail != null && mTicketList != null && mTicketList.size() > 0) {
                    Log.d(TAG, "mTicketList.get(mTicketList.size() -1).getContent() : " + mTicketList.get(mTicketList.size() - 1).getContent());


                    if (detail != null && detail.getFkTicketDetailId() != mTicketList.get(mTicketList.size() - 1).getFkTicketDetailId()) {
                        mTicketList.add(detail);
                        Log.d(TAG, "mTicketList.get(mTicketList.size() -1).getContent() : " + mTicketList.get(mTicketList.size() - 1).getContent());
                        mAdapter.notifyDataSetChanged();
                    }


                }

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_detail);

        mTitleStr = getIntent().getStringExtra("title");
        initView();
        mTicketManager = new TicketManager();
        mTicketManager.createRefreshMassage(mHandler);

//		mLoadingDialog = new LoadingProgressDialog(this);

        initData();
    }


    private void initView() {
        mRootView = findViewById(R.id.layout_root);
        mRootView.setVisibility(View.GONE);
        mListView = (ListView) findViewById(R.id.lv_ticket_content);
        mSendTickeImageBtn = (ImageButton) findViewById(R.id.ib_send_ticket);
        mSendTickeImageBtn.setOnClickListener(this);
        mResponseTicketET = (EditText) findViewById(R.id.et_response_ticket);
        mTitleTV = (TextView) findViewById(R.id.tv_title);
    }

    private void initData() {
        mRootView.setVisibility(View.VISIBLE);
        if (mTitleStr != null) {
            mTitleTV.setText(mTitleStr);
        } else {
            mTitleTV.setText("在线客服");
        }

        if (mTicketList == null || mTicketList.size() > 0) {
            mTicketList = new ArrayList<Ticket>();
            Ticket ticket = new Ticket();
            TicketDetail detail = new TicketDetail();
            detail.setSenderType(2);
            detail.setCreateTime(DateUtils.getCurrentDetailLongTime());
            ticket.setContent("顾客您好，请问有什么可以为您服务?");

            ticket.setTicketDetail(detail);
            mTicketList.add(ticket);
        }


        mAdapter = new TicketDetailAdapter(this, mTicketList);
        mListView.setStackFromBottom(true);
        mListView.setAdapter(mAdapter);


    }


    private Ticket mResponseTicket;

    private void responseTicket() {
        if (TextUtils.isEmpty(mResponseTicketET.getText().toString())) {
            return;
        }
        TicketDetail detail = new TicketDetail();
        detail.setSenderType(1);
        detail.setCreateTime(DateUtils.getCurrentDetailLongTime());

        mResponseTicket = new Ticket();

        if (mTicketId != 0) {
            mResponseTicket.setId(mTicketId);
        }

        mResponseTicket.setContent(mResponseTicketET.getText().toString());
        mResponseTicket.setTicketDetail(detail);

        TicketManager manager = new TicketManager();
        manager.createTicket(mResponseTicket, sendTicketRequestlistener);

//        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
//            mLoadingDialog.show();
//            mLoadingDialog.setCanceledOnTouchOutside(false);
//        }


    }

    /**
     *
     */
    private OnRequestListener sendTicketRequestlistener = new OnRequestListener() {
        @Override
        public void onResult(Session session) {
            if (TicketManager.isReturnDataSuccess(session)) {
                Ticket data = (Ticket) session.getResponse().getData();
                if (mResponseTicket != null && data != null) {
                    mTicketId = data.getId();
//                    mResponseTicket = data;

                    mTicketList.add(mResponseTicket);
                    mAdapter.notifyDataSetChanged();
                    mResponseTicketET.setText("");

                    if (!mTicketManager.isRun) {
                        mTicketManager.getNewMessage(data.getId(), data.getFkTicketDetailId());
                    } else {
                        if (data.getFkTicketDetailId() != null) {
                            Log.d("TAG", "mTicketManager.setTicketDetailId : " + data.getFkTicketDetailId());
                            mTicketManager.setTicketDetailId(data.getFkTicketDetailId());
                        }

                    }

                } else {
                }


                isFirstSend = false;
            } else {
                UIUtils.falseToast(TicketsDetailActivity.this, "消息发送失败，请稍后重试.");
            }

//            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
//                mLoadingDialog.dismiss();
//            }
        }
    };


    private boolean isFirstSend = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                UIUtils.hideSoftInputMethod(TicketsDetailActivity.this, mResponseTicketET, false);
                finish();
                break;
            case R.id.ib_send_ticket:
                if (isFirstSend || mTicketId != 0) {
                    responseTicket();
                } else {
                    UIUtils.falseToast(this, "客服人员正在回复中,请稍后");
                }

                break;

            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTicketManager != null) {
            mTicketManager.quitRefreshLooper();
        }
    }

    long mExitTime;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            UIUtils.falseToast(this, "再次点击将关闭此次在线咨询");
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }


}
