package idv.kuma.komica.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import idv.kuma.komica.R;
import idv.kuma.komica.adapters.LoadMoreViewHolder;
import idv.kuma.komica.configs.BundleKeyConfigs;
import idv.kuma.komica.entity.KReply;
import idv.kuma.komica.entity.KTitle;
import idv.kuma.komica.fragments.base.BaseFragment;
import idv.kuma.komica.http.NetworkCallback;
import idv.kuma.komica.http.OkHttpClientConnect;
import idv.kuma.komica.utils.AppTools;
import idv.kuma.komica.utils.KLog;
import idv.kuma.komica.views.PostView;
import idv.kuma.komica.widgets.DividerItemDecoration;
import idv.kuma.komica.widgets.KLinearLayoutManager;
import tw.showang.recycleradaterbase.RecyclerAdapterBase;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

/**
 * Created by TakumaLee on 2016/12/10.
 */

public class SectionPreviewFragment extends BaseFragment {
    private static final String TAG = SectionPreviewFragment.class.getSimpleName();

    private String indexUrl;
    private String url;
    private String title;

    private RecyclerView recyclerView;
    private KLinearLayoutManager linearLayoutManager;
    private SectionPreviewAdapter adapter;

    private List<KTitle> titlePostList = Collections.emptyList();

    public static SectionPreviewFragment newInstance(String url) {
        SectionPreviewFragment fragment = new SectionPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BundleKeyConfigs.KEY_WEB_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(BundleKeyConfigs.KEY_WEB_URL);
        indexUrl = url;
        titlePostList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_section_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        loadSection();
    }

    private void initView() {
        recyclerView = findViewById(getView(), R.id.recyclerView_sction_preview);
        adapter = new SectionPreviewAdapter(titlePostList);
        linearLayoutManager = new KLinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void loadNewSection(String url) {
        this.url = url;
        titlePostList.clear();
        loadSection();
    }

    private void loadSection() {
        OkHttpClientConnect.excuteAutoGet(url, new NetworkCallback() {
            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void onResponse(int responseCode, String result) {
                Document document = Jsoup.parse(result);
                title = document.getElementsByTag("title").text();
                notifyTitle();
                Elements elements = document.getElementById("threads").children();
                KTitle titlePost = null;
                List<KReply> replyList = new ArrayList<KReply>();
                for (Element element : elements) {
                    KLog.v(TAG, "Element: " + element);
                    if ("threadpost".equals(element.className())) {
                        // TODO post head
                        // TODO new a post head, and continue reply to next new.
                        if (null != titlePost) {
                            titlePost.setReplyList(replyList);
                            titlePostList.add(titlePost);
                            replyList = new ArrayList<KReply>();
                        }
                        titlePost = new KTitle(element, url);
                    } else if ("reply".equals(element.className())) {
                        // TODO post reply
                        KReply replyPost = new KReply(element, url);
                        replyList.add(replyPost);
                    }
                }
                titlePost.setReplyList(replyList);
                titlePostList.add(titlePost);
                notifyAdapter();
            }
        });
    }

    private void notifyTitle() {
        if (null != getActivity()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getActivity().setTitle(title);
                }
            });
        }
    }

    private void notifyAdapter() {
        if (null != getActivity()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    class SectionPreviewAdapter extends RecyclerAdapterBase {

        private List<KTitle> titleList;

        protected SectionPreviewAdapter(List<KTitle> dataList) {
            super(dataList);
            this.titleList = dataList;
        }

        @Override
        protected RecyclerView.ViewHolder onCreateItemViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            return new SectionPreViewHolder(inflater.inflate(R.layout.adapter_section_list_item, parent, false));
        }

        @Override
        protected void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            SectionPreViewHolder holder = (SectionPreViewHolder) viewHolder;
            KTitle head = titleList.get(position);
            holder.postIdTextView.setText("No. " + head.getId());
            holder.postTitleTextView.setText(head.getTitle());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.postQuoteTextView.setText(Html.fromHtml(head.getQuote(), FROM_HTML_MODE_LEGACY));
            } else {
                holder.postQuoteTextView.setText(Html.fromHtml(head.getQuote()));
            }
            Glide.with(getContext()).load(head.getImageUrl()).into(holder.postThumbImageView);
            if (titleList.get(position).getReplyList().size() > 0) {
                holder.replyLinearLayout.setVisibility(View.VISIBLE);
                for (KReply reply : titleList.get(position).getReplyList()) {
                    PostView postView = new PostView(viewHolder.itemView.getContext());
                    postView.setBackgroundResource(R.color.md_blue_50);
                    postView.setPadding(AppTools.dpToPx(16), AppTools.dpToPx(16), AppTools.dpToPx(16), AppTools.dpToPx(16));
                    postView.setPost(reply);
                    postView.notifyDataSetChanged();
                    holder.replyLinearLayout.addView(postView);
                }
            } else {
                holder.replyLinearLayout.setVisibility(View.GONE);
            }
        }

        @Override
        protected RecyclerView.ViewHolder onCreateLoadMoreViewHolder(LayoutInflater from, ViewGroup parent) {
            return new LoadMoreViewHolder(from.inflate(R.layout.item_loadmore, parent, false));
        }

        @Override
        protected void onBindLoadMoreViewHolder(RecyclerView.ViewHolder viewHolder, boolean isLoadMoreFailed) {
            super.onBindLoadMoreViewHolder(viewHolder, isLoadMoreFailed);
            LoadMoreViewHolder vh = (LoadMoreViewHolder) viewHolder;
            vh.progressBar.setVisibility(isLoadMoreFailed ? View.GONE : View.VISIBLE);
            vh.failText.setVisibility(isLoadMoreFailed ? View.VISIBLE : View.GONE);
        }
    }

    class SectionPreViewHolder extends RecyclerView.ViewHolder {

        TextView postIdTextView;
        TextView postTitleTextView;
        TextView postQuoteTextView;
        ImageView postThumbImageView;

        TextView postWarnTextView;
        Button moreBtn;
        LinearLayout replyLinearLayout;

        public SectionPreViewHolder(View itemView) {
            super(itemView);
            postIdTextView = findViewById(itemView, R.id.textView_section_post_id);
            postTitleTextView = findViewById(itemView, R.id.textView_section_post_title);
            postQuoteTextView = findViewById(itemView, R.id.textView_section_post_quote);
            postThumbImageView = findViewById(itemView, R.id.imageView_section_post_thumb);
            postWarnTextView = findViewById(itemView, R.id.textView_section_preview_warnText);
            moreBtn = findViewById(itemView, R.id.button_section_preview_more);
            replyLinearLayout = findViewById(itemView, R.id.linearLayout_section_preview_replyContainer);
        }
    }

}
