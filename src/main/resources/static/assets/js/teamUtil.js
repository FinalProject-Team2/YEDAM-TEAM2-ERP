// teamExportUtil.js

$(function() {

	// ==========================
	// 1. 공통 datepicker 기본 초기화
	// ==========================

	// 클래스가 datepicker인 모든 input
	$('.datepicker').datepicker({
		format: "yyyy-mm-dd",
		autoclose: true,
		todayHighlight: true
	});

	// 특정 id용 (쓰고 있으면)
	if ($('#datePickerGroup').length) {
		$('#datePickerGroup').datepicker({
			format: 'yyyy-mm-dd',
			autoclose: true
		});
	}

	// 적용일 단일 input-group (아이콘 포함)
	if ($('#applyDatePicker').length) {
		$('#applyDatePicker input').datepicker({
			format: "yyyy-mm-dd",
			autoclose: true,
			todayHighlight: true
		});

		// 아이콘 클릭 → input focus → 달력 열림
		$('#icon-calendar').on('click', function() {
			$('#applyDate').focus();
		});
	}

	// ==========================
	// 2. 시작/종료 기간 datepicker (range)
	// ==========================

	if ($('#applyDatePickerStart').length && $('#applyDatePickerEnd').length) {

		const $startInput = $('#applyDatePickerStart input'); // id: applyDateStart
		const $endInput   = $('#applyDatePickerEnd input');   // id: applyDateEnd

		// 시작일 → 종료일 최소 날짜/보정
		function handleStartChange(date) {
			if (!date) return;

			// 종료일 최소 날짜 제한
			$endInput.datepicker('setStartDate', date);

			const endDate = $endInput.datepicker('getDate');
			if (endDate && endDate < date) {
				$endInput.datepicker('setDate', date);
			}
		}

		// 종료일 → 시작일 최대 날짜/보정
		function handleEndChange(date) {
			if (!date) return;

			// 시작일 최대 날짜 제한
			$startInput.datepicker('setEndDate', date);

			const startDate = $startInput.datepicker('getDate');
			if (startDate && startDate > date) {
				$startInput.datepicker('setDate', date);
			}
		}

		// datepicker 기본 설정 + changeDate 이벤트
		$startInput.datepicker({
			format: "yyyy-mm-dd",
			autoclose: true,
			todayHighlight: true
		}).on('changeDate', function(e) {
			handleStartChange(e.date);
		});

		$endInput.datepicker({
			format: "yyyy-mm-dd",
			autoclose: true,
			todayHighlight: true
		}).on('changeDate', function(e) {
			handleEndChange(e.date);
		});

		// yyyy-mm-dd 문자열을 Date 객체로 파싱
		function parseYMD(str) {
			if (!str) return null;
			const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(str);
			if (!m) return null;

			const y = parseInt(m[1], 10);
			const mo = parseInt(m[2], 10) - 1; // 0-based
			const d = parseInt(m[3], 10);

			const dt = new Date(y, mo, d);
			// 유효성 체크 (예: 2025-02-31 같은 이상한 날짜 방지)
			if (dt.getFullYear() !== y || dt.getMonth() !== mo || dt.getDate() !== d) {
				return null;
			}
			return dt;
		}

		// ★ 사용자가 직접 숫자로 입력하고 마우스로 다른 곳 클릭할 때도
		//    datepicker 내부 값 업데이트 + changeDate 로직 실행되도록 처리
		$startInput.on('blur', function() {
			const date = parseYMD($(this).val());
			if (date) {
				// setDate → changeDate 이벤트 발생 → handleStartChange 호출
				$startInput.datepicker('setDate', date);
			}
		});

		$endInput.on('blur', function() {
			const date = parseYMD($(this).val());
			if (date) {
				// setDate → changeDate 이벤트 발생 → handleEndChange 호출
				$endInput.datepicker('setDate', date);
			}
		});

		// 아이콘 클릭 → 각 input focus
		$('#icon-calendar-start').on('click', function() {
			$('#applyDateStart').focus();
		});

		$('#icon-calendar-end').on('click', function() {
			$('#applyDateEnd').focus();
		});
	}

	// ==========================
	// 3. 입력 마스크: 숫자만 + yyyy-mm-dd 형식 강제
	//    (단일/시작/종료 모두 공통)
	// ==========================

	function attachDateMask($input) {
		if (!$input.length) return;

		// (1) input 이벤트: 숫자만 허용 + yyyy-mm-dd 포맷 + 월/일 보정
		$input.on('input', function() {
			let value = $(this).val();

			// 숫자만 남기기
			value = value.replace(/\D/g, '');
			value = value.slice(0, 8);   // 최대 8자리(yyyymmdd)
			let len = value.length;

			// ---- 월 범위(1~12) 보정 ----
			if (len >= 5) {
				const yearPart = value.slice(0, 4); // YYYY
				let monthPart  = value.slice(4, 6); // MM

				if (monthPart.length === 2) {
					let monthNum = parseInt(monthPart, 10);

					if (isNaN(monthNum) || monthNum <= 0) {
						monthNum = 1;
					} else if (monthNum > 12) {
						monthNum = 12;
					}
					monthPart = monthNum.toString().padStart(2, '0');

					value = yearPart + monthPart + value.slice(6);
					len = value.length;
				}
			}

			// ---- 일 범위(1~31) 보정 ----
			if (len === 8) {
				const ymPart  = value.slice(0, 6); // YYYYMM
				let dayPart   = value.slice(6, 8); // DD
				let dayNum    = parseInt(dayPart, 10);

				if (isNaN(dayNum) || dayNum <= 0) {
					dayNum = 1;
				} else if (dayNum > 31) {
					dayNum = 31;
				}
				dayPart = dayNum.toString().padStart(2, '0');
				value   = ymPart + dayPart;
			}

			// ---- 화면 표시용 yyyy-mm-dd 변환 ----
			let result = '';
			len = value.length;

			if (len <= 4) {
				result = value; // YYYY
			} else if (len <= 6) {
				result = value.slice(0, 4) + '-' + value.slice(4); // YYYY-MM
			} else {
				result =
					value.slice(0, 4) + '-' +
					value.slice(4, 6) + '-' +
					value.slice(6); // YYYY-MM-DD
			}

			$(this).val(result);
		});

		// (2) keydown: 숫자 + 기본 제어키만 허용
		$input.on('keydown', function(e) {
			const allowedControlKeys = [
				'Backspace', 'Tab', 'ArrowLeft', 'ArrowRight',
				'Delete', 'Home', 'End', 'Enter' // Enter도 허용
			];

			if (
				(e.key >= '0' && e.key <= '9') ||
				allowedControlKeys.includes(e.key)
			) {
				return; // 허용
			}

			e.preventDefault();
		});
	}

	// 단일, 시작, 종료 input 모두에 마스크 적용
	attachDateMask($('#applyDate'));       // 단일 날짜
	attachDateMask($('#applyDateStart'));  // 시작일
	attachDateMask($('#applyDateEnd'));    // 종료일

	// ==========================
	// 4. Toast UI Grid 테마 적용
	// ==========================

	// teamExportUtil.js를 모든 페이지에서 사용해도 에러 안 나도록 방어
	if (window.tui && tui.Grid && typeof tui.Grid.applyTheme === 'function') {
		tui.Grid.applyTheme('default', {
			cell: {
				normal: {
					border: '#dedede',
					background: '#ffffff',
					showVerticalBorder: true
				},
				header: {
					border: '#dedede',
					background: '#f5f5f5',
					showVerticalBorder: true
				},
				rowHeader: {
					border: '#dedede',
					background: '#ffffff',
					showVerticalBorder: true
				},
				editable: {
					background: '#FFFDF0',
					text: '#000'
				},
				selectedHeader: {
					background: '#f5f5f5'
				},
				selected: {
					background: '#ffffff'
				}
			}
		});
	}
});

