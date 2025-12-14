package store.yd2team.common.interceptor;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MenuUrlMatcher {

    // 먼저 선언한 것이 우선 매칭되도록 LinkedHashMap 사용
    private static final Map<String, String> PREFIX_TO_MENU_ID = new LinkedHashMap<>();

    static {
        // TODO: 너희 실제 메뉴 URL prefix들을 여기에 쌓아주면 됨
        PREFIX_TO_MENU_ID.put("/hr/attd",   "HR_ATTD");    // 근태
        PREFIX_TO_MENU_ID.put("/hr/vcatn",  "HR_VCATN");   // 휴가
        PREFIX_TO_MENU_ID.put("/empAcct",   "CM_EMP_ACCT");// 계정관리
        // PREFIX_TO_MENU_ID.put("/business/rcipt", "BS_RCIPT");
    }

    private MenuUrlMatcher() {}

    /** 요청 URI를 보고 menuId를 찾아 반환. 없으면 null */
    public static String resolveMenuId(String uri) {
        if (uri == null) return null;

        for (Map.Entry<String, String> e : PREFIX_TO_MENU_ID.entrySet()) {
            if (uri.startsWith(e.getKey())) {
                return e.getValue();
            }
        }
        return null;
    }
}
