package com.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gmall.bean.PmsSearchSkuInfo;
import com.gmall.bean.PmsSkuInfo;
import com.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GmallSearchServiceApplicationTests {


	@Autowired
	private JestClient jestClient;
//	@Reference
//	private SkuService skuService;
	@Test
	void contextLoads() throws IOException {

//		List<PmsSkuInfo> pmsSkuInfos = skuService.selAllSku();
//		List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
//		for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
//			PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
//			BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);
//			pmsSearchSkuInfos.add(pmsSearchSkuInfo);
//		}
//
//		//遍历需要插入的对象集合
//		for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
//			//创建es插入对象
//			Index PUT = new Index.Builder(pmsSearchSkuInfo).index("gmall").type("PmsSkuInfo").id(pmsSearchSkuInfo.getId()).build();
//			//执行插入操作
//			DocumentResult result = jestClient.execute(PUT);
//			//打印返回信息
//			System.out.println(result.getJsonString());

		}



	@Test
	public void select() throws IOException {
		//创建查询语句
		String query = "{\n" +
				"  \"query\": {\n" +
				"    \"bool\": {\n" +
				"      \"filter\": {\n" +
				"        \"terms\": {\n" +
				"          \"skuAttrValueList.attrId\": [\n" +
				"            \"12\",\n" +
				"            \"13\"\n" +
				"          ]\n" +
				"        }\n" +
				"        \n" +
				"      }, \"must\": [\n" +
				"        {\"match\": {\n" +
				"          \"skuDesc\": \"琚\"\n" +
				"        }}\n" +
				"      ]\n" +
				"    }\n" +
				"  }\n" +
				"}";
		//创建查询对象
		Search search = new Search.Builder(query).addIndex("gmall").addType("PmsSkuInfo").build();
		//执行查询
		SearchResult result = jestClient.execute(search);
		//创建查询结果对象集合
		List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
		//遍历查询结果
		List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = result.getHits(PmsSearchSkuInfo.class);
		for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
			//从结果对象中，找到需要的source
			PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;
			pmsSearchSkuInfos.add(pmsSearchSkuInfo);
		}

		//打印查询到集合的数量
		System.out.println(pmsSearchSkuInfos.size());
	}

	/**
	 *	使用jestAPI实现查询功能
	 */
	@Test
	public void selectAPI() throws IOException {

		//创建封装了搜索功能的对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//创建from对象
		searchSourceBuilder.from(0);
		//创建size对象
		searchSourceBuilder.size(20);
		//创建highlight对象
//		HighlightBuilder highlightBuilder = new HighlightBuilder(null);
//		searchSourceBuilder.highlighter(highlightBuilder);
		//创建bool对象
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		//创建filter对象
//		TermQueryBuilder termQueryBuilder = new TermQueryBuilder("","");
		TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("skuAttrValueList.attrId","12","13");
		boolQueryBuilder.filter(termsQueryBuilder);
		//创建must对象
		MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuDesc", "琚");
		boolQueryBuilder.must(matchQueryBuilder);
		searchSourceBuilder.query(boolQueryBuilder);

		//将查询对象转为字符串
		String dsl = searchSourceBuilder.toString();
		Search search = new Search.Builder(dsl).addIndex("").addType("").build();

		SearchResult result = jestClient.execute(search);
		List<PmsSearchSkuInfo> list = new ArrayList<>();
		List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = result.getHits(PmsSearchSkuInfo.class);
		for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {

			PmsSearchSkuInfo source = hit.source;
			list.add(source);
		}

		System.out.println(list.size());
	}

}
