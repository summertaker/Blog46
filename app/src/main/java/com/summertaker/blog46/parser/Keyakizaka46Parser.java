package com.summertaker.blog46.parser;

import com.summertaker.blog46.data.Article;
import com.summertaker.blog46.common.BaseParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Keyakizaka46Parser extends BaseParser {

    public void parseBlogList(String response, ArrayList<Article> articles) {
        /*
        <article>
            <div class="innerHead">
                <div class="box-date">
                    <time>2016.4</time><time>01</time>
                </div>
                <div class="box-ttl">
                    <h3>
                        <a href="/mob/news/diarKijiShw.php?site=k46o&ima=4841&id=2349&cd=member">
                            日常のなかにプロレスあり～114～まさにこの通りです。
                        </a>
                    </h3>
                    <p class="name">
                        尾関 梨香
                    </p>
                </div>
                <div class="box-sns"></div>
            </div>
            <div class="box-article">
                <div><br><br>こんばんは〜</div><div><br></div><div><br></div><div><div>4月1日、新年度になりました!</div>
                <img src="/files/14/diary/k46/member/moblog/201604/mob8SaU44.jpg" alt="image1.JPG" id="2E79D24E-D688-4812-A8F1-09B6F552A594">
            </div>
            <div class="box-bottom">
                <ul>
                    <li>2016/04/01 23:40</li>
                    <li class="singlePage">
                        <a href="/mob/news/diarKijiShw.php?site=k46o&ima=4841&id=2349&cd=member">個別ページ</a>
                    </li>
                </ul>
            </div>
        </article>
        <article>
            ....
        </article>
        */

        //Log.e(TAG, response);

        Document doc = Jsoup.parse(response);

        for (Element row : doc.select("article")) {

            String title;
            String name;
            String date;
            String content;
            String url;

            Element el;

            //Elements times = row.select("time");
            //if (times == null) {
            //    continue;
            //}
            //date = times.get(0).text();
            //date += "." + times.get(1).text();

            Element ttl = row.select(".box-ttl").first();
            if (ttl == null) {
                continue;
            }

            el = ttl.select("h3").first();
            if (el == null) {
                continue;
            }
            title = el.text();

            el = el.select("a").first();
            url = el.attr("href");
            url = "http://www.keyakizaka46.com" + url;

            el = ttl.select("p").first();
            name = el.text();

            el = row.select(".box-article").first();
            content = el.text().trim();

            //Log.e(TAG, "title: " + title);

            ArrayList<String> imageUrls = new ArrayList<>();
            ArrayList<String> thumbnails = new ArrayList<>();

            for (Element img : el.select("img")) {
                String src = img.attr("src");
                src = "http://www.keyakizaka46.com" + src;

                thumbnails.add(src);
                imageUrls.add(src);
            }

            el = row.select(".box-bottom").first();
            date = el.select("ul").first().select("li").first().text();

            //Log.e(TAG, title + " " + thumbnailUrl);

            Article article = new Article();
            article.setTitle(title);
            article.setName(name);
            article.setDate(date);
            article.setContent(content);
            article.setUrl(url);

            article.setThumbnails(thumbnails);
            article.setImageUrls(imageUrls);

            articles.add(article);
        }
    }
}
