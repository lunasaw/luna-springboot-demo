package com.xkcoding.elasticsearch.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import com.xkcoding.elasticsearch.config.ElasticsearchProperties;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StringUtils;

/**
 * BaseElasticsearchService
 *
 * @author fxbin
 * @version 1.0v
 * @since 2019/9/16 15:44
 */
public abstract class ElasticsearchUtil {

    public static final Logger             log = LoggerFactory.getLogger(ElasticsearchUtil.class);

    @Autowired
    public RestHighLevelClient restHighLevelClient;

    @Autowired
    public ElasticsearchProperties elasticsearchProperties;

    private static ElasticsearchProperties properties;

    private static RestHighLevelClient client;

    /**
     * @PostContruct是spring框架的注解
     * spring容器初始化的时候执行该方法
     */
    @PostConstruct
    public void init() {
        properties = this.elasticsearchProperties;
        client = this.restHighLevelClient;
    }

    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();

        // 默认缓冲限制为100MB，此处修改为30MB。
        builder.setHttpAsyncResponseConsumerFactory(
            new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    public static boolean isIndexExist(String index) {
        try {
            GetRequest getRequest = new GetRequest(index);
            return client.exists(getRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            throw new ElasticsearchException(ResultCodes.ERROR_SYSTEM_EXCEPTION, "判断索引 {" + index + "} 失败");
        }
    }

    /**
     * 异步创建索引
     *
     * @param index elasticsearch index
     * @author fxbin
     */
    public static void createIndexRequest(String index) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            // Settings for this index
            request.settings(
                Settings.builder().put("index.number_of_shards", properties.getIndex().getNumberOfShards())
                    .put("index.number_of_replicas", properties.getIndex().getNumberOfReplicas()));

            CreateIndexResponse createIndexResponse = client.indices().create(request, COMMON_OPTIONS);

            log.info(" whether all of the nodes have acknowledged the request : {}",
                createIndexResponse.isAcknowledged());
            log.info(
                " Indicates whether the requisite number of shard copies were started for each shard in the index before timing out :{}",
                createIndexResponse.isShardsAcknowledged());
        } catch (IOException e) {
            throw new ElasticsearchException(ResultCodes.ERROR_SYSTEM_EXCEPTION, "创建索引 {" + index + "} 失败");
        }
    }

