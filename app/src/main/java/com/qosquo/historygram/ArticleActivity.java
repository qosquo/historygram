package com.qosquo.historygram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import com.qosquo.historygram.models.Article;
import com.qosquo.historygram.viewmodels.ArticleViewModel;

public class ArticleActivity extends AppCompatActivity {
    private static final String TAG = "ArticleActivity";
    public static final String EXTRA_ARTICLEID = "EXTRA_ARTICLEID";

    private ArticleViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_24);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        int articleId = getIntent().getIntExtra(EXTRA_ARTICLEID, 0);

        mViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);
        mViewModel.init(articleId);

        mViewModel.getArticle().observe(this, this::setArticle);
    }

    private void setArticle(final Article article) {
        TextView nameTextView = findViewById(R.id.textView_name);
        nameTextView.setText(article.getName());

        TextView bodyTextView = findViewById(R.id.textView_body);
        Spanned body = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            body = Html.fromHtml(article.getBody(), Html.FROM_HTML_MODE_LEGACY);
        } else {
            body = Html.fromHtml(article.getBody());
        }
        bodyTextView.setText(body);

        TextView viewsTextView = findViewById(R.id.textView_views);
        String views = getResources().getString(R.string.article_views) + ": " + article.getViews();
        viewsTextView.setText(views);
    }
}
