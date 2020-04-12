package com.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gmall.bean.PmsSearchParam;
import com.gmall.bean.PmsSearchSkuInfo;
import com.gmall.bean.PmsSkuAttrValue;
import com.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private JestClient jestClient;
    @Override
    public List<PmsSearchSkuInfo> selPmsSearchInfo(PmsSearchParam pmsSearchParam) {
        //创建返回值对象
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        try {
            //设置查询对象
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //设置过滤条件
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            String catalog3Id = pmsSearchParam.getCatalog3Id();
            if(catalog3Id!=null&&!catalog3Id.equals("")){

                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
                boolQueryBuilder.filter(termQueryBuilder);
            }
            String[] valueId = pmsSearchParam.getValueId();
            if(valueId!=null){
                for (String s : valueId) {

                    TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",s);
                    boolQueryBuilder.filter(termQueryBuilder);
                }
            }

            String name = pmsSearchParam.getKeyword();
            if(name!=null&&!name.equals("")){
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", name);
                boolQueryBuilder.must(matchQueryBuilder);

                //设置高亮
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.preTags("<span style='color:red'>");
                highlightBuilder.field("skuName");
                highlightBuilder.postTags("</span>");
                searchSourceBuilder.highlighter(highlightBuilder);
            }



            //设置起始
            searchSourceBuilder.from(0);
            //设置条数
//            searchSourceBuilder.size(20);
           //将查询对象转化为字符串
            searchSourceBuilder.query(boolQueryBuilder);
            String execute = searchSourceBuilder.toString();
            System.out.println(execute);
            Search search = new Search.Builder(execute).addIndex("gmall").addType("PmsSkuInfo").build();

            //执行查询
            SearchResult result = jestClient.execute(search);

            //处理查询结果
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = result.getHits(PmsSearchSkuInfo.class);

            System.out.println("hit的size："+hits.size());
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;

                //查询结果高亮处理
                Map<String, List<String>> highlight = hit.highlight;
                if(highlight!=null){
                    String skuName = highlight.get("skuName").get(0);
                    pmsSearchSkuInfo.setSkuName(skuName);
                }
                //将对象放入集合中
                pmsSearchSkuInfos.add(pmsSearchSkuInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("查询到的size"+pmsSearchSkuInfos.size());
        return pmsSearchSkuInfos;
    }
}
