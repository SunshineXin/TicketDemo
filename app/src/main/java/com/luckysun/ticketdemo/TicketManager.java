package com.luckysun.ticketdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.luckysun.ticketdemo.network.CoreNetRequest;
import com.luckysun.ticketdemo.network.NetworkManager;
import com.luckysun.ticketdemo.network.OnRequestListener;
import com.luckysun.ticketdemo.network.Session;

/**
 */
public class TicketManager {

	public static final String TICKET_ID = "ticketId";
	public static final String FK_TICKET_DETAIL_ID = "fkTicketDetailId";
	public static final String TICKET_DETAIL = "ticketDetail";
	public static final String IS_RUN = "run";

    public static String TICKET_SEND = "http://www.baidu.com";
    public static String TICKET_NEW_MASSAGE = "http://www.baidu.com";

	public boolean isRun = false;
	
	/**
	 * 发送Ticket
	 */
	public void createTicket(Ticket ticket, OnRequestListener createTicketRequestlistener) {
        CoreNetRequest request = new CoreNetRequest(TICKET_SEND, createTicketRequestlistener);
        if(ticket.getId() != null){
            request.put("ticketId", ticket.getId());
        }
        request.put("content", ticket.getContent());
        request.put("token", "token");
        NetworkManager.getInstance().sendRequest(request, new TypeToken<Ticket>() {}.getType());
	}
	
	
	
	
	/**
	 * 获取新的Tieckt
	 */
	public void getNewTicketRequest(int ticketId, int fkTicketDetailId, OnRequestListener findTicketListener){
		CoreNetRequest request = new CoreNetRequest(TICKET_NEW_MASSAGE, findTicketListener);
		
		request.put("ticketId", ticketId);
		request.put("fkTicketDetailId", fkTicketDetailId);
		request.put("token", "token");
		
		NetworkManager.getInstance().sendRequest(request, new TypeToken<Ticket>() {}.getType());
	}
	
	
	private RefreshMassageThread mRefreshThread;
	
	public static int REFRESH_DELAYED = 10 * 1000;
	
	public void createRefreshMassage(Handler mainHandler){
		if(mRefreshThread == null){
			mRefreshThread = new RefreshMassageThread(mainHandler);
			mRefreshThread.start();
		}
		
	}
	
	public boolean isRefreshThreadIsRun(){
		 Log.d(TicketsDetailActivity.TAG,"isRun : " + isRun);
			return isRun;
	}
	
	public void pauseRefreshThread(){
		Message toMain = new Message();
		Bundle bundle = new Bundle();
		bundle.putBoolean(IS_RUN, false);
		toMain.setData(bundle);
		getThreadHanlder().sendMessage(toMain);
	}
	
	public void getNewMessage(int ticketId, int fkTicketDetailId){
		if(mRefreshThread != null){
			Message toMain = new Message();
			Bundle bundle = new Bundle();
			bundle.putInt(TICKET_ID, ticketId);
			bundle.putBoolean(IS_RUN, true);
			bundle.putInt(FK_TICKET_DETAIL_ID, fkTicketDetailId);
			toMain.setData(bundle);
			getThreadHanlder().sendMessage(toMain);
		}
	}
	
	public void quitRefreshLooper(){
		if(mRefreshThread != null && getThreadHanlder() != null){
			 Log.d(TicketsDetailActivity.TAG,"Stop looping the child thread's message queue");
			isRun = false;
			getThreadHanlder().getLooper().quit();
		}
	}
	
	public Handler getThreadHanlder(){
		if(mRefreshThread != null){
			return mRefreshThread.getChildHander();
		}
		return null;
	}
	
	private int ticketId;
	private int fkTicketDetailId;
	
	public void setTicketDetailId(int detailId){
		fkTicketDetailId = detailId;
		Message toMain = mRefreshThread.getChildHander().obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt(TICKET_ID, ticketId);
		bundle.putInt(FK_TICKET_DETAIL_ID, fkTicketDetailId);
		bundle.putBoolean(IS_RUN, true);
		toMain.setData(bundle);
	}
	
	private class RefreshMassageThread extends Thread{
		private Handler mainHandler;
		private Handler childHandler;
		
		public RefreshMassageThread(Handler mainHandler){
			this.mainHandler = mainHandler;
		}
		
		public Handler getChildHander (){
			return childHandler;
		}
		
		@SuppressLint("HandlerLeak")
		public void run() {
            this.setName("RefreshMassageThread");
            //初始化消息循环队列，需要在Handler创建之前
            Looper.prepare();
            childHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                	Bundle bundle = msg.getData();
                	if(bundle != null){
                		ticketId = bundle.getInt(TICKET_ID);
                		fkTicketDetailId = bundle.getInt(FK_TICKET_DETAIL_ID);
                		isRun = bundle.getBoolean(IS_RUN);
                		
                		 Log.d(TicketsDetailActivity.TAG,"-----------------------childHandler------------------");
                		 Log.d(TicketsDetailActivity.TAG,"ticketId : " + ticketId);
                		 Log.d(TicketsDetailActivity.TAG,"fkTicketDetailId : " + fkTicketDetailId);
                		 Log.d(TicketsDetailActivity.TAG,"isRun : " + isRun);
                        Log.d(TicketsDetailActivity.TAG,"-----------------------childHandler------------------");
                	}
                	if(isRun){
                        getNewTicketRequest(ticketId,fkTicketDetailId,getNewTicketRequestlistener);
                    }

                }
            };
            
            
           Log.d(TicketsDetailActivity.TAG,"Child handler is bound to --> "+ childHandler.getLooper().getThread().getName());
          Looper.loop();
        }
		
		
		
		/**
		 * 
		 */
		private OnRequestListener getNewTicketRequestlistener = new OnRequestListener() {
			@Override
			public void onResult(Session session) {
				if (isReturnDataSuccess(session)) {
					 Log.d(TicketsDetailActivity.TAG,"----------------sendTicketRequestlistener success-----------------");
					@SuppressWarnings("unchecked")
					Ticket detail = (Ticket) session.getResponse().getData();
					if(detail != null ){
						Message toMain = new Message();
						Bundle bundle = new Bundle();
						bundle.putSerializable(TICKET_DETAIL, detail);
						toMain.setData(bundle);
						mainHandler.sendMessage(toMain);

//						return;//FIXME 用于阻止childHandler发送消息，使得isRun＝false，停止消息循环
					}else{
						
					}
				} else {

				}
				
				Message toMain = childHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putInt(TICKET_ID, ticketId);
				bundle.putInt(FK_TICKET_DETAIL_ID, fkTicketDetailId);
				bundle.putBoolean(IS_RUN, true);
				toMain.setData(bundle);
				childHandler.sendMessageDelayed(toMain, REFRESH_DELAYED);
			}
		};
		
	}


    /**
     * 服务器返回数据是否成功
     */
    public static boolean isReturnDataSuccess(Session session) {
        return session != null && session.getResponse() != null && session.getResponse().isSuccess();
    }
}
