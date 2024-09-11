package com.hurontg.tiger.staff;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hurontg.tiger.R;
import com.hurontg.tiger.domain.Parent;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class ParentListAdapter extends RecyclerView.Adapter<ParentListAdapter.ParentViewHolder> {
    private final String TAG = "ParentListAdapter";
    private final LinkedList<Parent> mParentList;
    private LayoutInflater mInflater;
    ParentListAdapter.OnClickListener mListener;

    public ParentListAdapter(Context context, LinkedList<Parent> parentList, ParentListAdapter.OnClickListener listener) {
      mInflater = LayoutInflater.from(context);
      this.mParentList = parentList;
      mListener = listener;
    }

    @NonNull
    @Override
    public ParentListAdapter.ParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View mItemView = mInflater.inflate(R.layout.listview_parent_pending_activation, parent, false);
      return new ParentListAdapter.ParentViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NotNull ParentListAdapter.ParentViewHolder holder, int position) {
      Parent mCurrent = mParentList.get(position);
      bindCurrentItemToView(holder, mCurrent);
      holder.uuid = mCurrent.getUuid();
    }

    @Override
    public int getItemCount() {
      return mParentList.size();
    }

    private void bindCurrentItemToView(ParentListAdapter.ParentViewHolder holder, Parent parent) {
      holder.name.setText(parent.getName());
      holder.email.setText(parent.getEmail());
      holder.phone.setText(parent.getPhone());
      holder.children.setText(parent.childrenAsOneLine());
    }

    public void onApprove(String uuid) {
      mListener.onApprove(uuid);
    }

    public void onReject(String uuid) {
      mListener.onReject(uuid);
    }

    /**
     *
     */
    class ParentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

      public final TextView name, email, phone, children;
      public final ImageView approve, reject;
      public String uuid;
      final ParentListAdapter mAdapter;

      public ParentViewHolder(View itemView, ParentListAdapter adapter) {
        super(itemView);
        this.mAdapter = adapter;

        name = itemView.findViewById(R.id.parent_name);
        email = itemView.findViewById(R.id.email);
        phone = itemView.findViewById(R.id.phone);
        children = itemView.findViewById(R.id.children);
        approve = itemView.findViewById(R.id.approve);
        reject = itemView.findViewById(R.id.reject);

        approve.setOnClickListener(this);
        reject.setOnClickListener(this);
      }

      @Override
      public void onClick(View v) {
        switch (v.getId()) {
          case R.id.approve:
            mAdapter.onApprove(uuid);
            break;
          case R.id.reject:
            mAdapter.onReject(uuid);
            break;
        }
      }
    }

    public interface OnClickListener {
      void onApprove(String uuid);
      void onReject(String uuid);
    }
  }