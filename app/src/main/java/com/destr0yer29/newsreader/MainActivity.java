package com.destr0yer29.newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<NewsItem> news;

    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        news = new ArrayList<>();

        newsAdapter = new NewsAdapter(this);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new GetNews().execute();

    }

    private class GetNews extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            InputStream inputStream = getInputStream();
            if (null != inputStream){
                try {
                    initXMLPullParser(inputStream);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            newsAdapter.setNewsItem(news);
        }



        private InputStream getInputStream(){
            try {
                URL url = new URL("https://www.espncricinfo.com/rss/content/story/feeds/6.xml");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    private void initXMLPullParser(InputStream inputStream) throws XmlPullParserException, IOException {
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
        pullParser.setInput(inputStream,null);

        //iterating thought rss feed
        pullParser.next();
        pullParser.require(XmlPullParser.START_TAG,null,"rss");
        while (pullParser.next() != XmlPullParser.END_TAG){
            if (pullParser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            pullParser.require(XmlPullParser.START_TAG,null,"channel");
            while (pullParser.next() != XmlPullParser.END_TAG){
                if (pullParser.getEventType() != XmlPullParser.START_TAG){
                    continue;
                }


                if (pullParser.getName().equals("item")){
                    pullParser.require(XmlPullParser.START_TAG,null,"item");

                    String title = "";
                    String description="";
                    String date="";
                    String link="";

                    while (pullParser.next() != XmlPullParser.END_TAG){
                        if (pullParser.getEventType() != XmlPullParser.START_TAG){
                            continue;
                        }

                        String tagName = pullParser.getName();
                        if (tagName.equals("title")){
                            title = getContent(pullParser,"title");
                        }else if (tagName.equals("description")){
                            description = getContent(pullParser,"description");
                        }else if (tagName.equals("link")){
                            link = getContent(pullParser,"link");
                        }else if (tagName.equals("pubDate")){
                            date = getContent(pullParser,"pubDate");
                        }else{
                            //skip the tag
                            skipTag(pullParser);
                        }

                    }
                    NewsItem item = new NewsItem(title,description,date,link);
                    news.add(item);
                }else{
                    //skip the tag
                    skipTag(pullParser);
                }

            }
        }
    }
    private String getContent(XmlPullParser pullParser,String tagName) throws IOException, XmlPullParserException {
        String content = "";

        pullParser.require(XmlPullParser.START_TAG,null,tagName);
        if (pullParser.next() == XmlPullParser.TEXT){
            content = pullParser.getText();
            pullParser.next();
        }
        return content;
    }

    private void skipTag(XmlPullParser pullParser) throws XmlPullParserException, IOException {
        if (pullParser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        int number = 1;
        while (number != 0) {
            switch (pullParser.next()) {

                case XmlPullParser.START_TAG:
                    number++;
                    break;
                case XmlPullParser.END_TAG:
                    number--;
                    break;
                    default:
                        break;
            }
        }
    }
}
