package com.hurontg.tiger.staff;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hurontg.tiger.R;
import com.hurontg.tiger.core.HttpService;
import com.hurontg.tiger.domain.Parent;
import com.hurontg.tiger.util.Constants;

import java.util.LinkedList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AdminActivity extends BaseMenuActivity
  implements ParentListAdapter.OnClickListener, ConfirmationDialog.ConfirmationDialogListener {
  private final String TAG = "AdminActivity";

  private Disposable mDisposable = null;
  private HttpService mHttp = new HttpService();
  private boolean mTakeMore = true;
  private LinkedList<Parent> mParentsPendingActivation = new LinkedList<>();
  private ParentListAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    RecyclerView mRecyclerView = findViewById(R.id.recyclerview_parent_pending_activation);
    mAdapter = new ParentListAdapter(this, mParentsPendingActivation, this);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    loadPendingActivationClients();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.mTakeMore = false;
    if (mDisposable != null && !mDisposable.isDisposed()) {
      mDisposable.dispose();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean b = super.onCreateOptionsMenu(menu);
    setMapMenuItemsState(menu);
    return b;
  }

  private void setMapMenuItemsState(Menu menu) {
    menu.findItem(R.id.satellite_map).setVisible(false);
    menu.findItem(R.id.my_location).setVisible(false);
    menu.findItem(R.id.admin_activity).setVisible(false);
    menu.findItem(R.id.campus_primary).setVisible(false);
    menu.findItem(R.id.campus_secondary).setVisible(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.normal_map:
        startMapsActivity();
        return true;
      case R.id.listview_activity:
        startListViewActivity();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void loadPendingActivationClients() {
    mDisposable = mHttp.loadPendingActivationClients()
        .takeWhile(evt -> mTakeMore)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(parents -> {
          mParentsPendingActivation.clear();
          mParentsPendingActivation.addAll(parents);
          mAdapter.notifyDataSetChanged();
        }, err -> {
          Toast.makeText(this, "An unexpected error occurred, please try again", Toast.LENGTH_LONG).show();
        });
  }

  @Override
  public void onApprove(String uuid) {
    setApplicationStatus(Constants.ACTIVE, uuid);
  }

  @Override
  public void onReject(String uuid) {
    ConfirmationDialog dialog = ConfirmationDialog.instance("Are you sure you want to Reject this?", uuid);
    dialog.show(getSupportFragmentManager(), "DialogFragment");
  }

  @Override
  public void onDialogPositiveClick(DialogFragment dialog) {
    String uuid = ((ConfirmationDialog) dialog).uuid;
    setApplicationStatus(Constants.REJECTED, uuid);
  }

  private void setApplicationStatus(String status, String uuid) {
    final int[] index = new int[1];
    index[0] = -1;
    for(Parent p: mParentsPendingActivation) {
      index[0]++;
      if (p.getUuid().equals(uuid)) {
        break;
      }
    }

    Parent parent = mParentsPendingActivation.get(index[0]);
    mDisposable = mHttp.setParentApplictaionStatus(status, parent.getUuid())
        .takeWhile(evt -> mTakeMore)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(res -> {
          mParentsPendingActivation.remove(index[0]);
          mAdapter.notifyItemRemoved(index[0]);
        }, err -> Toast.makeText(this, "An unexpected error occurred, please try again", Toast.LENGTH_LONG).show());
  }

  @Override
  public void onDialogNegativeClick(DialogFragment dialog) {

  }
}
