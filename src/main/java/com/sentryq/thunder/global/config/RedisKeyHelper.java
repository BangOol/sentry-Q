package com.sentryq.thunder.global.config;

public class RedisKeyHelper {

    // Private Constructor: 인스턴스화 방지 -> ??? : 왜 이렇게 했는지 자세하게 파악할 필요가 있음.
    private RedisKeyHelper() {}

    private static final String STOCK_PREFIX = "stock";
    private static final String APPLY_SET_PREFIX = "apply:users";

    /*
    * 재고 key 생성
    * ex) stock : {event:100}
    * 설명 : {event:100} 부분이 해시 태그가 되어, 슬롯을 결정
    * */
    public static String getStockKey(Long eventId) {
        return String.format("%s:{event:%d", STOCK_PREFIX, eventId);
    }

    /*
    * 중복 검사 Set Key 생성
    * ex) apply:users:{event:100}
    * 설명 : 위와 동일한 해시 태그 사용 -> 같은 노드에 저장 보장.
    * */
    public static String getApplySetKey(Long eventId) {
        return String.format("%s:{event:%d", APPLY_SET_PREFIX, eventId);
    }
}
