// /assets/js/sessionTimeout.js
(function () {

  // -------------------------
  // 0. 서버에서 내려준 전역 변수들
  // -------------------------
  var enabled       = !!window.SESSION_TIMER_ENABLED;
  var timeoutMin     = window.SESSION_TIMEOUT_MIN    || 0;    // 정책 타임아웃(분)
  var timeoutAction  = window.SESSION_TIMEOUT_ACTION || 't1'; // t1: 경고 없이 자동 로그아웃, t2: 경고 + 자동 로그아웃
  var serverExpireAt = window.SESSION_EXPIRE_AT      || 0;    // ms
  var serverNowMs    = window.SERVER_NOW_MS          || 0;    // ms

  // 이 탭에서만 유지되는 만료 시각 저장 키
  var STORAGE_KEY = 'erp_session_expire_at';
	
  if (!enabled || timeoutMin <= 0) {
    $(function () {
      $('#session-remaining-badge').hide();
    });
    return;
  }
	
  // 만료 60초 전 경고 모달
  var WARN_BEFORE_SEC = 60;

  // 타이머 관련
  var warnTimerId = null;
  var logoutTimerId = null;
  var countdownIntervalId = null;
  var expireAt = null; // 클라이언트 기준 만료 시각(ms)

  // DOM 요소들
  var $headerBadge, $headerRemain, $headerExtendBtn;
  var $modal, $modalMessage, $modalExtendBtn, $modalLogoutBtn;

  // 타임아웃(분)이 0 이하면 세션 타이머 자체를 안 띄움
  if (timeoutMin <= 0) {
    $(function () {
      $('#session-remaining-badge').hide();
    });
    return;
  }

  // -------------------------
  // 1. DOM 요소 초기화
  // -------------------------
  function initElements() {
    // 헤더 영역
    $headerBadge     = $('#session-remaining-badge');
    $headerRemain    = $('#session-remaining-text');
    $headerExtendBtn = $('#session-extend-btn');

    // 모달 영역 (sessionTimeoutModal.html 기준)
    $modal          = $('#session-timeout-modal');
    $modalMessage   = $('#session-timeout-message');
    $modalExtendBtn = $('#session-modal-extend-btn');
    $modalLogoutBtn = $('#session-logout-btn');
  }

  // -------------------------
  // 2. 유틸: 포맷팅
  // -------------------------
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
    if (remainMs < 0) remainMs = 0;

    $headerRemain.text(formatRemain(remainMs));

    if (remainMs <= 0 && countdownIntervalId) {
      clearInterval(countdownIntervalId);
      countdownIntervalId = null;
    }
  }

  function startCountdown() {
    if (!$headerRemain || !$headerRemain.length || !expireAt) return;

    if (countdownIntervalId) {
      clearInterval(countdownIntervalId);
    }
    updateHeaderRemain();
    countdownIntervalId = setInterval(updateHeaderRemain, 1000);
  }

  function clearTimers() {
    if (warnTimerId) {
      clearTimeout(warnTimerId);
      warnTimerId = null;
    }
    if (logoutTimerId) {
      clearTimeout(logoutTimerId);
      logoutTimerId = null;
    }
    if (countdownIntervalId) {
      clearInterval(countdownIntervalId);
      countdownIntervalId = null;
    }
  }

  // -------------------------
  // 3. 모달 제어
  // -------------------------
  function openModal() {
    if (!$modal || !$modal.length) return;

    var text = '일정 시간 동안 사용이 없어 곧 로그아웃됩니다.\n'
      + '계속 사용하시려면 [세션 연장] 버튼을 눌러주세요.';

    if ($modalMessage && $modalMessage.length) {
      $modalMessage.text(text);
    }

    $modal.show();
  }

  function closeModal() {
    if ($modal && $modal.length) {
      $modal.hide();
    }
  }

  // -------------------------
  // 4. expireAt 초기 세팅
  //    - 우선 sessionStorage 값 사용
  //    - 없거나 이미 지난 값이면 서버 값으로 계산
  // -------------------------
  function applyInitialExpireAt() {
    var stored = null;

    try {
      stored = parseInt(sessionStorage.getItem(STORAGE_KEY), 10);
    } catch (e) {
      stored = null;
    }

    var now = Date.now();

    if (!isNaN(stored) && stored > now) {
      // 이전 페이지에서 쓰던 expireAt 그대로 사용
      expireAt = stored;
    } else {
      // 처음 진입이거나, 예전 값이 이미 지난 경우 → 서버 값 기준 초기화
      if (serverExpireAt > 0 && serverNowMs > 0) {
        var offset = now - serverNowMs; // 클라이언트-서버 시간차 보정
        expireAt = serverExpireAt + offset;
      } else {
        // 서버에서 값 못 받으면 정책값 기준으로 N분 후
        var totalMs = timeoutMin * 60 * 1000;
        expireAt = now + totalMs;
      }
    }

    // 현재 계산된 expireAt 을 이 탭에 저장
    try {
      sessionStorage.setItem(STORAGE_KEY, String(expireAt));
    } catch (e) {
      // 실패해도 동작은 계속
    }
  }

  // -------------------------
  // 5. 현재 expireAt 기준으로 타이머/경고/로그아웃 스케줄링
  // -------------------------
  function scheduleFromCurrentExpireAt() {
    clearTimers();

    if (!expireAt) return;

    var now = Date.now();
    var remainMs = expireAt - now;

    if (remainMs <= 0) {
      updateHeaderRemain();
      // 이미 만료 → 바로 로그아웃 처리
      forceLogout();
      return;
    }

    // 헤더 카운트다운 시작
    startCountdown();

    // t1 / t2 공통: 만료 시점에는 자동 로그아웃
    logoutTimerId = setTimeout(forceLogout, remainMs);

    // t2 인 경우에만 "만료 60초 전 경고 모달" 추가
    if (timeoutAction === 't2') {
      var warnMs = remainMs - WARN_BEFORE_SEC * 1000;
      if (warnMs < 0) warnMs = 0;

      warnTimerId = setTimeout(openModal, warnMs);
    }
  }

  // -------------------------
  // 6. 서버에 세션 연장 요청
  // -------------------------
  function extendSessionAjax() {
    $.ajax({
      url: '/api/session/extend',
      type: 'POST',
      success: function (res) {
        if (res && res.success) {

          // 정책이 바뀌었을 수도 있으니 서버에서 내려준 값으로 갱신
          timeoutMin = res.timeoutMin || timeoutMin;

          // 서버에서 expireAtMs, serverNowMs 내려줬으면 우선 사용
          /*if (res.expireAtMs && res.serverNowMs) {
            var offset = Date.now() - res.serverNowMs;
            expireAt = res.expireAtMs + offset;
          } else {
            // 혹시 필드가 없다면 정책 기준으로 계산 (fallback)
            expireAt = Date.now() + (timeoutMin * 60 * 1000);
          }*/
		  
		  expireAt = Date.now() + (timeoutMin * 60 * 1000);
		  
          // 새 만료시각을 브라우저에 저장
          try {
            sessionStorage.setItem(STORAGE_KEY, String(expireAt));
          } catch (e) {}

          closeModal();
          scheduleFromCurrentExpireAt();

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

  // -------------------------
  // 7. 강제 로그아웃
  // -------------------------
  function forceLogout() {
    $.ajax({
      url: '/logIn/logout',
      type: 'POST',
      complete: function () {
        // 이 탭에서 쓰던 만료시각 정보 제거
        try {
          sessionStorage.removeItem(STORAGE_KEY);
        } catch (e) {}
        window.location.href = '/logIn';
      }
    });
  }

  // -------------------------
  // 8. 이벤트 바인딩
  // -------------------------
  function bindEvents() {

    // 헤더의 [세션 연장] 버튼
    if ($headerExtendBtn && $headerExtendBtn.length) {
      $headerExtendBtn.on('click', function () {
        extendSessionAjax();
      });
    }

    // 모달 안의 [세션 연장]
    if ($modalExtendBtn && $modalExtendBtn.length) {
      $modalExtendBtn.on('click', function () {
        extendSessionAjax();
      });
    }

    // 모달 안의 [지금 로그아웃]
    if ($modalLogoutBtn && $modalLogoutBtn.length) {
      $modalLogoutBtn.on('click', function () {
        forceLogout();
      });
    }
  }

  // -------------------------
  // 9. 초기 실행
  // -------------------------
  $(function () {
    initElements();
    bindEvents();

    // (1) sessionStorage / 서버 값 기준으로 expireAt 설정
    applyInitialExpireAt();

    // (2) expireAt 기준으로 카운트다운 + 로그아웃/경고 예약
    scheduleFromCurrentExpireAt();

    // ⚠️ 예전에는 ajaxComplete 에서 scheduleFromNow() 로
    //     요청만 해도 계속 연장되게 했는데,
    //     이제는 "세션 연장 버튼"으로만 연장되게 하기 위해 제거함.
    //
    // $(document).ajaxComplete(function () {
    //   scheduleFromNow();
    // });
  });

})();
