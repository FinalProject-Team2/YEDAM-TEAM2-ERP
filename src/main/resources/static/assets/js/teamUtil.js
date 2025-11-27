// teamUtil.js (공통 유틸)

// jQuery DOM ready
$(function () {

  // ==========================
  // 0-0. bootstrap-datepicker 기본 언어를 ko로 통일
  // ==========================
  if ($.fn.datepicker) {
    $.fn.datepicker.defaults.language = 'ko';
  }

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
// -------------------------------------------------------------------

(function (global, $) {
  global.TeamCommon = global.TeamCommon || {};

  const ns = global.TeamCommon.autocomplete = global.TeamCommon.autocomplete || {};

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

})(window, jQuery);


// -------------------------------------------------------------------
// Toast UI Grid 관련 공통 유틸
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

  gridNs.markRequiredHeaderMulti = function (configs) {
    if (!Array.isArray(configs)) return;

    configs.forEach(function (conf) {
      if (!conf || !conf.gridId || !Array.isArray(conf.columns)) return;
      gridNs.markRequiredHeader(conf.gridId, conf.columns);
    });
  };

  gridNs.applyRequiredHeaders = function (configs) {
    if (!Array.isArray(configs)) return;
    if (!global.tui || !global.tui.Grid) return;

    setTimeout(function () {
      gridNs.markRequiredHeaderMulti(configs);
    }, 0);
  };

})(window);