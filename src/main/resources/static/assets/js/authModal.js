// /assets/js/authModal.js
// 권한 조회 모달
document.addEventListener('DOMContentLoaded', function () {

  const modal    = document.getElementById('authModal');
  const backdrop = document.getElementById('roleSelectBackdrop');
  if (!modal || !backdrop) {
    return;
  }

  const closeBtn = document.getElementById('roleSelectCloseBtn');
  const okBtn    = document.getElementById('roleSelectOkBtn');
  const openBtn  = document.getElementById('btnRoleSelectModal'); // 사원 계정 페이지 버튼 (있으면 사용)

  const searchBtn = document.getElementById('roleSelectSearchBtn');
  const resetBtn  = document.getElementById('roleSelectResetBtn');

  const nameInput   = document.getElementById('roleSelectName');
  const typeSelect  = document.getElementById('roleSelectType');
  const useYnSelect = document.getElementById('roleSelectUseYn');

  let roleGrid;
  let authGridHr, authGridSales, authGridCommon;
  let currentAuthRoleId     = null;      // 권한 영역에 보여줄 대상 역할 ID
  let onSelectCallback      = null;      // 부모 페이지에서 넘겨주는 콜백
  let defaultCheckedRoleIds = [];        // 모달 open 시 넘겨준 checkedRoleIds 저장

  const ROLE_TYPE_ITEMS = [
    { text: '인사', value: 'd1' },
    { text: '공통', value: 'd2' },
    { text: '영업', value: 'd3' }
  ];

  if (!window.tui || !tui.Grid) {
    console.warn('tui.Grid not loaded for authModal');
    return;
  }

  // ===============================
  // 1. 역할 목록 Grid
  // ===============================
  roleGrid = new tui.Grid({
    el: document.getElementById('roleSelectRoleGrid'),
    data: [],
    bodyHeight: 405,
    rowHeight: 32,
    scrollX: false,
    scrollY: true,
    rowHeaders: ['checkbox'],
    columnOptions: {
      minWidth: 80,
      resizable: true
    },
    columns: [
      { header: '역할 ID', name: 'roleId', hidden: true },
      {
        header: '역할 명',
        name: 'roleNm',
        minWidth: 140
      },
      {
        header: '역할 유형',
        name: 'roleTy',
        minWidth: 90,
        formatter: function ({ value }) {
          const item = ROLE_TYPE_ITEMS.find(i => i.value === value);
          return item ? item.text : value;
        }
      },
      {
        header: '사용 여부',
        name: 'yn',
        minWidth: 80,
        formatter: function ({ value }) {
          if (value === 'e1') return '사용';
          if (value === 'e2') return '미사용';
          return value;
        }
      }
    ]
  });

  // ===============================
  // 2. 권한 Grid들 (아코디언 내부)
  // ===============================
  function createAuthGrid(elId) {
    return new tui.Grid({
      el: document.getElementById(elId),
      data: [],
      bodyHeight: 220,
      rowHeight: 32,
      scrollX: false,
      scrollY: true,
      rowHeaders: [],
      columnOptions: {
        minWidth: 80,
        resizable: true
      },
      columns: [
        { header: '메뉴ID', name: 'menuId', hidden: true },
        { header: '메뉴 / 화면', name: 'menuNm', minWidth: 220 },
        {
          header: '조회',
          name: 'selYn',
          align: 'center',
          formatter: ({ value }) =>
            '<input type="checkbox" class="auth-chk" ' + (value === 'e1' ? 'checked' : '') + ' disabled />'
        },
        {
          header: '저장 / 수정',
          name: 'insYn',
          align: 'center',
          formatter: ({ value }) =>
            '<input type="checkbox" class="auth-chk" ' + (value === 'e1' ? 'checked' : '') + ' disabled />'
        },
        {
          header: '삭제',
          name: 'delYn',
          align: 'center',
          formatter: ({ value }) =>
            '<input type="checkbox" class="auth-chk" ' + (value === 'e1' ? 'checked' : '') + ' disabled />'
        }
      ]
    });
  }

  authGridHr     = createAuthGrid('roleSelectAuthGridHr');
  authGridSales  = createAuthGrid('roleSelectAuthGridSales');
  authGridCommon = createAuthGrid('roleSelectAuthGridCommon');

  const moduleGridMap = {
    hr:     { grid: authGridHr,     moduleId: 'd1' },
    sales:  { grid: authGridSales,  moduleId: 'd3' },
    common: { grid: authGridCommon, moduleId: 'd2' }
  };

  // ===============================
  // 3. AJAX - 역할 / 권한 조회
  // ===============================

  // ★ 조회 버튼 눌렀을 때만 호출해서 왼쪽 그리드에 데이터 세팅
  function loadRoleList() {
    const params = {
      roleNm: nameInput ? (nameInput.value || '') : '',
      roleTy: typeSelect ? (typeSelect.value || '') : '',
      useYn:  useYnSelect ? (useYnSelect.value || '') : ''
    };

    $.getJSON('/api/authModal/roles', params, function (data) {
      const list = data || [];
      roleGrid.resetData(list);

      // 모달 open 시 넘겼던 checkedRoleIds 다시 체크해 주기
      if (defaultCheckedRoleIds.length > 0) {
        list.forEach(function (row, idx) {
          if (defaultCheckedRoleIds.indexOf(row.roleId) !== -1) {
            roleGrid.check(idx);
          }
        });
      }
      // 👉 여기서는 currentAuthRoleId나 우측 아코디언은 건드리지 않는다 (그대로 유지).
    }).fail(function (xhr) {
      console.error('역할 목록 조회 실패', xhr);
      roleGrid.resetData([]);
    });
  }

  function loadAuthGridByModule(roleId, key) {
    const info = moduleGridMap[key];
    if (!info) return;

    const params = {
      moduleId: info.moduleId,
      roleId:   roleId || ''
    };

    $.getJSON('/api/auth/menus', params, function (data) {
      info.grid.resetData(data || []);
      info.grid.refreshLayout();
    }).fail(function (xhr) {
      console.error('권한 목록 조회 실패 [' + key + ']', xhr);
      info.grid.resetData([]);
      info.grid.refreshLayout();
    });
  }

  function loadAllAuthGrids(roleId) {
    loadAuthGridByModule(roleId, 'hr');
    loadAuthGridByModule(roleId, 'sales');
    loadAuthGridByModule(roleId, 'common');
  }

  // ===============================
  // 4. Grid / 아코디언 이벤트
  // ===============================

  // 역할 행 클릭 → 해당 역할 기준으로 권한 조회
  roleGrid.on('click', function (ev) {
    const row = roleGrid.getRow(ev.rowKey);
    if (!row) return;
    currentAuthRoleId = row.roleId;
    loadAllAuthGrids(currentAuthRoleId);
  });

  // 아코디언이 열릴 때 레이아웃 보정
  $('#roleSelectCollapseHr, #roleSelectCollapseSales, #roleSelectCollapseCommon')
    .on('shown.bs.collapse', function (e) {
      const id = e.target.id;
      setTimeout(function () {
        if (id === 'roleSelectCollapseHr') {
          authGridHr.refreshLayout();
        } else if (id === 'roleSelectCollapseSales') {
          authGridSales.refreshLayout();
        } else if (id === 'roleSelectCollapseCommon') {
          authGridCommon.refreshLayout();
        }
      }, 0);
    });

  // ===============================
  // 5. 검색 / 초기화 버튼
  // ===============================
  if (searchBtn) {
    searchBtn.addEventListener('click', function () {
      loadRoleList();
    });
  }

  if (resetBtn) {
    resetBtn.addEventListener('click', function () {
      if (nameInput)   nameInput.value = '';
      if (typeSelect)  typeSelect.value = '';
      if (useYnSelect) useYnSelect.value = '';

      roleGrid.resetData([]);
      currentAuthRoleId = null;
      // 🔁 초기화 시에는 "기본 메뉴만" 다시 로딩
      loadAllAuthGrids(null);
    });
  }

  // ===============================
  // 6. 자동완성 (역할 명)
  // ===============================
  if (window.TeamCommon && TeamCommon.autocomplete && TeamCommon.autocomplete.init) {
    TeamCommon.autocomplete.init({
      inputSelector: '#roleSelectName',
      listSelector: '#roleSelectNameSuggest',
      url: '/api/authModal/roles/nameSuggest',
      paramName: 'keyword',
      minLength: 1,
      preventClose: true,
      mapResponse: function (item) {
        return {
          id: item.roleId,
          label: item.roleNm,
          value: item.roleNm
        };
      },
      onSelect: function (item) {
        $('#roleSelectName').val(item.value);
      }
    });
  }

  // ===============================
  // 7. 모달 열기/닫기
  // ===============================
  function open(options) {
    onSelectCallback = options && typeof options.onSelect === 'function'
      ? options.onSelect
      : null;

    // 모달 진입 시 체크되어 있던 역할 ID들을 저장해 둔다
    defaultCheckedRoleIds = options && Array.isArray(options.checkedRoleIds)
      ? options.checkedRoleIds
      : [];

    // 모달 초기 상태
    roleGrid.resetData([]);
    currentAuthRoleId = null;

    modal.style.display = 'block';
    backdrop.style.display = 'block';

    setTimeout(function () {
      modal.classList.add('show');
      backdrop.classList.add('show');

      roleGrid.refreshLayout();
      authGridHr.refreshLayout();
      authGridSales.refreshLayout();
      authGridCommon.refreshLayout();

      // 🔹 모달 진입 시 우측 권한 설정은 "역할 없음" 기준으로 메뉴만 조회
      loadAllAuthGrids(null);
    }, 10);
  }

  function close() {
    modal.classList.remove('show');
    backdrop.classList.remove('show');

    setTimeout(function () {
      modal.style.display = 'none';
      backdrop.style.display = 'none';
    }, 150);
  }

  if (closeBtn) {
    closeBtn.addEventListener('click', close);
  }

  // 선택 버튼
  if (okBtn) {
    okBtn.addEventListener('click', function () {
      const rows = roleGrid.getCheckedRows();
      if (!rows || rows.length === 0) {
        alert('역할을 하나 이상 선택해 주세요.');
        return;
      }

      const result = rows.map(function (r) {
        const typeItem = ROLE_TYPE_ITEMS.find(i => i.value === r.roleTy);
        return {
          roleId:   r.roleId,
          roleNm:   r.roleNm,
          roleTy:   r.roleTy,
          roleTyNm: typeItem ? typeItem.text : r.roleTy,
          yn:       r.yn
        };
      });

      if (onSelectCallback) {
        onSelectCallback(result);
      }
      close();
    });
  }

  // 전역으로 노출
  window.authModal = {
    open: open,
    close: close
  };

  // 창 리사이즈 시 레이아웃 보정
  window.addEventListener('resize', function () {
    roleGrid.refreshLayout();
    authGridHr.refreshLayout();
    authGridSales.refreshLayout();
    authGridCommon.refreshLayout();
  });
});