// -------------------------------------------------------------------
// 팀 공통 자동완성(autocomplete) 유틸리티
// - jQuery 기반
// - 서버에서 JSON(예: [{id, name}, ...])을 받아와서 자동완성 목록을 출력
// - 화면에서는 TeamCommon.autocomplete.init(config) 형태로 사용
// -------------------------------------------------------------------

(function (global, $) {
  // ---------------------------------------------------------------
  // 0. 전역 네임스페이스(TeamCommon) 보장
  //    - 만약 다른 JS에서 이미 window.TeamCommon을 만들었다면 그대로 사용
  //    - 없다면 새 객체 생성
  // ---------------------------------------------------------------
  global.TeamCommon = global.TeamCommon || {};

  // ---------------------------------------------------------------
  // 1. 자동완성 전용 네임스페이스 생성/획득
  //    - TeamCommon.autocomplete 객체를 만들고 ns 변수로 참조
  //    - 이미 존재하면 그 객체를 재사용
  // ---------------------------------------------------------------
  const ns = global.TeamCommon.autocomplete = global.TeamCommon.autocomplete || {};

  /**
   * 자동완성 초기화 함수
   *
   * @param {Object} config - 설정 객체
   *  - inputSelector : (필수) 사용자가 타이핑할 input 요소의 jQuery 셀렉터
   *                    예) '#customerName'
   *  - listSelector  : (필수) 자동완성 후보를 표시할 컨테이너(주로 <ul>)의 셀렉터
   *                    예) '#customerSuggest'
   *  - url           : (필수) 서버 자동완성 API URL
   *                    예) '/api/customers/autocomplete'
   *  - paramName     : (선택) 서버에 전송할 파라미터 이름, 기본값 'keyword'
   *                    예) 서버에서 @RequestParam("keyword") String keyword
   *  - minLength     : (선택) 몇 글자 이상 입력했을 때부터 검색할지, 기본값 2
   *  - delay         : (선택) 디바운스 지연 시간(ms), 기본값 300
   *  - mapResponse   : (선택) 서버 응답 item -> {id, label, value}로 변환하는 함수
   *                    (각 화면별 응답 구조에 맞게 커스터마이징)
   *  - onSelect      : (선택) 사용자가 목록에서 항목을 클릭했을 때 호출되는 콜백
   *                    (기본: input에 item.value를 넣는 동작)
   */
  ns.init = function (config) {
    // -------------------------------------------------------------
    // 2. 셀렉터로 실제 DOM 요소(jQuery 객체) 가져오기
    // -------------------------------------------------------------
    const $input = $(config.inputSelector); // 자동완성을 적용할 입력창
    const $list = $(config.listSelector);   // 후보 목록을 표시할 컨테이너(보통 <ul>)

    // 입력창 또는 리스트 컨테이너가 없으면 콘솔 경고 후 초기화 중단
    if ($input.length === 0 || $list.length === 0) {
      console.warn('autocomplete - selector 확인 필요', config);
      return;
    }

    // -------------------------------------------------------------
    // 3. 옵션 기본값 설정
    // -------------------------------------------------------------
    const url       = config.url;                      // 서버 호출 URL (필수)
    const paramName = config.paramName || 'keyword';   // 서버로 보낼 파라미터 이름
    const minLength = config.minLength ?? 2;           // 최소 글자 수 (기본 2)
    const delay     = config.delay ?? 300;             // 디바운스 지연(ms) (기본 300)

    // -------------------------------------------------------------
    // 4. 서버 응답 -> 화면 표현용 데이터로 매핑하는 함수
    //    - 서버 응답 item 하나를 {id, label, value} 형태로 변환
    //    - 기본 구현은 {id, name} 응답을 가정
    // -------------------------------------------------------------
    const mapResponse = config.mapResponse || function (item) {
      return {
        id: item.id,       // 식별자 (hidden input 등에 활용 가능)
        label: item.name,  // 목록에 보여줄 문자열
        value: item.name   // input에 실제로 채워 넣을 값
      };
    };

    // -------------------------------------------------------------
    // 5. 항목 선택 시(onClick) 수행할 콜백
    //    - 기본 구현: input에 선택된 값(item.value) 넣기
    // -------------------------------------------------------------
    const onSelect = config.onSelect || function (item) {
      $input.val(item.value);
    };

    // -------------------------------------------------------------
    // 6. 디바운스용 타이머 ID
    //    - 사용자가 입력할 때마다 서버 요청을 바로 보내지 않고,
    //      일정 시간(delay) 동안 입력이 멈췄을 때만 요청 보내기 위해 사용
    // -------------------------------------------------------------
    let timerId = null;

    // -------------------------------------------------------------
    // 7. 목록 비우기 + 숨기기
    //    - 후보가 없거나 ESC/바깥 클릭 등으로 목록을 닫을 때 사용
    // -------------------------------------------------------------
    function clearList() {
      $list.empty().hide();
    }

    // -------------------------------------------------------------
    // 8. 목록 렌더링 함수
    //    - items: [{id, label, value}, ...] 배열
    //    - 각 item을 <li>로 만들어 $list에 추가
    // -------------------------------------------------------------
    function renderList(items) {
      // 이전 목록 비우기
      $list.empty();

      // 결과가 없으면 목록 숨기고 종료
      if (!items || items.length === 0) {
        $list.hide();
        return;
      }

      // 각 item마다 <li> 요소 생성
      items.forEach(function (item) {
        const $li = $('<li>')
          // Bootstrap 스타일 + 자동완성용 클래스 추가
          .addClass('list-group-item list-group-item-action autocomplete-item')
          // 목록에 표시될 텍스트는 item.label
          .text(item.label)
          // 실제 item 객체를 jQuery data로 <li>에 저장
          .data('autocomplete-item', item)
          // <li> 클릭 시 동작 정의
          .on('click', function () {
            // 클릭된 <li>에 저장된 item 객체 꺼내오기
            const selected = $(this).data('autocomplete-item');
            // 외부에서 넘겨준 onSelect 콜백 호출
            onSelect(selected);
            // 선택 후 목록 닫기
            clearList();
          });

        // 생성된 <li>를 리스트 컨테이너에 추가
        $list.append($li);
      });

      // 하나 이상 항목이 있으면 목록 보여주기
      $list.show();
    }

    // -------------------------------------------------------------
    // 9. input 이벤트 핸들러
    //    - 사용자가 타이핑할 때마다 호출
    //    - minLength 이상 입력 시 디바운스를 걸고 서버 요청
    // -------------------------------------------------------------
    $input.on('input', function () {
      const q = $(this).val(); // 현재 입력된 문자열

      // 값이 없거나 최소 글자 수 미만이면 목록 닫고 종료
      if (!q || q.length < minLength) {
        clearList();
        return;
      }

      // 이전에 예약된 타이머가 있으면 취소 (디바운스 핵심)
      clearTimeout(timerId);

      // delay(ms) 이후 서버 요청을 수행하도록 타이머 등록
      timerId = setTimeout(function () {
        // 서버로 보낼 파라미터 객체 구성
        const params = {};
        // params['keyword'] = q 와 같은 효과 (paramName이 기본값 'keyword'인 경우)
        params[paramName] = q;

        // jQuery의 GET JSON 호출
        // 예: GET /api/.../autocomplete?keyword=입력값
        $.getJSON(url, params)
          .done(function (data) {
            // 서버에서 내려준 data를 배열로 보고, mapResponse로 변환
            // (data가 null/undefined일 경우를 대비해 (data || []) 사용)
            const items = (data || []).map(mapResponse);
            // 변환된 items로 목록 렌더링
            renderList(items);
          })
          .fail(function (xhr, status, err) {
            // 요청 실패 시 콘솔에 에러 출력 후 목록 닫기
            console.error('autocomplete 요청 실패', err);
            clearList();
          });
      }, delay); // delay 후에만 실제 서버 요청
    });

    // -------------------------------------------------------------
    // 10. keydown 이벤트 (ESC 키 처리)
    //     - ESC를 누르면 자동완성 목록 닫기
    // -------------------------------------------------------------
    $input.on('keydown', function (e) {
      if (e.key === 'Escape') clearList();
    });

    // -------------------------------------------------------------
    // 11. 문서 전체 클릭 이벤트
    //     - input이나 목록 바깥을 클릭하면 목록 닫기
    //     - UX: "바깥을 클릭하면 자동완성 창이 닫힌다" 동작 구현
    // -------------------------------------------------------------
    $(document).on('click', function (e) {
      const $target = $(e.target);

      // 클릭된 요소가 input 영역 안에도 아니고,
      // 목록 영역 안에도 아니면 -> 자동완성과 무관한 영역 클릭
      if (
        !$target.closest(config.inputSelector).length &&
        !$target.closest(config.listSelector).length
      ) {
        clearList();
      }
    });
  };

  // ---------------------------------------------------------------
  // 즉시 실행 함수(IIFE) 끝
  // - 위에서 정의한 내용은 이 함수 스코프 안에 캡슐화되고
  //   바깥에는 TeamCommon.autocomplete.init 만 노출되는 구조
  // ---------------------------------------------------------------

})(window, jQuery); // <- 여기서 global=window, $=jQuery 를 인자로 전달


