package org.smart.customviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import org.anoopam.main.AMAppMasterActivity;

public class SmartRecyclerView extends RecyclerView {
   private View emptyView;
   private Context context;

  public SmartRecyclerView(Context context) {
      super(context);
      this.context=context;
  }

  public SmartRecyclerView(Context context, AttributeSet attrs) { super(context, attrs);
  this.context=context;}

  public SmartRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
      this.context=context;
  }

  void checkIfEmpty() {
    if (emptyView != null) {
      emptyView.setVisibility(getAdapter().getItemCount() > 0 ? GONE : VISIBLE);
    }
  }

  final AdapterDataObserver observer = new AdapterDataObserver() {
    @Override public void onChanged() {
      super.onChanged();
      checkIfEmpty();
    }
  };

  @Override public void setAdapter(Adapter adapter) {
    final Adapter oldAdapter = getAdapter();
    if (oldAdapter != null) {
      oldAdapter.unregisterAdapterDataObserver(observer);
    }
    super.setAdapter(adapter);
    if (adapter != null) {
      adapter.registerAdapterDataObserver(observer);
    }
  }

  public void setEmptyView( View emptyView) {
    this.emptyView = emptyView;
    checkIfEmpty();
  }

  public void setEmptyView( int emptyViewID) {

    this.emptyView = ((AMAppMasterActivity)this.context).getLayoutInflater().from(context).inflate(emptyViewID,null);
    checkIfEmpty();
  }
}