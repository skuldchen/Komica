package idv.kuma.komica.views;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import idv.kuma.komica.R;
import idv.kuma.komica.entity.KPost;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

/**
 * Created by TakumaLee on 2016/12/22.
 */

public class PostView extends LinearLayout {

    public TextView postIdTextView;
    public TextView postTitleTextView;
    public TextView postQuoteTextView;
    public ImageView postThumbImageView;

    private KPost post;

    public PostView(Context context) {
        this(context, null);
    }

    public PostView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_section_post, this, true);
        initView();
    }

    private void initView() {
        postIdTextView = (TextView) findViewById(R.id.textView_section_post_id);
        postTitleTextView = (TextView) findViewById(R.id.textView_section_post_title);
        postQuoteTextView = (TextView) findViewById(R.id.textView_section_post_quote);
        postThumbImageView = (ImageView) findViewById(R.id.imageView_section_post_thumb);
    }

    public void setPost(KPost post) {
        this.post = post;
    }

    public void notifyDataSetChanged() {
        postIdTextView.setText("No. " + post.getId());
        postTitleTextView.setText(post.getTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            postQuoteTextView.setText(Html.fromHtml(post.getQuote(), FROM_HTML_MODE_LEGACY));
        } else {
            postQuoteTextView.setText(Html.fromHtml(post.getQuote()));
        }
        Glide.with(getContext()).load(post.getImageUrl()).into(postThumbImageView);
    }
}
