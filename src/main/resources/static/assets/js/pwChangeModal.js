// 비밀번호 변경 모달
document.addEventListener('DOMContentLoaded', function () {

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

  // 🔹 비밀번호 정책 불러오기: /mypage/pwPolicyInfo (하나만 사용)
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

  function openPwChangeModal() {
    hidePwError();
    resetPwInputs();
    loadPwPolicy();

    if (!pwModal || !pwBackdrop) {
      console.warn('[PW MODAL] 모달 요소를 찾을 수 없습니다.');
      return;
    }

    pwModal.classList.add('show');
    pwModal.style.display = 'block';
    pwBackdrop.style.display = 'block';
  }

  function closePwChangeModal() {
    if (!pwModal || !pwBackdrop) return;
    pwModal.classList.remove('show');
    pwModal.style.display = 'none';
    pwBackdrop.style.display = 'none';
    hidePwError();
  }

  // ================== 이벤트 바인딩 ==================

  // 메뉴 클릭 → 모달 열기
  if (pwMenuItem) {
    pwMenuItem.addEventListener('click', function (e) {
      e.preventDefault();
      openPwChangeModal();
    });
  }

  // 닫기/취소 버튼
  if (pwCloseBtn)  pwCloseBtn.addEventListener('click', closePwChangeModal);
  if (pwCancelBtn) pwCancelBtn.addEventListener('click', closePwChangeModal);

  // 모달 바깥 클릭 시 닫기
  if (pwModal) {
    pwModal.addEventListener('click', function (e) {
      if (!e.target.closest('.modal-content')) {
        closePwChangeModal();
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
        currentPassword: currentPwInput.value,
        newPassword:     newPwInput.value,
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
            newPwInput.value = '';
            newPwInput.focus();
            return;
          }

          alert(data.message || '비밀번호가 변경되었습니다.');
          closePwChangeModal();
        })
        .catch(err => {
          console.error('비밀번호 변경 오류:', err);
          showPwError('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        });
    });
  }

});
