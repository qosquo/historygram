package com.qosquo.historygram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.historygram.api.Api;
import com.historygram.api.ApiCallback;
import com.historygram.api.requests.ApiRequest;
import com.historygram.api.requests.ArticleRequest;
import com.qosquo.historygram.fragments.CaptionFragment;
import com.qosquo.historygram.models.Article;
import com.qosquo.historygram.repositories.MediaRepository;

import java.util.HashMap;

public class CreateArticleActivity extends AppCompatActivity {
    public static final String EXTRA_MEDIAID = "EXTRA_MEDIAID";
    public static final String EXTRA_ARTICLEID = "EXTRA_ARTICLEID";

    private Toolbar mToolbar;

    private EditText mNameEditText;
    private EditText mEditTextArticle;

    private boolean mEditor;
    private int mediaId;
    private int articleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_cancel_24);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        mNameEditText = findViewById(R.id.editText_name);
        mEditTextArticle = findViewById(R.id.editText_article);

        mediaId = getIntent().getIntExtra(EXTRA_MEDIAID, 0);
        articleId = getIntent().getIntExtra(EXTRA_ARTICLEID, 0);
        if (articleId > 0) {
            editArticle();
            mEditor = true;
        }

        ImageButton mBoldButton = findViewById(R.id.button_bold);
        mBoldButton.setOnClickListener(v -> setSpannable(new StyleSpan(Typeface.BOLD)));
        ImageButton mItalicButton = findViewById(R.id.button_italic);
        mItalicButton.setOnClickListener(v -> setSpannable(new StyleSpan(Typeface.ITALIC)));
        ImageButton mUnderlinedButton = findViewById(R.id.button_underline);
        mUnderlinedButton.setOnClickListener(v -> setSpannable(new UnderlineSpan()));

        ImageButton mAlignLeftButton = findViewById(R.id.button_align_left);
        mAlignLeftButton.setOnClickListener(v ->
                setSpannable(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL)));
        ImageButton mAlignCenterButton = findViewById(R.id.button_align_center);
        mAlignCenterButton.setOnClickListener(v ->
                setSpannable(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)));
        ImageButton mAlignRightButton = findViewById(R.id.button_align_right);
        mAlignRightButton.setOnClickListener(v ->
                setSpannable(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text_editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            if (mEditor) {
                saveArticle();
            } else {
                createArticle();
            }
            return true;
        }

        return false;
    }

    private void createArticle() {
        final String name = mNameEditText.getText().toString();
        final String body;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            body = Html.toHtml(mEditTextArticle.getText(), 0);
        } else {
            body = Html.toHtml(mEditTextArticle.getText());
        }
        Log.d("Article", body);

        Article article = new Article(name, body);
        System.out.println(article.toString());

        MenuItem menuItem = mToolbar.getMenu().findItem(R.id.action_done);
        menuItem.setEnabled(false);

        ApiRequest<Integer> postArticle = new ApiRequest<>("/articles/new", Integer.class);
        postArticle.setMapping(ApiRequest.Mapping.POST);
        final HashMap<String, Object> articleObj = new HashMap<>();
        articleObj.put("name", article.getName());
        articleObj.put("body", article.getBody());
        articleObj.put("owner", Api.getConfig().getUserId());
        postArticle.addBody(new Gson().toJson(articleObj));

        Api.execute(postArticle, new ApiCallback<Integer>() {
            @Override
            public void success(Integer result) {
                if (mediaId > 0) {
                    MediaRepository.getInstance().addArticle(mediaId, result);
                }

                Intent data = new Intent();
                data.putExtra(CaptionFragment.EXTRA_ARTICLEID, result);
                Log.d("Article", "Article ID: " + result);

                setResult(Activity.RESULT_OK, data);
                finish();
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
                Toast.makeText(CreateArticleActivity.this, "Failed to create new Article\n" + error.getMessage(), Toast.LENGTH_SHORT).show();

                menuItem.setEnabled(true);
            }
        });
    }

    private void saveArticle() {
        final String name = mNameEditText.getText().toString();
        final String body;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            body = Html.toHtml(mEditTextArticle.getText(), 0);
        } else {
            body = Html.toHtml(mEditTextArticle.getText());
        }
        Log.d("Article", body);

        Article article = new Article(name, body);
        System.out.println(article.toString());

        MenuItem menuItem = mToolbar.getMenu().getItem(0);
        menuItem.setEnabled(false);

        ApiRequest<Integer> postArticle = new ApiRequest<>("/articles/" + articleId, Integer.class);
        postArticle.setMapping(ApiRequest.Mapping.POST);
        final HashMap<String, Object> articleObj = new HashMap<>();
        articleObj.put("name", article.getName());
        articleObj.put("body", article.getBody());
        postArticle.addBody(new Gson().toJson(articleObj));

        Api.execute(postArticle, new ApiCallback<Integer>() {
            @Override
            public void success(Integer result) {
                finish();
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
                Toast.makeText(CreateArticleActivity.this, "Failed to save an Article\n" + error.getMessage(), Toast.LENGTH_SHORT).show();

                menuItem.setEnabled(true);
            }
        });
    }

    private void editArticle() {
        Api.execute(new ArticleRequest(articleId), new ApiCallback<Article>() {
            @Override
            public void success(Article result) {
                mNameEditText.setText(result.getName());

                Spanned body = Html.fromHtml(result.getBody());
                mEditTextArticle.setText(body);
            }

            @Override
            public void fail(Exception error) {
                error.printStackTrace();
                Toast.makeText(CreateArticleActivity.this, "Error in loading Article:\n" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSpannable(Object span) {
        Editable spannable = mEditTextArticle.getText();

        int start = mEditTextArticle.getSelectionStart();
        int end = mEditTextArticle.getSelectionEnd();

        Object[] spans = spannable.getSpans(start, end, span.getClass());

        System.out.println(spans.length);

        if (spans.length > 0) {
            for (Object what : spans) {
                spannable.removeSpan(what);
            }
        } else {
            spannable.setSpan(
                    span,
                    start, end,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
    }
}
