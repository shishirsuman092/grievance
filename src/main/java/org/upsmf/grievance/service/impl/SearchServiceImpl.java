package org.upsmf.grievance.service.impl;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.constants.Constants;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.request.SearchRequest;
import org.upsmf.grievance.service.SearchService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private static final String ASC_ORDER = "asc";
    private static final String AND = "AND" ;

    @Override
    public Ticket search(SearchRequest searchRequest) {
        SearchSourceBuilder query = processSearchQuery(searchRequest, true);
        return null;
    }
    private Integer getIntValue(Object num) throws Exception {
        int i = 100;
        if (null != num) {
            try {
                i = (int) num;
            } catch (Exception e) {
                if(num instanceof String){
                    try{
                        return Integer.parseInt((String) num);
                    }catch (Exception ex){
                        throw new Exception(Constants.ERR_COMPOSITE_SEARCH_INVALID_PARAMS);
                    }
                }
                i = new Long(num.toString()).intValue();
            }
        }
        return i;
    }

    private SearchSourceBuilder processSearchQuery(SearchRequest searchRequest, boolean sort) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        List<String> fields = searchRequest.getFields();
        if (null != fields && !fields.isEmpty()) {
            fields.add("objectType");
            fields.add("identifier");
            searchSourceBuilder.fetchSource(fields.toArray(new String[fields.size()]), null);
        }

        searchSourceBuilder.size(searchRequest.getSize());
        searchSourceBuilder.from(searchRequest.getOffset());
        QueryBuilder query = getSearchQuery(searchRequest);

        searchSourceBuilder.query(query);

        if (sort) {
            Map<String, String> sorting = searchRequest.getSort();
            if (sorting == null || sorting.isEmpty()) {
                sorting = new HashMap<String, String>();
                sorting.put("createdTimeTS", "asc");
            }
            for (String key : sorting.keySet()){
                if(key.contains(".")){
                    String nestedPath = key.split("\\.")[0];
                    searchSourceBuilder.sort(SortBuilders.fieldSort(key + Constants.RAW_FIELD_EXTENSION).order(getSortOrder(sorting.get(key))).setNestedSort(new NestedSortBuilder(nestedPath)));
                } else{
                    searchSourceBuilder.sort(key + Constants.RAW_FIELD_EXTENSION,
                            getSortOrder(sorting.get(key)));
                }
            }
        }
        searchSourceBuilder.trackScores(true);
        return searchSourceBuilder;
    }

    private SortOrder getSortOrder(String value) {
        return value.equalsIgnoreCase(ASC_ORDER) ? SortOrder.ASC : SortOrder.DESC;
    }

    private QueryBuilder getSearchQuery(SearchRequest searchRequest) {
        QueryBuilder origFilterQry = prepareSearchQuery(searchRequest);
        return origFilterQry;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private QueryBuilder prepareSearchQuery(SearchRequest searchRequest) {
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        QueryBuilder queryBuilder = null;
        String totalOperation = searchRequest.getOperation();
        List<Map> properties = searchRequest.getProperties();
        for (Map<String, Object> property : properties) {
            String opertation = (String) property.get("operation");

            List<Object> values;
            try {
                values = (List<Object>) property.get("values");
            } catch (Exception e) {
                values = Arrays.asList(property.get("values"));
            }
            values = values.stream().filter(value -> (null != value)).collect(Collectors.toList());


            String propertyName = property.get("propertyName") + Constants.RAW_FIELD_EXTENSION;

            switch (opertation) {
                case Constants.SEARCH_OPERATION_EQUAL: {
                    queryBuilder = getMustTermQuery(propertyName, values, true);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_NOT_EQUAL: {
                    queryBuilder = getMustTermQuery(propertyName, values, false);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_NOT_IN: {
                    queryBuilder = getNotInQuery(propertyName, values);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_ENDS_WITH: {
                    queryBuilder = getRegexQuery(propertyName, values);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_LIKE:
                case Constants.SEARCH_OPERATION_CONTAINS: {
                    queryBuilder = getMatchPhraseQuery(propertyName, values, true);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_NOT_LIKE: {
                    queryBuilder = getMatchPhraseQuery(propertyName, values, false);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_STARTS_WITH: {
                    queryBuilder = getMatchPhrasePrefixQuery(propertyName, values);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_EXISTS: {
                    queryBuilder = getExistsQuery(propertyName, values, true);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_NOT_EXISTS: {
                    queryBuilder = getExistsQuery(propertyName, values, false);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_GREATER_THAN: {
                    queryBuilder = getRangeQuery(propertyName, values,
                            Constants.SEARCH_OPERATION_GREATER_THAN);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_GREATER_THAN_EQUALS: {
                    queryBuilder = getRangeQuery(propertyName, values,
                            Constants.SEARCH_OPERATION_GREATER_THAN_EQUALS);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_LESS_THAN: {
                    queryBuilder = getRangeQuery(propertyName, values, Constants.SEARCH_OPERATION_LESS_THAN);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_LESS_THAN_EQUALS: {
                    queryBuilder = getRangeQuery(propertyName, values,
                            Constants.SEARCH_OPERATION_LESS_THAN_EQUALS);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_RANGE: {
                    queryBuilder = getRangeQuery(propertyName, values);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
                case Constants.SEARCH_OPERATION_AND: {
                    queryBuilder = getAndQuery(propertyName, values);
                    queryBuilder = checkNestedProperty(queryBuilder, propertyName);
                    break;
                }
            }
            if (totalOperation.equalsIgnoreCase(AND)) {
                boolQuery.must(queryBuilder);
            } else {
                boolQuery.should(queryBuilder);
            }

        }
        return boolQuery;
    }

    private QueryBuilder getAndQuery(String propertyName, List<Object> values) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Object value : values) {
            queryBuilder.must(
                    QueryBuilders.matchQuery(propertyName, value).operator(Operator.AND));
        }
        return queryBuilder;
    }

    private QueryBuilder getRangeQuery(String propertyName, List<Object> values, String operation) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Object value : values) {
            switch (operation) {
                case Constants.SEARCH_OPERATION_GREATER_THAN: {
                    queryBuilder.should(QueryBuilders
                            .rangeQuery(propertyName).gt(value));
                    break;
                }
                case Constants.SEARCH_OPERATION_GREATER_THAN_EQUALS: {
                    queryBuilder.should(QueryBuilders
                            .rangeQuery(propertyName).gte(value));
                    break;
                }
                case Constants.SEARCH_OPERATION_LESS_THAN: {
                    queryBuilder.should(QueryBuilders
                            .rangeQuery(propertyName).lt(value));
                    break;
                }
                case Constants.SEARCH_OPERATION_LESS_THAN_EQUALS: {
                    queryBuilder.should(QueryBuilders
                            .rangeQuery(propertyName).lte(value));
                    break;
                }
            }
        }

        return queryBuilder;
    }

    @SuppressWarnings("unchecked")
    private QueryBuilder getRangeQuery(String propertyName, List<Object> values) {
        RangeQueryBuilder queryBuilder = new RangeQueryBuilder(propertyName);
        for (Object value : values) {
            Map<String, Object> rangeMap = (Map<String, Object>) value;
            if (!rangeMap.isEmpty()) {
                for (String key : rangeMap.keySet()) {
                    switch (key) {
                        case Constants.SEARCH_OPERATION_RANGE_GTE: {
                            queryBuilder.from(rangeMap.get(key));
                            break;
                        }
                        case Constants.SEARCH_OPERATION_RANGE_LTE: {
                            queryBuilder.to(rangeMap.get(key));
                            break;
                        }
                    }
                }
            }
        }
        return queryBuilder;
    }
    private QueryBuilder getMustTermQuery(String propertyName, List<Object> values, boolean match) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Object value : values) {
            if (match) {
                queryBuilder.should(
                        QueryBuilders.matchQuery(propertyName, value));
            } else {
                queryBuilder.mustNot(
                        QueryBuilders.matchQuery(propertyName, value));
            }
        }
        return queryBuilder;
    }

    private QueryBuilder checkNestedProperty(QueryBuilder queryBuilder, String propertyName) {
        if(propertyName.replaceAll(Constants.RAW_FIELD_EXTENSION, "").contains(".")) {
            queryBuilder = QueryBuilders.nestedQuery(propertyName.split("\\.")[0], queryBuilder, org.apache.lucene.search.join.ScoreMode.None);
        }
        return queryBuilder;
    }

    private QueryBuilder getExistsQuery(String propertyName, List<Object> values, boolean exists) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Object value : values) {
            if (exists) {
                queryBuilder.should(QueryBuilders.existsQuery(String.valueOf(value)));
            } else {
                queryBuilder.mustNot(QueryBuilders.existsQuery(String.valueOf(value)));
            }
        }
        return queryBuilder;
    }
    private QueryBuilder getNotInQuery(String propertyName, List<Object> values) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder
                .mustNot(QueryBuilders.termsQuery(propertyName, values));
        return queryBuilder;
    }
    private QueryBuilder getMatchPhrasePrefixQuery(String propertyName, List<Object> values) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Object value : values) {
            queryBuilder.should(QueryBuilders.prefixQuery(
                    propertyName, ((String) value).toLowerCase()));
        }
        return queryBuilder;
    }

    private QueryBuilder getMatchPhraseQuery(String propertyName, List<Object> values, boolean match) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Object value : values) {
            String stringValue = String.valueOf(value);
            if (match) {
                queryBuilder.should(QueryBuilders
                        .regexpQuery(propertyName,
                                ".*" + stringValue.toLowerCase() + ".*"));
            } else {
                queryBuilder.mustNot(QueryBuilders
                        .regexpQuery(propertyName,
                                ".*" + stringValue.toLowerCase() + ".*"));
            }
        }
        return queryBuilder;
    }

    private QueryBuilder getRegexQuery(String propertyName, List<Object> values) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (Object value : values) {
            String stringValue = String.valueOf(value);
            queryBuilder.should(QueryBuilders.regexpQuery(propertyName,
                    ".*" + stringValue.toLowerCase()));
        }
        return queryBuilder;
    }

}
