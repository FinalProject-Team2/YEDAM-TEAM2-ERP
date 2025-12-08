// teamUtil.js (공통 유틸)

// jQuery DOM ready
$(function () {

  // ==========================
  // 0. 공통 datepicker 기본 초기화
  // ==========================

  // 클래스가 datepicker인 모든 input에 bootstrap-datepicker 적용
  $('.datepicker').datepicker({
    format: 'yyyy-mm-dd',
    autoclose: true,
    todayHighlight: true,
    language: 'ko'
  });

  // 특정 id용 (쓰고 있으면) - 예전 코드 호환
  if ($('#datePickerGroup').length) {
    $('#datePickerGroup').datepicker({
      format: 'yyyy-mm-dd',
      autoclose: true,
      language: 'ko'
    });
  }

  // 예전 "적용일" 패턴 호환용
  // <div id="applyDatePicker"><input id="applyDate"> + <span id="icon-calendar">
  if ($('#applyDatePicker').length) {
    const $input = $('#applyDatePicker input');

    $input.datepicker({
      format: 'yyyy-mm-dd',
      autoclose: true,
      todayHighlight: true,
      language: 'ko'
    });

    // 아이콘 클릭 → input focus → 달력 열기
    $('#icon-calendar').on('click', function () {
      $('#applyDate').focus();
    });

    // 모달 위로 z-index 강제
    fixDatepickerZIndex($input);
  }

  // ==========================
  // 1. 입력 마스크: 숫자만 + yyyy-mm-dd 형식 강제
  // ==========================

  function attachDateMask($input) {
    if (!$input || !$input.length) return;

    // (1) input 이벤트: 숫자만 허용 + yyyy-mm-dd 포맷 + 월/일 보정
    $input.on('input', function () {
      let value = $(this).val();

      // 숫자만 남기기
      value = value.replace(/\D/g, '');
      value = value.slice(0, 8);   // 최대 8자리(yyyymmdd)
      let len = value.length;

      // ---- 월 범위(1~12) 보정 ----
      if (len >= 5) {
        const yearPart = value.slice(0, 4); // YYYY
        let monthPart = value.slice(4, 6);  // MM

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
        const ymPart = value.slice(0, 6); // YYYYMM
        let dayPart = value.slice(6, 8);  // DD
        let dayNum = parseInt(dayPart, 10);

        if (isNaN(dayNum) || dayNum <= 0) {
          dayNum = 1;
        } else if (dayNum > 31) {
          dayNum = 31;
        }
        dayPart = dayNum.toString().padStart(2, '0');
        value = ymPart + dayPart;
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
    $input.on('keydown', function (e) {
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

  // ==========================
  // 1-2. 모달 위로 datepicker z-index 강제 함수
  // ==========================
  function fixDatepickerZIndex($input) {
    if (!$input || !$input.length) return;

    // bootstrap-datepicker 의 show 이벤트에 걸기
    $input.on('show', function () {
      const $self  = $(this);
      const $modal = $self.closest('.modal');

      // 모달 안이 아니면 의미 없음
      if (!$modal.length) return;

      // 모달 z-index 읽기 (없으면 기본 1055로 가정)
      const modalZ = parseInt($modal.css('z-index'), 10) || 1055;

      // datepicker DOM이 생성되는 타이밍 때문에 약간 딜레이
      setTimeout(function () {
        $('.datepicker-dropdown').each(function () {
          this.style.setProperty('z-index', String(modalZ + 10), 'important');
        });
      }, 0);
    });
  }

  // ==========================
  // 2. 단일 datepicker (여러 개 가능, class 기반)
  // ==========================

  $('.js-date-single').each(function () {
    const $group = $(this);
    const $input = $group.find('.js-date-input');  // 실제 input
    const $icon  = $group.find('.js-date-icon');   // 달력 아이콘

    if (!$input.length) return;

    $input.datepicker({
      format: 'yyyy-mm-dd',
      autoclose: true,
      todayHighlight: true,
      language: 'ko'
    });

    // 숫자/포맷 마스크 적용
    attachDateMask($input);

    // 모달 위로 z-index 강제
    fixDatepickerZIndex($input);

    // 아이콘 클릭 → input focus → 달력 열림
    if ($icon.length) {
      $icon.on('click', function () {
        $input.focus();
      });
    }
  });

  // ==========================
  // 3. 시작/종료 기간 datepicker (range, 여러 쌍 가능 / class 기반)
  //
  // HTML 예시:
  //
  // <div class="d-flex align-items-center js-date-range">
  //   <!-- 시작일 -->
  //   <div class="input-group date">
  //     <input type="text"
  //            class="form-control datepicker js-date-range-start"
  //            placeholder="시작일">
  //     <span class="input-group-text js-date-range-icon-start">
  //       <i class="bi bi-calendar"></i>
  //     </span>
  //   </div>
  //
  //   <span class="mx-1 fw-bold fs-5">~</span>
  //
  //   <!-- 종료일 -->
  //   <div class="input-group date ms-2">
  //     <input type="text"
  //            class="form-control datepicker js-date-range-end"
  //            placeholder="종료일">
  //     <span class="input-group-text js-date-range-icon-end">
  //       <i class="bi bi-calendar"></i>
  //     </span>
  //   </div>
  // </div>
  //
  // 이 블럭 자체를 한 페이지에 여러 번 둬도 각자 독립적으로 동작
  // ==========================

  $('.js-date-range').each(function () {
    const $wrap       = $(this);
    const $startInput = $wrap.find('.js-date-range-start');
    const $endInput   = $wrap.find('.js-date-range-end');
    const $startIcon  = $wrap.find('.js-date-range-icon-start');
    const $endIcon    = $wrap.find('.js-date-range-icon-end');

    if (!$startInput.length || !$endInput.length) {
      console.warn('js-date-range: start/end input이 없습니다.', $wrap);
      return;
    }

    // yyyy-mm-dd 문자열을 Date 객체로 파싱
    function parseYMD(str) {
      if (!str) return null;
      const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(str);
      if (!m) return null;

      const y  = parseInt(m[1], 10);
      const mo = parseInt(m[2], 10) - 1; // 0-based
      const d  = parseInt(m[3], 10);

      const dt = new Date(y, mo, d);
      if (dt.getFullYear() !== y || dt.getMonth() !== mo || dt.getDate() !== d) {
        return null;
      }
      return dt;
    }

    // 시작일 → 종료일 최소 날짜/보정
    function handleStartChange(date) {
      if (!date) return;

      $endInput.datepicker('setStartDate', date);

      const endDate = $endInput.datepicker('getDate');
      if (endDate && endDate < date) {
        $endInput.datepicker('setDate', date);
      }
    }

    // 종료일 → 시작일 최대 날짜/보정
    function handleEndChange(date) {
      if (!date) return;

      $startInput.datepicker('setEndDate', date);

      const startDate = $startInput.datepicker('getDate');
      if (startDate && startDate > date) {
        $startInput.datepicker('setDate', date);
      }
    }

    // datepicker 기본 설정 + changeDate 이벤트
    $startInput
      .datepicker({
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayHighlight: true,
        language: 'ko'
      })
      .on('changeDate', function (e) {
        handleStartChange(e.date);
      });

    $endInput
      .datepicker({
        format: 'yyyy-mm-dd',
        autoclose: true,
        todayHighlight: true,
        language: 'ko'
      })
      .on('changeDate', function (e) {
        handleEndChange(e.date);
      });

    // 숫자/포맷 마스크 적용
    attachDateMask($startInput);
    attachDateMask($endInput);

    // 모달 위로 z-index 강제
    fixDatepickerZIndex($startInput);
    fixDatepickerZIndex($endInput);

    // 직접 숫자 입력 후 blur 시에도 datepicker 값 + 범위 로직 반영
    $startInput.on('blur', function () {
      const date = parseYMD($(this).val());
      if (date) {
        $startInput.datepicker('setDate', date);
      }
    });

    $endInput.on('blur', function () {
      const date = parseYMD($(this).val());
      if (date) {
        $endInput.datepicker('setDate', date);
      }
    });

    // 아이콘 클릭 → 해당 input focus → datepicker 열림
    if ($startIcon.length) {
      $startIcon.on('click', function () {
        $startInput.focus();
      });
    }
    if ($endIcon.length) {
      $endIcon.on('click', function () {
        $endInput.focus();
      });
    }
  });

  // ==========================
  // 4. 예전 id 기반 input에도 마스크 적용 (호환용)
  // ==========================


  const $legacyApplyDate = $('#applyDate');
  if ($legacyApplyDate.length) {
    attachDateMask($legacyApplyDate);
    fixDatepickerZIndex($legacyApplyDate);
  }


  /*attachDateMask($('#applyDate')); // 단일 날짜(예전 패턴)*/

  // ==========================
  // 5. Toast UI Grid 테마 적용
  // ==========================

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
	      background: '#FFFDF0',   // 연한 아이보리
	      text: '#000',
	      border: '#E1DBB8',       // 테두리
	      showVerticalBorder: true // 칸 구분 더 또렷
	    },
        selectedHeader: {
          background: '#f5f5f5'
        },
	    selected: {
	      background: '#FFF7C2',
	      border: '#C9A93F'
	    },
	    focused: {
	      border: '#9E812A'
	    },
      }
    });
  }
});


// -------------------------------------------------------------------
// 팀 공통 자동완성(autocomplete) 유틸리티
// -------------------------------------------------------------------

(function (global, $) {
  global.TeamCommon = global.TeamCommon || {};
  const ns = global.TeamCommon.autocomplete = global.TeamCommon.autocomplete || {};

  ns.init = function (config) {

    const $input = $(config.inputSelector);
    const $list = $(config.listSelector);

    if ($input.length === 0 || $list.length === 0) {
      console.warn('autocomplete - selector 확인 필요', config);
      return;
    }

    const url         = config.url;
    const paramName   = config.paramName || 'keyword';
    const minLength   = config.minLength ?? 2;
    const delay       = config.delay ?? 300;
    const preventClose = config.preventClose ?? false;   // ★ 추가 (모달 닫힘 방지 옵션)

    const mapResponse = config.mapResponse || function (item) {
      return {
        id: item.id,
        label: item.name,
        value: item.name
      };
    };

    const onSelect = config.onSelect || function (item) {
      $input.val(item.value);
    };

    function clearList() {
      $list.empty().hide();
    }

    function renderList(items) {
      $list.empty();
      if (!items || items.length === 0) {
        $list.hide();
        return;
      }

      items.forEach(function (item) {
        const $li = $('<li>')
          .addClass('list-group-item list-group-item-action autocomplete-item')
          .text(item.label)
          .data('autocomplete-item', item)
          // ★ mousedown 사용 (click 대신)
          .on('mousedown', function (e) {
            if (preventClose) {
              e.preventDefault();
              e.stopPropagation();
            }
            const selected = $(this).data('autocomplete-item');
            onSelect(selected);
            clearList();
          });

        $list.append($li);
      });

      $list.show();
    }

    let timerId = null;
    $input.on('input', function () {
      const q = $(this).val();
      if (!q || q.length < minLength) {
        clearList();
        return;
      }

      clearTimeout(timerId);
      timerId = setTimeout(function () {
        const params = {};
        params[paramName] = q;

        $.getJSON(url, params)
          .done(function (data) {
            const items = (data || []).map(mapResponse);
            renderList(items);
          })
          .fail(function () {
            console.error('autocomplete 요청 실패');
            clearList();
          });
      }, delay);
    });

    // esc
    $input.on('keydown', function (e) {
      if (e.key === 'Escape') clearList();
    });

    if (preventClose) {
      // ★ 목록 클릭해도 모달 닫히지 않게 처리
      $list.on('mousedown', function (e) {
        e.preventDefault();
        e.stopPropagation();
      });
    }

    // 외부 클릭 시 닫기
    $(document).on('click', function (e) {
      const $target = $(e.target);
      if (
        !$target.closest(config.inputSelector).length &&
        !$target.closest(config.listSelector).length
      ) {
        clearList();
      }
    });
  };

})(window, jQuery);

/*(function (global, $) {
  // 전역 네임스페이스(TeamCommon) 보장
  global.TeamCommon = global.TeamCommon || {};

  // 자동완성 전용 네임스페이스
  const ns = global.TeamCommon.autocomplete = global.TeamCommon.autocomplete || {};

  *
   * 자동완성 초기화 함수
   *
   * @param {Object} config - 설정 객체
   *  - inputSelector : (필수) input 요소 selector
   *  - listSelector  : (필수) 목록 컨테이너(ul 등) selector
   *  - url           : (필수) 서버 자동완성 API URL
   *  - paramName     : (선택) 파라미터 이름 (기본 'keyword')
   *  - minLength     : (선택) 최소 글자 수 (기본 2)
   *  - delay         : (선택) 디바운스(ms, 기본 300)
   *  - mapResponse   : (선택) item -> {id, label, value} 변환 함수
   *  - onSelect      : (선택) 항목 클릭 시 콜백
   
  ns.init = function (config) {
    const $input = $(config.inputSelector);
    const $list  = $(config.listSelector);

    if ($input.length === 0 || $list.length === 0) {
      console.warn('autocomplete - selector 확인 필요', config);
      return;
    }

    const url       = config.url;
    const paramName = config.paramName || 'keyword';
    const minLength = config.minLength ?? 2;
    const delay     = config.delay ?? 300;

    const mapResponse = config.mapResponse || function (item) {
      return {
        id: item.id,
        label: item.name,
        value: item.name
      };
    };

    const onSelect = config.onSelect || function (item) {
      $input.val(item.value);
    };

    let timerId = null;

    function clearList() {
      $list.empty().hide();
    }

    function renderList(items) {
      $list.empty();

      if (!items || items.length === 0) {
        $list.hide();
        return;
      }

      items.forEach(function (item) {
        const $li = $('<li>')
          .addClass('list-group-item list-group-item-action autocomplete-item')
          .text(item.label)
          .data('autocomplete-item', item)
          .on('click', function () {
            const selected = $(this).data('autocomplete-item');
            onSelect(selected);
            clearList();
          });

        $list.append($li);
      });

      $list.show();
    }

    // input 이벤트 (디바운스 + 서버 요청)
    $input.on('input', function () {
      const q = $(this).val();

      if (!q || q.length < minLength) {
        clearList();
        return;
      }

      clearTimeout(timerId);

      timerId = setTimeout(function () {
        const params = {};
        params[paramName] = q;

        $.getJSON(url, params)
          .done(function (data) {
            const items = (data || []).map(mapResponse);
            renderList(items);
          })
          .fail(function (xhr, status, err) {
            console.error('autocomplete 요청 실패', err);
            clearList();
          });
      }, delay);
    });

    // ESC 키로 목록 닫기
    $input.on('keydown', function (e) {
      if (e.key === 'Escape') clearList();
    });

    // input / 목록 바깥 클릭 시 목록 닫기
    $(document).on('click', function (e) {
      const $target = $(e.target);

      if (
        !$target.closest(config.inputSelector).length &&
        !$target.closest(config.listSelector).length
      ) {
        clearList();
      }
    });
  };

})(window, jQuery);*/


// -------------------------------------------------------------------
// Toast UI Grid 관련 공통 유틸
// - 필수 컬럼 헤더에 required-header 클래스 붙이기
// -------------------------------------------------------------------

(function (global) {
  global.TeamCommon = global.TeamCommon || {};
  const gridNs = global.TeamCommon.grid = global.TeamCommon.grid || {};

  gridNs.markRequiredHeader = function (gridElementId, columnNames) {
    if (!gridElementId || !Array.isArray(columnNames)) return;

    const selector =
      '#' + gridElementId + ' .tui-grid-header-area .tui-grid-cell-header';

    const headerCells = document.querySelectorAll(selector);

    headerCells.forEach(function (th) {
      const colName = th.getAttribute('data-column-name');
      if (columnNames.includes(colName)) {
        th.classList.add('required-header');
      }
    });
  };

  /**
   * [2] 여러 Grid를 한 번에 처리
   * @param {Array<{gridId:string, columns:string[]}>} configs
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
   *  - 화면에서는 TeamCommon.grid.applyRequiredHeaders([...]) 한 줄만 호출
   */
  gridNs.applyRequiredHeaders = function (configs) {
    if (!Array.isArray(configs)) return;
    if (!global.tui || !global.tui.Grid) return;

    setTimeout(function () {
      gridNs.markRequiredHeaderMulti(configs);
    }, 0);
  };

})(window);


// -------------------------------------------------------------------
// 모달 공통 유틸 - 모달이 닫힐 때 내부 폼/날짜/체크박스 초기화
// -------------------------------------------------------------------
(function (global, $) {
  global.TeamCommon = global.TeamCommon || {};
  const ns = global.TeamCommon.modal = global.TeamCommon.modal || {};

  /**
   * 모달 내부 폼 요소 초기화
   * @param {HTMLElement | string | jQuery} modalEl - 모달 엘리먼트 또는 selector
   */
  ns.reset = function (modalEl) {
    const $modal = $(modalEl);
    if (!$modal.length) return;

    // 1) 텍스트 input / textarea 초기화 (hidden, checkbox, radio 제외)
    $modal
      .find('input:not([type="hidden"]):not([type="checkbox"]):not([type="radio"]), textarea')
      .each(function () {
        $(this).val('');
      });

    // 2) select 박스는 첫 옵션으로
    $modal.find('select').each(function () {
      this.selectedIndex = 0;
      $(this).trigger('change'); // 필요하면 change 이벤트 발생
    });

    // 3) checkbox / radio 해제
    $modal.find('input[type="checkbox"], input[type="radio"]').prop('checked', false);

    // 4) datepicker / 팀 공통 날짜 input 정리
    $modal
      .find('.datepicker, .js-date-input, .js-ym-input, .js-date-range-start, .js-date-range-end')
      .each(function () {
        const $input = $(this);

        // bootstrap-datepicker가 붙어있는 경우
        if ($input.data('datepicker')) {
          // 값 지우기
          $input.datepicker('clearDates').datepicker('setDate', null);

          // 시작/종료 제한도 초기화 (필요 없으면 주석)
          $input.datepicker('setStartDate', null);
          $input.datepicker('setEndDate', null);
        } else {
          // 단순 input 인 경우
          $input.val('');
        }
      });

    // 5) 유효성 표시 같은 클래스 정리 (원하면)
    $modal.find('.is-invalid, .is-valid').removeClass('is-invalid is-valid');

    // 6) Toast UI Grid 관련: 선택만 지울지, 데이터까지 초기화할지는 팀 컨벤션에 맞게 선택
    if (global.tui && global.tui.Grid && typeof tui.Grid.getInstance === 'function') {
      // 이 모달 안에 있는 그리드 컨테이너 찾기
      $modal.find('.tui-grid-container, .tui-grid').each(function () {
        const inst = tui.Grid.getInstance(this);
        if (!inst) return;

        // (A) 데이터까지 비우고 싶으면
        // inst.clear();

        // (B) 데이터는 두고 선택만 지우고 싶으면
        try {
          inst.uncheckAll(); // rowHeaders에 checkbox 있을 때
          inst.clearSelection();
        } catch (e) {
          console.warn('Grid reset 중 오류', e);
        }
      });
    }
  };

  /**
   * Bootstrap 모달을 쓰는 페이지라면, 자동으로 hidden.bs.modal 에 묶어서 쓸 수도 있음
   * (지금은 수동으로 closeModal에서 호출할 거라, 옵션 느낌으로만 둠)
   */
  $(function () {
    $(document).on('hidden.bs.modal', '.modal', function () {
      // Bootstrap 모달을 쓴다면, 모달이 완전히 닫힌 시점에 자동 초기화
      ns.reset(this);
    });
  });

})(window, jQuery);

function closeModal() {
  if (!modal || !back) return;
  // 🔹 모달 닫기 전에 공통 초기화 호출
  if (window.TeamCommon && TeamCommon.modal && typeof TeamCommon.modal.reset === 'function') {
    TeamCommon.modal.reset(modal);
  }  
  modal.classList.remove("show");
  modal.style.display = "none";
  back.style.display = "none";
}


if (typeof modal !== "undefined") {
    modal.addEventListener("click", (event) => {
        if (!event.target.closest(".modal-content")) {
            closeModal();
        }
    });
}
        
        
/*
윤기추가 - 251129
date 타입 포멧 메소드 value, 'yyyy-MM-dd'
*/
const dateFormat = function (value, format) {
  let date = value == null ? new Date() : new Date(value)

  let year = date.getFullYear()
  let month = ('0' + (date.getMonth() + 1)).slice(-2)
  let day = ('0' + date.getDate()).slice(-2)

  let result = format.replace('yyyy', year).replace('MM', month).replace('dd', day)
  return result
}


// ==========================
// 공통 로그아웃 버튼 처리
// ==========================
$(function () {
  const $logoutMenuItem = $('#logoutMenuItem');

  if ($logoutMenuItem.length) {
    $logoutMenuItem.on('click', function (e) {
      e.preventDefault(); // a 태그의 기본 이동 막기

      if (!confirm('로그아웃 하시겠습니까?')) {
        return;
      }

      $.ajax({
        url: '/logIn/logout',
        type: 'POST'
      })
        .done(function (res) {
          // EmpLoginResultDto.ok() 구조를 쓴다고 가정
          if (!res || res.success === false) {
            alert((res && res.message) || '로그아웃 중 오류가 발생했습니다.');
            return;
          }

          // 성공 → 로그인 화면으로 이동
          location.href = '/logIn';
        })
        .fail(function () {
          alert('로그아웃 요청에 실패했습니다.');
        });
    });
  }
});
