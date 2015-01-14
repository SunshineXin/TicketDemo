package com.luckysun.ticketdemo.network;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luckysun.ticketdemo.Ticket;
import com.luckysun.ticketdemo.TicketDetail;
import com.luckysun.ticketdemo.TicketsDetailActivity;
import com.luckysun.ticketdemo.util.DateUtils;

/**
 * RequestDispatcher
 */
public class NetworkManager implements Singleton {
	private static NetworkManager instance;
	private NetworkThreadPoolExecutor executor;
	private Handler handler;
	
	private NetworkManager() {
		executor = NetworkThreadPoolExecutor.getInstance();
		handler = new Handler(Looper.getMainLooper());
	}
	
	@Override
	public void init() {
		if(executor == null) {
			executor = NetworkThreadPoolExecutor.getInstance();
		}
		if(handler == null) {
			handler = new Handler(Looper.getMainLooper());
		}
	}
	
	public static synchronized NetworkManager getInstance() {
		if(instance == null) {
			instance = new NetworkManager();
		}
		return instance;
	}
	
	/**
	 * 发送核心网络请求
	 * 
	 * @param request ：核心请求，不能为空
	 */
	public void sendRequest(CoreNetRequest request, final Type type) {

		if(request == null || request.isIllegal()) {
			throw new RuntimeException("请求非法");
		}

        Log.d(TicketsDetailActivity.TAG,"-----------------------client send message------------------");
        Log.d(TicketsDetailActivity.TAG,"send request : " + request.getNormalURLParams());

		NetworkCallback callback = new NetworkCallback() {
			@Override
			public void onResult(final CoreNetRequest request, final String data) {
				BaseResponse response = parseJsonToResponse(data, type);

				final Session session = new Session(request, response);
				handler.post(new Runnable() {
					@Override
					public void run() {
						if(!request.isCancel()){//如果没有取消的话，将结果传回Activity
							request.getListener().onResult(session);
						}
						request.setCancel(false);
					}
				});
			}
		};
		
		if(CoreNetRequest.Priority.HIGH.equals(request.getPriority())) {
		    //优先级高，直接开线程处理
			executeHighPriorityTask(request, callback);
		}else {
		    //TODO 优先级低，加入请求队列，逐一处理
		}
	}
	
        private void executeHighPriorityTask(final CoreNetRequest request, final NetworkCallback callback) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
                //TODO 网络请求,使用postDelayed()来模拟网络请求
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();//GSON 解析
                        Ticket ticket = new Ticket();
                        ticket.setId(1);
                        int rdDetailId = (int)(Math.random() * 10000);
                        ticket.setFkTicketDetailId(rdDetailId);
                        ticket.setContent("Re:" + request.get("content"));

                        TicketDetail detail = new TicketDetail();
                        detail.setCreateTime(DateUtils.getCurrentDetailLongTime());
                        detail.setSenderType(2);
                        ticket.setTicketDetail(detail);

                        String data = "{ \"result\":\"success\", \"message\":\"\", \"data\":"  + gson.toJson(ticket) + "}";


                        Log.d(TicketsDetailActivity.TAG,"---------------server reply message------------ ");
                        Log.d(TicketsDetailActivity.TAG,"result getNormalURLParams : " + request.getNormalURLParams());
                        Log.d(TicketsDetailActivity.TAG,"result data : " + data);
                        Log.d(TicketsDetailActivity.TAG,"rdDetailId : " + rdDetailId);

                        callback.onResult(request,data);
                    }
                },2*1000);
			}
		};
		executor.execute(runnable);
	}
	
	public BaseResponse parseJsonToResponse(String data, Type type) {
		BaseResponse response = null;
		try {
			JSONObject object = new JSONObject(data);
			
			boolean success = false;
			String message = null;
			
			if(object.has("result")){
				String result = object.get("result").toString().trim();

				if(result.equals("success")){
					success = true;
				}else{
					success = false;
				}
			}
			
			if(object.has("message")){
				message = object.get("message").toString();
			}
			
			Object dataModel = null;
			if(object.has("data") && type != null) {
				String dataString = object.get("data").toString();
				Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();//GSON 解析
				dataModel = gson.fromJson(dataString, type);
			}
			
			response = new BaseResponse(success, message, dataModel);
			response.setResult(data);
		}catch(Exception e) {
			e.printStackTrace();

		}
		return response;
	}



	@Override
	public void release() {
	}
}
