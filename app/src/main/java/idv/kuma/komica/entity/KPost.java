package idv.kuma.komica.entity;

import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TakumaLee on 2016/12/11.
 */

public class KPost {

    /**
     * id="r2212869"
     * <label for="2212869">
     *      <span class="title">Helck 84(後)</span>Name
     *      <span class="name"></span> [16/12/20(火)02:40 ID:BVpAKriM]
     *  </label>
     * */
    protected String id;
    protected String idStr;
    protected String title;
    protected String name;

    /**
     * <div class="quote">作者不是蘿莉控嗎 結婚的都沒有幼女體型的</div>
     * <div class="quote">本文なし</div>
     *
     * <div class="quote">結束,啊戰爭部份??
     * <br />那不重要啦XDD
     * </div>
     *
     * */
    protected String quote;
    protected String imageUrl;
    protected String imageFileName;
    protected boolean hasImage = false;

    /**
     * Pixel
     * style="width: 153px; height: 240px;"
     * class="img" alt="20 KB" title="20 KB"
     * [file name] - (20 KB, 153x240)
     * */
    protected String thumbUrl;
    protected int thumbWidth;
    protected int thumbHeight;

    public KPost() {

    }

    public KPost(Element element, String postType, String domainUrl) {
        setId(element.getElementsByTag("input").attr("id"));
        if (element.hasClass(postType)) {
            setIdStr(element.getElementsByClass(postType).attr("id"));
        }
        Element label = element.getElementsByTag("label").first();
        setTitle(label.getElementsByClass("title").text());
        setName(label.getElementsByClass("name").text());// loss the time date
        Element quoteElement = element.getElementsByClass("quote").first();
        setQuote(quoteElement.html());
        Element thumbElement = element.getElementsByTag("img").first();
        setHasImage(thumbElement != null);
        if (hasImage) {
            Element imgElement = element.getElementsByAttributeValue("rel", "_blank").first();
            setImageUrl(imgElement.attr("href"));
            setImageFileName(imgElement.text());
            setThumbUrl(thumbElement.attr("src"));
            setThumbWidth(findWidthPixel(thumbElement.attr("style")));
            setThumbHeight(findHeightPixel(thumbElement.attr("style")));
        }
    }

    private int findWidthPixel(String source) {
        Pattern pattern = Pattern.compile("width: (.*?)px;");
        return matcherPattern(pattern, source);
    }

    private int findHeightPixel(String source) {
        Pattern pattern = Pattern.compile("height: (.*?)px;");
        return matcherPattern(pattern, source);
    }

    private int matcherPattern(Pattern pattern, String source) {
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public boolean hasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
}
