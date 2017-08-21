package com.yit.deal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.List;

import com.yit.common.utils.SqlHelper;

import com.yit.product.api.MediaService;

import com.yit.product.entity.Media;
import com.yit.product.entity.Media.Type;
import com.yit.quartz.api.JobService;
import com.yit.test.BaseTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/14.
 *
 * @author sober
 * @date 2017/08/14
 */
public class replaceChineseImageRunner extends BaseTest {

    @Autowired
    SqlHelper sqlHelper;

    @Autowired
    MediaService mediaService;

    @Autowired
    JobService jobService;

    List<String> generatorSql = new ArrayList<>();

    List<UrlInfo> imageURL = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(replaceChineseImageRunner.class);

    public static void main(String[] args) {
        runTest(replaceChineseImageRunner.class);
    }

    @Override
    public void run() throws Exception {
        //prepareData();
        //uploadAndGenerator();
        reloadSpu();
    }

    public void prepareData() {
        String sql = "select "
            + " m.url as url,"
            + " m.id as id"
            + " from yitiao_media m"
            + " left join yitiao_product_spu_images img on img.image_id = m.id"
            + " left join yitiao_product_sku sku on m.id = sku.thumbnail_id"
            + " where"
            + " length(url) != char_length(url);";

        sqlHelper.exec(sql, (row) -> {
            String url = row.getString("url");
            String id = row.getString("id");
            UrlInfo info = new UrlInfo();
            info.id = id;
            info.url = url;
            imageURL.add(info);
        });

        logger.info("prepare data finish!");
    }

    public boolean isHaveChinese(String str) {
        int byteLength = str.getBytes().length;
        int length = str.length();
        if (byteLength == length) {
            return false;
        } else {
            return true;
        }
    }

    public void uploadAndGenerator() throws Exception {
        for (UrlInfo info : imageURL) {
                String url = urlEncode(info.url);
                Media media = mediaService.upload(url, Type.IMAGE, 0, "", "系统", 0);
                String sql = String.format("update yitiao_media set url = '%s' where id = %s;\n", media.url, info.id);
                generatorSql.add(sql);
        }

        OutputStream os = new FileOutputStream("/Users/sober/Desktop/update_image.sql");
        for (String s : generatorSql) {
            os.write(s.getBytes());
        }
        os.close();

        logger.info("generator success ! ");
    }

    public void reloadSpu() {
        int[] spuIds = new int[] {
            65,
            94,
            96,
            99,
            1873,
            2,
            66,
            82,
            1698,
            1699,
            1704,
            1733,
            1756,
            1757,
            1860,
            1862,
            1863,
            1864,
            1866,
            1867,
            1868,
            1869,
            1870,
            1872,
            1873,
            1874,
            1875,
            1876,
            1877,
            1878,
            1879,
            1880,
            1881,
            1882,
            1883,
            1884,
            7818,
            7816,
            7815,
            7814,
            7813};

        jobService.addSpuReloadJob(spuIds);
        logger.info("SPU reload finish --------------------------> please check quartz log status!");
    }

    //url encode
    public String urlEncode(String urlStr) throws MalformedURLException, URISyntaxException {
        URL url = new URL(urlStr);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
            url.getQuery(), url.getRef());
        return uri.toASCIIString();
    }

    class UrlInfo {
        String id;
        String url;
    }
}