(function (global) {
  // 전역 네임스페이스 보장
  global.TeamCommon = global.TeamCommon || {};
  const gridNs = global.TeamCommon.grid = global.TeamCommon.grid || {};

  /**
   * [1] 단일 Grid 헤더에 required-header 클래스 붙이기
   * @param {string} gridElementId  - Grid 컨테이너 id (예: 'groupGrid')
   * @param {string[]} columnNames  - 필수 컬럼 name 배열 (예: ['groupName'])
   */
  gridNs.markRequiredHeader = function (gridElementId, columnNames) {
    if (!gridElementId || !Array.isArray(columnNames)) return;

    // Toast UI Grid 헤더 셀(th)
    const selector =
      `#${gridElementId} .tui-grid-header-area .tui-grid-cell-header`;

    const headerCells = document.querySelectorAll(selector);

    headerCells.forEach(function (th) {
      const colName = th.getAttribute('data-column-name'); // 컬럼 name
      if (columnNames.includes(colName)) {
        th.classList.add('required-header'); // CSS 로 별표 붙일 클래스
      }
    });
  };

  /**
   * [2] 여러 Grid를 한 번에 처리
   * @param {Array<{gridId:string, columns:string[]}>} configs
   *   예: [
   *     { gridId: 'groupGrid', columns: ['groupName'] },
   *     { gridId: 'codeGrid',  columns: ['codeName']  }
   *   ]
   */
  gridNs.markRequiredHeaderMulti = function (configs) {
    if (!Array.isArray(configs)) return;

    configs.forEach(function (conf) {
      if (!conf || !conf.gridId || !Array.isArray(conf.columns)) return;
      gridNs.markRequiredHeader(conf.gridId, conf.columns);
    });
  };

  /**
   * [3] 화면에서 쓰기 편한 래퍼
   *  - Toast Grid 로딩 여부 / setTimeout 까지 내부에서 처리
   *  - 화면에서는 그냥 TeamCommon.grid.applyRequiredHeaders([...]) 한 줄만 호출
   */
  gridNs.applyRequiredHeaders = function (configs) {
    if (!Array.isArray(configs)) return;

    // Toast UI Grid 가 없으면 그냥 무시
    if (!global.tui || !global.tui.Grid) return;

    // 그리드 렌더 직후 DOM 이 꽉 잡히도록 0ms 딜레이
    setTimeout(function () {
      gridNs.markRequiredHeaderMulti(configs);
    }, 0);
  };

})(window);
