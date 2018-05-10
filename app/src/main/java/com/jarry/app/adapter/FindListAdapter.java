package com.jarry.app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jarry.app.App;
import com.jarry.app.R;
import com.jarry.app.bean.Comments;
import com.jarry.app.bean.Favorite;
import com.jarry.app.bean.Photo;
import com.jarry.app.bean.Status;
import com.jarry.app.bean.User;
import com.jarry.app.bean.UserComm;
import com.jarry.app.db.StatusDao;
import com.jarry.app.ui.activity.CommentAndRepostActivity;
import com.jarry.app.ui.activity.MainActivity;
import com.jarry.app.util.CommentFun;
import com.jarry.app.util.CustomTagHandler;
import com.jarry.app.util.DataUtil;
import com.jarry.app.util.ScreenUtil;
import com.jarry.app.util.StringUtil;
import com.jarry.app.view.ClickCircleImageView;
import com.jarry.app.view.LikesView;
import com.jarry.app.view.ninegridlayout.NineGridlayout;
import com.jarry.app.view.ninegridlayout.OneImage;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FindListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List list;
    private String tag;

    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    public static final int LOAD_END = 3;

    private static final int TYPE_FOOTER = -1;
    private int status = 1;
    private CustomTagHandler mTagHandler;
    RecyclerView recyclerView;

    public FindListAdapter(Context context, List list, String tag, CustomTagHandler tagHandler) {
        this.context = context;
        this.list = list;
        this.tag = tag;
        this.mTagHandler = tagHandler;

    }

    public FindListAdapter(Context context, List list, String tag, CustomTagHandler tagHandler, RecyclerView recyclerView) {
        this.context = context;
        this.list = list;
        this.tag = tag;
        this.mTagHandler = tagHandler;
        this.recyclerView = recyclerView;
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
            switch (tag) {
                case "home_fg": {
                    View view = View.inflate(parent.getContext(), R.layout.item_weibo_home_list, null);
                    return new WeiBoListViewHolder(view);
                }
                case "favorites": {
                    View view = View.inflate(parent.getContext(), R.layout.item_weibo_home_list, null);
                    return new WeiBoListViewHolder(view);
                }
                case "tab_fg": {
                    View view = View.inflate(parent.getContext(), R.layout.fragment_tab_item, null);
                    return new WeiBoTabViewHolder(view);
                }
                case "message_at_comment": {
                    View view = View.inflate(parent.getContext(), R.layout.item_message_at_comment, null);
                    return new MessageAtCommentViewHolder(view);
                }
                case "friend_ship": {
                    View view = View.inflate(parent.getContext(), R.layout.item_friends_ship, null);
                    return new FriendShipsViewHolder(view);
                }
                case "user_photo": {
                    View view = View.inflate(parent.getContext(), R.layout.item_photo, null);
                    return new UserPhotoViewHolder(view);
                }
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.bindItem();
        } else {
            switch (tag) {
                case "home_fg":
                    WeiBoListViewHolder weiBoListViewHolder = (WeiBoListViewHolder) holder;
                    weiBoListViewHolder.bindItem(context, (Status) list.get(position));
                    break;
                case "favorites":
                    Favorite favorite = (Favorite) list.get(position);
                    WeiBoListViewHolder weiBoListHolder = (WeiBoListViewHolder) holder;
                    weiBoListHolder.bindItem(context, favorite.getStatus());
                    break;
                case "tab_fg":
                    WeiBoTabViewHolder weiBoTabViewHolder = (WeiBoTabViewHolder) holder;
                    weiBoTabViewHolder.bindItem(context, (Comments) list.get(position));
                    break;
                case "message_at_comment":
                    MessageAtCommentViewHolder atCommentViewHolder = (MessageAtCommentViewHolder) holder;
                    atCommentViewHolder.bindItem((Comments) list.get(position));
                    break;
                case "friend_ship":
                    FriendShipsViewHolder friendShipsViewHolder = (FriendShipsViewHolder) holder;
                    friendShipsViewHolder.bindItem((User) list.get(position));
                    break;
                case "user_photo":
                    UserPhotoViewHolder userPhotoViewHolder = (UserPhotoViewHolder) holder;
                    userPhotoViewHolder.bindItem((Photo) list.get(position));
                    break;
            }
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

    class WeiBoListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout_weibo_list)
        LinearLayout layout_weibo_list;
        @BindView(R.id.tv_weibo_userName)
        TextView tv_weibo_userName;
        @BindView(R.id.tv_weibo_source)
        TextView tv_weibo_source;
        @BindView(R.id.tv_weibo_create_time)
        TextView tv_weibo_create_time;
        @BindView(R.id.tv_weibo_text)
        TextView tv_weibo_text;
        @BindView(R.id.tv_weibo_attitudes_count)
        TextView tv_weibo_attitudes_count;
        @BindView(R.id.tv_weibo_reposts_count)
        TextView tv_weibo_reposts_count;
        @BindView(R.id.tv_weibo_comments_count)
        TextView tv_weibo_comments_count;
        @BindView(R.id.iv_weibo_icon)
        ClickCircleImageView iv_weibo_icon;
        @BindView(R.id.iv_one_image)
        OneImage iv_one_image;
        @BindView(R.id.iv_nine_grid_layout)
        NineGridlayout iv_nine_grid_layout;
        @BindView(R.id.tv_quick_dialog)
        TextView tv_quick_dialog;
        // zhuanfa
        @BindView(R.id.layout_weibo_zhuanfa)
        LinearLayout layout_weibo_zhuanfa;
        @BindView(R.id.tv_weibo_zhuanfa_userName)
        TextView tv_weibo_zhuanfa_userName;
        @BindView(R.id.tv_weibo_zhuanfa_text)
        TextView tv_weibo_zhuanfa_text;
        @BindView(R.id.tv_weibo_zhuanfa_reposts_count)
        TextView tv_weibo_zhuanfa_reposts_count;
        @BindView(R.id.tv_weibo_zhuanfa_comments_count)
        TextView tv_weibo_zhuanfa_comments_count;
        @BindView(R.id.iv_z_one_image)
        OneImage iv_z_one_image;
        @BindView(R.id.iv_z_nine_grid_layout)
        NineGridlayout iv_z_nine_grid_layout;
        @BindView(R.id.comment_list)
        LinearLayout mCommentList;
        @BindView(R.id.btn_input_comment)
        View mBtnInput;

        @BindView(R.id.tv_zan)
        LikesView tv_zan;

        @OnClick(R.id.btn_input_comment)
        public void inputComment(final View v) {
            inputComment(v, null);
        }

        /**
         * 弹出评论对话框
         *
         * @param v
         * @param receiver
         */
        public void inputComment(final View v, UserComm receiver) {
            Status status = (Status) v.getTag();
            CommentFun.inputComment(status, (Activity) context, recyclerView, v, receiver, new CommentFun.InputCommentListener() {
                @Override
                public void onCommitComment() {
                    //存数据库
                    status.setmCommentStr();
                    Log.e("ttt", new Gson().toJson(receiver));
                    Log.e("更改之后的消息", status == null ? "null" : new Gson().toJson(status));
                    //插入之前改下数据格式
                    App.mDb.insert(status, ConflictAlgorithm.Replace);
                    Log.e("更改之后的消息", "--->" + new Gson().toJson(StatusDao.getAll()));
                    ;
                    notifyDataSetChanged();
                }
            });
        }

        public WeiBoListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            setDrawableSize(tv_weibo_attitudes_count, R.drawable.good_16px);
            setDrawableSize(tv_weibo_reposts_count, R.drawable.skip_16px);
            setDrawableSize(tv_weibo_comments_count, R.drawable.comment_16px);
            setDrawableSize(tv_quick_dialog, R.drawable.more_16px);

            LinearLayout.LayoutParams paramsIv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsIv.gravity = Gravity.LEFT;
            paramsIv.setMargins(0, ScreenUtil.instance(context).dip2px(8), 0, 0);
            iv_one_image.setLayoutParams(paramsIv);
        }

        @SuppressLint("RestrictedApi")
        public void bindItem(Context context, Status status) {
            mBtnInput.setTag(status);
//            if (status.getText().contains("抱歉，已被删除")) {
//                layout_weibo_zhuanfa.setVisibility(View.GONE);
//                tv_weibo_text.setText(StringUtil.getWeiBoText(context, status.getText()));
//                return;
//            }
            tv_weibo_userName.setText(status.getUser().getScreen_name());
            tv_weibo_source.setText(StringUtil.getWeiboSource(status.getSource()));
            tv_weibo_create_time.setText(DataUtil.showTime(status.getCreated_at()));
            tv_weibo_attitudes_count.setText(status.getAttitudes_count());
            tv_weibo_comments_count.setText(status.getComments_count());
            tv_weibo_reposts_count.setText(status.getReposts_count());
            // text
            tv_weibo_text.setMovementMethod(LinkMovementMethod.getInstance());
            tv_weibo_text.setText(StringUtil.getWeiBoText(context, status.getText()));

            // icon
            iv_weibo_icon.setUserImage(status.getUser());
            // img && zhuanfa
            if (status.getRetweeted_status() == null) {
                layout_weibo_zhuanfa.setVisibility(View.GONE);
                setWeiBoImg(status.getPic_urls(), iv_one_image, iv_nine_grid_layout);
            } else {
                layout_weibo_zhuanfa.setVisibility(View.VISIBLE);
                iv_nine_grid_layout.setVisibility(View.GONE);
                iv_one_image.setVisibility(View.GONE);
                tv_weibo_zhuanfa_reposts_count.setText("转发:(" + status.getRetweeted_status().getReposts_count() + ")");
                tv_weibo_zhuanfa_comments_count.setText("评论:(" + status.getRetweeted_status().getComments_count() + ")");
                // zhuanfa userName
                if (status.getRetweeted_status().getUser() != null && !status.getRetweeted_status().getUser().equals("")) {
                    tv_weibo_zhuanfa_userName.setMovementMethod(LinkMovementMethod.getInstance());
                    tv_weibo_zhuanfa_userName.setText(StringUtil.getWeiBoText(context, "@" + status.getRetweeted_status().getUser().getScreen_name()));
                }
                // zhuanfa text
                tv_weibo_zhuanfa_text.setMovementMethod(LinkMovementMethod.getInstance());
                tv_weibo_zhuanfa_text.setText(StringUtil.getWeiBoText(context, status.getRetweeted_status().getText()));
                // img
                setWeiBoImg(status.getRetweeted_status().getPic_urls(), iv_z_one_image, iv_z_nine_grid_layout);
            }
            ArrayList<User> likeUsers = status.likeUsers;
            if (likeUsers != null && likeUsers.size() > 0) {
                tv_zan.setVisibility(View.VISIBLE);
                tv_zan.setList(status.likeUsers);
                tv_zan.notifyDataSetChanged();
            } else {
                tv_zan.setVisibility(View.GONE);

            }
            tv_weibo_attitudes_count.setOnClickListener(view -> {
                //设置
                //先判断如果已经喜欢了，则取消点赞，如果没，则点赞
                if (status.likeUsers != null && status.likeUsers.size() > 0) {
                    for (User user : status.likeUsers) {
                        //暂时认为名字唯一
                        if (user.getName().equals(User.getLoginUser().getName())) {
                            status.likeUsers.remove(user);
                            //删除喜欢
                            status.setLikeUsersStr();
                            App.mDb.insert(status, ConflictAlgorithm.Replace);
                            setDrawableSize(tv_weibo_attitudes_count, R.drawable.good_16px);
                            notifyDataSetChanged();
                            return;
                        }
                    }
                }
                if (status.likeUsers == null) {
                    status.likeUsers = new ArrayList<>();
                }
                status.likeUsers.add(User.getLoginUser());
                status.setLikeUsersStr();
                App.mDb.insert(status, ConflictAlgorithm.Replace);
                setDrawableSize(tv_weibo_attitudes_count, R.drawable.good_h);
                notifyDataSetChanged();
            });
            tv_weibo_reposts_count.setOnClickListener(view -> {
                context.startActivity(CommentAndRepostActivity.newIntent(context, status, "转发微博", null));

            });
            tv_weibo_comments_count.setOnClickListener(view -> {
                //直接回复
                inputComment(mBtnInput, null);
//                context.startActivity(CommentAndRepostActivity.newIntent(context, status, "回复微博", null));
            });

            //评论
            Log.e("tag", "status.getText=" + status.getText());
            Log.e("tag", "status.mComment=" + new Gson().toJson(status.mComment));
            if (status.mComment != null && status.mComment.size() > 0) {
                mCommentList.setVisibility(View.VISIBLE);
                CommentFun.parseCommentList(context, status.mComment,
                        mCommentList, mBtnInput, mTagHandler);
            } else {
                mCommentList.setVisibility(View.GONE);
            }


        }

        private void setDrawableSize(TextView textView, int id) {
            Drawable drawable = context.getResources().getDrawable(id);
            drawable.setBounds(0, 0, (drawable.getIntrinsicWidth() / 3), (drawable.getIntrinsicHeight() / 3));
            textView.setCompoundDrawables(drawable, null, null, null);
        }

        private void setWeiBoImg(ArrayList<Status.ThumbnailPic> pic_urls, OneImage oneImage, NineGridlayout nineGridlayout) {
            if (pic_urls != null) {
                if (pic_urls.size() == 1) {
                    nineGridlayout.setVisibility(View.GONE);
                    oneImage.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(pic_urls.get(0).localPic))
                        oneImage.setImageLocal(pic_urls.get(0).localPic);
                    else
                        oneImage.setImageUrl(pic_urls.get(0).getSmallImg());
                } else {
                    nineGridlayout.setVisibility(View.VISIBLE);
                    oneImage.setVisibility(View.GONE);
                    nineGridlayout.setImagesData(pic_urls);
                }
            } else {
                nineGridlayout.setVisibility(View.GONE);
                oneImage.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 微博Tab列表
     */
    class WeiBoTabViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_icon)
        ClickCircleImageView iv_user_icon;
        @BindView(R.id.tv_user_name)
        TextView tv_user_name;
        @BindView(R.id.tv_user_create_at)
        TextView tv_user_create_at;
        @BindView(R.id.tv_weibo_text)
        TextView tv_weibo_text;
        @BindView(R.id.iv_reply_comment)
        ImageView iv_reply_comment;

        public WeiBoTabViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
        }

        public void bindItem(Context context, Comments comments) {
            tv_user_name.setText(comments.getUser().getScreen_name());
            tv_user_create_at.setText(DataUtil.showTime(comments.getCreated_at()));
            tv_weibo_text.setMovementMethod(LinkMovementMethod.getInstance());
            tv_weibo_text.setText(StringUtil.getWeiBoText(context, comments.getText()));
            iv_user_icon.setUserImage(comments.getUser());

            iv_reply_comment.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, iv_reply_comment);
                popup.getMenuInflater()
                        .inflate(R.menu.pop_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.comment_reply:
//                            context.startActivity(CommentAndRepostActivity.newIntent(context, comments.getStatus(), "回复评论", comments));
                            break;
                        case R.id.copy:
                            StringUtil.copy(comments.getText(), context);
                            break;
                    }
                    return true;
                });
                popup.show();
            });
        }
    }

    /**
     * 微博消息列表
     */
    class MessageAtCommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_at_comment_icon)
        ClickCircleImageView iv_at_comment_icon;
        @BindView(R.id.tv_at_comment_userName)
        TextView tv_at_comment_userName;
        @BindView(R.id.tv_at_comment_text)
        TextView tv_at_comment_text;
        @BindView(R.id.tv_at_comment_status_userName_text)
        TextView tv_at_comment_status_userName_text;
        @BindView(R.id.layout_at_message)
        LinearLayout layout_at_message;
        @BindView(R.id.reply_at_comment)
        ImageView reply_at_comment;

        public MessageAtCommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ScreenUtil screenUtil = ScreenUtil.instance(context);
            layout_at_message.setLayoutParams(new LinearLayout.LayoutParams(screenUtil.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public void bindItem(Comments comments) {
            iv_at_comment_icon.setUserImage(comments.getUser());
            tv_at_comment_userName.setText(comments.getUser().getScreen_name());
            tv_at_comment_text.setText(StringUtil.getWeiBoText(context, comments.getText()));
            tv_at_comment_status_userName_text.setText(StringUtil.getWeiBoText(context, "@" + comments.getStatus().getUser().getScreen_name() + ":" + comments.getStatus().getText()));

            //点击事件
//            layout_at_message.setOnClickListener(v -> context.startActivity(WeiBoDetailActivity.newIntent(context, comments.getStatus())));
            //点击回复
            reply_at_comment.setOnClickListener(v -> {
//                context.startActivity(CommentAndRepostActivity.newIntent(context, comments.getStatus(), "回复评论", comments));
            });
        }
    }


    /**
     * 微博好友列表
     */
    class FriendShipsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_friend_icon)
        ClickCircleImageView iv_friend_icon;
        @BindView(R.id.tv_friend_userName)
        TextView tv_friend_userName;
        @BindView(R.id.tv_friend_desc)
        TextView tv_friend_desc;

        public FriendShipsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
        }

        public void bindItem(User user) {
            tv_friend_userName.setText(user.getScreen_name());
            tv_friend_desc.setText(user.getDescription());
            iv_friend_icon.setUserImage(user);
        }
    }

    /**
     * User 界面相册界面
     */
    class UserPhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView iv_photo;
        @BindView(R.id.layout_photo)
        LinearLayout layout_photo;

        public UserPhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ScreenUtil screenUtil = ScreenUtil.instance(context);
            layout_photo.setLayoutParams(new LinearLayout.LayoutParams(screenUtil.getScreenWidth() / 3, screenUtil.getScreenWidth() / 3));
            iv_photo.setLayoutParams(new LinearLayout.LayoutParams(screenUtil.getScreenWidth() / 3, screenUtil.getScreenWidth() / 3));
        }

        public void bindItem(Photo photo) {
            Glide.with(context).load(photo.getPic_url()).centerCrop().into(iv_photo);
        }
    }


    // change recycler state
    public void updateLoadStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }
}
