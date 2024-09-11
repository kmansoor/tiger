package com.hurontg.tiger.parent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hurontg.tiger.R;
import com.hurontg.tiger.domain.Child;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class ChildListAdapter extends
    RecyclerView.Adapter<ChildListAdapter.ChildViewHolder>  {

  private final LinkedList<Child> mChildList;
  private LayoutInflater mInflater;
  ChildListAdapter.OnClickListener mListener;

  public ChildListAdapter(Context context, LinkedList<Child> childList, ChildListAdapter.OnClickListener listener) {
    mInflater = LayoutInflater.from(context);
    this.mChildList = childList;
    mListener = listener;
  }

  @NonNull
  @Override
  public ChildListAdapter.ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View mItemView = mInflater.inflate(R.layout.listview_setup_children, parent, false);
    return new ChildViewHolder(mItemView, this);
  }

  @Override
  public void onBindViewHolder(@NotNull ChildViewHolder holder, int position) {
    Child mCurrent = mChildList.get(position);
    holder.childItemView.setText(mCurrent.toString());
    holder.position = position;
  }

  @Override
  public int getItemCount() {
    return mChildList.size();
  }

  public void onEdit(int position) {
    mListener.onEdit(position);
  }

  public void onDelete(int position) {
    mListener.onDelete(position);
  }

  /**
   *
   */
  class ChildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView childItemView;
    public final ImageView edit, delete;
    public int position;
    final ChildListAdapter mAdapter;

    public ChildViewHolder(View itemView, ChildListAdapter adapter) {
      super(itemView);
      this.mAdapter = adapter;

      childItemView = itemView.findViewById(R.id.child_name);
      edit = (ImageView) itemView.findViewById(R.id.edit_child);
      delete = (ImageView) itemView.findViewById(R.id.delete_child);

      edit.setOnClickListener(this);
      delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.edit_child:
          mAdapter.onEdit(position);
          break;
        case R.id.delete_child:
          mAdapter.onDelete(position);
          break;
      }
    }
  }

  public interface OnClickListener {
    void onEdit(int position);
    void onDelete(int position);
  }
}