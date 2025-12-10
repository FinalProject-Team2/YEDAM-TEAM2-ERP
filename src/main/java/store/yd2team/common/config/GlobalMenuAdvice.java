package store.yd2team.common.config;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.MenuModuleViewDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.util.LoginSession;

@Slf4j
@ControllerAdvice
public class GlobalMenuAdvice {

    @ModelAttribute("menuModules")
    public List<MenuModuleViewDto> buildMenuModules() {
        SessionDto s = LoginSession.getLoginSession();
        if (s == null || s.getMenuAuthMap() == null || s.getMenuAuthMap().isEmpty()) {
            return Collections.emptyList();
        }

        Collection<MenuAuthDto> allMenus = s.getMenuAuthMap().values();

        // 1) 조회 가능한 메뉴만 (canRead == 1)
        List<MenuAuthDto> readableMenus = allMenus.stream()
                .filter(Objects::nonNull)
                .filter(MenuAuthDto::isReadable)
                .collect(Collectors.toList());

        // 2) moduleId 기준으로 그룹핑
        Map<String, List<MenuAuthDto>> grouped = readableMenus.stream()
                .collect(Collectors.groupingBy(MenuAuthDto::getModuleId));

        List<MenuModuleViewDto> result = new ArrayList<>();

        for (Map.Entry<String, List<MenuAuthDto>> entry : grouped.entrySet()) {
            String moduleId = entry.getKey();
            List<MenuAuthDto> menus = entry.getValue();

            // 메뉴 정렬: sortOrd → menuId
            menus.sort(Comparator
                    .comparing(MenuAuthDto::getSortOrd, Comparator.nullsLast(Long::compareTo))
                    .thenComparing(MenuAuthDto::getMenuId, Comparator.nullsLast(String::compareTo)));

            MenuModuleViewDto vm = new MenuModuleViewDto();
            vm.setModuleId(moduleId);
            vm.setModuleNm(toModuleName(moduleId)); // 표시용 이름
            vm.setMenus(menus);

            result.add(vm);
        }

        // 3) 모듈 자체 정렬 (원하면 moduleId 말고 별도 sort 기준 사용 가능)
        result.sort(Comparator.comparing(MenuModuleViewDto::getModuleId,
                Comparator.nullsLast(String::compareTo)));

        log.info("사이드 메뉴 모듈 구성: modules={}", 
                 result.stream().map(MenuModuleViewDto::getModuleId).collect(Collectors.toList()));

        return result;
    }

    // 간단 버전: moduleId → 모듈 이름 매핑 (나중에 DB(t경 모듈 테이블)로 빼도 됨)
    private String toModuleName(String moduleId) {
        if (moduleId == null) return "기타";

        return switch (moduleId) {
            case "d1" -> "인사";   // HR
            case "d2" -> "공통";   // COMM
            case "d3" -> "영업";   // SALES
            default    -> "기타";
        };
    }
}
