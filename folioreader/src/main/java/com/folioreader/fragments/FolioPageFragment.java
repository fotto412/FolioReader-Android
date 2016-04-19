package com.folioreader.fragments;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.folioreader.Config;
import com.folioreader.R;
import com.folioreader.view.ObservableWebView;

/**
 * Created by mahavir on 4/2/16.
 */
public class FolioPageFragment extends Fragment {
    public static final String KEY_FRAGMENT_FOLIO_POSITION = "com.folioreader.fragments.FolioPageFragment.POSITION";

    private View mRootView;
    private SeekBar mScrollSeekbar;

    public static FolioPageFragment newInstance(int position) {
        FolioPageFragment fragment = new FolioPageFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_FRAGMENT_FOLIO_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public static interface FolioPageFragmentCallback {
        public String getChapterHtmlContent(int position);
    }

    private int mPosition = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_FRAGMENT_FOLIO_POSITION)) {
            mPosition = savedInstanceState.getInt(KEY_FRAGMENT_FOLIO_POSITION);
        } else {
            mPosition = getArguments().getInt(KEY_FRAGMENT_FOLIO_POSITION);
        }

        String htmlContent = getHtmlContent();

        mRootView = View.inflate(getActivity(), R.layout.folio_page_fragment, null);
        ObservableWebView webView = (ObservableWebView) mRootView.findViewById(R.id.contentWebView);
        mScrollSeekbar = (SeekBar)mRootView.findViewById(R.id.scrollSeekbar);
        mScrollSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.app_green), PorterDuff.Mode.SRC_IN);

        webView.setVerticalScrollBarEnabled(false);
        webView.setScrollListener(new ObservableWebView.ScrollListener() {
            @Override
            public void onScrollChange(float percent) {
                mScrollSeekbar.setProgress((int)percent);
            }
        });
        webView.loadDataWithBaseURL(null, htmlContent, "text/html; charset=UTF-8", "UTF-8", null);

        return mRootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_FRAGMENT_FOLIO_POSITION, mPosition);
    }

    public void reload(){
        WebView webView = (WebView) mRootView.findViewById(R.id.contentWebView);
        String htmlContent = getHtmlContent();
        Log.d("html/content",htmlContent);
        webView.loadDataWithBaseURL(null, htmlContent, "text/html; charset=UTF-8", "UTF-8", null);
    }

    private String getHtmlContent(){
        String htmlContent = "???";
        if (getActivity() instanceof FolioPageFragmentCallback && mPosition != -1){
            htmlContent = ((FolioPageFragmentCallback)getActivity()).getChapterHtmlContent(mPosition);
        }

        String cssPath = String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\">", "file:///android_asset/Style.css");
        String jsPath = String.format("<script type=\"text/javascript\" src=\"%s\"></script>", "file:///android_asset/Bridge.js");
        String toInject = "\n"+cssPath+"\n"+jsPath+"\n</head>";
        htmlContent = htmlContent.replace("</head>", toInject);

        String classes = "";
        Config config = Config.getConfig();
        switch (config.getFont()) {
            case 0:
                classes="andada";
                break;
            case 1:
                classes="lato";
                break;
            case 2:
                classes="lora";
                break;
            case 3:
                classes="raleway";
                break;
            default:
                break;
        }

        if (config.isNightMode()){
            classes += " nightMode";
        }

        switch (config.getFontSize()){
            case 0:
                classes += " textSizeOne";
                break;
            case 1:
                classes += " textSizeTwo";
                break;
            case 2:
                classes += " textSizeThree";
                break;
            case 3:
                classes += " textSizeFour";
                break;
            case 4:
                classes += " textSizeFive";
                break;
            default:
                break;
        }

        htmlContent = htmlContent.replace("<html ", "<html class=\""+classes+"\" ");
        return htmlContent;
    }
}
