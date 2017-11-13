package com.xfy.fakeview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import xfy.fakeview.library.DebugInfo;
import xfy.fakeview.library.layermerge.LayersMergeEngine;
import xfy.fakeview.library.layermerge.LayersMergeManager;

public class TestMergeEngineActivity extends Activity {
    private int tag;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_merge_engine);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        tag = hashCode();
        DebugInfo.setDebug(true);
        recyclerView.setAdapter(new Adapter(30));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LayersMergeEngine.getEngine().resume();
                } else {
                    LayersMergeEngine.getEngine().pause();
                }
            }
        });
    }

    private class Adapter extends RecyclerView.Adapter<VH> {
        int count;
        Adapter(int count) {
            this.count = count;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.test_merge_layers, parent, false));
        }

        @Override
        public void onBindViewHolder(final VH holder, final int position) {
            holder.itemView.findViewById(R.id.linear_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("vh", "onclick " + position + " " + v);
                }
            });
            LayersMergeEngine.getEngine().addMergeAction(tag, (FrameLayout) holder.itemView, LayersMergeManager.EXTRACT_ALL);
        }

        @Override
        public int getItemCount() {
            return count;
        }
    }

    private class VH extends RecyclerView.ViewHolder {

        public VH(View itemView) {
            super(itemView);
        }
    }
}
