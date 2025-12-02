// teamExportUtil.js

//toast ui의 key를 html태그의 id값과 매핑시키는 메서드 (규칙 -> 컬럼명 앞에 v_ 붙이면 알아서 매핑해줌)
export function bindGridToForm(rowData) {
  Object.keys(rowData).forEach(key => {
    const input = document.getElementById("v_" + key);
    if (!input) return;   // ✅ 이게 핵심 안전장치

    let value = rowData[key];

    // ✅ 날짜 컬럼 포맷 처리  
    if (key === "encpn" || key === "brthdy" || key === "creaDt" || key === "edcBeginDt" || key === "edcEndDt" || key === "complDt" || key === "vcatnBegin" || key === "vcatnEnd"
     || key === "updtDt") {
      if (value) {
        const d = new Date(value);
        value = d.toISOString().substring(0, 10); // yyyy-MM-dd
      }
    }

    // ✅ 기본 value 세팅
    input.value = value;

    

    // ✅ 이미지 처리
    if ("v_" + key === "v_proofPhoto") {
      input.src = value;
    }
  });
}

//input태그에 있는 id에 붙은 v_제거하여 db에 fetch로 보낼 데이터 가공해주는 메서드
export function formToJson(prefix = "v_") {
  const inputs = document.querySelectorAll(`[id^='${prefix}']`);
  const obj = {};
  inputs.forEach(input => {
    const key = input.id.replace(prefix, "");
    obj[key] = input.value;
  });
  return obj;
}
