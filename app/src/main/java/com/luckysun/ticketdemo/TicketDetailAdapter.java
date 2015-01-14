package com.luckysun.ticketdemo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luckysun.ticketdemo.util.DateUtils;

public class TicketDetailAdapter extends BaseAdapter{

	private List<Ticket> mList = new ArrayList<Ticket>();
	private Context mContext;
	
	public TicketDetailAdapter(Context context,List<Ticket> list ) {
		this.mContext = context;
		this.mList = list;
	}

	public void appendToList(List<Ticket> lists) {

		if (lists == null) {
			return;
		}
		mList.addAll(lists);
		notifyDataSetChanged();
	}
	

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Ticket getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Ticket item = mList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_tickets_user_list,null);
			holder.ticketTime = (TextView) convertView.findViewById(R.id.tv_time);
			holder.ticketContentL = (TextView) convertView.findViewById(R.id.tv_tickets_feedback_l);
			holder.ticketContentR = (TextView) convertView.findViewById(R.id.tv_tickets_feedback_r);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(item != null){
			if(item.getTicketDetail().getSenderType() != null && item.getTicketDetail().getSenderType() == 2){
				holder.ticketContentL.setVisibility(View.VISIBLE);
				holder.ticketContentR.setVisibility(View.GONE);
				holder.ticketContentL.setText(item.getContent());
			}else{
				holder.ticketContentL.setVisibility(View.GONE);
				holder.ticketContentR.setVisibility(View.VISIBLE);
				holder.ticketContentR.setText(item.getContent());
			}
			holder.ticketTime.setText(DateUtils.getStringDateFromLong(item.getTicketDetail().getCreateTime()));
		}
		
		return convertView;
	}
	

	class ViewHolder {
		public TextView ticketTime;
		public TextView ticketContentL;
		public TextView ticketContentR;
		
	}



}
