// 비밀번호 변경 모달
document.addEventListener('DOMContentLoaded', function () {
  
  const usernameHidden = document.getElementById('pwChangeUsername');
  const isLoggedIn = !!(usernameHidden && usernameHidden.value);

  // 로그인 안 된 상태면 비밀번호 변경 모달 기능 전체 비활성화
  if (!isLoggedIn) {
    console.log('[PW MODAL] 비로그인 상태이므로 모달 스크립트를 실행하지 않습니다.');
    return;
  }
  
  const pwMenuItem   = document.getElementById('pwChangeMenuItem');
  const pwModal      = document.getElementById('pwChangeModal');
  const pwBackdrop   = document.getElementById('pwChangeBackdrop');
  const pwCloseBtn   = document.getElementById('pwChangeCloseBtn');
  const pwCancelBtn  = document.getElementById('pwChangeCancelBtn');
  const pwSaveBtn    = document.getElementById('pwChangeSaveBtn');

  const currentPwInput = document.getElementById('currentPassword');
  const newPwInput     = document.getElementById('newPassword');
  const confirmPwInput = document.getElementById('confirmPassword');

  const errorBox = document.getElementById('pwChangeErrorBox');
  const errorMsg = document.getElementById('pwChangeErrorMsg');

  // 🔹 정책 안내 영역 (새 HTML 구조 기준)
  const policyGuideEl  = document.getElementById('pwPolicyGuide');   // guide
  const policyLengthEl = document.getElementById('pwPolicyLength');  // lengthText
  const policyRulesEl  = document.getElementById('pwPolicyRules');   // ruleHtml

  // ================== 강제 비밀번호 변경 모드 여부 ==================
  let forcePwChangeMode = false;

  // URL 파라미터에서 forcePwChange 체크
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.get('forcePwChange') === 'true') {
    forcePwChangeMode = true;
  }

  // ================== 공통 함수 ==================

  function showPwError(msg) {
    if (!errorBox || !errorMsg) return;
    errorMsg.innerHTML = msg;   // 서버에서 넘어온 <br> 그대로 사용
    errorBox.classList.remove('d-none');
  }

  function hidePwError() {
    if (!errorBox || !errorMsg) return;
    errorBox.classList.add('d-none');
    errorMsg.textContent = '';
  }

  function resetPwInputs() {
    if (currentPwInput) currentPwInput.value = '';
    if (newPwInput)     newPwInput.value     = '';
    if (confirmPwInput) confirmPwInput.value = '';
  }

  // 🔹 비밀번호 정책 불러오기: /mypage/pwPolicyInfo
  function loadPwPolicy() {
    if (!policyGuideEl || !policyLengthEl || !policyRulesEl) {
      return;
    }

    // 로딩 중 표시
    policyGuideEl.textContent  = '비밀번호 정책을 불러오는 중입니다.';
    policyLengthEl.textContent = '';
    policyRulesEl.innerHTML    = '';

    axios.get('/mypage/pwPolicyInfo')
      .then(res => {
        const data = res.data; // PwPolicyInfoDto { guide, lengthText, ruleHtml }

        policyGuideEl.textContent  = data.guide || '';
        policyLengthEl.textContent = data.lengthText || '';
        // ruleHtml 안에 <br> 들어 있으니 innerHTML 사용
        policyRulesEl.innerHTML    = data.ruleHtml || '';
      })
      .catch(err => {
        console.error('비밀번호 정책 조회 오류:', err);
        policyGuideEl.textContent  = '비밀번호 정책을 불러오지 못했습니다.';
        policyLengthEl.textContent = '';
        policyRulesEl.innerHTML    = '';
      });
  }

  // ================== 모달 열기/닫기 ==================

  /**
   * 모달 열기
   * @param {boolean} forceOpen - true면 강제 모드로 열기 (닫기 막힘)
   */
  function openPwChangeModal(forceOpen) {
    hidePwError();
    resetPwInputs();
    loadPwPolicy();

    // 서버에서 강제로 부른 경우(forceOpen=true)에는 강제 모드 ON
    if (forceOpen === true) {
      forcePwChangeMode = true;
    }

    // 강제 모드면 X/취소 버튼 숨기기
    if (forcePwChangeMode) {
      if (pwCloseBtn)  pwCloseBtn.style.display  = 'none';
      if (pwCancelBtn) pwCancelBtn.style.display = 'none';
    } else {
      if (pwCloseBtn)  pwCloseBtn.style.display  = '';
      if (pwCancelBtn) pwCancelBtn.style.display = '';
    }

    if (!pwModal || !pwBackdrop) {
      console.warn('[PW MODAL] 모달 요소를 찾을 수 없습니다.');
      return;
    }

    pwModal.classList.add('show');
    pwModal.style.display = 'block';
    pwBackdrop.style.display = 'block';
  }

  /**
   * 모달 닫기
   * @param {boolean} forceClose - true면 강제 모드라도 닫기 허용 (비번 변경 성공 시)
   */
  function closePwChangeModal(forceClose) {
    // 강제 모드 + 일반 닫기 시도 → 막기
    if (forcePwChangeMode && !forceClose) {
      alert('비밀번호를 변경해 주세요.');
      return;
    }

    if (!pwModal || !pwBackdrop) return;
    pwModal.classList.remove('show');
    pwModal.style.display = 'none';
    pwBackdrop.style.display = 'none';
    hidePwError();
  }

  // ================== 이벤트 바인딩 ==================

  // 메뉴 클릭 → 모달 열기 (이건 강제 모드 아님)
  if (pwMenuItem) {
    pwMenuItem.addEventListener('click', function (e) {
      e.preventDefault();
      openPwChangeModal(false);
    });
  }

  // 닫기/취소 버튼
  if (pwCloseBtn) {
    pwCloseBtn.addEventListener('click', function () {
      closePwChangeModal(false);
    });
  }
  if (pwCancelBtn) {
    pwCancelBtn.addEventListener('click', function () {
      closePwChangeModal(false);
    });
  }

  // 모달 바깥 클릭 시 닫기
  if (pwModal) {
    pwModal.addEventListener('click', function (e) {
      if (!e.target.closest('.modal-content')) {
        closePwChangeModal(false);
      }
    });
  }

  // ================== 저장 버튼 → 비밀번호 변경 ==================

  if (pwSaveBtn) {
    pwSaveBtn.addEventListener('click', function () {
      hidePwError();

      if (!currentPwInput || !newPwInput || !confirmPwInput) {
        showPwError('비밀번호 입력 필드를 찾을 수 없습니다.');
        return;
      }

      const payload = {
        currentPassword:    currentPwInput.value,
        newPassword:        newPwInput.value,
        newPasswordConfirm: confirmPwInput.value
      };

      if (!payload.currentPassword || !payload.newPassword || !payload.newPasswordConfirm) {
        showPwError('모든 비밀번호를 입력해주세요.');
        return;
      }

      axios.post('/mypage/pwChange', payload)
        .then(res => {
          const data = res.data; // PwChangeResultDto { success, message }

          if (!data || data.success === false) {
            // 서버에서 내려준 메시지가 있으면 그대로 사용 (비밀번호 규칙 위반 등)
            showPwError(data && data.message ? data.message : '비밀번호 변경에 실패했습니다.');
            confirmPwInput.value = '';
            newPwInput.value     = '';
            newPwInput.focus();
            return;
          }

          alert(data.message || '비밀번호가 변경되었습니다.');

          // ✅ 비밀번호 변경 성공 시:
          //  1) 강제 모드라도 모달 닫기 허용
          //  2) URL에서 forcePwChange 파라미터 제거
          closePwChangeModal(true);

          const url = new URL(window.location.href);
          url.searchParams.delete('forcePwChange');
          window.history.replaceState({}, '', url.toString());

          // 이후부터는 일반 계정처럼 메뉴로 열고 자유롭게 닫을 수 있도록
          forcePwChangeMode = false;
        })
        .catch(err => {
          console.error('비밀번호 변경 오류:', err);
          showPwError('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        });
    });
  }

  // ================== 페이지 최초 로드 시 강제 모달 띄우기 ==================

  if (forcePwChangeMode) {
    // 서버에서 /?forcePwChange=true 로 보낸 경우 → 자동으로 모달 강제 오픈
    openPwChangeModal(true);
  }

});
