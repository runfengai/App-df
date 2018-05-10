package com.jarry.app.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jarry.app.R;
import com.jarry.app.bean.ActiveBean;
import com.jarry.app.util.ScreenUtil;
import com.jarry.app.util.StringUtil;
import com.jarry.app.view.ClickCircleImageView;
import com.jarry.app.view.ninegridlayout.OneImage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ActiveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List list;

    private int status = 1;
    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    public static final int LOAD_END = 3;

    private static final int TYPE_FOOTER = -1;
    private int ActiveBean = 1;
    boolean isLecture = false;

    public ActiveListAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    public ActiveListAdapter(Context context, List list, boolean isLecture) {
        this.context = context;
        this.list = list;
        this.isLecture = isLecture;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return position;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = View.inflate(parent.getContext(), R.layout.item_footer, null);
            return new FooterViewHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.item_active_list, null);
            return new ActiveListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.bindItem();
        } else {
            ActiveListViewHolder activeListViewHolder = (ActiveListViewHolder) holder;
            activeListViewHolder.bindItem(context, (ActiveBean) list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    /**
     * footer view
     */
    class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_load_prompt)
        TextView tv_load_prompt;
        @BindView(R.id.progress)
        ProgressBar progress;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.instance(context).dip2px(40));
            itemView.setLayoutParams(params);
        }

        private void bindItem() {
            switch (status) {
                case LOAD_MORE:
                    progress.setVisibility(View.VISIBLE);
                    tv_load_prompt.setText("正在加载...");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_PULL_TO:
                    progress.setVisibility(View.GONE);
                    tv_load_prompt.setText("上拉加载更多");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_NONE:
                    System.out.println("LOAD_NONE----");
                    progress.setVisibility(View.GONE);
                    tv_load_prompt.setText("已无更多加载");
                    break;
                case LOAD_END:
                    itemView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    }

    class ActiveListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout_weibo_list)
        LinearLayout layout_weibo_list;
        @BindView(R.id.tv_weibo_userName)
        TextView tv_weibo_userName;
        @BindView(R.id.tv_weibo_org)
        TextView tv_weibo_org;
        @BindView(R.id.tv_weibo_create_time)
        TextView tv_weibo_create_time;
        @BindView(R.id.tv_weibo_text)
        TextView tv_weibo_text;
        @BindView(R.id.btn_join)
        Button btnJoin;

        @BindView(R.id.iv_weibo_icon)
        ClickCircleImageView iv_weibo_icon;


        public ActiveListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

//            LinearLayout.LayoutParams paramsIv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            paramsIv.gravity = Gravity.LEFT;
//            paramsIv.setMargins(0, ScreenUtil.instance(context).dip2px(8), 0, 0);
//            iv_one_image.setLayoutParams(paramsIv);
        }

        public void bindItem(Context context, ActiveBean activeBean) {
            Glide.with(context.getApplicationContext()).load(activeBean.icon).centerCrop().into(iv_weibo_icon);
            tv_weibo_userName.setText(activeBean.name);
            tv_weibo_org.setText("组织者：" + activeBean.orgnizeName);
            tv_weibo_create_time.setText(activeBean.createTime);
            // text
            tv_weibo_text.setMovementMethod(LinkMovementMethod.getInstance());
            tv_weibo_text.setText(StringUtil.getWeiBoText(context, activeBean.description));
            btnJoin.setText(isLecture ? "预约讲座" : "申请加入");
            btnJoin.setEnabled(true);
            if (activeBean.hasParticipate) {
                btnJoin.setText(isLecture ? "已预约" : "已加入");
                btnJoin.setEnabled(false);
            } else {
                btnJoin.setText(isLecture ? "预约讲座" : "申请加入");
                btnJoin.setEnabled(true);
            }
            btnJoin.setOnClickListener(view -> {
                activeBean.hasParticipate = true;
                if (isLecture) {
                    btnJoin.setText("已加入");
                } else btnJoin.setText("已预约");
                btnJoin.setEnabled(false);
                notifyDataSetChanged();
                Toast.makeText(context, isLecture ? "预约成功" : "加入成功", Toast.LENGTH_SHORT).show();

            });
        }
    }

    // change recycler state
    public void updateLoadStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }
}
