package com.hurontg.tiger.staff;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hurontg.tiger.R;

import java.util.LinkedList;

public class LocationListAdapter extends
    RecyclerView.Adapter<LocationListAdapter.LocationViewHolder>  {

  private final LinkedList<String> mWordList;
  private LayoutInflater mInflater;
  private  LocationListAdapter.OnClickListener mListener;

  public LocationListAdapter(Context context,
                             LinkedList<String> wordList, LocationListAdapter.OnClickListener listener) {
    mInflater = LayoutInflater.from(context);
    this.mWordList = wordList;
    this.mListener = listener;
  }

  @NonNull
  @Override
  public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View mItemView = mInflater.inflate(R.layout.listview_item,
        parent, false);
    return new LocationViewHolder(mItemView, this);
  }

  @Override
  public void onBindViewHolder(LocationViewHolder holder, int position) {
    String mCurrent = mWordList.get(position);
    holder.wordItemView.setText(mCurrent);
    holder.position = position;
  }

  @Override
  public int getItemCount() {
    return mWordList.size();
  }

  public void onClick(int position) {
    mListener.onClick(position);
  }

  class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public int position;
    public final TextView wordItemView;
    final LocationListAdapter mAdapter;

    public LocationViewHolder(View itemView, LocationListAdapter adapter) {
      super(itemView);
      this.mAdapter = adapter;
      wordItemView = itemView.findViewById(R.id.word);
      wordItemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      mAdapter.onClick(position);
    }
  }

  public interface OnClickListener {
    void onClick(int position);
  }
}