// teamExportUtil.js

//toast ui의 key를 html태그의 id값과 매핑시키는 메서드 (규칙 -> 컬럼명 앞에 v_ 붙이면 알아서 매핑해줌)
export function bindGridToForm(rowData) {
  Object.keys(rowData).forEach(key => {
    const input = document.getElementById("v_" + key);
    if (input) input.value = rowData[key];
    if(("v_" + key) == "v_st") {
		if(rowData[key] == '신청'){
			//stIsShow
			document.getElementById("hugadeta").classList.add("stIsShow");
		}else if(rowData[key] == '승인'){
			document.getElementById("hugadeta").classList.remove("stIsShow");			
			document.getElementById("hugaSt1").classList.remove("stIsShow");			
			document.getElementById("hugaSt2").classList.add("stIsShow");			
		}else{
			document.getElementById("hugadeta").classList.remove("stIsShow");			
			document.getElementById("hugaSt1").classList.add("stIsShow");			
			document.getElementById("hugaSt2").classList.remove("stIsShow");
		}			
	}
	if(("v_" + key) == "v_proofPhoto") input.src = rowData[key];
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
