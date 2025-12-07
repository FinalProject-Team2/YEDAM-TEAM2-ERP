// /assets/js/sessionTimeout.js
(function () {

  // 1) 서버에서 내려준 값
  var timeoutMin    = window.SESSION_TIMEOUT_MIN || 0;
  var timeoutAction = window.SESSION_TIMEOUT_ACTION || 't1';

  var WARN_BEFORE_SEC = 60; // 만료 60초 전에 경고 모달

  var warnTimerId = null;
  var logoutTimerId = null;
  var countdownIntervalId = null;
  var expireAt = null; // 만료 예정 시각 (ms)

  var $modal, $message, $btnExtend, $btnLogout;
  var $headerRemain, $headerExtendBtn;

  // 로그인 안 되어 있거나 세션 타임아웃이 설정 안 되어 있으면 상단바 숨기고 종료
  if (timeoutMin <= 0) {
    $(function () {
      $('#session-timer-box').hide();
    });
    return;
  }

  // -------------------------
  // DOM 요소 초기화
  // -------------------------
  function initElements() {
    // t2 경고 모달 쪽 요소 (이미 만들어 둔 HTML 기준)
    $modal     = $('#session-timeout-modal');
    $message   = $('#session-timeout-message');
    $btnExtend = $('#session-extend-btn');
    $btnLogout = $('#session-logout-btn');

    // 상단바 카운트다운 영역
    $headerRemain    = $('#session-remaining-text');
    $headerExtendBtn = $('#session-extend-top-btn');
  }

  // -------------------------
  // 모달 열기/닫기
  // -------------------------
  function openModal() {
    if (!$modal || !$modal.length) return;

    var text = '일정 시간 동안 사용이 없어 곧 로그아웃됩니다.\n'
      + '계속 사용하시려면 [세션 연장] 버튼을 눌러주세요.';

    if ($message && $message.length) {
      $message.text(text);
    }
    $modal.show();
  }

  function closeModal() {
    if ($modal && $modal.length) {
      $modal.hide();
    }
  }

  // -------------------------
  // 타이머/카운트다운 유틸
  // -------------------------
  function clearTimers() {
    if (warnTimerId) {
      clearTimeout(warnTimerId);
      warnTimerId = null;
    }
    if (logoutTimerId) {
      clearTimeout(logoutTimerId);
      logoutTimerId = null;
    }
  }

  function formatRemain(ms) {
    if (ms <= 0) return '00:00';

    var totalSec = Math.floor(ms / 1000);
    var min = Math.floor(totalSec / 60);
    var sec = totalSec % 60;

    var mm = (min < 10 ? '0' : '') + min;
    var ss = (sec < 10 ? '0' : '') + sec;
    return mm + ':' + ss;
  }

  function updateHeaderRemain() {
    if (!$headerRemain || !$headerRemain.length || !expireAt) return;

    var remainMs = expireAt - Date.now();
    $headerRemain.text(formatRemain(remainMs));

    if (remainMs <= 0 && countdownIntervalId) {
      clearInterval(countdownIntervalId);
      countdownIntervalId = null;
    }
  }

  function startCountdown() {
    if (!$headerRemain || !$headerRemain.length) return;

    if (countdownIntervalId) {
      clearInterval(countdownIntervalId);
    }
    updateHeaderRemain();
    countdownIntervalId = setInterval(updateHeaderRemain, 1000);
  }

  // 세션 만료 예정 시각을 "지금부터 timeoutMin 분 뒤"로 다시 잡고,
  // 상단 카운트다운 + t2 모달/자동 로그아웃 타이머를 모두 재설정
  function scheduleTimersFromNow() {
    clearTimers();

    var totalMs = timeoutMin * 60 * 1000;
    var warnMs = totalMs - WARN_BEFORE_SEC * 1000;
    if (warnMs < 0) warnMs = 0;

    expireAt = Date.now() + totalMs;
    startCountdown();

    // t2일 때만 경고 모달 + 강제 로그아웃
    if (timeoutAction === 't2') {
      warnTimerId = setTimeout(openModal, warnMs);
      logoutTimerId = setTimeout(forceLogout, totalMs);
    }
  }

  // -------------------------
  // 세션 연장 / 로그아웃 Ajax
  // -------------------------
  function extendSessionAjax() {
    $.ajax({
      url: '/api/session/extend',
      type: 'POST',
      success: function (res) {
        if (res && res.success) {
          // 정책이 바뀌었을 수도 있으니 서버에서 내려준 값으로 갱신
          timeoutMin = res.timeoutMin || timeoutMin;

          closeModal();
          scheduleTimersFromNow();
          alert('세션이 연장되었습니다.');
        } else {
          closeModal();
          alert((res && res.message)
            ? res.message
            : '세션 연장에 실패했습니다.\n다시 로그인해 주세요.');
          window.location.href = '/logIn';
        }
      },
      error: function () {
        closeModal();
        alert('세션 연장 요청 중 오류가 발생했습니다.\n다시 로그인해 주세요.');
        window.location.href = '/logIn';
      }
    });
  }

  function forceLogout() {
    $.ajax({
      url: '/logIn/logout',
      type: 'POST',
      complete: function () {
        window.location.href = '/logIn';
      }
    });
  }

  // -------------------------
  // 이벤트 바인딩
  // -------------------------
  function bindEvents() {
    // 모달 안의 [세션 연장] 버튼
    if ($btnExtend && $btnExtend.length) {
      $btnExtend.on('click', function () {
        extendSessionAjax();
      });
    }

    // 모달 안의 [지금 로그아웃] 버튼 (있다면)
    if ($btnLogout && $btnLogout.length) {
      $btnLogout.on('click', function () {
        forceLogout();
      });
    }

    // 상단바의 [세션 연장] 버튼
    if ($headerExtendBtn && $headerExtendBtn.length) {
      $headerExtendBtn.on('click', function () {
        extendSessionAjax();
      });
    }
  }

  // -------------------------
  // 초기 실행
  // -------------------------
  $(function () {
    initElements();
    bindEvents();
    scheduleTimersFromNow();

    // jQuery AJAX 요청이 한번 끝날 때마다
    // 서버 세션도 갱신됐다고 보고 클라이언트 타이머도 리셋
    $(document).ajaxComplete(function () {
      scheduleTimersFromNow();
    });
  });

})();
