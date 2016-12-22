package com.summertaker.blog46.parser;

import com.summertaker.blog46.data.Article;
import com.summertaker.blog46.common.BaseParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Nogizaka46Parser extends BaseParser {

    public void parseBlogList(String response, ArrayList<Article> articles) {
        /*
        <div class="right2in" id="sheet">
            <div class="paginate">
                &nbsp;1&nbsp; |
                <a href="?p=2">&nbsp;2&nbsp;</a> |
                <a href="?p=3">&nbsp;3&nbsp;</a> |
                ...
                <a href="?p=15">&nbsp;15&nbsp;</a> |
                <a href="?p=2">&#65310;</a>
            </div>
            <h1 class="clearfix">
                <span class="date">
                    <span class="yearmonth">2016/03</span>
                    <span class="daydate">
                        <span class="dd1">29</span>
                        <span class="dd2">Tue</span>
                    </span>
                </span>
                <span class="heading">
                    <span class="author">若月佑美</span>
                    <span class="entrytitle">
                        <a href="http://blog.nogizaka46.com/yumi.wakatsuki/2016/03/031299.php" rel="bookmark">1週間も経ってるー(´,,•﹏• ,,｀)</a>
                    </span>
                </span>
            </h1>
            <div class="fkd"></div>
            <div class="entrybody">
                <div>髪のカラーの抜け方が早い。</div>
                <div>
                    <a href="http://dcimg.awalker.jp/img1.php?id=phu2qg0Vf9cBKA4BKCZQ81j2pg07yhJsTMg9A100AdAgpABNlZWV5e9V3A6FWvSiKCcZB7FvYxNFOLuocFWp8LYkc0XhC2u3t81pNarOGlyqtuPV6FCw1WKtIqwG3fT8BCsMfPZbULPaLQtvcFkbr2TRw01VSoafd3WxO6RpxMR70kahY7KwfuKzhg2jkR1Zx40M7Nx1">
                        <img src="http://img.nogizaka46.com/blog/yumi.wakatsuki/img/2016/03/29/8178961/0000.jpeg">
                    </a>
                </div>
            </div>
            <div class="entrybottom">
                2016/03/29 08:00｜
                <a href="http://blog.nogizaka46.com/yumi.wakatsuki/2016/03/031299.php">個別ページ</a>｜
                <a href="http://blog.nogizaka46.com/yumi.wakatsuki/2016/03/031299.php#comments">コメント(3)</a>
            </div>
            ...
        </div>
        */

        //response = Util.getJapaneseString(response, "SHIFT-JIS");
        //Log.e(tag, response);

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("sheet");

        Elements h1s = root.select("h1");
        if (h1s == null) {
            return;
        }

        Elements entrybodys = root.select(".entrybody");
        if (entrybodys == null) {
            return;
        }

        //Log.e(mTag, "h1s.size(): " + h1s.size());
        //Log.e(tag, "entrybodys.size(): " + entrybodys.size());

        for (int i = 0; i < h1s.size(); i++) {
            String id;
            String title;
            String name;
            String date;
            String content;
            String url;
            String thumbnailUrl = "";
            String imageUrl = "";

            Element el;

            Element h1 = h1s.get(i);

            el = h1.select(".yearmonth").first();
            if (el == null) {
                continue;
            }
            date = el.text().replace("/", "-");
            date += "-" + h1.select(".dd1").first().text();
            date += " " + h1.select(".dd2").first().text();

            name = h1.select(".author").first().text();

            el = h1.select(".entrytitle").first();
            el = el.select("a").first();
            title = el.text();
            url = el.attr("href");

            //if (i >= entrybodys.size()) {
            //    break;
            //}
            //Element entitybody = entrybodys.get(i);

            el = h1.nextElementSibling();
            Element body = el.nextElementSibling();
            content = body.text().trim();
            content = content.replace("&nbsp;", "");
            content = content.replace(" ", "").replace("　", "");
            content = content.replaceAll("\\p{Z}", "");

            ArrayList<String> imageUrls = new ArrayList<>();
            ArrayList<String> thumbnails = new ArrayList<>();
            //ArrayList<String> duplicates = new ArrayList<>();
            for (Element img : body.select("img")) {
                //Log.e(mTag, a.html());

                String src = img.attr("src");
                if (src.contains(".gif")) {
                    continue;
                }

                boolean exist = false;
                for (String str : thumbnails) {
                    if (src.equals(str)) {
                        exist = true;
                        //duplicates.add(src);
                        break;
                    }
                }
                if (exist) {
                    continue;
                }

                //Log.e(tag, src);
                thumbnails.add(src);
                //thumbnailUrl += src + "*";

                Element parent = img.parent();
                if (parent.tagName().equals("a")) {
                    imageUrls.add(parent.attr("href"));
                } else {
                    imageUrls.add(null);
                }

                // 이미지 보호장치 있음 - 그냥 웹 뷰로 이동시킬 것
                // http://dcimg.awalker.jp/img1.php?id=phu2qg0Vf9cBKA4BKCZQ81j2pg07yhJsTMg9A100AdAgpABNlZWV5e9V3A6FWvSiKCcZB7FvYxNFOLuocFWp8LYkc0XhC2u3t81pNarOGlyqtuPV6FCw1WKtIqwG3fT8BCsMfPZbULPaLQtvcFkbr2TRw01VSoafd3WxO6RpxMR70kahY7KwfuKzhg2jkR1Zx40M7Nx1
                // http://dcimg.awalker.jp/img2.php?sec_key=phu2qg0Vf9cBKA4BKCZQ81j2pg07yhJsTMg9A100AdAgpABNlZWV5e9V3A6FWvSiKCcZB7FvYxNFOLuocFWp8LYkc0XhC2u3t81pNarOGlyqtuPV6FCw1WKtIqwG3fT8BCsMfPZbULPaLQtvcFkbr2TRw01VSoafd3WxO6RpxMR70kahY7KwfuKzhg2jkR1Zx40M7Nx1
                //imageUrl = imageUrl.replace("/img1.php?id=", "/img2.php?sec_key=");

                //el = img.parent();
                //if (!el.tagName().equals("a")) { // 큰 사진에는 링크가 걸려있음
                //    continue;
                //}
                //imageUrls.add(el.attr("href"));
                //imageUrl = el.attr("href") + "*";

                //boolean exist = false;
                //for (WebData webData : webDataList) {
                //    if (id.equals(webData.getGroupId())) {
                //        exist = true;
                //        break;
                //    }
                //}
            }

            /*
            for (int j = 0; j < thumbnails.size(); j++) {
                String img = thumbnails.get(j);
                boolean valid = true;
                for (String dup : duplicates) {
                    if (img.equals(dup)) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    thumbnailUrl += img + "*";
                    imageUrl = imageUrls.get(j) + "*";
                }
            }

            if (!thumbnailUrl.isEmpty()) {
                thumbnailUrl = thumbnailUrl + "*";
                thumbnailUrl = thumbnailUrl.replace("**", "");

                imageUrl = imageUrl + "*";
                imageUrl = imageUrl.replace("**", "");
            }
            Log.e(tag, title + " / " + url + " / " + thumbnailUrl + " / " + imageUrl);
            */

            Article item = new Article();
            item.setTitle(title);
            item.setName(name);
            item.setDate(date);
            item.setContent(content);
            item.setUrl(url);
            item.setThumbnails(thumbnails);
            item.setImageUrls(imageUrls);

            articles.add(item);
        }
    }
}