    /**
     * 删除索引
     *
     * @param index elasticsearch index name
     * @author fxbin
     */
    public static void deleteIndexRequest(String index) {
        DeleteIndexRequest deleteIndexRequest = buildDeleteIndexRequest(index);
        try {
            client.indices().delete(deleteIndexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            throw new ElasticsearchException(ResultCodes.ERROR_SYSTEM_EXCEPTION, "删除索引 {" + index + "} 失败");
        }
    }

    /**
     * 数据添加，正定ID
     *
     * @param jsonObject 要增加的数据
     * @param index 索引，类似数据库
     * @param type 类型，类似表
     * @param id 数据ID
     * @return
     */
    public static String addData(JSONObject jsonObject, String index, String type, String id) {
        try {
            IndexResponse response = client.index(buildIndexRequest(index, type, id, jsonObject), COMMON_OPTIONS);
            log.info("addData response status:{},id:{}", response.status().getStatus(), response.getId());
            return response.getId();
        } catch (IOException e) {
            throw new ElasticsearchException(ResultCodes.ERROR_SYSTEM_EXCEPTION,
                "新增索引 {" + index + "} 数据 {" + jsonObject + "} 失败");
        }
    }

    /**
     * 数据添加
     *
     * @param jsonObject 要增加的数据
     * @param index 索引，类似数据库
     * @param type 类型，类似表
     * @return
     */
    public static String addData(JSONObject jsonObject, String index, String type) {
        return addData(jsonObject, index, type, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }

    /**
     * 构造删除索引请求
     *
     * @param index elasticsearch index name
     * @author fxbin
     */
    public static DeleteIndexRequest buildDeleteIndexRequest(String index) {
        return new DeleteIndexRequest(index);
    }

    /**
     * 构造索引请求
     *
     * @param index elasticsearch index name
     * @param id request object id
     * @param object request object
     * @return {@link IndexRequest}
     * @author fxbin
     */
    public static IndexRequest buildIndexRequest(String index, String type, String id, Object object) {
        return new IndexRequest(index).id(id).type(type).source(BeanUtil.beanToMap(object), XContentType.JSON);
    }

    /**
     * 构造数据请求
     *
     * @param index elasticsearch index name
     * @param id request object id
     * @param id
     * @return
     */
    public static GetRequest buildGetRequest(String index, String type, String id, String fields) {
        String[] split = new String[] {};
        if (!StringUtils.isEmpty(fields)) {
            split = fields.split(",");
        }
        return new GetRequest(index).id(id).type(type).storedFields(split);
    }

    /**
     * 更新请求
     *
     * @param index elasticsearch index name
     * @param id Document id
     * @param object request object
     * @author fxbin
     */
    public static void updateRequest(String index, String type, String id, Object object) {
        try {
            UpdateRequest updateRequest =
                new UpdateRequest(index, type, id).doc(BeanUtil.beanToMap(object), XContentType.JSON);
            client.update(updateRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            throw new ElasticsearchException(ResultCodes.ERROR_SYSTEM_EXCEPTION,
                "更新索引 {" + index + "} 数据 {" + object + "} 失败");
        }
    }

    /**
     * 删除请求
     *
     * @param index elasticsearch index name
     * @param id Document id
     * @author fxbin
     */
    public static void deleteRequest(String index, String type, String id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
            DeleteResponse response = client.delete(deleteRequest, COMMON_OPTIONS);
            log.info("deleteDataById response status:{},id:{}", response.status().getStatus(), response.getId());
        } catch (IOException e) {
            throw new ElasticsearchException(ResultCodes.ERROR_SYSTEM_EXCEPTION,
                "删除索引 {" + index + "} 数据id {" + id + "} 失败");
        }
    }

    /**
     * 通过ID获取数据
     *
     * @param index 索引，类似数据库
     * @param type 类型，类似表
     * @param id 数据ID
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    public static Map<String, Object> searchDataById(String index, String type, String id, String fields) {
        try {
            GetResponse response = client.get(buildGetRequest(index, type, id, fields), COMMON_OPTIONS);
            return response.getSource();
        } catch (IOException e) {
            throw new ElasticsearchException(ResultCodes.ERROR_SYSTEM_EXCEPTION,
                "通过ID获取数据 {" + index + "} 数据id {" + id + "} 失败 {" + fields + "}");
        }
    }

//    public static SearchSourceBuilder buildSearchSourceBuilder(
//        int startPage, int pageSize, String fields,
//        String include,
//        String export, String miniMactch,
//        String sortField, Integer size, QueryBuilder query,
//        String highlightField) {
//        // 搜索源构建对象
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        // 排序
//        if (StringUtils.isNotEmpty(sortField)) {
//            searchSourceBuilder.sort(sortField, SortOrder.DESC);
//        }
//        if (size != null && size > 0) {
//            searchSourceBuilder.size(size);
//        }
//        searchSourceBuilder.query(query);
//
//        // 分页应用
//        searchSourceBuilder.from(startPage).size(pageSize);
//
//        // 搜索方式
//        // multiMatchQuery(Object text, String... fieldNames)
//
//        if (StringUtils.isNotEmpty(fields) || StringUtils.isNotEmpty(miniMactch)) {
//            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(fields.split(","))
//                .minimumShouldMatch(miniMactch));
//        }
//
//        // 设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
//        if (StringUtils.isNotEmpty(include) || StringUtils.isNotEmpty(export)) {
//            searchSourceBuilder.fetchSource(include.split(","), export.split(","));
//        }
//
//        if (StringUtils.isNotEmpty(highlightField)) {
//            HighlightBuilder highlightBuilder = new HighlightBuilder();
//            // 设置高亮字段
//            highlightBuilder.field(highlightField);
//            searchSourceBuilder.highlighter(highlightBuilder);
//        }
//        return searchSourceBuilder;
//
//    }
//
//    /**
//     * 使用分词查询,并分页
//     *
//     * @param index 索引名称
//     * @param type 类型名称,可传入多个type逗号分隔
//     * @param startPage 当前页
//     * @param pageSize 每页显示条数
//     * @param query 查询条件
//     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
//     * @param sortField 排序字段
//     * @param highlightField 高亮字段
//     * @return
//     */
//    public static EsPageDTO searchDataPage(String index, String type, int startPage, int pageSize, String fields,
//        String include,
//        String export,
//        String miniMactch,
//        String sortField, Integer size, QueryBuilder query,
//        String highlightField) {
//        SearchRequest searchRequest = new SearchRequest(index).types(type);
//        searchRequest.source(
//            buildSearchSourceBuilder(startPage, pageSize, fields, include, export, miniMactch, sortField, size,
//                query, highlightField));
//        try {
//            // 执行搜索,向ES发起http请求
//            SearchResponse searchResponse = client.search(searchRequest, COMMON_OPTIONS);
//            // 搜索结果
//            SearchHits hits = searchResponse.getHits();
//            // 匹配到的总记录数
//            long totalHits = hits.getTotalHits();
//            // 得到匹配度高的文档
//            SearchHit[] searchHits = hits.getHits();
//            List<Map<String, Object>> sourceList = setSearchResponse(searchResponse, highlightField);
//            return new EsPageDTO(startPage, pageSize, Math.toIntExact(totalHits), sourceList);
//        } catch (IOException e) {
//            throw new ElasticsearchException(ResultCodes.ERROR_SYSTEM_EXCEPTION,
//                "使用分词查询,并分页失败" + e.getMessage());
//        }
//    }
//
//    /**
//     * 使用分词查询
//     *
//     * @param index 索引名称
//     * @param type 类型名称,可传入多个type逗号分隔
//     * @param query 查询条件
//     * @param size 文档大小限制
//     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
//     * @param sortField 排序字段
//     * @param highlightField 高亮字段
//     * @return
//     */
//    public static List<Map<String, Object>> searchListData(String index, String type, QueryBuilder query, Integer size,
//        String fields, String sortField, String highlightField) {
//        SearchRequest searchRequest = new SearchRequest(index).types(type);
//        searchRequest.source(
//            buildSearchSourceBuilder(0, 0, fields, null, null, null, sortField, size,
//                query, highlightField));
//        try {
//            // 执行搜索,向ES发起http请求
//            SearchResponse searchResponse = client.search(searchRequest, COMMON_OPTIONS);
//            // 搜索结果
//            SearchHits hits = searchResponse.getHits();
//            // 得到匹配度高的文档
//            return setSearchResponse(searchResponse, highlightField);
//        } catch (IOException e) {
//            throw new ElasticsearchException(ResultCodes.ERROR_SYSTEM_EXCEPTION,
//                "使用分词查询" + e.getMessage());
//        }
//    }
//
//    /**
//     * 高亮结果集 特殊处理
//     *
//     * @param searchResponse
//     * @param highlightField
//     */
//    private static List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
//        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
//        StringBuffer stringBuffer = new StringBuffer();
//        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
//            searchHit.getSourceAsMap().put("id", searchHit.getId());
//            if (StringUtils.isNotEmpty(highlightField)) {
//                System.out.println("遍历 高亮结果集，覆盖 正常结果集" + searchHit.getSourceAsMap());
//                Text[] text = searchHit.getHighlightFields().get(highlightField).getFragments();
//                if (text != null) {
//                    for (Text str : text) {
//                        stringBuffer.append(str.string());
//                    }
//                    // 遍历 高亮结果集，覆盖 正常结果集
//                    searchHit.getSourceAsMap().put(highlightField, stringBuffer.toString());
//                }
//            }
//            sourceList.add(searchHit.getSourceAsMap());
//        }
//        return sourceList;
//    }

    /**
     * 查找所有
     *
     * @param index elasticsearch index name
     * @return {@link SearchResponse}
     * @author fxbin
     */
    public static SearchResponse search(String index, String type) {
        SearchRequest searchRequest = new SearchRequest(index).types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        try {
            return client.search(searchRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            throw new ElasticsearchException(ResultCodes.ERROR_SYSTEM_EXCEPTION,
                "查找所有 {" + index + "} {" + type + "} 失败");
        }
    }
}
