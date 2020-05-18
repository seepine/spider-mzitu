package com.seepine.mzitu.processor;

import com.seepine.mzitu.constant.CommonConstant;
import com.seepine.mzitu.util.DownloadUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Seepine
 * @date 2020-05-18 17:29
 */
public class CrawlPageProcessor  implements PageProcessor {
    private final Site site = Site.me()
            .addHeader("Upgrade-Insecure-Requests", "1")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
            .setRetryTimes(3).setSleepTime(CommonConstant.CRAWL_MILLIS);

    @Override
    public void process(Page page) {
        //可能需要爬取
        HashSet<String> set = new HashSet<>();
        set.addAll(page.getHtml().links().regex("https://www\\.mzitu\\.com/\\d+").all());
        set.addAll(page.getHtml().links().regex("https://www\\.mzitu\\.com/page/\\d+/").all());
        //已爬取的
        List<String> alreadySet = DownloadUtil.getInstance().get();
        //还未爬取的
        set.removeAll(alreadySet);
        page.addTargetRequests(new ArrayList<>(set));
        //这是图集的url时
        if (page.getUrl().regex("https://www\\.mzitu\\.com/\\d+").match()) {
            //获取图集有多少图片
            List<String> list = page.getHtml().xpath("//div[@class='pagenavi']/a/span/text()").all();
            page.putField("title", page.getHtml().xpath("//h2[@class='main-title']/text()").toString());
            page.putField("imageUrl", page.getHtml().xpath("//div[@class='main-image']//a/img/@src").toString());
            page.putField("time", page.getHtml().xpath("//div[@class='main-meta']/span[2]/text()").toString().substring(4));
            page.putField("category", page.getHtml().xpath("//div[@class='main-meta']//a/text()").toString());
            page.putField("total", list.get(list.size() - 2));
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

}