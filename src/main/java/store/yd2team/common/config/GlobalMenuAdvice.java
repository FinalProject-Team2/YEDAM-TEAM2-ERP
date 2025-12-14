package store.yd2team.common.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.yd2team.common.dto.MenuAuthDto;
import store.yd2team.common.dto.MenuModuleViewDto;
import store.yd2team.common.dto.SessionDto;
import store.yd2team.common.mapper.MenuAuthMapper;
import store.yd2team.common.util.LoginSession;

@RequiredArgsConstructor
@Slf4j
@ControllerAdvice
public class GlobalMenuAdvice {
	
	private final MenuAuthMapper menuAuthMapper;
	
	@ModelAttribute("menuModules")
	public List<MenuModuleViewDto> buildMenuModules() {
	    SessionDto s = LoginSession.getLoginSession();
	    if (s == null) return Collections.emptyList();

	    // ✅ 운영자면: DB에서 전체 메뉴 조회
	    Collection<MenuAuthDto> allMenus;
	    if ("e1".equals(s.getOprtrYn())) {
	        allMenus = menuAuthMapper.selectAllMenusForOprtr();
	    } else if ("r4".equals(s.getAcctSt())) {
	        allMenus = menuAuthMapper.selectMenusForUnpaid(s.getVendId());
	    } else {
	        if (s.getMenuAuthMap() == null || s.getMenuAuthMap().isEmpty()) return Collections.emptyList();
	        allMenus = s.getMenuAuthMap().values();
	    }

	    List<MenuAuthDto> readableMenus = allMenus.stream()
	            .filter(Objects::nonNull)
	            .filter(MenuAuthDto::isReadable)
	            .collect(Collectors.toList());

	    Map<String, List<MenuAuthDto>> grouped = readableMenus.stream()
	            .collect(Collectors.groupingBy(MenuAuthDto::getModuleId));

	    List<MenuModuleViewDto> result = new ArrayList<>();

	    for (Map.Entry<String, List<MenuAuthDto>> entry : grouped.entrySet()) {
	        String moduleId = entry.getKey();
	        List<MenuAuthDto> menus = entry.getValue();

	        menus.sort(Comparator
	                .comparing(MenuAuthDto::getSortOrd, Comparator.nullsLast(Long::compareTo))
	                .thenComparing(MenuAuthDto::getMenuId, Comparator.nullsLast(String::compareTo)));

	        MenuModuleViewDto vm = new MenuModuleViewDto();
	        vm.setModuleId(moduleId);
	        vm.setModuleNm(toModuleName(moduleId));
	        vm.setMenus(menus);

	        result.add(vm);
	    }

	    result.sort(Comparator.comparing(MenuModuleViewDto::getModuleId,
	            Comparator.nullsLast(String::compareTo)));

	    return result;
	}
    
    @ModelAttribute("menuAuthMap")
    public Map<String, MenuAuthDto> exposeMenuAuthMap() {
        SessionDto s = LoginSession.getLoginSession();
        if (s == null || s.getMenuAuthMap() == null) {
            return Collections.emptyMap();
        }
        return s.getMenuAuthMap();
    }

    // 간단 버전: moduleId → 모듈 이름 매핑 
    private String toModuleName(String moduleId) {
        if (moduleId == null) return "기타";

        return switch (moduleId) {
            case "d1" -> "인사";   // HR
            case "d2" -> "공통";   // COMM
            case "d3" -> "영업";   // SALES
            default   -> "기타";
        };
    }
    
    @ModelAttribute("sideMenuRoots")
    public List<MenuAuthDto> sideMenuRoots() {
        SessionDto s = LoginSession.getLoginSession();
        if (s == null) return Collections.emptyList();

        List<MenuAuthDto> sourceMenus;

        // 1) ✅ 운영자: 전체 메뉴
        if ("e1".equals(s.getOprtrYn())) {
            sourceMenus = menuAuthMapper.selectAllMenusForOprtr();
        }
        // 2) ✅ 사원 + 구독 미결제(r4): 구독 메뉴만
        else if ("r4".equals(s.getAcctSt())) {
            // 마스터만 허용이면 && "e1".equals(s.getMasYn()) 추가
            sourceMenus = menuAuthMapper.selectMenusForUnpaid(s.getVendId());
        }
        // 3) ✅ 사원 정상: 세션 권한 메뉴
        else {
            if (s.getMenuAuthMap() == null || s.getMenuAuthMap().isEmpty()) return Collections.emptyList();
            sourceMenus = new ArrayList<>(s.getMenuAuthMap().values());
        }

        // ---- 아래 트리 구성 로직은 너꺼 그대로 ----
        Map<String, MenuAuthDto> nodeMap = new HashMap<>();
        for (MenuAuthDto src : sourceMenus) {
            if (src == null) continue;
            MenuAuthDto copy = copyMenu(src);
            nodeMap.put(copy.getMenuId(), copy);
        }

        List<MenuAuthDto> roots = new ArrayList<>();
        for (MenuAuthDto m : nodeMap.values()) {
            String pid = m.getPrntMenuId();
            if (pid == null || pid.isBlank()) roots.add(m);
            else {
                MenuAuthDto parent = nodeMap.get(pid);
                if (parent != null) parent.getChildren().add(m);
                else roots.add(m);
            }
        }

        sortTree(roots);

        // 운영자면 prune 생략 가능
        if ("e1".equals(s.getOprtrYn())) return roots;

        return pruneInvisible(roots);
    }

    private MenuAuthDto copyMenu(MenuAuthDto src) {
        MenuAuthDto m = new MenuAuthDto();
        m.setMenuId(src.getMenuId());
        m.setMenuNm(src.getMenuNm());
        m.setMenuUrl(src.getMenuUrl());
        m.setModuleId(src.getModuleId());
        m.setPrntMenuId(src.getPrntMenuId());
        m.setSortOrd(src.getSortOrd());
        m.setCanRead(src.getCanRead());
        m.setCanWrite(src.getCanWrite());
        m.setCanDelete(src.getCanDelete());

        // ✅ children은 무조건 새 리스트
        m.setChildren(new ArrayList<>());
        return m;
    }
    
    private void sortTree(List<MenuAuthDto> nodes) {
        nodes.sort(Comparator
            .comparing(MenuAuthDto::getSortOrd, Comparator.nullsLast(Long::compareTo))
            .thenComparing(MenuAuthDto::getMenuId, Comparator.nullsLast(String::compareTo)));

        for (MenuAuthDto n : nodes) {
            if (n.getChildren() != null && !n.getChildren().isEmpty()) {
                sortTree(n.getChildren());
            }
        }
    }

    private List<MenuAuthDto> pruneInvisible(List<MenuAuthDto> nodes) {
        List<MenuAuthDto> out = new ArrayList<>();

        for (MenuAuthDto n : nodes) {
            List<MenuAuthDto> prunedChildren = pruneInvisible(
                n.getChildren() == null ? Collections.emptyList() : n.getChildren()
            );
            n.setChildren(prunedChildren);

            boolean visible = n.isReadable() || !prunedChildren.isEmpty();
            if (visible) out.add(n);
        }
        
        return out;
    }
}
