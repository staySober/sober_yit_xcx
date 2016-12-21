package com.yit.test;

// region Import

import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.yit.common.entity.Action2;
import com.yit.common.entity.ActionWithException;
import com.yit.common.entity.Func3;
import com.yit.common.entity.PageParameter;
import com.yit.common.utils.*;
import com.yit.common.utils.cache.Cache;
import com.yit.common.utils.cache.CacheService;
import com.yit.common.utils.disconf.DisconfClient;
import com.yit.common.utils.file.FileService;
import com.yit.common.utils.keyvalue.KeyValueService;
import com.yit.common.utils.queue.QueueService;
import com.yit.common.utils.topic.TopicService;
import com.yit.demo.api.DemoTestService;
import com.yit.export.api.ExportService;
import com.yit.export.entity.ExportTable;
import com.yit.export.entity.ExportType;
import com.yit.fcategory.api.FCategoryService;
import com.yit.fcategory.entity.FCategoryInfo;
import com.yit.mail.api.EmailService;
import com.yit.mail.entity.MailInfo;
import com.yit.order.api.OrderService;
import com.yit.order.entity.OrderBriefInfo;
import com.yit.product.api.*;
import com.yit.product.entity.*;
import com.yit.promotionevent.api.PromotionEventService;
import com.yit.promotionevent.entity.PromotionEvent;
import com.yit.quartz.api.JobService;
import com.yit.quartz.api.QuartzService;
import com.yit.quartz.entity.Job;
import com.yit.quartz.entity.PromotionEventExecutionJobData;
import com.yit.spreadsheet.api.SpreadsheetService;
import com.yit.spreadsheet.entity.Spreadsheet;
import com.yit.spreadsheet.entity.SpreadsheetRowPageResult;
import com.yit.util.StringUtil;
import com.yit.vendor.api.VendorService;
import com.yit.vendor.entity.Vendor;
import com.yit.vendor.entity.VendorRunLog;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.xml.ws.Provider;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// endregion Import

public class DemoTest extends BaseTest {

    // region 依赖

    @Autowired
    DemoTestService demoTestService;

    // endregion 依赖

    public static void main(String[] args) throws Exception {
        runTest(DemoTest.class);
    }

    @Override
    public void run() throws Exception {
        Object result = null;

        // 测试代码
        result = demoTestService.getList(null);
        print(result);
    }
}
