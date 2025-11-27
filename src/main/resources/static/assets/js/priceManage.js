// 단가정책유형 코드 매핑 (p1 → 거래처별, p2 → 품목별)
const priceTypeMap = {};   // ← 여기에 나중에 공통코드로 값이 채워짐

// 페이지 로딩 후 공통코드 로딩
window.addEventListener("DOMContentLoaded", () => {
    loadPriceTypeCodes();
});

// 공통코드(PTY) 불러오기
function loadPriceTypeCodes() {
    fetch('/price/type-codes')
        .then(res => res.json())
        .then(data => {
            // 1) 화면의 select(조회조건 + 등록폼) 채우기
            const selects = document.querySelectorAll('.price-type');

            selects.forEach(select => {
                data.forEach(item => {
                    const opt = document.createElement('option');
                    opt.value = item.codeId;       // p1, p2
                    opt.textContent = item.codeNm; // 거래처별, 품목별
                    select.appendChild(opt);
                });
            });

            // 2) 그리드 표시용 매핑 생성
            data.forEach(item => {
                // DB에 p1/p2 로 들어간다고 했으니 그대로 키 사용
                priceTypeMap[item.codeId] = item.codeNm;
            });

            // 3) 그리드가 이미 만들어져 있으면 다시 그리기
            if (window.priceGrid) {
                priceGrid.refresh();
            }
        })
        .catch(err => console.error('단가정책유형 코드 조회 오류:', err));
}
