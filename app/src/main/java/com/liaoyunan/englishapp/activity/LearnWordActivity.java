package com.liaoyunan.englishapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.liaoyunan.englishapp.R;
import com.liaoyunan.englishapp.db.WordDB;
import com.liaoyunan.englishapp.model.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quhaofeng on 16-5-1.
 */
public class LearnWordActivity extends AppCompatActivity {
    private ListView wordListView;
    private List<Word.RECORDSBean> wordList = new ArrayList<Word.RECORDSBean>();
    /**
     * list中的数据
     */
    private List<String> dataList = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter;
    private WordDB wordDB;
    private WebView mWebView;
    private int mIndex = 0;
    private TextView wordView;
    private TextView meaningView;
    private TextView yinbiaoView;
    private Button showHideBtn;
    private String readWord = "word";
    private Word.RECORDSBean defaultWord = new Word.RECORDSBean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_book);

        defaultWord.setWord("word");
        defaultWord.setYinbiao("「音标」");
        defaultWord.setMeaning("「单词释义」");

        wordView = (TextView) findViewById(R.id.word);
        meaningView = (TextView) findViewById(R.id.meaning);
        yinbiaoView = (TextView) findViewById(R.id.yinbiao);
        wordListView = (ListView) findViewById(R.id.word_list1);
        mAdapter = new ArrayAdapter<String>(this, R.layout.listview_item, R.id.list_item, dataList);
        wordListView.setAdapter(mAdapter);
        wordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIndex = position;
                setWord(wordList.get(mIndex));
                wordListView.setVisibility(View.GONE);
            }
        });

        showHideBtn = (Button) findViewById(R.id.show_hide_btn);
        showHideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordListView.getVisibility() == View.GONE) {
                    wordListView.setVisibility(View.VISIBLE);
                } else {
                    wordListView.setVisibility(View.GONE);
                }
            }
        });

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        /*mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
            }
        });*/
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.w("TAG", "" + newProgress);
                if (newProgress == 100) {
                    mWebView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
                }
            }
        });

        init();

    }

    /**
     * 初始化函数
     */
    public void init() {
        wordDB = WordDB.getInstance(this);
        wordList = wordDB.loadWordLib();//从单词库表获取单词

        if (wordDB.loadIndex() < 0) {
            wordDB.saveIndex( -1 );
        } else {
            mIndex = wordDB.loadIndex();//获取Index
        }

        dataList.clear();
        for (Word.RECORDSBean recordsBean : wordList) {
            dataList.add(recordsBean.getWord());
        }
        mAdapter.notifyDataSetChanged();
        if (wordList.isEmpty()){
            setWord(defaultWord);
        }else {
            setWord(wordList.get(mIndex));
        }

    }

    /**
     * 显示单词
     */
    public void setWord(Word.RECORDSBean word) {
        try {
            String s = word.getWord();
            wordView.setText(s);
            readWord = s;
            s = word.getYinbiao();
            yinbiaoView.setText(s);
            s = word.getMeaning();
            meaningView.setText(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下一个单词
     */
    public void nextWord(View view) {
        if (mIndex < wordList.size() - 1) {
            mIndex++;
            setWord(wordList.get(mIndex));
            wordListView.setVisibility(View.GONE);
        }

    }

    /**
     * 上一个单词
     */
    public void previousWord(View view) {
        if (mIndex > 0) {
            mIndex--;
            setWord(wordList.get(mIndex));
            wordListView.setVisibility(View.GONE);
        }
    }

    /**
     * 添加到单词本
     */
    public void addToCollect(View view) {
        if (!wordList.isEmpty()){
            wordDB.addToCollect(wordList.get(mIndex));
        }
    }

    /**
     * 播放声音
     */
    public void play(View view) {
        mWebView.loadUrl("http://dict.youdao.com/dictvoice?type=2&audio=" + readWord);
    }


    /**
     * back时回调，存储浏览索引到数据库
     * */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (wordList.size() == 0){
            wordDB.saveIndex(-1);
        }else {
            wordDB.saveIndex(mIndex);
        }
    }

}
