package com.gaoyy.restaurant.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.bean.Order;
import com.gaoyy.restaurant.utils.Constant;

import java.util.LinkedList;

public class OrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private LayoutInflater inflater;
    private LinkedList<Order> data;
    private String[] status = Constant.status;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.onItemClickListener = listener;
    }


    public OrderListAdapter(Context context, LinkedList<Order> data)
    {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View rootView = inflater.inflate(R.layout.item_check, parent, false);
        ItemOrderViewHolder itemOrderViewHolder = new ItemOrderViewHolder(rootView);
        return itemOrderViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        Order order = data.get(position);
        ItemOrderViewHolder itemOrderViewHolder = (ItemOrderViewHolder) holder;
//        itemOrderViewHolder.itemCheckPhone.setText("客户电话：" + order.getCustomer_phone());
        itemOrderViewHolder.itemCheckPhone.setText(order.getId()+"===客户电话：" + order.getCustomer_phone());
        itemOrderViewHolder.itemCheckStatus.setText(status[Integer.valueOf(order.getStatus())]);
        itemOrderViewHolder.itemCheckAddress.setText("地址：" + order.getCustomer_address());
        itemOrderViewHolder.itemCheckTime.setText(order.getCreate_time());

        if(onItemClickListener != null)
        {
            itemOrderViewHolder.itemCheckLayout.setOnClickListener(new BasicOnClickListener(itemOrderViewHolder));
        }

    }

    /**
     * 下拉刷新
     *
     * @param newDatas
     */
    public void addItem(LinkedList<Order> newDatas)
    {
        if (data.size() != 0)
        {
            data.clear();
        }

        for (int i = 0; i < newDatas.size(); i++)
        {
            data.addLast(newDatas.get(i));
        }
        notifyDataSetChanged();
    }

    /**
     * 上拉加载更多
     *
     * @param newDatas
     */
    public void addMoreItem(LinkedList<Order> newDatas)
    {
        for (int i = 0; i < newDatas.size(); i++)
        {
            data.addLast(newDatas.get(i));
        }
        notifyItemRangeInserted(getItemCount(), newDatas.size());
        notifyItemRangeChanged(getItemCount(), getItemCount() - newDatas.size());
    }

    /**
     * 指定item刷新
     * @param position
     * @param order
     */
    public void updateFromPosition(int position, Order order)
    {
        data.remove(position);
        data.add(position, order);
        notifyItemChanged(position);
    }

    public class BasicOnClickListener implements View.OnClickListener
    {
        ItemOrderViewHolder itemOrderViewHolder;

        public BasicOnClickListener(ItemOrderViewHolder itemOrderViewHolder)
        {
            this.itemOrderViewHolder = itemOrderViewHolder;
        }

        @Override
        public void onClick(View v)
        {
            int id = v.getId();
            switch (id)
            {
                case R.id.item_check_layout:
                    onItemClickListener.onItemClick(itemOrderViewHolder.itemCheckLayout, itemOrderViewHolder.getLayoutPosition());
                    break;
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public static class ItemOrderViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout itemCheckLayout;
        private TextView itemCheckPhone;
        private TextView itemCheckStatus;
        private TextView itemCheckAddress;
        private TextView itemCheckTime;

        public ItemOrderViewHolder(View itemView)
        {
            super(itemView);
            itemCheckLayout = (LinearLayout)itemView.findViewById(R.id.item_check_layout);
            itemCheckPhone = (TextView) itemView.findViewById(R.id.item_check_phone);
            itemCheckStatus = (TextView) itemView.findViewById(R.id.item_check_status);
            itemCheckAddress = (TextView) itemView.findViewById(R.id.item_check_address);
            itemCheckTime = (TextView) itemView.findViewById(R.id.item_check_time);
        }
    }
}
